/**
  * TUI.scala
  * 
  * Class for the Text User Interface of the Mastermind game.
  */

//****************************************************************************** PACKAGE  

package aview

//****************************************************************************** IMPORTS
import model.GameComponent.GameBaseImpl._
import util.Observer
import util._
import model._
import FileIOComponent.fileIOyamlImpl.FileIO
import scala.io.StdIn.readLine
import scala.util.{Try, Success, Failure}
import akka.actor.typed.{ActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCode, HttpMethods, HttpResponse, HttpRequest}
import akka.http.scaladsl.server.{ExceptionHandler, Route}

import akka.http.scaladsl.unmarshalling.Unmarshal

//****************************************************************************** CLASS DEFINITION
class TUI():

  val uiController = new UIController()
  uiController.fetchGame()

  def run(): Unit =
    inputLoop()
  
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

  def update() =
    println(uiController.game.field)
    println("Remaining Turns: " + (uiController.game.field.matrix.rows - uiController.game.currentTurn))
