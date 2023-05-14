package SlickDB

import FileIOComponent.fileIOJsonImpl.FileIO
import model.GameComponent.GameInterface
import SQLTables.{MatrixTable, TurnTable, CodeTable, GameTable}
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
    print("got into db load\n")

    print("another print before the try\n")
    Try {
      print("before query")
      val query = id.map(id => gameTable.filter(_.id === id))
        .getOrElse(gameTable.filter(_.id === gameTable.map(_.id).max))

      val game = Await.result(database.run(query.result), WAIT_TIME)

      val matrix = game.head._2
      print(matrix + "\n")
      val hmatrix = game.head._3
      print(hmatrix + "\n")
      val code = game.head._4
      print(code + "\n")
      val turn = game.head._5
      print(turn + "\n")
      val state = sanitize(game.head._6)
      print(state + "\n")

      val jsonGame = Json.obj(
        "matrix" -> Json.parse(matrix),
        "hmatrix" -> Json.parse(hmatrix),
        "code" -> Json.parse(code),
        "turn" -> Json.toJson(turn),
        "state" -> Json.toJson(state)
      )
      val res = try {
        fileIO.jsonToGame(jsonGame.asInstanceOf[JsValue])
      } catch {
        case e: Exception =>
          e.printStackTrace()
          throw e
      }
      res
    }
  }

  override def save(game: GameInterface) =
    Try {
      print("Saving game into database...\n")
      val jsonGame = fileIO.gameToJson(game)
      
      val gameID = storeMatrix(
        jsonGame("matrix").toString(),
        jsonGame("hmatrix").toString(),
        jsonGame("code").toString(),
        jsonGame("turn").toString().toInt,
        jsonGame("state").toString()
      )
    }

  def storeMatrix(matrix: String, hmatrix: String, code: String, turn: Int, state: String) = {
    val gameID = Await.result(database.run(gameTable returning gameTable.map(_.id) += (0, matrix, hmatrix, code, turn, state)), WAIT_TIME)
    gameID
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