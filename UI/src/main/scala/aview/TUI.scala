package aview

import FileIOComponent.fileIOyamlImpl.FileIO
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.http.scaladsl.unmarshalling.Unmarshal
import model.*
import model.GameComponent.GameBaseImpl.*
import model.GameModeComponent.GameModeBaseImpl.GameMode
import util.*

import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

/**
 * The TUI class represents the text-based user interface of the application.
 */
class TUI():

  val uiController = new UIController()
  uiController.game = new Game(GameMode.strategy_medium, new Code(4), 0, Play())
  //uiController.fetchGame()

  /**
   * The run() method is the main method of the TUI class that starts the input loop.
   */
  def run(): Unit =
    inputLoop()

  /**
   * The inputLoop() method represents the main input loop of the TUI.
   * It continuously reads user input, parses it, and updates the game state accordingly.
   */
  //@todo Boolean return type for testing?
  def inputLoop(): Unit =
    update()
    val input = readLine(">> ")
    
    parseInput(input) match 
      case pInp: PlayerInput  =>
        inputLoop()
      case pWin: PlayerWin    =>
        print("--- Thank you for playing the game\n")
        Thread.sleep(2000)                                                      // Wait 2 seconds  @todo: reset game?
      case pLos: PlayerLose   =>
        print("--- Thank you for playing the game and see you soon\n")
        Thread.sleep(2000)                                                      // Wait 2 seconds  @todo: reset game?
      case help: Help         =>
        inputLoop()
      case menu: Menu         =>
        print("Code:" + uiController.game.getCode().toString() + "\n")
        inputLoop()
      case play: Play         =>
        inputLoop()    
      case quit: Quit         =>
        print("--- See you later alligator...\n")

  /**
   * The parseInput() method takes the user input as a string and parses it to determine the appropriate game state.
   *
   * @param input The user input as a string.
   * @return The corresponding game state based on the user input.
   */
  def parseInput(input: String): State =

    val emptyVector: Vector[Stone] = Vector()
    val chars = input.toCharArray()

    chars.size match
      case 0 =>  // Handles no user input -> stay in current state
        uiController.handleSingleCharReq("h")
        return uiController.game.state

      case 1 =>  //Handles single char user input (first with CoR, then with State Pattern)
        uiController.handleSingleCharReq(input)
        return uiController.game.state

      case _ => //Handles multi char user input
        uiController.handleMultiCharReq(input)
        return uiController.game.state

  /**
   * The update() method prints the current game field and the remaining turns.
   */
  def update() =
    println(uiController.game.field)
    println("Remaining Turns: " + (uiController.game.field.matrix.rows - uiController.game.currentTurn))
