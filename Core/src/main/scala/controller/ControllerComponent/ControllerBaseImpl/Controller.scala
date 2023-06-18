package controller.ControllerComponent.ControllerBaseImpl

import FileIOComponent.FileIOInterface
import FileIOComponent.fileIOJsonImpl.FileIO
import controller.ControllerComponent.ControllerInterface
import controller.ControllerComponent.aview.PersistenceController
import model.GameComponent.GameBaseImpl.*
import model.GameComponent.GameInterface
import util.{Event, Observable, Request}
import play.api.libs.json.*

/**
 * Controller.scala
 * Base implementation of the controller
 */
class Controller(using var game: GameInterface) extends ControllerInterface:

  val persistenceController = new PersistenceController
  val invoker = new Invoker

  /**
   * Retrieves the current game instance.
   *
   * @return The current game instance.
   */
  def getGame: GameInterface = game

  /**
   * Sends a request to the game, triggering an event and updating the game state.
   *
   * @param event The event to be processed by the game.
   * @return The current state of the game.
   */
  def request(event: Event): State =
    val currState = game.request(event)
    game = game.asInstanceOf[Game].copy(game.field, game.code, game.currentTurn, currState)
    notifyObservers
    currState

  /**
   * Handles a request from the game view.
   *
   * @param request The request to be handled.
   * @return The corresponding event triggered by the request.
   */
  def handleRequest(request: Request): Event =
    game.handleRequest(request)

  /**
   * Places the guesses and hints on the game field.
   *
   * @param stone The stones to be placed as a guess.
   * @param hints The stones to be placed as hints.
   * @param row   The row in which the stones are placed.
   * @return The updated game instance.
   */
  def placeGuessAndHints(stone: Vector[Stone])(hints: Vector[HStone])(row: Int): GameInterface =
    val field = invoker.doStep(PlaceCommand(game, stone, hints, row))
    game = game.asInstanceOf[Game].copy(field, game.code, game.currentTurn + 1)
    notifyObservers
    game

  /**
   * Redoes the previous step in the game.
   */
  def redo =
    val field = invoker.redoStep.getOrElse(game.field)
    game = game.asInstanceOf[Game].copy(field, game.code, game.currentTurn + 1)
    notifyObservers
    game

  //@TODO: check if oldfield == newfield for undo and redo
  /**
   * Undoes the previous step in the game.
   */
  def undo =
    val field = invoker.undoStep.getOrElse(game.field)
    game = game.asInstanceOf[Game].copy(field, game.code, game.currentTurn - 1)
    notifyObservers
    game

  /**
   * Saves the current game state to a file.
   *
   * @param game The game instance to be saved.
   */
  def save(game: GameInterface) =
    persistenceController.save(game)

  /**
   * Loads a game state from a file.
   *
   * @return The loaded game instance.
   */
  def load =
    persistenceController.load()
    notifyObservers
    game = persistenceController.game
    game

  /**
   * Saves the current game state to a database.
   *
   * @param game      The game instance to be saved.
   * @param save_name The name of the save in the database.
   */
  def dbsave(game: GameInterface, save_name: String) =
    persistenceController.dbsave(game, save_name)

  /**
   * Loads a game state from the database by its ID.
   *
   * @param num The ID of the save in the database.
   */
  def dbload(num: Int) =
    persistenceController.dbload(num)
    notifyObservers
    game = persistenceController.game
    game

  /**
   * Loads a game state from the database by its name.
   *
   * @param name The name of the save in the database.
   */
  def dbloadname(name: String) =
    persistenceController.dbloadByName(name)
    notifyObservers
    game = persistenceController.game
    game

  /**
   * Retrieves a list of saved game states from the database.
   *
   * @return A list of saved game states.
   */
  def dblist =
    persistenceController.dblist()

  /**
   * Updates a game state in the database.
   *
   * @param game The updated game instance.
   * @param id   The ID of the save in the database.
   */
  def dbupdate(game: GameInterface, id: Int) =
    persistenceController.dbupdate(game, id)

  /**
   * Deletes a game state from the database.
   *
   * @param id The ID of the save in the database.
   */
  def dbdelete(id: Int) =
    persistenceController.dbdelete(id)

  /**
   * Resets the game to its initial state.
   *
   * @return The updated game field.
   */
  def reset =
    game = game.resetGame()
    notifyObservers
    game.field

  /**
   * Update function (not used in this implementation).
   *
   * @return An empty string.
   */
  def update: String =
    print("How was it possible for you to call this function?")
    ""

  /**
   * Converts a cell object to JSON format.
   *
   * @param cell The cell object to be converted.
   * @param x    The x-coordinate of the cell.
   * @param y    The y-coordinate of the cell.
   * @return The JSON representation of the cell.
   */
  def cellToJson(cell: Object, x: Int, y: Int) =
    val cellJson = Json.obj(
      "x" -> x,
      "y" -> y,
      "value" -> cell.toString()
    )
    cellJson

  /**
   * Converts a vector of objects to JSON format.
   *
   * @param vector The vector of objects to be converted.
   * @param row    The row index of the vector.
   * @return The JSON representation of the vector.
   */
  def vectorToJson(vector: Vector[Object], row: Int) =
    Json.obj(
      "row" -> row,
      "cells" -> {
        vector.zipWithIndex.map { case (cell, index) => cellToJson(cell, row, index) }
      }
    )

  /**
   * Converts the game state to JSON format.
   *
   * @param state The game state to be converted.
   * @return The JSON representation of the game state.
   */
  def stateToJson(state: State) =
    val json = Json.obj(
      "value" -> state.toString()
    )
    json

  /**
   * Converts the game instance to JSON format.
   *
   * @param game The game instance to be converted.
   * @return The JSON representation of the game instance.
   */
  def gameToJson(game: GameInterface) =
    val json = Json.obj(
      "matrix" -> {
        game.field.matrix.m.zipWithIndex.map { case (vector, index) => vectorToJson(vector, index) }
      },
      "hmatrix" -> {
        game.field.hmatrix.m.zipWithIndex.map { case (vector, index) => vectorToJson(vector, index) }
      },
      "turn" -> game.currentTurn,
      "code" -> vectorToJson(game.code.code.asInstanceOf[Vector[Object]], 0),
      "state" -> stateToJson(game.state),
    )
    json.toString()
