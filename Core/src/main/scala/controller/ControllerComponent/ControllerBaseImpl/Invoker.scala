package controller.ControllerComponent.ControllerBaseImpl

import model.GameComponent.GameBaseImpl.Field
import util.Command

import scala.annotation.meta.param

/**
 * This class represents an Invoker, which is responsible for executing and managing commands.
 *
 * @constructor Creates a new Invoker instance with empty undo and redo stacks.
 */
case class Invoker():
  // the var´s Stores the executed or undone commands for the undo or redo operations.
  private var undoStack: List[Command[Field]] = Nil
  private var redoStack: List[Command[Field]] = Nil

  /**
   * Executes a given command and adds it to the undo stack.
   *
   * @param command The command to be executed.
   * @return The result of the command execution.
   */
  def doStep(command: Command[Field]): Field =
    command match
      case _ =>
        undoStack = command :: undoStack
        redoStack = Nil
    command.execute

  /**
   * Undoes the last executed command by retrieving it from the undo stack.
   *
   * @return An optional Field representing the state after undoing the command.
   *         If the undo stack is empty, returns None.
   */
  def undoStep: Option[Field] = 
    undoStack match 
      case Nil => None
      case head :: stack => 
        undoStack = stack
        redoStack = head :: redoStack
        Some(head.undoStep)

  /**
   * Redoes the last undone command by retrieving it from the redo stack.
   *
   * @return An optional Field representing the state after redoing the command.
   *         If the redo stack is empty, returns None.
   */
  def redoStep: Option[Field] =
    redoStack match
      case Nil => None
      case head :: stack =>
        redoStack = stack
        undoStack = head :: undoStack
        Some(head.redoStep)