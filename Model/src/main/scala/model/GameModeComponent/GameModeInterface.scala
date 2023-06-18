package model.GameModeComponent

import model.GameComponent.GameBaseImpl.Field

/**
 * Interface for the GameModes
 */
trait GameModeInterface:
  
  val selectMode : String
  
  def strategy_easy: Field
  
  def strategy_medium: Field
  
  def strategy_hard: Field
  
  def strategy_extrem: Field
  
  def parseInput(): Field