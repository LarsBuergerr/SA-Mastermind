package util

/**
 * A trait representing a command with execute, undoStep, and redoStep operations.
 */
trait Command[T]:
  def execute:  T
  def undoStep: T
  def redoStep: T