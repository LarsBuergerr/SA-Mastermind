package SQLTables

import slick.jdbc.MySQLProfile.api.*

/**
 * A class representing the definition of a database table for the "State" data type.
 *
 * @param tag The tag for the table.
 */
class StateTable(tag: Tag) extends Table[(Int, String)](tag, "STATE"):
  
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def turn = column[String]("STATE")


  def * = (id, turn)