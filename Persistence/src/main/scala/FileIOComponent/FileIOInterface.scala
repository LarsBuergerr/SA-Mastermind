package FileIOComponent

import model.GameComponent.GameInterface
import model.GameComponent.GameBaseImpl.{Field, Stone, Matrix}
import play.api.libs.json.*

trait FileIOInterface:

  def load(): GameInterface

  def save(game: GameInterface): Unit