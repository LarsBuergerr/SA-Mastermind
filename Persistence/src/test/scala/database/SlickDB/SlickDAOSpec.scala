package SlickDB

import FileIOComponent.fileIOJsonImpl.FileIO
import SQLTables.{CodeTable, GameTable, MatrixTable, TurnTable}
import com.github.nscala_time.time.Imports.Duration
import model.GameComponent.GameBaseImpl.Game
import model.GameComponent.GameInterface
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers.shouldBe
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api.*
import slick.lifted.TableQuery

import java.io.{ByteArrayOutputStream, PrintStream}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util.Try


class SlickDAOSPec extends AnyWordSpec with MockitoSugar with BeforeAndAfterEach {

  val mockDatabase: Database = mock[Database]
  val slickDAO: SlickDAO = new SlickDAO {
    override val database: Database = mockDatabase
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDatabase)
  }

  "SlickDAO" should {

    "load a game from the database" in {
      val gameId = 1
      val matrix = "matrix data"
      val hmatrix = "hmatrix data"
      val code = "code data"
      val turn = 5
      val state = "state data"

      val mockResult = Seq((gameId, matrix, hmatrix, code, turn, state))
      when(mockDatabase.run(any)).thenReturn(mockResult)

      val result = slickDAO.load(Some(gameId))

      assert(result.isSuccess)
      val loadedGame = result.get
      assert(loadedGame.field.matrix.toString() == matrix)
      assert(loadedGame.field.hmatrix.toString() == hmatrix)
      assert(loadedGame.code.toString() == code)
      assert(loadedGame.currentTurn == turn)
      assert(loadedGame.state.toString() == state)

      verify(mockDatabase).run(any)
    }

    "save a game into the database" in {
      val game = mock[GameInterface]
      val matrix = "matrix data"
      val hmatrix = "hmatrix data"
      val code = "code data"
      val turn = 5
      val state = "state data"

      when(game.field.matrix.toString()).thenReturn(matrix)
      when(game.field.hmatrix.toString()).thenReturn(hmatrix)
      when(game.code.toString()).thenReturn(code)
      when(game.currentTurn.toString()).thenReturn(turn)
      when(game.state.toString()).thenReturn(state)

      val gameId = 1
      when(mockDatabase.run(any)).thenReturn(gameId)

      val result = slickDAO.save(game, "0")

      assert(result.asInstanceOf[Try[Int]].isSuccess)

      verify(mockDatabase).run(any)
    }

    "delete a game from the database" in {
      val gameId = 1
      val mockResult = Seq((gameId, "matrix", "hmatrix", "code", 5, "state"))
      when(mockDatabase.run(any)).thenReturn(Future.successful(mockResult))

      val result = slickDAO.load(Some(gameId))

      assert(result.isSuccess)
      val loadedGame = result.get

      val deleteResult = slickDAO.delete(gameId)

      assert(deleteResult.isSuccess)
      assert(deleteResult.get)

      verify(mockDatabase, times(2)).run(any)
    }

    "update a game in the database" in {
      val gameId = 1
      val mockResult = Seq((gameId, "matrix", "hmatrix", "code", 5, "state"))
      when(mockDatabase.run(any)).thenReturn(Future.successful(mockResult))

      val result = slickDAO.load(Some(gameId))

      assert(result.isSuccess)
      val loadedGame = result.get.asInstanceOf[Game]
      val updatedTurn = 10
      val updatedGame = Game(
        field = loadedGame.field,
        code = loadedGame.code,
        updatedTurn,
        state = loadedGame.state
      )


      val updateResult = slickDAO.update(updatedGame, gameId)

      updateResult shouldBe true

      verify(mockDatabase, times(2)).run(any)
    }

    "list all games from the database" in {
      val mockResults = Seq(
        (1, "matrix1", "hmatrix1", "code1", 5, "state1"),
        (2, "matrix2", "hmatrix2", "code2", 3, "state2"),
        (3, "matrix3", "hmatrix3", "code3", 7, "state3")
      )
      val expectedGames = Seq(
        (1, "matrix1", "hmatrix1", "code1", 5, "state1"),
        (2, "matrix2", "hmatrix2", "code2", 3, "state2"),
        (3, "matrix3", "hmatrix3", "code3", 7, "state3")
      )
      when(mockDatabase.run(any)).thenReturn(Future.successful(mockResults))

      val result: Unit = slickDAO.listAllGames()

      verify(mockDatabase).run(any)

      val actualCount = mockResults.length
      val expectedCount = expectedGames.length
      actualCount shouldBe expectedCount

      val actualGames = mockResults.map { case (id, matrix, hmatrix, code, turn, state) =>
        (id, matrix, hmatrix, code, turn, state)
      }
      actualGames shouldBe expectedGames
    }

    "print all games to the console" in {
      val mockResults = Seq(
        (1, "matrix1", "hmatrix1", "code1", 5, "state1"),
        (2, "matrix2", "hmatrix2", "code2", 3, "state2"),
        (3, "matrix3", "hmatrix3", "code3", 7, "state3")
      )
      when(mockDatabase.run(any)).thenReturn(Future.successful(mockResults))

      val outputStream = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputStream)) {
        slickDAO.printGames
      }

      val expectedOutput =
        """Game ID: 1
          |Matrix: matrix1
          |HMatrix: hmatrix1
          |Code: code1
          |Turn: 5
          |State: state1
          |
          |Game ID: 2
          |Matrix: matrix2
          |HMatrix: hmatrix2
          |Code: code2
          |Turn: 3
          |State: state2
          |
          |Game ID: 3
          |Matrix: matrix3
          |HMatrix: hmatrix3
          |Code: code3
          |Turn: 7
          |State: state3
          |""".stripMargin

      val actualOutput = outputStream.toString

      actualOutput shouldBe expectedOutput

      verify(mockDatabase).run(any)
    }
    /*
    "store the matrix in the database" in {
      val game: GameInterface = Game(/* pass the required parameters for Game */)
      val matrix = game.field.matrix

      val mockInsertAction = mock[DBIO[Int]]
      when(mockDatabase.run(any)).thenReturn(Future.successful(1))
      when(mockDatabase.run(any[DBIO[Int]])).thenReturn(Future.successful(1))

      val result = slickDAO.storeMatrix(matrix)

      Await.result(result, Duration.Inf) shouldBe 1

      verify(mockDatabase).run(any[DBIO[Int]])
    }
    */

    "sanitize the input string" in {
      val input = "   Hello World!   "
      val expectedOutput = "Hello World!"

      val result = slickDAO.sanitize(input)

      result shouldBe expectedOutput
    }

    "sanitize the input string with leading and trailing whitespaces" in {
      val input = "   Spaces at the beginning and end   "
      val expectedOutput = "Spaces at the beginning and end"

      val result = slickDAO.sanitize(input)

      result shouldBe expectedOutput
    }

    "sanitize the input string with multiple whitespaces" in {
      val input = "This     is    a   test"
      val expectedOutput = "This is a test"

      val result = slickDAO.sanitize(input)

      result shouldBe expectedOutput
    }

  }
}