package scala

import model.GameComponent.GameInterface
import scala.util.Try

trait DAOInterface {

  def save(game: GameInterface): Unit

  def load(id: Option[Int]): Try[GameInterface]

}
