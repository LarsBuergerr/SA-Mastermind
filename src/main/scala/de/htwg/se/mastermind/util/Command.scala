/**
  * Command.scala
  */

//****************************************************************************** PACKAGE  
package de.htwg.se.mastermind
package util


//****************************************************************************** IMPORTS
import model.GameComponent.GameBaseImpl.Field


//****************************************************************************** INTERFACE DEFINITION
trait Command[T]:
  def execute:  T
  def undoStep: T
  def redoStep: T