package SQLTables

import slick.jdbc.MySQLProfile.api.*

class StateTable(tag: Tag) extends Table[(Int, String)](tag, "STATE"):
  
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def turn = column[String]("STATE")


  def * = (id, turn)