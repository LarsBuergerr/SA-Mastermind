package model.GameComponent

import GameBaseImpl.{Code, Field, State, Stone}
import util.{Event, Request}

/**
 * Represents the interface for the game.
 */
trait GameInterface():

  val field: Field

  val code: Code

  val currentTurn: Int

  val state: State

  /**
   * Processes the given event and returns the resulting state.
   *
   * @param event The event to process.
   * @return The resulting state.
   */
  def request(event: Event): State

  def getCode(): Code

  /**
   * Builds a vector of stones based on the given character array.
   *
   * @param vector The vector of stones to update.
   * @param chars  The character array representing the stones.
   * @return The updated vector of stones.
   */
  def buildVector(vector: Vector[Stone])(chars: Array[Char]): Vector[Stone]

  def getCurrentStateEvent(): Event

  /**
   * Handles the given request and returns the corresponding event.
   *
   * @param request The request to handle.
   * @return The event corresponding to the request.
   */
  def handleRequest(request: Request): Event

  /**
   * Retrieves the default input rule event for the given input.
   *
   * @param input The input string.
   * @return The default input rule event.
   */
  def getDefaultInputRule(input: String): Event

  /**
   * Resets the game to its initial state.
   *
   * @return The reset game interface.
   */
  def resetGame() : GameInterface
