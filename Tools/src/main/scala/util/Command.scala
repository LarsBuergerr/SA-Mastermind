/**
  * Command.scala
  */

//****************************************************************************** PACKAGE  



//****************************************************************************** IMPORTS
import model.GameComponent.GameBaseImpl.Field


//****************************************************************************** INTERFACE DEFINITION
trait Command:
  def execute:  Field
  def undoStep: Field
  def redoStep: Field