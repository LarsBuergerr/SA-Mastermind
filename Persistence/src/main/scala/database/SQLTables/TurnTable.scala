package SQLTables

import slick.jdbc.MySQLProfile.api.*

/**
 * A class representing the definition of a database table for the "Turn" data type.
 *
 * @param tag The tag for the table.
 */
class TurnTable(tag: Tag) extends Table[(Int, Int)](tag, "TURN"):
  
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def turn = column[Int]("TURN")


  def * = (id, turn)