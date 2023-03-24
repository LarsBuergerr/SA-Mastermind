/**
  * Controller.scala
  * Base implementation of the controller
  */

//****************************************************************************** PACKAGE  
package de.htwg.se.mastermind
package controller
package ControllerComponent
package ControllerBaseImpl


//****************************************************************************** IMPORTS
import model.GameComponent.GameInterface
import model.GameComponent.GameBaseImpl.Game
import model.GameComponent.GameBaseImpl.{State, Stone, HStone, Field}
import model.FileIOComponent.FileIOInterface
import util.{Request, Event, Observable}


//****************************************************************************** CLASS DEFINITION
class Controller (using var game: GameInterface, var fileIO: FileIOInterface) extends ControllerInterface:

  val invoker = new Invoker
  
  // Pass on the game state to the view and the event to game
  def request(event: Event): State =
    val currState = game.request(event)
    notifyObservers
    currState
  
  def handleRequest(request: Request): Event =
    game.handleRequest(request)

  def placeGuessAndHints(stone: Vector[Stone])(hints: Vector[HStone])(row: Int): Field =
    val field = invoker.doStep(PlaceCommand(game, stone, hints, row))
    game = game.asInstanceOf[Game].copy(field, game.code, game.currentTurn)
    notifyObservers
    game.field

  def redo =
    val field = invoker.redoStep.getOrElse(game.field)
    game = game.asInstanceOf[Game].copy(field, game.code, game.currentTurn)
    notifyObservers

  def undo =
    val field = invoker.undoStep.getOrElse(game.field)
    game = game.asInstanceOf[Game].copy(field, game.code, game.currentTurn)
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