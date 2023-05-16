package SlickDB

import FileIOComponent.fileIOJsonImpl.FileIO
import model.GameComponent.GameInterface
import SQLTables.{MatrixTable, TurnTable, CodeTable, GameTable, GameTable2, StateTable}
import slick.jdbc.JdbcBackend.Database

import java.sql.SQLNonTransientException
import concurrent.duration.DurationInt
import scala.annotation.unused
import scala.concurrent.Await
import scala.util.Try
import slick.lifted.TableQuery
import com.mysql.cj.jdbc.exceptions.CommunicationsException

import java.sql.SQLNonTransientException
import play.api.libs.json.{JsObject, Json}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api.*
import model.GameComponent.GameBaseImpl.Game
import play.api.libs.json.JsValue

val WAIT_TIME = 5.seconds
val WAIT_DB = 5000

class SlickDAO extends DAOInterface {
  //extends DAOInterface
  val fileIO = new FileIO()
  val databaseDB: String = sys.env.getOrElse("MYSQL_DATABASE", "mastermind")
  val databaseUser: String = sys.env.getOrElse("MYSQL_USER", "admin")
  val databasePassword: String = sys.env.getOrElse("MYSQL_PASSWORD", "root")
  val databasePort: String = sys.env.getOrElse("MYSQL_PORT", "3306")
  val databaseHost: String = sys.env.getOrElse("MYSQL_HOST", "mastermind-database")
  val databaseUrl = s"jdbc:mysql://$databaseHost:$databasePort/$databaseDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true"
  println(databaseUrl)
  val database = Database.forURL(
    url = databaseUrl,
    driver = "com.mysql.cj.jdbc.Driver",
    user = databaseUser,
    password = databasePassword
  )
  val matrixTable = new TableQuery(new MatrixTable(_))
  val turnTable = new TableQuery(new TurnTable(_))
  val codeTable = new TableQuery(new CodeTable(_))
  val gameTable = new TableQuery(new GameTable(_))
  val stateTable = new TableQuery(new StateTable(_))
  val gameTable2 = new TableQuery(new GameTable2(_))

  val setup: DBIOAction[Unit, NoStream, Effect.Schema] = DBIO.seq(matrixTable.schema.createIfNotExists, 
                                                                  turnTable.schema.createIfNotExists, 
                                                                  codeTable.schema.createIfNotExists,
                                                                  gameTable.schema.createIfNotExists)
  println("create tables")
  try {
    Await.result(database.run(setup), WAIT_TIME)
  } catch {
    case e: SQLNonTransientException =>
      println("Waiting for DB connection")
      Thread.sleep(WAIT_DB)
      Await.result(database.run(setup), WAIT_TIME)
  }
  println("tables created")

  override def load(id: Option[Int] = None): Try[GameInterface] = {
    Try {
      print("before query")
      val query = id.map(id => gameTable.filter(_.id === id))
        .getOrElse(gameTable.filter(_.id === gameTable.map(_.id).max))

      val game = Await.result(database.run(query.result), WAIT_TIME)

      val matrix = game.head._2
      val hmatrix = game.head._3
      val code = game.head._4
      val turn = game.head._5
      val state = sanitize(game.head._6)

      val jsonGame = Json.obj(
        "matrix" -> Json.parse(matrix),
        "hmatrix" -> Json.parse(hmatrix),
        "code" -> Json.parse(code),
        "turn" -> Json.toJson(turn),
        "state" -> Json.parse(state)
      )
      val res = fileIO.jsonToGame(jsonGame.asInstanceOf[JsValue])
      res
    }
  }

  override def save(game: GameInterface) =
    Try {
      print("Saving game into database...\n")
      val jsonGame = fileIO.gameToJson(game)
      
      val gameID = storeGame(
        jsonGame("matrix").toString(),
        jsonGame("hmatrix").toString(),
        jsonGame("code").toString(),
        jsonGame("turn").toString().toInt,
        jsonGame("state").toString()
      )
    }

