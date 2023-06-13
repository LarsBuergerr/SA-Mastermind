package MongoDB

import FileIOComponent.fileIOJsonImpl.FileIO
import MongoDB.MongoDAO
import scala.concurrent.Future
import model.GameComponent.GameBaseImpl.*
import model.GameComponent.GameInterface
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.gridfs.ObservableFuture
import org.mongodb.scala.model.Aggregates.*
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.Sorts.*
import org.mongodb.scala.model.Updates.*
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observable, ObservableFuture}
import org.scalactic.Prettifier.default
import org.scalatest.concurrent.Futures.whenReady
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class MongoDAOSpec extends AnyWordSpec with Matchers with BeforeAndAfter {
  val fileIO = new FileIO()
  val gameFinal = Game()
  // Initialization of the test database
  val databasePassword = sys.env.getOrElse("MONGO_INITDB_ROOT_PASSWORD", "mongo")
  val databaseUser = sys.env.getOrElse("MONGO_INITDB_ROOT_USERNAME", "root")
  val databaseHost = sys.env.getOrElse("MONGO_INITDB_HOST", "localhost")
  val databasePort = sys.env.getOrElse("MONGO_INITDB_PORT", "27017")
  val databaseUrl = s"mongodb://$databaseUser:$databasePassword@$databaseHost:$databasePort/?authSource=admin"

  private val testDatabase: String = "mastermindTestDB"
  private val testCollection: String = "test_gameCollection"
  private var client: MongoClient = _
  private var db: MongoDatabase = _
  private var collection: MongoCollection[Document] = _
  private var mongoDAO: MongoDAO = _

  before {
    client = MongoClient(databaseUrl)
    db = client.getDatabase(testDatabase)
    collection = db.getCollection(testCollection)
    mongoDAO = new MongoDAO()
    mongoDAO.gameCollection = collection
  }

  after {
    Await.result(collection.drop().toFuture(), 5.seconds)
    client.close()
  }

  "MongoDAO" should {
    "save the game to the MongoDB collection" in {
      val game = Game()
      val solutionCode = new Code(Vector(Stone.apply("R"), Stone.apply("R"), Stone.apply("B"), Stone.apply("Y")))
      val savedGame1 = new Game(new Field(), solutionCode, 0, Init())
      val savedGame2 = new Game(new Field(), game.getCode(), 0, Init())

      val saveResult = Await.result(mongoDAO.save(savedGame2, "saveTest"), 5.seconds)
      val loadedGame = Await.result(mongoDAO.loadByName(Some("saveTest")), 5.seconds)

      saveResult shouldEqual true //true if Futures Success
      loadedGame.toString shouldEqual savedGame2.toString()
    }

    "delete the game from the MongoDB collection" in {
      val game = Game()
      val saveResult = mongoDAO.save(game, "deleteTest")
      Await.result(saveResult, 5.seconds)

      val currentHighestID = mongoDAO.getID(collection)
      val deleteResult = mongoDAO.delete(currentHighestID)
      val result = Await.result(deleteResult, 5.seconds)

      result shouldBe true //true if Futures Success
      val count = Await.result(collection.countDocuments().toFuture(), 5.seconds).head
      count shouldEqual (currentHighestID - 1)
    }

    "load the game by ID from the MongoDB collection" in {
      val game = Game()
      val saveResultThatWillBeLoaded = Await.result(mongoDAO.save(game, "loadTest"), 5.seconds)
      val saveResultThatNOTWillBeLoaded = Await.result(mongoDAO.save(game, "loadTestNotUse"), 5.seconds)

      val loadedGameFuture = mongoDAO.load(Some(1))
      val loadedGameOption = Await.result(loadedGameFuture, 5.seconds)
      loadedGameOption shouldBe Some(game)
    }
    "load the game by Name from the MongoDB collection" in {
      val game = Game()
      val saveResult = mongoDAO.save(game, "loadNameTest")
      Await.result(saveResult, 5.seconds)

      val loadedGameFuture = mongoDAO.loadByName(Some("loadNameTest"))
      val loadedGame = Await.result(loadedGameFuture, 5.seconds)

      loadedGame shouldBe game
    }
  }
}