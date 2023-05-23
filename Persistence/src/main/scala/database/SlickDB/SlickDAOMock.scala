package SlickDB

import model.GameComponent.GameInterface
import scala.util.Try
import model.GameComponent.GameBaseImpl.Game

class SlickDAOMock extends DAOInterface:
  override def save(game: GameInterface, save_name: String): Unit =
    Try(())

  override def delete(id: Int): Try[Boolean] =
    Try(true)

  override def load(id: Option[Int]): Try[Game] =
    Try(Game())

  override def update(game: GameInterface, id: Int): Boolean =
    true

  override def listAllGames(): Unit =
    ()

end SlickDAOMock