package SlickDB

import FileIOComponent.fileIOJsonImpl.FileIO
import SQLTables.*
import database.FutureRetryResolver
import model.GameComponent.GameBaseImpl.Game
import model.GameComponent.GameInterface
import play.api.libs.json.{JsObject, JsValue, Json}
import slick.dbio.Effect.Read
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api.*
import slick.lifted.TableQuery

import java.sql.SQLNonTransientException
import scala.annotation.unused
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Try

/**
 * The SlickDAO class represents the data access object for Slick database operations.
 */
class SlickDAO extends DAOInterface {
  val WAIT_TIME = 10.seconds
  val WAIT_DB = 10000

  val fileIO = new FileIO()
  val futureRetryResolver = new FutureRetryResolver()

  val databaseDB: String = sys.env.getOrElse("MYSQL_DATABASE", "mastermind")
  val databaseUser: String = sys.env.getOrElse("MYSQL_USER", "admin")
  val databasePassword: String = sys.env.getOrElse("MYSQL_PASSWORD", "root")
  val databasePort: String = sys.env.getOrElse("MYSQL_PORT", "3306")
  val databaseHost: String = sys.env.getOrElse("MYSQL_HOST", "mastermind-database")
  val databaseUrl = s"jdbc:mysql://$databaseHost:$databasePort/$databaseDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true"

  // Establish the database connection
  val database = Database.forURL(
    url = databaseUrl,
    driver = "com.mysql.cj.jdbc.Driver",
    user = databaseUser,
    password = databasePassword
  )

  // Define table queries
  val matrixTable = new TableQuery(new MatrixTable(_))
  val matrixTable2 = new TableQuery(new MatrixTable(_))
  val hmatrixTable = new TableQuery(new HMatrixTable(_))
  val turnTable = new TableQuery(new TurnTable(_))
  val codeTable = new TableQuery(new CodeTable(_))
  val gameTable = new TableQuery(new GameTable(_))
  val stateTable = new TableQuery(new StateTable(_))
  val gameTable2 = new TableQuery(new GameTable2(_))

  // Define setup action to create tables if they don't exist
  val setup: DBIOAction[Unit, NoStream, Effect.Schema] = DBIO.seq(matrixTable.schema.createIfNotExists,
                                                                  matrixTable2.schema.createIfNotExists,
                                                                  hmatrixTable.schema.createIfNotExists,
                                                                  turnTable.schema.createIfNotExists,
                                                                  codeTable.schema.createIfNotExists,
                                                                  gameTable.schema.createIfNotExists,
                                                                  gameTable2.schema.createIfNotExists,
                                                                  stateTable.schema.createIfNotExists)
  try {
    Await.result(database.run(setup), WAIT_TIME)
  } catch {
    case e: SQLNonTransientException =>
      Thread.sleep(WAIT_DB)
      Await.result(database.run(setup), WAIT_TIME)
  }

  /**
   * Saves the game to the database.
   *
   * @param game      The game to be saved.
   * @param save_name The name of the save.
   * @return A Future indicating whether the save operation was successful (true) or not (false).
   */
  override def save(game: GameInterface, save_name: String): Future[Boolean] = {
    println("Slick save")
    val json_game = fileIO.gameToJson(game)

    val matrixIDFuture = storeMatrix(json_game("matrix").toString())
    val hmatrixIDFuture = storeHMatrix(json_game("hmatrix").toString())
    val codeIDFuture = storeCode(json_game("code").toString())
    val turnIDFuture = storeTurn(json_game("turn").toString().toInt)
    val stateIDFuture = storeState(json_game("state").toString())

    val gameIDFuture = for {
      matrixID <- matrixIDFuture
      hmatrixID <- hmatrixIDFuture
      codeID <- codeIDFuture
      turnID <- turnIDFuture
      stateID <- stateIDFuture
    } yield storeGame(matrixID, hmatrixID, codeID, turnID, stateID, save_name)

    val resolvedFuture = futureRetryResolver.resolveNonBlockingOnFuture(gameIDFuture)

    resolvedFuture.map(_ => true)
      .recover { case _ => false }
  }

