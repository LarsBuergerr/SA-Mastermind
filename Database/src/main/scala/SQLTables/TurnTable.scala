package SQLTables

import slick.jdbc.MySQLProfile.api.*

class TurnTable(tag: Tag) extends Table[(Int, Int)](tag, "TURN"):
  
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def turn = column[Int]("TURN")


  def * = (id, turn)