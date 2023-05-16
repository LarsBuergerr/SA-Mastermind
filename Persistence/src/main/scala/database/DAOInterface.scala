package scala

import model.GameComponent.GameInterface
import scala.util.Try

trait DAOInterface {

  def save(game: GameInterface, save_name: String): Unit

  def load(id: Option[Int]): Try[GameInterface]

  def delete(id: Int): Try[Boolean]

  def update(game: GameInterface,  id: Int): Boolean
}
