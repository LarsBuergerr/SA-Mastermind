package controller.ControllerComponent.ControllerBaseImpl

import model.GameComponent.GameBaseImpl.*
import model.GameComponent.GameInterface
import util.*

/**
 * This class represents a PlaceCommand, which is a command for placing a stone on the game field.
 *
 * @param game   The game interface to execute the command on.
 * @param stone  The stone vector to be placed on the game field.
 * @param hints  The hints vector associated with the placed stone.
 * @param row    The row index where the stone is placed.
 */
case class PlaceCommand(game: GameInterface, stone: Vector[Stone], hints: Vector[HStone], row: Int) extends Command[Field] {
  // Stores the previous state of the game field.
  val oldfield: Field = game.field
  // Stores the updated game field after placing the stone.
  val newfield: Field = game.field.placeGuessAndHints(stone)(hints)(row)

  /**
   * Executes the place command by updating the game field.
   *
   * @return The updated game field.
   */
  override def execute: Field = {
    val newfield: Field = game.field.placeGuessAndHints(stone)(hints)(row)
    newfield
  }

  /**
   * Undoes the place command by reverting the game field to its previous state.
   *
   * @return The game field before executing the command.
   */
  override def undoStep: Field =
    oldfield

  /**
   * Redoes the place command by applying the updated game field.
   *
   * @return The game field after executing the command.
   */
  override def redoStep: Field =
    newfield
}
