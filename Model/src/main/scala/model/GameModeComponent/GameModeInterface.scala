/**
  * GameModeInterface.scala
  */

//****************************************************************************** PACKAGE  


//****************************************************************************** IMPORTS

import model.GameComponent.GameBaseImpl.Field

//****************************************************************************** INTERFACE DEFINITION
trait GameModeInterface:
  
  val selectMode : Field
  
  def strategy_easy: Field
  
  def strategy_medium: Field
  
  def strategy_hard: Field
  
  def strategy_extrem: Field
  
  def parseInput(): Field