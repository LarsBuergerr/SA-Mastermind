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
import play.api.libs.json.*

//****************************************************************************** CLASS DEFINITION
class Controller(using var game: GameInterface, val fileIO: FileIOInterface) extends ControllerInterface:

  val invoker = new Invoker
  
  def getGame: GameInterface = game

  // Pass on the game state to the view and the event to game
  def request(event: Event): State =
    val currState = game.request(event)
    game = game.asInstanceOf[Game].copy(game.field, game.code, game.currentTurn, currState)
    notifyObservers
    currState
  
  def handleRequest(request: Request): Event =
    game.handleRequest(request)

  def placeGuessAndHints(stone: Vector[Stone])(hints: Vector[HStone])(row: Int): GameInterface =
    val field = invoker.doStep(PlaceCommand(game, stone, hints, row))
    game = game.asInstanceOf[Game].copy(field, game.code, game.currentTurn + 1)
    notifyObservers
    game

  def redo =
    val field = invoker.redoStep.getOrElse(game.field)
    game = game.asInstanceOf[Game].copy(field, game.code, game.currentTurn + 1)
    notifyObservers
    game

    //@TODO: check if oldfield == newfield for undo and redo
  def undo =
    val field = invoker.undoStep.getOrElse(game.field)
    game = game.asInstanceOf[Game].copy(field, game.code, game.currentTurn - 1)
    notifyObservers
    game

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

  def cellToJson(cell: Object, x: Int, y: Int) = 
    val cellJson = Json.obj(
      "x" -> x,
      "y" -> y,
      "value" -> cell.toString()
    )
    cellJson

  def vectorToJson(vector: Vector[Object], row: Int) =
    Json.obj(
      "row" -> row,
      "cells" -> {
        vector.zipWithIndex.map{ case (cell, index) => cellToJson(cell, row, index)}
      }
    )

  def stateToJson(state: State) =
    val json = Json.obj(
      "value" -> state.toString()
    )
    json

  def gameToJson(game: GameInterface) =
    val json = Json.obj(
      "matrix" -> {
        game.field.matrix.m.zipWithIndex.map{ case (vector, index) => vectorToJson(vector, index)}
      },
      "hmatrix" -> {
        game.field.hmatrix.m.zipWithIndex.map{ case (vector, index) => vectorToJson(vector, index)}
      },
      "turn" -> game.currentTurn,
      "code" -> vectorToJson(game.code.code.asInstanceOf[Vector[Object]], 0),
      "state" -> stateToJson(game.state),
    )
    json.toString()
