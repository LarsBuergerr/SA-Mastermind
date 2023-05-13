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

  override def load(id: Option[Int] = None): Try[GameInterface] =
    ??? // TODO

  override def save(game: GameInterface) =
    Try {
      print("Saving game into database...\n")
      val jsonGame = fileIO.gameToJson(game)
      
      val gameID = storeMatrix(
        (jsonGame \ "matrix").get.toString(),
        (jsonGame \ "hmatrix").get.toString(),
        (jsonGame \ "code").get.toString(),
        (jsonGame \ "turn").get.toString().toInt,
        (jsonGame \ "state").get.toString()
      )
    }

  def storeMatrix(matrix: String, hmatrix: String, code: String, turn: Int, state: String) = {
    val gameID = Await.result(database.run(gameTable returning gameTable.map(_.id) += (0, matrix, hmatrix, code, turn, state)), WAIT_TIME)
    gameID
  }
}