  def save2(game: GameInterface) =
    val json_game = fileIO.gameToJson(game)

    val matrixID = storeMatrix(json_game("matrix").toString())

    val hmatrixID = storeHMatrix(json_game("hmatrix").toString())

    val codeID = storeCode(json_game("code").toString())

    val turnID = storeTurn(json_game("turn").toString().toInt)

    val stateID = storeState(json_game("state").toString())

    val gameID = storeGame2(
      matrixID,
      hmatrixID,
      codeID,
      turnID,
      stateID
    )

  def load2(id: Option[Int] = None) =
    val query = id.map(id => gameTable2.filter(_.id === id))
      .getOrElse(gameTable2.filter(_.id === gameTable2.map(_.id).max))

    val game = Await.result(database.run(query.result), WAIT_TIME)

    val matrixID = game.head._2
    val hmatrixID = game.head._3
    val codeID = game.head._4
    val turnID = game.head._5
    val stateID = game.head._6

    val matrix = Await.result(database.run(matrixTable.filter(_.id === matrixID).result), WAIT_TIME).head._2
    val hmatrix = Await.result(database.run(matrixTable.filter(_.id === hmatrixID).result), WAIT_TIME).head._2
    val code = Await.result(database.run(codeTable.filter(_.id === codeID).result), WAIT_TIME).head._2
    val turn = Await.result(database.run(turnTable.filter(_.id === turnID).result), WAIT_TIME).head._2
    val state = Await.result(database.run(stateTable.filter(_.id === stateID).result), WAIT_TIME).head._2

    val jsonGame = Json.obj(
      "matrix" -> Json.parse(matrix),
      "hmatrix" -> Json.parse(hmatrix),
      "code" -> Json.parse(code),
      "turn" -> Json.toJson(turn),
      "state" -> Json.parse(state)
    )
    val res = fileIO.jsonToGame(jsonGame.asInstanceOf[JsValue])
    res

  def storeGame2(matrixID: Int, hmatrixID: Int, codeID: Int, turnID: Int, stateID: Int) = {
    val gameID = Await.result(database.run(gameTable2 returning gameTable2.map(_.id) += (0, matrixID, hmatrixID, codeID, turnID, stateID)), WAIT_TIME)
    gameID
  }
  
  override def delete(id: Int): Try[Boolean] = {
    Try {
      Await.result(database.run(gameTable.filter(_.id === id).delete), WAIT_TIME)
      true
    }
  }

  override def update(game: GameInterface, id: Int): Try[Boolean] = {
    Try{
      val jsonGame = fileIO.gameToJson(game)
      val query = gameTable.filter(_.id === id)
      val action = query.update(
        (id, jsonGame("matrix").toString(), jsonGame("hmatrix").toString(), jsonGame("code").toString(), jsonGame("turn").toString().toInt, jsonGame("state").toString())
      )
      Await.result(database.run(action), WAIT_TIME)
      true
    }
  }

  def listAllGames() = {
    val query = gameTable
    val games = Await.result(database.run(query.result), WAIT_TIME)
    printGames(games)
  }

  def printGames(games: Seq[(Int, String, String, String, Int, String)]) = {

    games.foreach(game => println("GameID: " + game._1))
  }


  def storeGame(matrix: String, hmatrix: String, code: String, turn: Int, state: String) = {
    val gameID = Await.result(database.run(gameTable returning gameTable.map(_.id) += (0, matrix, hmatrix, code, turn, state)), WAIT_TIME)
    gameID
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
    val hmatrixID = Await.result(database.run(matrixTable returning matrixTable.map(_.id) += (0, hmatrix)), WAIT_TIME)
    hmatrixID
  }

  def storeState(state: String) = {
    val stateID = Await.result(database.run(matrixTable returning matrixTable.map(_.id) += (0, state)), WAIT_TIME)
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