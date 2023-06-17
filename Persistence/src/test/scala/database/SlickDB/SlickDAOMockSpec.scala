package SlickDB

import FileIOComponent.fileIOJsonImpl.FileIO
import SQLTables.{CodeTable, GameTable, MatrixTable, TurnTable}
import SlickDB.SlickDAOMock
import com.github.nscala_time.time.Imports.Duration
import model.GameComponent.GameBaseImpl.Game
import model.GameComponent.GameInterface
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers.shouldBe
import org.scalatest.wordspec.AnyWordSpec
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api.*
import slick.lifted.TableQuery
import org.scalactic.Prettifier.default
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util.{Success, Try}
class SlickDAOMockSpec extends AnyWordSpec with Matchers with BeforeAndAfter {
  "A Database Interface" when {

    val mockGame = Game()
    "defined" should {
      "have the necessary interface operations" in {
        class AnyDAO extends DAOInterface:
          override def save(game: GameInterface, save_name: String): Future[Boolean] = Future.successful(true)
          override def load(id: Option[Int]): Future[Option[GameInterface]] = Future.successful(Some(Game()))
          override def delete(id: Int): Future[Boolean] = Future.successful(true)
          override def update(game: GameInterface, id: Int): Future[Boolean] = Future.successful(true)
          override def listAllGames(): Future[Boolean] = Future.successful(true)
        end AnyDAO
        val anyDAO = new AnyDAO()
        anyDAO shouldBe a[DAOInterface]
      }
    }
    "implemented" should {
      val slickDAOMock = new SlickDAOMock()
      "be a SlickDAO" in {
        slickDAOMock shouldBe a[DAOInterface]
      }
      "have a save method that inserts a new game" in {
        val saveTest = slickDAOMock.save(mockGame, "test")
        saveTest map {
          success => success should be(true)
        }
      }
      "have a delete method that deletes a game" in {
        val deleteTest = slickDAOMock.delete(1)
        deleteTest map {
          success => success should be(true)
        }
      }
      "have a load method that loads a game" in {
        val game = Game()
        val loadTest = slickDAOMock.load(Some(1))
        loadTest map {
          success => success should be(Some(game))
        }
      }
      "have a update method that updates a game" in {
        val updateTest = slickDAOMock.update(mockGame, 1)
        updateTest map {
          success => success should be(true)
        }
      }
      "have a listAllGames method that lists all games" in {
        val listAllTest =  slickDAOMock.listAllGames()
        listAllTest map {
          success => success should be(true)
        }
      }
    }
  }
}
