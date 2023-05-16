package SQLTables

import slick.jdbc.MySQLProfile.api.*

class HMatrixTable(tag: Tag) extends Table[(Int, String)](tag, "HMATRIX"):
  
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def matrix = column[String]("HMATRIX")

  def * = (id, matrix)