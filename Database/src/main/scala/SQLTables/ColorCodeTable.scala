package SQLTables

import slick.jdbc.MySQLProfile.api.*

class ColorCodeTable(tag: Tag) extends Table[(Int, Int, String, String, String, Char)](tag, "ColorCode") :

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def round = column[Int]("Round")

  def colorCode = column[String]("ColorCode")

  def colorCodeHint = column[String]("ColorCodeHint")

//can be stored in a separate "gameSettingTable" linked to a GameSessionID
  def colorCodeSolution = column[String]("ColorCodeSolution")
  def gameDifficulty = column[Char]("gameDifficulty")


  def * = (id, round, colorCode, colorCodeHint,colorCodeSolution,gameDifficulty)

