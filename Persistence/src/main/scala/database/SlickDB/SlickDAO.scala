package SlickDB

import FileIOComponent.fileIOJsonImpl.FileIO
import SQLTables.*
import model.GameComponent.GameBaseImpl.Game
import model.GameComponent.GameInterface
import play.api.libs.json.{JsObject, JsValue, Json}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api.*
import slick.lifted.TableQuery

import java.sql.SQLNonTransientException
import scala.annotation.unused
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.Try

val WAIT_TIME = 5.seconds
val WAIT_DB = 5000

class SlickDAO extends DAOInterface {
  //extends DAOInterface
  val fileIO = new FileIO()
  val databaseDB: String = sys.env.getOrElse("MYSQL_DATABASE", "mastermind")
  val databaseUser: String = sys.env.getOrElse("MYSQL_USER", "admin")
  val databasePassword: String = sys.env.getOrElse("MYSQL_PASSWORD", "root")
  val databasePort: String = sys.env.getOrElse("MYSQL_PORT", "3306")
  val databaseHost: String = sys.env.getOrElse("MYSQL_HOST", "localhost")
  val databaseUrl = s"jdbc:mysql://$databaseHost:$databasePort/$databaseDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true"
  val database = Database.forURL(
    url = databaseUrl,
    driver = "com.mysql.cj.jdbc.Driver",
    user = databaseUser,
    password = databasePassword
  )
  val matrixTable = new TableQuery(new MatrixTable(_))
  val matrixTable2 = new TableQuery(new MatrixTable(_))
  val hmatrixTable = new TableQuery(new HMatrixTable(_))
  val turnTable = new TableQuery(new TurnTable(_))
  val codeTable = new TableQuery(new CodeTable(_))
  val gameTable = new TableQuery(new GameTable(_))
  val stateTable = new TableQuery(new StateTable(_))
  val gameTable2 = new TableQuery(new GameTable2(_))

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


  override def save(game: GameInterface, save_name: String) =
    Try {
      val json_game = fileIO.gameToJson(game)

      val matrixID = storeMatrix(json_game("matrix").toString())

      val hmatrixID = storeHMatrix(json_game("hmatrix").toString())

      val codeID = storeCode(json_game("code").toString())

      val turnID = storeTurn(json_game("turn").toString().toInt)

      val stateID = storeState(json_game("state").toString())

      val gameID = storeGame(
        matrixID,
        hmatrixID,
        codeID,
        turnID,
        stateID,
        save_name
      )
    }


  override def load(id: Option[Int] = None) =
    Try {
      val query = gameTable2.filter(_.id === gameTable2.map(_.id).max)

      val game = Await.result(database.run(query.result), WAIT_TIME)

      val matrixID = game.head._2
      val hmatrixID = game.head._3
      val codeID = game.head._4
      val turnID = game.head._5
      val stateID = game.head._6

      val matrix = Await.result(database.run(matrixTable.filter(_.id === matrixID).result), WAIT_TIME).head._2
      val hmatrix = Await.result(database.run(hmatrixTable.filter(_.id === hmatrixID).result), WAIT_TIME).head._2
      val code = Await.result(database.run(codeTable.filter(_.id === codeID).result), WAIT_TIME).head._2
      val turn = Await.result(database.run(turnTable.filter(_.id === turnID).result), WAIT_TIME).head._2
      val state = Await.result(database.run(stateTable.filter(_.id === stateID).result), WAIT_TIME).head._2

      val jsonGame = Json.obj(
        "matrix" -> Json.parse(matrix),
        "hmatrix" -> Json.parse(hmatrix),
        "code" -> Json.parse(code),
        "turn" -> Json.toJson(turn),
        "state" -> Json.parse (state)
      )
      val res = fileIO.jsonToGame(jsonGame.asInstanceOf[JsValue])
      res
    }

  override def delete(id: Int): Try[Boolean] = {
    Try {
      Await.result(database.run(gameTable2.filter(_.id === gameTable2.map(_.id).max).delete), WAIT_TIME)
      true
    }
  }


  def storeGame(matrixID: Int, hmatrixID: Int, codeID: Int, turnID: Int, stateID: Int, save_name: String) = {
    val gameID = Await.result(database.run(gameTable2 returning gameTable2.map(_.id) += (0, matrixID, hmatrixID, codeID, turnID, stateID, save_name)), WAIT_TIME)
    gameID
  }

  override def update(game: GameInterface, id: Int): Try[Boolean] = {
    Try {
      val jsonGame = fileIO.gameToJson(game)

      val matrix = jsonGame("matrix").toString()
      val hmatrix = jsonGame("hmatrix").toString()
      val code = jsonGame("code").toString()
      val turn = jsonGame("turn").toString().toInt
      val state = jsonGame("state").toString()

      val gameQ = gameTable2.filter(_.id === gameTable2.map(_.id).max)
      val gameRes = Await.result(database.run(gameQ.result), WAIT_TIME)

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
      Await.result(database.run(query), WAIT_TIME)
      true
    }
  }
  
  override def listAllGames() = {
    val query = gameTable2
    val games = Await.result(database.run(query.result), WAIT_TIME)
    printGames(games)
  }

  def printGames(games: Seq[(Int, Int, Int, Int, Int, Int, String)]) = {
    games.foreach(game => println("GameID: " + game._1 + " | Name: " + game._7))
  }

  def storeTurn(turn: Int) = {
    val turnID = Await.result(database.run(turnTable returning turnTable.map(_.id) += (0, turn)), WAIT_TIME)
    turnID
  }

  def storeCode(code: String) = {
    val codeID = Await.result(database.run(codeTable returning codeTable.map(_.id) += (0, code)), WAIT_TIME)
    codeID
  }

  def storeMatrix(matrix: String) = {
    val matrixID = Await.result(database.run(matrixTable returning matrixTable.map(_.id) += (0, matrix)), WAIT_TIME)
    matrixID
  }

  def storeHMatrix(hmatrix: String) = {
    val hmatrixID = Await.result(database.run(hmatrixTable returning hmatrixTable.map(_.id) += (0, hmatrix)), WAIT_TIME)
    hmatrixID
  }

  def storeState(state: String) = {
    val stateID = Await.result(database.run(stateTable returning stateTable.map(_.id) += (0, state)), WAIT_TIME)
    stateID
  }

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