package SQLTables

import slick.jdbc.MySQLProfile.api.*

class MatrixTable(tag: Tag) extends Table[(Int, String)](tag, "MATRIX"):
  
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def matrix = column[String]("MATRIX")

  def * = (id, matrix)