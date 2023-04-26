package controller.ControllerComponent.ControllerBaseImpl

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
import model.GameComponent.GameBaseImpl.{Field, Game, HStone, HintStone, Matrix, Stone}

class InvokerSpec extends AnyWordSpec {
  "A Invoker" when {

    "new" should {
      val invoker = new Invoker()
      val game = new Game()
      val placeCommand = new PlaceCommand(game, Vector[Stone](Stone("B"), Stone("R"), Stone("Y"), Stone("P")), Vector[HStone](HintStone("R"), HintStone("R"), HintStone("R"), HintStone("R")), 0)
      "have a doStep function that executes the last command" in {
        val field = invoker.doStep(placeCommand)
        field.matrix.cell(0, 0) should equal(Stone("B"))
        field.matrix.cell(0, 1) should be(Stone("R"))
        field.matrix.cell(0, 2) should be(Stone("Y"))
        field.matrix.cell(0, 3) should be(Stone("P"))

        field.hmatrix.cell(0, 0) should be(HintStone("R"))
        field.hmatrix.cell(0, 1) should be(HintStone("R"))
        field.hmatrix.cell(0, 2) should be(HintStone("R"))
        field.hmatrix.cell(0, 3) should be(HintStone("R"))
      }
    }
    //NEW Tests
    "return None when undoStep is called and undoStack is empty" in {
      val invoker = new Invoker()
      invoker.undoStep should be(None)
    }
/*
    "return the result of undoing the last command when undoStep is called and undoStack is not empty" in {
*/
    "return None when redoStep is called and redoStack is empty" in {
      val invoker = new Invoker()
      invoker.redoStep shouldEqual None
    }
/*
    "return the result of redoing the last undone command when redoStep is called and redoStack is not empty" in {
      val invoker = new Invoker()
      val fieldBefore = new Field(new Matrix(1, 1), new HintMatrix(1, 1))
      val fieldAfter = invoker.doStep(placeCommand)
      val undoResult = invoker.undoStep.get
      val expectedField = invoker.redoStack.head.redoStep

      undoResult shouldEqual fieldBefore
      invoker.redoStep shouldEqual Some(expectedField)
    }
  }
*/
  }
}

