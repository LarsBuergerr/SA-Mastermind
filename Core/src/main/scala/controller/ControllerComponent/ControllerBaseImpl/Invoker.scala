/**
  * Invoker.scala
  */

//****************************************************************************** PACKAGE  

package controller.ControllerComponent.ControllerBaseImpl

//****************************************************************************** IMPORTS
import model.GameComponent.GameBaseImpl.{Field}
import util.Command


//****************************************************************************** CLASS DEFINITION
case class Invoker():
  private var undoStack: List[Command[Field]] = Nil
  private var redoStack: List[Command[Field]] = Nil

  def doStep(command: Command[Field]): Field =
    command match
      case _ =>
        undoStack = command :: undoStack
        redoStack = Nil
    command.execute


  def undoStep: Option[Field] = 
    undoStack match 
      case Nil => None
      case head :: stack => 
        undoStack = stack
        redoStack = head :: redoStack
        Some(head.undoStep)

      
  def redoStep: Option[Field] =
    redoStack match
      case Nil => None
      case head :: stack =>
        redoStack = stack
        undoStack = head :: undoStack
        Some(head.redoStep)