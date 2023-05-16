package SlickDB

import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar

import FileIOComponent.fileIOJsonImpl.FileIO
import SQLTables.{MatrixTable, TurnTable, CodeTable, GameTable}
import model.GameComponent.GameInterface
import model.GameComponent.GameBaseImpl.Game
import org.scalatest.{BeforeAndAfterEach}
import org.scalatest.wordspec.AnyWordSpec
import org.mockito.ArgumentMatchers._
import slick.jdbc.JdbcBackend.Database
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
  }
}