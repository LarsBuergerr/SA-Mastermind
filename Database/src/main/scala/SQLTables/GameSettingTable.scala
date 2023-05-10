package SQLTables

import slick.jdbc.MySQLProfile.api.*

class GameSettingTable(tag: Tag) extends Table[(Int, Int, String, String)](tag, "GameSetting") :

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def round = column[Int]("Round")

  def colorCode = column[String]("Color1")

  def colorCodeHint = column[String]("Color2")


  def * = (id, round, colorCode, colorCodeHint)