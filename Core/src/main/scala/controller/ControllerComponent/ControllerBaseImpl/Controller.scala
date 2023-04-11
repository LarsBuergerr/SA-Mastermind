/**
  * Controller.scala
  * Base implementation of the controller
  */

//****************************************************************************** PACKAGE  

package controller.ControllerComponent.ControllerBaseImpl

//****************************************************************************** IMPORTS
import model.GameComponent.GameInterface
import model.GameComponent.GameBaseImpl.Game
import model.GameComponent.GameBaseImpl.{State, Stone, HStone, Field}
import FileIOComponent.FileIOInterface
import controller.ControllerComponent.ControllerInterface
import util.{Request, Event, Observable}


//****************************************************************************** CLASS DEFINITION
class Controller (using var game: GameInterface, val fileIO: FileIOInterface) extends ControllerInterface:

  val invoker = new Invoker
  
  // Pass on the game state to the view and the event to game
  def request(event: Event): State =
    val currState = game.request(event)
    game = game.asInstanceOf[Game].copy(game.field, game.code, game.currentTurn, currState)
    notifyObservers
    currState
  
  def handleRequest(request: Request): Event =
    game.handleRequest(request)

  def placeGuessAndHints(stone: Vector[Stone])(hints: Vector[HStone])(row: Int): Field =
    val field = invoker.doStep(PlaceCommand(game, stone, hints, row))
    game = game.asInstanceOf[Game].copy(field, game.code, game.currentTurn + 1)
    notifyObservers
    game.field

  def redo =
    val field = invoker.redoStep.getOrElse(game.field)
    game = game.asInstanceOf[Game].copy(field, game.code, game.currentTurn + 1)
    notifyObservers

    //@TODO: check if oldfield == newfield for undo and redo
  def undo =
    val field = invoker.undoStep.getOrElse(game.field)
    game = game.asInstanceOf[Game].copy(field, game.code, game.currentTurn - 1)
    notifyObservers

  def save =
    fileIO.save(game)

  def load =
    game = fileIO.load(game)
    notifyObservers
    game.field

  def reset =
    game = game.resetGame()
    notifyObservers
    game.field

  def update: String =
    print("How was it possible for you to call this function?")
    ""