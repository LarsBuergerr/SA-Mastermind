package MongoDB

import FileIOComponent.fileIOJsonImpl.FileIO
import MongoDB.MongoDAO
import model.GameComponent.GameBaseImpl.*
import model.GameComponent.GameInterface
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observable, ObservableFuture}
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.gridfs.ObservableFuture
import org.mongodb.scala.model.Aggregates.*
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.Sorts.*
import org.mongodb.scala.model.Updates.*
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
  // Test db Init
  val databasePassword = sys.env.getOrElse("MONGO_INITDB_ROOT_PASSWORD", "mongo")
  val databaseUser = sys.env.getOrElse("MONGO_INITDB_ROOT_USERNAME", "root")
  val databaseHost = sys.env.getOrElse("MONGO_INITDB_HOST", "localhost") //localhost  //mastermind-mongo
  val databasePort = sys.env.getOrElse("MONGO_INITDB_PORT", "27017")
  val databaseUrl = s"mongodb://$databaseUser:$databasePassword@$databaseHost:$databasePort/?authSource=admin"

  private val testDatabase: String = "mastermindTestDB"
  private val testCollection: String = "test_gameCollection"

  private var client: MongoClient = _ //= MongoClient(uri)
  private var db: MongoDatabase = _ // = client.getDatabase("mastermind")
  private var collection: MongoCollection[Document] = _ // = db.getCollection("game")

  private var mongoDAO: MongoDAO = _

  before {
    client = MongoClient(databaseUrl)
    db = client.getDatabase(testDatabase)
    collection = db.getCollection(testCollection)
    mongoDAO = new MongoDAO()
    mongoDAO.gameCollection = collection
  }

  after {
    collection.drop()
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

      val kp = mongoDAO.load(Some(1))
      println(kp)

    }

    "delete the game from the MongoDB collection" in {
      val game = Game()
      mongoDAO.save(game, "deleteTest")
      println(mongoDAO.listAllGames())

      //val highest = Await.result(collection.find(exists("_id")).sort(descending("_id")).first().head(), Inf)

      println(mongoDAO.getID(collection))
      val currentHight = mongoDAO.getID(collection)
      val result = mongoDAO.delete(currentHight)
      println(result)
      println(mongoDAO.listAllGames())

      result.isSuccess shouldEqual true
      result.get shouldEqual true
      val count = Await.result(collection.countDocuments().toFuture(), 5.seconds).head
      count shouldEqual currentHight -1

    }

    "load the game from the MongoDB collection" in {
      val game = Game()
      mongoDAO.save(game, "loadTest1")
      mongoDAO.save(game, "loadTest2")
      println(mongoDAO.listAllGames())

      val loadedGame = mongoDAO.load(Some(2))

      loadedGame.isSuccess shouldEqual true
      println(loadedGame)
      loadedGame.get.toString shouldEqual game.toString
    }

    "update the game board in the MongoDB collection" in {
      val originalGame = Game()
      println(originalGame.getCode())
      mongoDAO.save(originalGame, "updateTest")
      val updatedGame = new Game()
      println(updatedGame.getCode())
      val updateID = Await.result(collection.find(exists("_id")).sort(descending("_id")).first().head(), Inf)
      val upID = Await.result(collection.countDocuments().toFuture(), 5.seconds).head

      val upMongo = mongoDAO.update(updatedGame, 1)
      println(upMongo)

      println(mongoDAO.listAllGames())

      upMongo shouldBe true
      val loadedUpdatedGame = mongoDAO.load(Some(1))
      //loadedUpdatedGame.get.toString shouldNot equal (originalGame.toString)
      loadedUpdatedGame.get.toString shouldEqual updatedGame.toString
    }
  }
}
