package SlickDB

import model.GameComponent.GameInterface
import scala.util.Try
import model.GameComponent.GameBaseImpl.Game

class SlickDAOMock extends DAOInterface:
  override def save(game: GameInterface, save_name: String): Unit =
    Try(())
    //override def save(game: GameInterface, save_name: String): Future[Boolean] = Future.successful(true)

  override def delete(id: Int): Try[Boolean] =
    Try(true)
    //override def delete(id: Int): Future[Boolean] = Future.successful(true)

  override def load(id: Option[Int]): Try[Game] =
    Try(Game())
  //override def load(id: Option[Int]): Future[Option[GameInterface]] = Future.successful(Some(Game()))

  override def update(game: GameInterface, id: Int): Try[Boolean] =
    Try(true)
    //override def update(game: GameInterface, id: Int): Future[Boolean] = Future.successful(true)

  override def listAllGames(): Unit =
    ()
    //override def listAllGames(): Future[Boolean] = Future.successful(true)

end SlickDAOMock