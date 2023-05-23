/**
  * ControllerInterface.scala
  */

//****************************************************************************** PACKAGE  

package controller.ControllerComponent

//****************************************************************************** IMPORTS

import model.GameComponent.GameBaseImpl.*
import model.GameComponent.GameInterface
import util.{Event, Observable, Request}
import play.api.libs.json.*

//****************************************************************************** INTERFACE DEFINITION
trait ControllerInterface extends Observable:

  var game: GameInterface

  def placeGuessAndHints(stone: Vector[Stone])(hints: Vector[HStone])(row: Int): GameInterface
    
  def getGame: GameInterface

  def redo: GameInterface
    
  def undo: GameInterface
    
  def reset: Field

  def save(game: GameInterface): Unit

  def load: GameInterface

  def dbsave(game: GameInterface, save_name: String): Unit

  def dbload(num: Int): GameInterface

  def dbloadname(num: String): GameInterface

  def dblist: Unit

  def dbupdate(game: GameInterface, id: Int): Unit

  def dbdelete(id: Int): Unit

  def update: String
    
  def request(event: Event): State
    
  def handleRequest(request: Request): Event

  def gameToJson(game: GameInterface): String
