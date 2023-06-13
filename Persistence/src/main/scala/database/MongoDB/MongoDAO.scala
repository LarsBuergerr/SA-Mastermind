package MongoDB

import FileIOComponent.fileIOJsonImpl.FileIO
import akka.http.javadsl.model.headers.Date
import database.FutureRetryResolver
import model.GameComponent.GameBaseImpl.Game
import model.GameComponent.GameInterface
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.gridfs.ObservableFuture
import org.mongodb.scala.model.*
import org.mongodb.scala.model.Aggregates.*
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.Sorts.*
import org.mongodb.scala.result.{DeleteResult, InsertOneResult, UpdateResult}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observable, ObservableFuture, Observer, SingleObservable, result}
import play.api.libs.json.{JsObject, Json}

import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Try

/**
 * This class represents a MongoDB DAO (Data Access Object) for managing game data.
 * It provides methods for saving, loading, updating, deleting, and listing games in the database.
 */
class MongoDAO extends DAOInterface {
  val fileIO = new FileIO()
  val futureRetryResolver = new FutureRetryResolver()

  // Database initialization
  private val database_pw = sys.env.getOrElse("MONGO_INITDB_ROOT_PASSWORD", "mongo")
  private val database_username = sys.env.getOrElse("MONGO_INITDB_ROOT_USERNAME", "root")
  private val host = sys.env.getOrElse("MONGO_INITDB_HOST", "mastermind-mongo")
  private val port = sys.env.getOrElse("MONGO_INITDB_PORT", "27017")

  val uri: String = s"mongodb://$database_username:$database_pw@$host:$port/?authSource=admin"
  private val client: MongoClient = MongoClient(uri)
  val db: MongoDatabase = client.getDatabase("mastermind")
  var gameCollection: MongoCollection[Document] = db.getCollection("game")

  /**
   * Saves a game to the database.
   *
   * @param game       The game to be saved.
   * @param save_name  The name under which the game should be saved.
   * @return           A future that completes with true if the game is successfully saved, false otherwise.
   */
  def save(game: GameInterface, save_name: String): Future[Boolean] = {
    val future = Future {
      handleResult(gameCollection.insertOne(Document(
        "_id" -> (getID(gameCollection) + 1),
        "name" -> save_name,
        "game" -> fileIO.gameToJson(game).toString(),
      )))
      true // onSuccess
    }
    futureRetryResolver.resolveNonBlockingOnFuture(future)
  }

  /**
   * Loads a game from the database.
   *
   * @param id  Optional ID of the game to be loaded. If not provided, the most recent game will be loaded.
   * @return    A future that completes with an option of the loaded game.
   *            Some(game) if the game is successfully loaded, None otherwise.
   */
  override def load(id: Option[Int]): Future[Option[GameInterface]] = {
    val future: Future[Option[GameInterface]] = Future {
      val gameOption = Await.result(gameCollection.find(equal("_id", id.getOrElse(getID(gameCollection)))).first().head(), 10.second).get("game")
      gameOption match {
        case Some(value) => Some(fileIO.jsonToGame(Json.parse(value.asString().getValue)))
        case None => Some(new Game())
      }
    }
    futureRetryResolver.resolveNonBlockingOnFuture[Option[GameInterface]](future)
  }

  /**
   * Loads a game from the database by its name.
   *
   * @param name  Optional name of the game to be loaded. If not provided, the most recent game will be loaded.
   * @return      A future that completes with the loaded game.
   *              The loaded game if it is successfully loaded.
   * @throws      An exception if no game with the given name is found.
   */
  def loadByName(name: Option[String]): Future[GameInterface] = {
    val future = Future {
      val gameOption = Await.result(gameCollection.find(equal("name", name.getOrElse(getID(gameCollection)))).first().head(), 10.second).get("game")
      gameOption match {
        case Some(value) => fileIO.jsonToGame(Json.parse(value.asString().getValue))
        case None => throw new Exception("No game found")
      }
    }
    futureRetryResolver.resolveNonBlockingOnFuture(future)
  }

  /**
   * Deletes a game from the database by its ID.
   *
   * @param id  The ID of the game to be deleted.
   * @return    A future that completes with true if the game is successfully deleted, false otherwise.
   */
  def delete(id: Int): Future[Boolean] = {
    val future = Future {
      val result = Await.result(gameCollection.deleteOne(equal("_id", id)).head(), 10.second)
      result.wasAcknowledged()
    }
    futureRetryResolver.resolveNonBlockingOnFuture(future)
  }

  /**
   * Updates a game in the database.
   *
   * @param game  The updated game.
   * @param id    The ID of the game to be updated.
   * @return      A future that completes with true if the game is successfully updated, false otherwise.
   */
  def update(game: GameInterface, id: Int): Future[Boolean] = {
    val future = Future {
      val result = Await.result(gameCollection.replaceOne(equal("_id", id), Document(
        "_id" -> id,
        "name" -> ("updated_" + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(java.time.LocalDateTime.now())),
        "game" -> fileIO.gameToJson(game).toString(),
      )).head(), 10.second)
      result.wasAcknowledged()
    }
    futureRetryResolver.resolveNonBlockingOnFuture(future)
  }

  /**
   * Lists all games in the database.
   *
   * @return  A future that completes with true after printing the names and IDs of all games.
   */
  def listAllGames(): Future[Boolean] = {
    val future = Future {
      val result = Await.result(gameCollection.find().toFuture(), 10.second)
      result.foreach(doc => println("Name: \t" + doc.get("name").get.asString().getValue + "\nID: \t" + doc.get("_id").get.asInt32().getValue.toString() + "\n"))
    }
    futureRetryResolver.resolveNonBlockingOnFuture(future).map(_ => true)
  }

  /**
   * Gets the latest ID from the given collection.
   *
   * @param coll  The MongoDB collection.
   * @return      The latest ID in the collection.
   */
  def getID(coll: MongoCollection[Document]): Int = {
    val result = Await.result(coll.aggregate(Seq(
      Aggregates.sort(Sorts.descending("_id")),
      Aggregates.limit(1),
      Aggregates.project(Document("_id" -> 1))
    )).headOption(), Inf)
    result.flatMap(_.get("_id").map(_.asInt32().getValue.toString())).getOrElse("0").toInt
  }

  /**
   * Handles the result of a MongoDB operation.
   *
   * @param obs  The single observable representing the result.
   */
  def handleResult[T](obs: SingleObservable[T]): Unit = {
    try {
      Await.result(obs.asInstanceOf[SingleObservable[Unit]].head(), 10.second)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  /**
   * Gets the newest ID from the given collection.
   *
   * @param collection  The MongoDB collection.
   * @return            The newest ID in the collection.
   */
  def getNewestId(collection: MongoCollection[Document]): Int = {
    val result = Await.result(collection.find(exists("_id")).sort(descending("_id")).first().head(), Inf)
    if (result != null)
      result("_id").asInt32().getValue
    else
      0
  }
}
