/**
  * PlaceCommand.scala
  */

//****************************************************************************** PACKAGE  
package de.htwg.se.mastermind
package controller


//****************************************************************************** IMPORTS
import model.GameComponent.GameBaseImpl.{Field, Stone, HStone, State, Game}
import model.GameComponent.GameInterface
import util.*


//****************************************************************************** CLASS DEFINITION
case class PlaceCommand(game: GameInterface, stone: Vector[Stone], hints: Vector[HStone], row: Int) extends Command():
  val oldfield = game.field
  val newfield = game.field.placeGuessAndHints(stone)(hints)(row)

  override def execute: Field =
    val newfield = game.field.placeGuessAndHints(stone)(hints)(row)
    game.currentTurn += 1
    newfield
    
  override def undoStep: Field = 
    game.currentTurn -= 1
    oldfield
    
  override def redoStep: Field = 
    if(newfield != oldfield)
      game.currentTurn += 1
    newfield
