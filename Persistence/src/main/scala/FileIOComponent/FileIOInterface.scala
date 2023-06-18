package FileIOComponent

import model.GameComponent.GameInterface
import model.GameComponent.GameBaseImpl.{Field, Stone, Matrix}
import play.api.libs.json.*

/**
 * Interface for file input/output operations.
 */
trait FileIOInterface:

  def load(): GameInterface

  def save(game: GameInterface): Unit