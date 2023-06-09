package model.GameComponent.GameBaseImpl

import util._
import model.GameComponent.GameInterface

/**
  * Represents a game instance with its current state and game field.
  *
  * @param field         The mastermind game field.
  * @param code          The code to be guessed.
  * @param currentTurn   The current turn of the game.
  * @param state         The state in which the game is currently.
 **/
case class Game(val field: Field = new Field(10, 4),
                val code: Code = new Code(4),
                val currentTurn: Int = 0,
                val state: State = Init()) extends GameInterface:

  //Partial function gets string and returns a event
  type PartialFunctionRule = PartialFunction[String, Event]
  
  // Defines the Chain of Responsibility (Pattern)
  val chainSCR: PartialFunctionRule =
    RequestHandlerSCR.HelpInputRule orElse
    RequestHandlerSCR.MenuInputRule orElse
    RequestHandlerSCR.PlayInputRule orElse
    RequestHandlerSCR.QuitInputRule orElse
    RequestHandlerSCR.UndoInputRule orElse
    RequestHandlerSCR.RedoInputRule orElse
    RequestHandlerSCR.SaveInputRule orElse
    RequestHandlerSCR.LoadInputRule orElse
    RequestHandlerSCR.DBLoadInputRule orElse
    RequestHandlerSCR.DBSaveInputRule

  /**
   * Calls the responsible chain to handle the request.
   *
   * @param request The request to be handled.
   * @return The resulting event.
   */
  def handleRequest(request: Request): Event =
    request match
      case SingleCharRequest(userinput) =>
        chainSCR.applyOrElse(userinput, RequestHandlerSCR.DefaultInputRule)

      case MultiCharRequest(userinput) =>
        if(userinput.size != field.matrix.cols) then
          return RequestHandlerSCR.DefaultInputRule(userinput)
        else
          return PlayerAnalyzeEvent()

  /**
   * Processes an event and returns the new state.
   *
   * @param event The event to be processed.
   * @return The new state.
   */
  def request(event: Event): State =
    val req_state = event match
      case init: InitStateEvent         =>  Init()
      case menu: MenuStateEvent         =>  Menu()
      case play: PlayStateEvent         =>  Play()
      case quit: QuitStateEvent         =>  Quit()
      case help: HelpStateEvent         =>  Help()
      
      case pInp: PlayerInputStateEvent  =>  PlayerInput()
      case pLos: PlayerLoseStateEvent   =>  PlayerLose()
      case pWin: PlayerWinStateEvent    =>  PlayerWin()
      case pAna: PlayerAnalyzeEvent     =>  PlayerAnalyze()
    
    return req_state.handle()
  
  override def toString(): String = field.toString

  def getCode(): Code = code
  
  def resetGame(): Game =
    Game(new Field(field.matrix.rows, field.matrix.cols), new Code(field.matrix.cols), 0, Init())

  def buildVector(vector: Vector[Stone])(chars: Array[Char]): Vector[Stone] =
  chars.take(field.cols - vector.size)
       .map(charToStone)
       .foldLeft(vector)(_ :+ _)

    def charToStone(char: Char): Stone =
      char match
        case 'R' | 'r' | '1' => Stone("R")
        case 'G' | 'g' | '2' => Stone("G")
        case 'B' | 'b' | '3' => Stone("B")
        case 'Y' | 'y' | '4' => Stone("Y")
        case 'W' | 'w' | '5' => Stone("W")
        case 'P' | 'p' | '6' => Stone("P")

  /**
    * Return the event that is needed to trigger the current state and 
    * can be used to stay in the current state
    * 
    * @return event that triggers the current state
    */
  def getCurrentStateEvent(): Event =
    state match
      case init:Init        => HelpStateEvent()
      case menu:Menu        => MenuStateEvent()
      case play:Play        => PlayStateEvent()
      case quit:Quit        => QuitStateEvent()
      case help:Help        => HelpStateEvent()
      case pInp:PlayerInput => PlayerInputStateEvent()

  def getDefaultInputRule(input: String): Event =
    RequestHandlerSCR.DefaultInputRule(input)
  
  object RequestHandlerSCR:
    //defines the general rule for the chain
    def singleCharRule(f: String => Boolean, result: Event): PartialFunctionRule =
      case s if f(s) => result
    
    //defines the concrete rules
    val HelpInputRule: PartialFunctionRule = singleCharRule(_ == "h", HelpStateEvent())
    val MenuInputRule: PartialFunctionRule = singleCharRule(_ == "m", MenuStateEvent())
    val PlayInputRule: PartialFunctionRule = singleCharRule(_ == "p", PlayStateEvent())
    val QuitInputRule: PartialFunctionRule = singleCharRule(_ == "q", QuitStateEvent())
    val UndoInputRule: PartialFunctionRule = singleCharRule(_ == "u", UndoStateEvent())
    val RedoInputRule: PartialFunctionRule = singleCharRule(_ == "r", RedoStateEvent())
    val SaveInputRule: PartialFunctionRule = singleCharRule(_ == "s", SaveStateEvent())
    val LoadInputRule: PartialFunctionRule = singleCharRule(_ == "l", LoadStateEvent())
    val DBSaveInputRule: PartialFunctionRule = singleCharRule(_ == "d", DBSaveStateEvent())
    val DBLoadInputRule: PartialFunctionRule = singleCharRule(_ == "b", DBLoadStateEvent())
    
    //defines the default rule
    def DefaultInputRule(userinput: String): Event =
      println(">>> Error: Invalid input [will be ignored]")
      getCurrentStateEvent()

