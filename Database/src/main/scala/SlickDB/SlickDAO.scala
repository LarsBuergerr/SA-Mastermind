package SlickDB

import FileIOComponent.fileIOJsonImpl.FileIO
import SQLTables.ColorCodeTable
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

@unused
class SlickDAO
  //extends DAOInterface
  {
  val fileIO = new FileIO()
  val databaseDB: String = sys.env.getOrElse("MYSQL_DATABASE", "mastermind")
  val databaseUser: String = sys.env.getOrElse("MYSQL_USER", "admin")
  val databasePassword: String = sys.env.getOrElse("MYSQL_PASSWORD", "root")
  val databasePort: String = sys.env.getOrElse("MYSQL_PORT", "3306")
  val databaseHost: String = sys.env.getOrElse("MYSQL_HOST", "localhost")
  val databaseUrl = s"jdbc:mysql://$databaseHost:$databasePort/$databaseDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true"
  println(databaseUrl)
  val database = Database.forURL(
    url = databaseUrl,
    driver = "com.mysql.cj.jdbc.Driver",
    user = databaseUser,
    password = databasePassword
  )

  val setup: DBIOAction[Unit, NoStream, Effect.Schema] = DBIO.seq(ColorCodeTable.schema.createIfNotExists)
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


}