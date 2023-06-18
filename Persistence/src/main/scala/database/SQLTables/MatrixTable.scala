package SQLTables

import slick.jdbc.MySQLProfile.api.*

/**
 * A class representing the definition of a database table for the "Matrix" data type.
 *
 * @param tag The tag for the table.
 */
class MatrixTable(tag: Tag) extends Table[(Int, String)](tag, "MATRIX"):
  
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def matrix = column[String]("MATRIX")

  def * = (id, matrix)