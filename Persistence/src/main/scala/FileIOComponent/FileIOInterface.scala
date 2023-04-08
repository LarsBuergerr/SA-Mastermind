

import GameComponent.GameInterface
import GameComponent.GameBaseImpl.{Field, Stone, Matrix}


trait FileIOInterface:

  def load(game: GameInterface): GameInterface

  def save(game: GameInterface): Unit

