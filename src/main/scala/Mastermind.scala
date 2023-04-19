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

import aview.RestUI.RestUiAPI
import aview.{GUI, TUI}
import controller.ControllerComponent.RestControllerAPI

import MastermindModule.given

//****************************************************************************** MAIN
object mastermind extends Thread:

  val tui = TUI()
  val gui = GUI()
  val restUI = RestUiAPI()
  val restConAPI = RestControllerAPI()

  @main 
  override def start(): Unit =
    
    val threadGui = new Thread:
      override def run(): Unit =
        gui.main(Array[String]())

    threadGui.start()
    //REST Services
    restUI.start()
    restConAPI.start()
    Thread.sleep(1000)
    tui.run()
