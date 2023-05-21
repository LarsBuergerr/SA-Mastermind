package MongoDB

import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import FileIOComponent.fileIOJsonImpl.FileIO
import model.GameComponent.GameInterface
import org.mongodb.scala.ObservableFuture
import org.mongodb.scala.gridfs.ObservableFuture
import org.mongodb.scala.model.Updates.{set, setOnInsert, unset}
import org.mongodb.scala.model.Filters.{equal, exists}
import org.mongodb.scala.model.Sorts.descending
import org.mongodb.scala.model.{Aggregates, Sorts, Updates}
import scala.concurrent.duration.*
import scala.concurrent.duration.Duration.Inf

import scala.util.Try
import akka.protobufv3.internal.Duration
import scala.concurrent.Await

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
  print(db)
  private val gameCollection: MongoCollection[Document] = db.getCollection("game")
  println("Connected to MongoDB")

  override def save(game: GameInterface, save_name: String): Unit = 
    println("save game to MongoDB")
    val document: Document = Document(
      "_id" -> getId(gameCollection),
      "save_name" -> save_name,
      "game" -> fileIO.gameToJson(game).toString()
    )
    Await.result(gameCollection.insertOne(document).head(), Inf)

  override def load(id: Option[Int]): Try[GameInterface] = ???
    println("load game from MongoDB")

  override def delete(id: Int): Try[Boolean] = ???
  println("update game in MongoDB")
  
  override def update(game: GameInterface, id: Int): Boolean = ???
    println("update game in MongoDB")
  
  override def listAllGames(): Unit =
    println("list all games from MongoDB")
    val result = Await.result(gameCollection.find().sort(descending("_id")).first().head(), Inf)
    if (result.isEmpty) println("No games found") else println(result.toJson())

  def getId(collection: MongoCollection[Document]): Int = {
    val result = Await.result(collection.find(exists("_id")).sort(descending("_id")).first().head(), Inf)
    if (result.isEmpty) 0 else result("_id").asInt32().getValue
  }
}
