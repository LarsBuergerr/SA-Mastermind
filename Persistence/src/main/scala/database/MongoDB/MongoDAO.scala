package MongoDB

import FileIOComponent.fileIOJsonImpl.FileIO
import model.GameComponent.GameInterface

import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.*
import org.mongodb.scala.model.Aggregates.*
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.Sorts.*
import org.mongodb.scala.result.{DeleteResult, InsertOneResult, UpdateResult}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observable, Observer, SingleObservable, result}
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Try
import java.time.format.DateTimeFormatter
import akka.http.javadsl.model.headers.Date
import org.mongodb.scala.ObservableFuture
import org.mongodb.scala.gridfs.ObservableFuture


class MongoDAO extends DAOInterface {
  val fileIO = new FileIO()
  // db Init
  private val database_pw = sys.env.getOrElse("MONGO_INITDB_ROOT_PASSWORD", "mongo")
  private val database_username = sys.env.getOrElse("MONGO_INITDB_ROOT_USERNAME", "root")
  private val host = sys.env.getOrElse("MONGO_INITDB_HOST", "mastermind-mongo")
  private val port = sys.env.getOrElse("MONGO_INITDB_PORT", "27017")

  val uri: String = s"mongodb://$database_username:$database_pw@$host:$port/?authSource=admin"
  private val client: MongoClient = MongoClient(uri)
  println(uri)
  val db: MongoDatabase = client.getDatabase("mastermind")
  print(db)
  var gameCollection: MongoCollection[Document] = db.getCollection("game")
  println("Connected to MongoDB")

  override def save(game: GameInterface, save_name: String): Unit = 
    println("saving game to MongoDB")
    handleResult(gameCollection.insertOne(Document(
      "_id" -> (getID(gameCollection) + 1),
      "name" -> save_name,
      "game" -> fileIO.gameToJson(game).toString(),
    )))
    println("Inserted game with game id " +(getID(gameCollection)) )

  override def load(id: Option[Int]): Try[GameInterface] = 
    Try {
      Await.result(gameCollection.find(equal("_id", id.getOrElse(getID(gameCollection)))).first().head(), 10.second).get("game") match {
        case Some(value) => fileIO.jsonToGame(Json.parse(value.asString().getValue))
        case None => throw new Exception("No game found")
      }
    }

  override def delete(id: Int): Try[Boolean] =
    println("Deleting game from MongoDB")
    Try {
      Await.result(gameCollection.deleteOne(equal("_id", id)).head(), 10.second).wasAcknowledged()
    }
  
  
  override def update(game: GameInterface, id: Int): Boolean = 
    println("updating game in MongoDB")
    val result = Await.result(gameCollection.replaceOne(equal("_id", id), Document(
      "_id" -> id,
      "name" -> ("updated_" + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(java.time.LocalDateTime.now())),
      "game" -> fileIO.gameToJson(game).toString(),
    )).head(), 10.second)
    println("Finished update")
    result.wasAcknowledged()
  
  override def listAllGames(): Unit =
    println("All Games with name and id: \n")
    val result = Await.result(gameCollection.find().toFuture(), 10.second)
    result.foreach(doc => println("Name: \t" + doc.get("name").get.asString().getValue + "\nID: \t" + doc.get("_id").get.asInt32().getValue.toHexString + "\n"))


  def getID(coll: MongoCollection[Document]): Int =
    val result = Await.result(coll.aggregate(Seq(
      Aggregates.sort(Sorts.descending("_id")),
      Aggregates.limit(1),
      Aggregates.project(Document("_id" -> 1))
    )).headOption(), Inf)
    result.flatMap(_.get("_id").map(_.asInt32().getValue.toHexString)).getOrElse("0").toInt

  def handleResult[T](obs: SingleObservable[T]): Unit =
    try {
      Await.result(obs.asInstanceOf[SingleObservable[Unit]].head(), 10.second)
    } catch {
      case e: Exception => e.printStackTrace()
    }
    
    println("db operation successful")

    def getNewestId(collection: MongoCollection[Document]): Int =
      val result = Await.result(collection.find(exists("_id")).sort(descending("_id")).first().head(), Inf)
      if result != null then result("_id").asInt32().getValue else 0
}