  /**
   * Loads a game from the database.
   *
   * @param id The ID of the game to load.
   * @return A Future containing an optional GameInterface object.
   */
  override def load(id: Option[Int] = None): Future[Option[GameInterface]] = {
    println("Slick load")
    val query = gameTable2.filter(_.id === gameTable2.map(_.id).max)

    val gameFuture = database.run(query.result)
    val resolvedFuture = futureRetryResolver.resolveNonBlockingOnFuture(gameFuture)

    resolvedFuture.flatMap { games =>
      val gameOption = games.headOption
      gameOption match {
        case Some(game) =>
          val matrixID = game._2
          val hmatrixID = game._3
          val codeID = game._4
          val turnID = game._5
          val stateID = game._6

          val matrixFuture = database.run(matrixTable.filter(_.id === matrixID).result)
          val hmatrixFuture = database.run(hmatrixTable.filter(_.id === hmatrixID).result)
          val codeFuture = database.run(codeTable.filter(_.id === codeID).result)
          val turnFuture = database.run(turnTable.filter(_.id === turnID).result)
          val stateFuture = database.run(stateTable.filter(_.id === stateID).result)

          val resolvedMatrixFuture = futureRetryResolver.resolveNonBlockingOnFuture(matrixFuture)
          val resolvedHMatrixFuture = futureRetryResolver.resolveNonBlockingOnFuture(hmatrixFuture)
          val resolvedCodeFuture = futureRetryResolver.resolveNonBlockingOnFuture(codeFuture)
          val resolvedTurnFuture = futureRetryResolver.resolveNonBlockingOnFuture(turnFuture)
          val resolvedStateFuture = futureRetryResolver.resolveNonBlockingOnFuture(stateFuture)

          for {
            matrix <- resolvedMatrixFuture
            hmatrix <- resolvedHMatrixFuture
            code <- resolvedCodeFuture
            turn <- resolvedTurnFuture
            state <- resolvedStateFuture
          } yield {
            val jsonGame = Json.obj(
              "matrix" -> Json.parse(matrix.head._2),
              "hmatrix" -> Json.parse(hmatrix.head._2),
              "code" -> Json.parse(code.head._2),
              "turn" -> Json.toJson(turn.head._2),
              "state" -> Json.parse(state.head._2)
            )
            Some(fileIO.jsonToGame(jsonGame.asInstanceOf[JsValue]))
          }

        case None => Future.successful(None)
      }
    }
  }

  /**
   * Deletes a game from the database.
   *
   * @param id The ID of the game to delete.
   * @return A Future indicating whether the delete operation was successful (true) or not (false).
   */
  override def delete(id: Int): Future[Boolean] = {
    println("maxID test: " +getHighestID())
    val future = for {
      maxIdOption <- database.run(gameTable2.map(_.id).max.result)
      _ <- maxIdOption match {
        case Some(maxId) => database.run(gameTable2.filter(_.id === maxId).delete)
        case None => Future.successful(())
      }
    } yield true

    future.recover { case _ => false }
  }


  def storeGame(matrixID: Int, hmatrixID: Int, codeID: Int, turnID: Int, stateID: Int, save_name: String) = {
    val gameID = Await.result(database.run(gameTable2 returning gameTable2.map(_.id) += (0, matrixID, hmatrixID, codeID, turnID, stateID, save_name)), WAIT_TIME)
    gameID
  }

