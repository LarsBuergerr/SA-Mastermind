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

//****************************************************************************** PACKAGE  

package scala

//****************************************************************************** IMPORTS

import aview.{GUI, TUI}
import controller.ControllerComponent.RestControllerAPI
import FileIOComponent.FileIOInterface
import FileIOComponent.RestPersistenceAPI
import SlickDB.SlickDAO
import scala.io.StdIn.readLine

import MastermindModule.given

//****************************************************************************** MAIN
object mastermind extends Thread:
  // val persistenceService = RestPersistenceAPI()
  // val controllerService = RestControllerAPI()

  // persistenceService.start()
  // controllerService.start()
  val tui = TUI()
  //val gui = GUI()

  @main 
  override def start(): Unit =
    
  //  val threadGui = new Thread:
  //    override def run(): Unit =
  //      gui.main(Array[String]())

  //  threadGui.start()
  //  //REST Services

  //  Thread.sleep(1000)
    tui.run()
