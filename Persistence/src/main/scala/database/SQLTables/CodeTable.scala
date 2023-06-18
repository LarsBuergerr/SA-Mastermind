package SQLTables

import slick.jdbc.MySQLProfile.api.*

/**
 * A class representing the definition of a database table for the "CODE" data type.
 *
 * @param tag The tag for the table.
 */
class CodeTable(tag: Tag) extends Table[(Int, String)](tag, "CODE"):
  
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def code = column[String]("CODESTRING")


  def * = (id, code)