/**
  * ███╗   ███╗ █████╗ ███████╗████████╗███████╗██████╗ ███╗   ███╗██╗███╗   ██╗██████╗ 
  * ████╗ ████║██╔══██╗██╔════╝╚══██╔══╝██╔════╝██╔══██╗████╗ ████║██║████╗  ██║██╔══██╗
  * ██╔████╔██║███████║███████╗   ██║   █████╗  ██████╔╝██╔████╔██║██║██╔██╗ ██║██║  ██║
  * ██║╚██╔╝██║██╔══██║╚════██║   ██║   ██╔══╝  ██╔══██╗██║╚██╔╝██║██║██║╚██╗██║██║  ██║
  * ██║ ╚═╝ ██║██║  ██║███████║   ██║   ███████╗██║  ██║██║ ╚═╝ ██║██║██║ ╚████║██████╔╝
  * ╚═╝     ╚═╝╚═╝  ╚═╝╚══════╝   ╚═╝   ╚══════╝╚═╝  ╚═╝╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝╚═════╝ 
  *                                                                                  
  * Mastermind.scala
  * Created by: LarsBuergerr & Smokey95
  * 
  * Project is part of the course "Software Engineering" at HTWG Konstanz
  * 
  * This is the main class of the Mastermind game.
  * 
  * © LarsBuergerr & Smokey95
  */

package controller

//****************************************************************************** IMPORTS
import controller.ControllerComponent.ControllerInterface
import controller.ControllerComponent.ControllerBaseImpl.Controller
//import controller.ControllerComponent.ControllerMockImpl.Controller

import model.GameComponent.GameInterface
import model.GameComponent.GameBaseImpl.Game
//import model.GameComponent.GameMockImpl.Game

import model.GameComponent.GameBaseImpl.{Code, Play}

import model.GameModeComponent.GameModeInterface
import model.GameModeComponent.GameModeBaseImpl.GameMode
//import model.GameModeComponent.GameModeMockImpl.GameMode

import controller.ControllerComponent.ControllerBaseImpl.Controller
import controller.ControllerComponent.ControllerInterface

//****************************************************************************** OBJECT DEFINITION
object CoreModule:
  val field = GameMode.strategy_medium
  given GameInterface       = Game(field, new Code(field.matrix.cols), 0, Play())
  given ControllerInterface = Controller()