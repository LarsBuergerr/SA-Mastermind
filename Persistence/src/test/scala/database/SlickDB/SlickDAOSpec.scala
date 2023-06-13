package SlickDB

import FileIOComponent.fileIOJsonImpl.FileIO
import SQLTables.{CodeTable, GameTable, GameTable2, HMatrixTable, MatrixTable, StateTable, TurnTable}
import com.github.nscala_time.time.Imports.Duration
import database.FutureRetryResolver
import model.GameComponent.GameBaseImpl.{Code, Field, Game, Init, Play, Stone}
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

import java.sql.SQLNonTransientException
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util.Try
import concurrent.duration.DurationInt


class SlickDAOSpec extends AnyWordSpec with Matchers with BeforeAndAfter {
    "A Database Interface" when {
        val mockGame = Game()

        "defined" should {
            "have the necessary interface operations" in {
                val anyDAO = new SlickDAOMock()//AnyDAO()
                anyDAO shouldBe a[DAOInterface]
            }
        }

        "implemented" should {
            val slickDAO = new SlickDAO()
            "be a SlickDAO" in {
                slickDAO shouldBe a[DAOInterface]
            }
/*
            "have a save method that inserts a new game" in {
                //slickDAOMock.save(mockGame, "test") shouldBe a[Unit]
                val game = Game()
                val solutionCode = new Code(Vector(Stone.apply("R"), Stone.apply("R"), Stone.apply("B"), Stone.apply("Y")))
                val savedGame1 = new Game(new Field(), solutionCode, 0, Play())
                val savedGame2 = new Game(new Field(), game.getCode(), 0, Play())
                val expected = game.toString()
                val saveResult = Await.result(slickDAO.save(savedGame2, "saveTest"), 5.seconds)
                val actual = Await.result(slickDAO.load(Some(2)), 5.seconds)

                saveResult shouldEqual true //true if Futures Success
                actual.get.field.toString() shouldEqual expected
            }

            "have a delete method that deletes a game" in {
                val game = Game()
                val saveResult = slickDAO.save(game, "deleteTest")
                Await.result(saveResult, 5.seconds)

                val currentHighestID = slickDAO.getHighestID()
                val deleteResult = slickDAO.delete(currentHighestID)
                val result = Await.result(deleteResult, 5.seconds)

                result shouldBe true //true if Futures Success
                //val count = Await.result(collection.countDocuments().toFuture(), 5.seconds).head
                //count shouldEqual (currentHighestID - 1)
            }
            "have a load method that loads a game" in {
                slickDAO.load(Some(1)) shouldBe a[Try[Game]]
            }
            "have a update method that updates a game" in {
                slickDAO.update(mockGame, 1) shouldBe a[Boolean]
            }
            "have a listAllGames method that lists all games" in {
                slickDAO.listAllGames() shouldBe a[Unit]
            }*/
        }
    }

}