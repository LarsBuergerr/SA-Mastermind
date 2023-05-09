package SQLTables

import slick.jdbc.MySQLProfile.api.*

class ColorCodeTable(tag: Tag) extends Table[(Int, Int, Char, Char, Char, Char, Char)](tag, "ColorCode") :

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def round = column[Int]("Round")

  def color1 = column[Char]("Color1")

  def color2 = column[Char]("Color2")

  def color3 = column[Char]("Color3")

  def color4 = column[Char]("Color4")

  def color5 = column[Char]("Color5")

  def * = (id, round, color1, color2, color3, color4, color5)

