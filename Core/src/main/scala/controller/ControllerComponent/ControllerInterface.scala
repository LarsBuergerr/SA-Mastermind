/**
  * ControllerInterface.scala
  */

//****************************************************************************** PACKAGE  

package controller.ControllerComponent

//****************************************************************************** IMPORTS

import model.GameComponent.GameBaseImpl.{Field, Stone, HStone, State, Game}
import model.GameComponent.GameInterface
import util.{Observable, Event, Request}
import play.api.libs.json.*

//****************************************************************************** INTERFACE DEFINITION
trait ControllerInterface extends Observable:

  var game: GameInterface

  def placeGuessAndHints(stone: Vector[Stone])(hints: Vector[HStone])(row: Int): GameInterface
    
  def getGame: GameInterface

  def redo: GameInterface
    
  def undo: GameInterface
    
  def reset: Field
  
  def save: Unit

  def load: Field

  def update: String
    
  def request(event: Event): State
    
  def handleRequest(request: Request): Event

  def gameToJson(game: GameInterface): String
