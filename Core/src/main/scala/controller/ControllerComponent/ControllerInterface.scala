package controller.ControllerComponent

import model.GameComponent.GameBaseImpl.*
import model.GameComponent.GameInterface
import util.{Event, Observable, Request}
import play.api.libs.json.*

/**
 * This trait represents the ControllerInterface, which defines the interface for a game controller.
 * It extends the Observable trait to allow for observing changes in the game state.
 */
trait ControllerInterface extends Observable {
  // The current game instance.
  var game: GameInterface 

  /**
   * Places a guess stone and associated hints on the game field.
   *
   * @param stone The vector of stones to be placed.
   * @param hints The vector of hints associated with the placed stones.
   * @param row   The row index where the stones are placed.
   * @return The updated game interface.
   */
  def placeGuessAndHints(stone: Vector[Stone])(hints: Vector[HStone])(row: Int): GameInterface

  /**
   * Retrieves the current game interface.
   *
   * @return The current game interface.
   */
  def getGame: GameInterface

  /**
   * Redoes the last undone command and returns the updated game interface.
   *
   * @return The updated game interface.
   */
  def redo: GameInterface

  /**
   * Undoes the last executed command and returns the updated game interface.
   *
   * @return The updated game interface.
   */
  def undo: GameInterface

  /**
   * Resets the game field to its initial state and returns the reset field.
   *
   * @return The reset game field.
   */
  def reset: Field

  /**
   * Saves the current game state.
   *
   * @param game The game interface to be saved.
   */
  def save(game: GameInterface): Unit

  /**
   * Loads a saved game state and returns the corresponding game interface.
   *
   * @return The loaded game interface.
   */
  def load: GameInterface

  /**
   * Saves the current game state to a database with the specified save name.
   *
   * @param game      The game interface to be saved.
   * @param save_name The name of the save in the database.
   */
  def dbsave(game: GameInterface, save_name: String): Unit

  /**
   * Loads a game state from the database with the specified index number and returns the corresponding game interface.
   *
   * @param num The index number of the save in the database.
   * @return The loaded game interface.
   */
  def dbload(num: Int): GameInterface

  /**
   * Loads a game state from the database with the specified save name and returns the corresponding game interface.
   *
   * @param num The name of the save in the database.
   * @return The loaded game interface.
   */
  def dbloadname(num: String): GameInterface

  /**
   * Lists all saves in the database.
   */
  def dblist: Unit

  /**
   * Updates the game state in the database with the specified ID.
   *
   * @param game The game interface to be updated.
   * @param id   The ID of the save in the database.
   */
  def dbupdate(game: GameInterface, id: Int): Unit

  /**
   * Deletes the game state from the database with the specified ID.
   *
   * @param id The ID of the save in the database.
   */
  def dbdelete(id: Int): Unit

  /**
   * Updates the game state and returns a string representation of the updated state.
   *
   * @return A string representation of the updated game state.
   */
  def update: String

  /**
   * Processes an event and returns the resulting game state.
   *
   * @param event The event to be processed.
   * @return The resulting game state.
   */
  def request(event: Event): State

  /**
   * Handles a request and returns the corresponding event.
   *
   * @param request The request to be handled.
   * @return The corresponding event.
   */
  def handleRequest(request: Request): Event

  /**
   * Converts the game interface to a JSON string representation.
   *
   * @param game The game interface to be converted.
   * @return The JSON string representation of the game interface.
   */
  def gameToJson(game: GameInterface): String
}

