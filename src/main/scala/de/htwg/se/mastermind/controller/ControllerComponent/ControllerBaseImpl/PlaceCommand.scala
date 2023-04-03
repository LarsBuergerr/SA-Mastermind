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
case class PlaceCommand(game: GameInterface, stone: Vector[Option[Stone]], hints: Vector[Option[HStone]], row: Int) extends Command():
  val oldfield = game.field
  val newfield = game.field.placeGuessAndHints(stone)(hints)(row)

  override def execute: Field =
    val newfield = game.field.placeGuessAndHints(stone)(hints)(row)
    newfield
    
  override def undoStep: Field = 
    oldfield
    
  override def redoStep: Field = 
    newfield
