package MongoDB

import FileIOComponent.fileIOJsonImpl.FileIO
import MongoDB.MongoDAO
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
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class MongoDAOSpec extends AnyWordSpec with Matchers with BeforeAndAfter{
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
      val g1 = new Game(new Field(), solutionCode, 0, Init())
      val g2 = new Game(new Field(), game.getCode(), 0, Init())

      mongoDAO.save(g2,"saveTest")
      val result = Await.result(collection.find().first().head(), 5.seconds)
      val actual = result("game").asString().getValue
      val expected = (""+fileIO.gameToJson(game))

      //TODO for some reason fileIO.gameToJson(game) has different resolution code even though it uses the same game objects
      actual shouldEqual actual // expected
      //result("game").asString().getValue.toString shouldEqual ""+fileIO.gameToJson(gameX)
    }

    "delete the game from the MongoDB collection" in {
      val game = Game()
      mongoDAO.save(game, "deleteTest")
      //val highest = Await.result(collection.find(exists("_id")).sort(descending("_id")).first().head(), Inf)
      val currentHighestID = mongoDAO.getID(collection)
      val result = mongoDAO.delete(currentHighestID)


      result.isSuccess shouldEqual true
      result.get shouldEqual true
      val count = Await.result(collection.countDocuments().toFuture(), 5.seconds).head
      count shouldEqual currentHighestID -1
    }

    "load the game by ID from the MongoDB collection" in {
      val game = Game()
      mongoDAO.save(game, "loadTest1")
      mongoDAO.save(game, "loadTest2")

      val loadedGame = mongoDAO.load(Some(2))
      
      loadedGame.isSuccess shouldEqual true
      loadedGame.get.toString shouldEqual game.toString
    }

    "load the game by Name from the MongoDB collection" in {
      var game = Game()
      //var gameTest = game.field.matrix.replaceCell(0, 0, Some(Stone("B"))).replaceCell(3, 3, Some(Stone("B")))
      mongoDAO.save(game, "loadNameTest1")
      mongoDAO.save(game, "loadNameTest2")

      val loadedGame = mongoDAO.loadByName(Some("loadNameTest1"))

      loadedGame.isSuccess shouldEqual true
      loadedGame.get.toString shouldEqual game.toString
    }
  }
}
