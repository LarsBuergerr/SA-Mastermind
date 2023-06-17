package scala

import model.GameComponent.GameInterface

import scala.concurrent.Future
import scala.util.Try

/**
 * DAOInterface represents the interface for a Data Access Object (DAO).
 * It defines the methods to perform CRUD (Create, Read, Update, and Delete) operations on game data.
 */
trait DAOInterface {

  /**
   * Saves the provided game with the given save name.
   *
   * @param game      The game to be saved.
   * @param save_name The name to be used for saving the game.
   * @return A Future indicating the success or failure of the save operation.
   */
  def save(game: GameInterface, save_name: String): Future[Boolean]

  /**
   * Loads a game with the specified ID.
   *
   * @param id An optional ID of the game to be loaded. If None, it loads the default game.
   * @return A Future containing an optional GameInterface, representing the loaded game.
   */
  def load(id: Option[Int]): Future[Option[GameInterface]]

  /**
   * Deletes the game with the specified ID.
   *
   * @param id The ID of the game to be deleted.
   * @return A Future indicating the success or failure of the delete operation.
   */
  def delete(id: Int): Future[Boolean]

  /**
   * Updates the game with the specified ID using the provided game data.
   *
   * @param game The updated game data.
   * @param id   The ID of the game to be updated.
   * @return A Future indicating the success or failure of the update operation.
   */
  def update(game: GameInterface, id: Int): Future[Boolean]

  /**
   * Retrieves a list of all games.
   *
   * @return A Future indicating the success or failure of retrieving the list of games.
   */
  def listAllGames(): Future[Boolean]
}
