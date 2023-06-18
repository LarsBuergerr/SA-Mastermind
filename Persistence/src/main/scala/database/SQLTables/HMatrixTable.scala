package SQLTables

import slick.jdbc.MySQLProfile.api.*

/**
 * A class representing the definition of a database table for the "Hint Matrix" data type.
 *
 * @param tag The tag for the table.
 */
class HMatrixTable(tag: Tag) extends Table[(Int, String)](tag, "HMATRIX"):
  
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def matrix = column[String]("HMATRIX")

  def * = (id, matrix)