/**
  * Stone.scala
  * Implements the Factory Pattern to create different stones
  */

//****************************************************************************** PACKAGE
package de.htwg.se.mastermind
package model
package GameComponent
package GameBaseImpl


//****************************************************************************** IMPORTS
import scala.util.Random


enum StoneColor:
  case Red, Green, Blue, Yellow, White, Purple, Empty



enum Stone(color: StoneColor, name: String):
  case Red extends Stone(StoneColor.Red, "R")
  case Green extends Stone(StoneColor.Green, "G")
  case Blue extends Stone(StoneColor.Blue, "B")
  case Yellow extends Stone(StoneColor.Yellow, "Y")
  case White extends Stone(StoneColor.White, "W")
  case Purple extends Stone(StoneColor.Purple, "P")
  case Empty extends Stone(StoneColor.Empty, "E")

  override def toString: String = name

enum HStone(color: StoneColor, name: String):
  case Red extends HStone(StoneColor.Red, "R")
  case White extends HStone(StoneColor.White, "W")
  case Empty extends HStone(StoneColor.Empty, "E")

  override def toString: String = name

object Stone:
  def apply(stringRepresentation: String): Option[Stone] =
    stringRepresentation match  
      case "R" => Some(Stone.Red)
      case "G" => Some(Stone.Green)
      case "B" => Some(Stone.Blue)
      case "Y" => Some(Stone.Yellow)
      case "W" => Some(Stone.White)
      case "P" => Some(Stone.Purple)
      case "E" => None

  def random: Stone =
    Random.nextInt(6) match
      case 0 => Stone.Red
      case 1 => Stone.Green
      case 2 => Stone.Blue
      case 3 => Stone.Yellow
      case 4 => Stone.White
      case 5 => Stone.Purple
      case _ => Stone.Empty


object HintStone:
  def apply(stringRepresentation: String): Option[HStone] =
    stringRepresentation match
      case "R" => Some(HStone.Red)
      case "W" => Some(HStone.White)
      case "E" => None
