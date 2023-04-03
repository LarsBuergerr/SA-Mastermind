/**
  * GameInterface.scala
  */

//****************************************************************************** PACKAGE  
package de.htwg.se.mastermind
package model
package GameComponent


//****************************************************************************** IMPORTS
import GameBaseImpl.{Code, Field, State, Stone}
import util.{Event, Request}


//****************************************************************************** INTERFACE DEFINITION
trait GameInterface():

  val field: Field

  val code: Code

  val currentTurn: Int

  val state: State

  def request(event: Event): State

  def getCode(): Code

  def buildVector(vector: Vector[Stone])(chars: List[String]): Vector[Stone]

  def getCurrentStateEvent(): Event

  def handleRequest(request: Request): Event

  def getDefaultInputRule(input: String): Event
  
  def resetGame() : GameInterface
