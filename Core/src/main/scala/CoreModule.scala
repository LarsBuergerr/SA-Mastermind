/**
  * ███╗   ███╗ █████╗ ███████╗████████╗███████╗██████╗ ███╗   ███╗██╗███╗   ██╗██████╗ 
  * ████╗ ████║██╔══██╗██╔════╝╚══██╔══╝██╔════╝██╔══██╗████╗ ████║██║████╗  ██║██╔══██╗
  * ██╔████╔██║███████║███████╗   ██║   █████╗  ██████╔╝██╔████╔██║██║██╔██╗ ██║██║  ██║
  * ██║╚██╔╝██║██╔══██║╚════██║   ██║   ██╔══╝  ██╔══██╗██║╚██╔╝██║██║██║╚██╗██║██║  ██║
  * ██║ ╚═╝ ██║██║  ██║███████║   ██║   ███████╗██║  ██║██║ ╚═╝ ██║██║██║ ╚████║██████╔╝
  * ╚═╝     ╚═╝╚═╝  ╚═╝╚══════╝   ╚═╝   ╚══════╝╚═╝  ╚═╝╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝╚═════╝ 
  *                                                                                  
  * Mastermind.scala
  * Created by: LarsBuergerr & SinusP-CW90
  * 
  * Project is part of the course "Software Architecture" at HTWG Konstanz
  * 
  * This is the main class of the Mastermind game.
  * 
  * © LarsBuergerr & SinusP-CW90
  */

package controller

import controller.ControllerComponent.ControllerBaseImpl.Controller
import controller.ControllerComponent.ControllerInterface
//import controller.ControllerComponent.ControllerMockImpl.Controller

import model.GameComponent.GameBaseImpl.Game
import model.GameComponent.GameInterface
//import model.GameComponent.GameMockImpl.Game

import model.GameComponent.GameBaseImpl.{Code, Play}
import model.GameModeComponent.GameModeBaseImpl.GameMode
import model.GameModeComponent.GameModeInterface
//import model.GameModeComponent.GameModeMockImpl.GameMode

import controller.ControllerComponent.ControllerBaseImpl.Controller
import controller.ControllerComponent.ControllerInterface

/**
 * This object represents the CoreModule, which provides instances for the game and controller interfaces.
 * It imports the necessary classes and creates instances using the given keyword.
 */
object CoreModule {
  // Defines the game field.
  val field = GameMode.strategy_medium
  
  // Creates an instance of GameInterface.
  given GameInterface = Game(field, new Code(field.matrix.cols), 0, Play())
  
  // Creates an instance of ControllerInterface.
  given ControllerInterface = Controller() 
}