  /**
   * Updates a game in the database.
   *
   * @param game The GameInterface object representing the updated game.
   * @param id   The ID of the game to update.
   * @return A Future indicating whether the update operation was successful (true) or not (false).
   */
  override def update(game: GameInterface, id: Int): Future[Boolean] = {
    val jsonGame = fileIO.gameToJson(game)

    val matrix = jsonGame("matrix").toString()
    val hmatrix = jsonGame("hmatrix").toString()
    val code = jsonGame("code").toString()
    val turn = jsonGame("turn").toString().toInt
    val state = jsonGame("state").toString()

    val gameQ = gameTable2.filter(_.id === gameTable2.map(_.id).max)
    val gameResFuture = database.run(gameQ.result)

    val updatedGameFuture = gameResFuture.flatMap { gameRes =>
      val matrixID = gameRes.head._2
      val hmatrixID = gameRes.head._3
      val codeID = gameRes.head._4
      val turnID = gameRes.head._5
      val stateID = gameRes.head._6

      val matrixQ = matrixTable.filter(_.id === matrixID).update((matrixID, matrix))
      val hmatrixQ = hmatrixTable.filter(_.id === hmatrixID).update((hmatrixID, hmatrix))
      val codeQ = codeTable.filter(_.id === codeID).update((codeID, code))
      val turnQ = turnTable.filter(_.id === turnID).update((turnID, turn))
      val stateQ = stateTable.filter(_.id === stateID).update((stateID, state))

      val query = matrixQ andThen hmatrixQ andThen codeQ andThen turnQ andThen stateQ
      database.run(query)
    }

    updatedGameFuture.map(_ => true)
  }

  /**
   * Lists all games in the database.
   *
   * @return A Future indicating whether the list operation was successful (true) or not (false).
   */
  override def listAllGames(): Future[Boolean] = {
    val query = gameTable2
    val gamesFuture = database.run(query.result)
    //val resolvedGamesFuture = futureRetryResolver.resolveBlockingOnFuture(gamesFuture, WAIT_TIME)
    gamesFuture.map { games =>
      printGames(games)
      true
    }
  }

  /**
   * Prints the list of games to the console.
   *
   * @param games The sequence of games to print.
   * @return A Future representing the completion of the print operation.
   */
  def printGames(games: Seq[(Int, Int, Int, Int, Int, Int, String)]): Future[Unit] = {
    Future {
      games.foreach(game => println("GameID: " + game._1 + " | Name: " + game._7))
    }
  }

  /**
   * Stores a turn in the database.
   *
   * @param turn The turn to be stored.
   * @return A Future containing the ID of the stored turn.
   */
  def storeTurn(turn: Int): Future[Int] = {
    database.run(turnTable returning turnTable.map(_.id) += (0, turn))
  }

  /**
   * Stores a code in the database.
   *
   * @param code The code to be stored.
   * @return A Future containing the ID of the stored code.
   */
  def storeCode(code: String): Future[Int] = {
    database.run(codeTable returning codeTable.map(_.id) += (0, code))
  }

  /**
   * Stores a matrix in the database.
   *
   * @param matrix The matrix to be stored.
   * @return A Future containing the ID of the stored matrix.
   */
  def storeMatrix(matrix: String): Future[Int] = {
    database.run(matrixTable returning matrixTable.map(_.id) += (0, matrix))
  }

  /**
   * Stores a hidden matrix in the database.
   *
   * @param hmatrix The hidden matrix to be stored.
   * @return A Future containing the ID of the stored hidden matrix.
   */
  def storeHMatrix(hmatrix: String): Future[Int] = {
    database.run(hmatrixTable returning hmatrixTable.map(_.id) += (0, hmatrix))
  }

  /**
   * Stores a state in the database.
   *
   * @param state The state to be stored.
   * @return A Future containing the ID of the stored state.
   */
  def storeState(state: String): Future[Int] = {
    database.run(stateTable returning stateTable.map(_.id) += (0, state))
  }
  def getHighestID(): Future[Int] = {
    val query = gameTable2.map(_.id).max
    val highestIDAction: DBIO[Option[Int]] = query.result
    val highestIDFuture = database.run(highestIDAction)

    highestIDFuture.map(_.getOrElse(0))
  }

  /**
   * Sanitizes a string by replacing certain escape sequences with their corresponding characters.
   *
   * @param str The string to be sanitized.
   * @return The sanitized string with escape sequences replaced by their corresponding characters.
   */
  def sanitize(str: String): String =
    str.replace("\\n", "\n")
      .replace("\\r", "\r")
      .replace("\\t", "\t")
      .replace("\\b", "\b")
      .replace("\\f", "\f")
      .replace("\\\\", "\\")
      .replace("\\\"", "\"")
      .replace("\\'", "'")
      .replace("\"\"", "\"")
}