import de.htwg.se.mastermind.model.GameComponent.GameBaseImpl.Game
import de.htwg.se.mastermind.model.GameComponent.GameBaseImpl.Matrix
import de.htwg.se.mastermind.model.GameComponent.GameBaseImpl.Field
import de.htwg.se.mastermind.model.GameComponent.GameBaseImpl.Stone
import de.htwg.se.mastermind.model.GameComponent.GameBaseImpl.HintStone
import de.htwg.se.mastermind.model.GameComponent.GameBaseImpl.HStone
import de.htwg.se.mastermind.model.GameComponent.GameBaseImpl.Code
import de.htwg.se.mastermind.model.FileIOComponent.fileIOxmlImpl.FileIO


val fileIO = new FileIO()

val game = new Game(new Field(4, 4 , Stone("G")), new Code(4), 0)
val matrix = game.field.matrix.replaceRow(1, Vector[Stone](Stone("R"), Stone("R"), Stone("R"), Stone("R")))
val matrix2 = game.field.matrix.replaceRow(2, Vector[Stone](Stone("B"), Stone("B"), Stone("B"), Stone("B")))
val matrix3 = game.field.matrix.replaceRow(3, Vector[Stone](Stone("Y"), Stone("Y"), Stone("Y"), Stone("Y")))

val hmatrix = game.field.hmatrix.replaceRow(1, Vector[HStone](HintStone("W"), HintStone("R"), HintStone("W"), HintStone("R")))
print(game.field)
fileIO.save(game)


val loadgame = fileIO.load
val game2 = loadgame



