/**
  * PlaceCommand.scala
  */

//****************************************************************************** PACKAGE  

package controller.ControllerComponent.ControllerBaseImpl

//****************************************************************************** IMPORTS
import model.GameComponent.GameBaseImpl.{Field, Stone, HStone, State, Game}
import model.GameComponent.GameInterface
import util.*


//****************************************************************************** CLASS DEFINITION
case class PlaceCommand(game: GameInterface, stone: Vector[Stone], hints: Vector[HStone], row: Int) extends Command[T]:
  val oldfield = game.field
  val newfield = game.field.placeGuessAndHints(stone)(hints)(row)

  override def execute: Field =
    val newfield = game.field.placeGuessAndHints(stone)(hints)(row)
    newfield
    
  override def undoStep: Field = 
    oldfield
    
  override def redoStep: Field = 
    newfield
