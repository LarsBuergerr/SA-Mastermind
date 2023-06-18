package model.GameComponent.GameBaseImpl

import util._

/** Represents the different states of the game.  **/
trait State:
  /** Handles the current state and returns the next state.
   * @return The next state.
   */
  def handle(): State

  /**
   * Returns a string representation of the state.
   *
   * @return The string representation of the state.
   */
  override def toString(): String

  /**
   * Checks if this state is equal to another object.
   *
   * @param obj The object to compare.
   * @return True if the states are equal, false otherwise.
   */
  override def equals(obj: Any): Boolean =
    obj match
      case that: State => this.toString == that.toString
      case _ => false


val horizontalLine = "----------------------------------------------------------------" + eol

/** Represents the initial state of the game. **/
class Init extends State:
  override def handle(): State =
    val welcomeMessage = "------------------ Welcome to Mastermind -----------------------" + eol
    printf(eol + horizontalLine + welcomeMessage + horizontalLine)
    return this

  override def toString(): String = "Init"

class Menu extends State:
  override def handle(): State =
    val line           = "--- Menu: ------------------------------------------------------" + eol
    printf(line)
    return this

  override def toString(): String = "Menu"

class Play extends State:
  override def handle(): State =
    val line      = "--- Play: ------------------------------------------------------" + eol
    printf(line)
    return this

  override def toString(): String = "Play"

class Help extends State:
  override def handle(): State =
    val line      = "--- Help: [Input] : Function-----------------------------------" + eol
    val linePlay  = "---       [p    ] : starts the game" + eol
    val lineMenu  = "---       [m    ] : opens the menu" + eol
    val lineQuit  = "---       [q    ] : quits the game" + eol
    val lineHelp  = "---       [h    ] : shows this help" + eol
    val lineGame  = "---               : Select Stone ------------------------------" + eol
    val redLine   = "---       [R/r/1] : red" + eol
    val greenLine = "---       [G/g/2] : green" + eol
    val blueLine  = "---       [B/b/3] : blue" + eol
    val yellowLine= "---       [Y/y/4] : yellow" + eol
    val whiteLine = "---       [W/w/5] : white" + eol
    val purpleLine= "---       [P/p/6] : purple" + eol
    val lineInput = "---       [Enter] : Enters input" + eol
    printf(line + linePlay + lineMenu + lineQuit + lineHelp + lineGame + redLine + greenLine + blueLine + yellowLine + whiteLine + purpleLine + lineInput + horizontalLine)
    return this

  override def toString(): String = "Help"



class Quit extends State:
  override def handle(): State =
    val line      = "--- Game quit---------------------------------------------------" + eol
    printf(line)
    return this

  override def toString(): String = "Quit"


class PlayerInput extends State:
  override def handle(): State =
    return this

  override def toString(): String = "PlayerInput"


class PlayerAnalyseState() extends State:
  override def handle(): State =
    return this

  override def toString(): String = "PlayerAnalysis"


class PlayerLose extends State:
  override def handle(): State =
    val line      = "--- You lost ---------------------------------------------------" + eol
    printf(line)
    return this

  override def toString(): String = "PlayerLose"


class PlayerWin extends State:
  override def handle(): State =
    val line      = "--- You won ----------------------------------------------------" + eol
    printf(line)
    return this

  override def toString(): String = "PlayerWin"


class PlayerAnalyze extends State:
  override def handle(): State =
    val line      = "--- Player analyzes ---------------------------------------------" + eol
    printf(line)
    return this

  override def toString(): String = "PlayerAnalyze"
