/**
  * GameModeInterface.scala
  */

//****************************************************************************** PACKAGE  

package model.GameModeComponent

//****************************************************************************** IMPORTS

import model.GameComponent.GameBaseImpl.Field

//****************************************************************************** INTERFACE DEFINITION
trait GameModeInterface:
  
  val selectMode : String
  
  def strategy_easy: Field
  
  def strategy_medium: Field
  
  def strategy_hard: Field
  
  def strategy_extrem: Field
  
  def parseInput(): Field