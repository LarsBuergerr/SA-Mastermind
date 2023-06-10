package SlickDB

import model.GameComponent.GameBaseImpl.Game
import model.GameComponent.GameInterface

import scala.concurrent.Future
import scala.util.Try

/**
 * A mock implementation of the DAOInterface using Slick.
 * This class provides methods for saving, loading, deleting, updating, and listing all games.
 */
class SlickDAOMock extends DAOInterface:

  /**
   * Saves a game with the given name.
   *
   * @param game      The game to be saved.
   * @param save_name The name to be associated with the saved game.
   * @return A Future containing a Boolean indicating the success of the save operation.
   */
  override def save(game: GameInterface, save_name: String): Future[Boolean] = Future.successful(true)

  /**
   * Loads a game with the specified ID.
   *
   * @param id An optional ID of the game to be loaded.
   * @return A Future containing an Option of GameInterface representing the loaded game, if found.
   */
  override def load(id: Option[Int]): Future[Option[GameInterface]] = Future.successful(Some(Game()))

  /**
   * Deletes a game with the specified ID.
   *
   * @param id The ID of the game to be deleted.
   * @return A Future containing a Boolean indicating the success of the delete operation.
   */
  override def delete(id: Int): Future[Boolean] = Future.successful(true)

  /**
   * Updates a game with the specified ID.
   *
   * @param game The updated game to be stored.
   * @param id   The ID of the game to be updated.
   * @return A Future containing a Boolean indicating the success of the update operation.
   */
  override def update(game: GameInterface, id: Int): Future[Boolean] = Future.successful(true)

  /**
   * Lists all games.
   *
   * @return A Future containing a Boolean indicating the success of listing all games operation.
   */
  override def listAllGames(): Future[Boolean] = Future.successful(true)

end SlickDAOMock
