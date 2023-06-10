package scala

import model.GameComponent.GameInterface

import scala.concurrent.Future
import scala.util.Try

trait DAOInterface {

  def save(game: GameInterface, save_name: String): Unit
  //def save(game: GameInterface, save_name: String): Future[Boolean]

  def load(id: Option[Int]): Try[GameInterface]
  //def load(id: Option[Int]): Future[Option[GameInterface]]

  def delete(id: Int): Try[Boolean]
  //def delete(id: Int): Future[Boolean]

  def update(game: GameInterface,  id: Int): Try[Boolean]
  // def update(game: GameInterface, id: Int): Future[Boolean]

  def listAllGames(): Unit
  //  def listAllGames(): Future[Boolean]
}