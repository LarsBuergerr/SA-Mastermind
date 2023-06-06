package SlickDB

import FileIOComponent.fileIOJsonImpl.FileIO
import SQLTables.{CodeTable, GameTable, MatrixTable, TurnTable}
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
import scala.util.Try


class SlickDAOSpec extends AnyWordSpec with Matchers with BeforeAndAfter {
    "A Database Interface" when {
        val mockGame = Game()
        "defined" should {
        "have the necessary interface operations" in {
            class AnyDAO extends DAOInterface:
                override def save(game: GameInterface, save_name: String): Unit = Try(())

                override def delete(id: Int): Try[Boolean] = Try(true)

                override def load(id: Option[Int]): Try[Game] = Try(mockGame.asInstanceOf[Game])

                override def update(game: GameInterface, id: Int): Try[Boolean] = Try(true)

                override def listAllGames(): Unit = ()
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
                slickDAOMock.save(mockGame, "test") shouldBe a[Unit]
            }
            "have a delete method that deletes a game" in {
                slickDAOMock.delete(1) shouldBe a[Try[Boolean]]
            }
            "have a load method that loads a game" in {
                slickDAOMock.load(Some(1)) shouldBe a[Try[Game]]
            }
            "have a update method that updates a game" in {
                slickDAOMock.update(mockGame, 1) shouldBe a[Boolean]
            }
            "have a listAllGames method that lists all games" in {
                slickDAOMock.listAllGames() shouldBe a[Unit]
            }
        }
    }
}