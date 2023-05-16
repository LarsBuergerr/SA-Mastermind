package MongoDB

import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import FileIOComponent.fileIOJsonImpl.FileIO
import model.GameComponent.GameInterface

import scala.util.Try
class MongoDAO extends DAOInterface {
  val fileIO = new FileIO()
  // db Init
  private val database_pw = sys.env.getOrElse("MONGO_INITDB_ROOT_PASSWORD", "mongo")
  private val database_username = sys.env.getOrElse("MONGO_INITDB_ROOT_USERNAME", "root")
  private val host = sys.env.getOrElse("MONGO_HOST", "localhost")
  private val port = sys.env.getOrElse("MONGO_PORT", "27017")

  val uri: String = s"mongodb://$database_username:$database_pw@$host:$port/?authSource=admin"
  private val client: MongoClient = MongoClient(uri)
  println(uri)
  val db: MongoDatabase = client.getDatabase("mastermind")

  private val gameCollection: MongoCollection[Document] = db.getCollection("game")
  println("Connected to MongoDB")

  override def save(game: GameInterface, save_name: String): Unit = ???
    println("Saving game to MongoDB")

  override def load(id: Option[Int]): Try[GameInterface] = ???
    println("load game from MongoDB")

  override def delete(id: Int): Try[Boolean] = ???
  println("update game in MongoDB")
  
  override def update(game: GameInterface, id: Int): Boolean = ???
    println("update game in MongoDB")
  
   
}
