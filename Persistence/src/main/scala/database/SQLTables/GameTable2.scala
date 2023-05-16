package SQLTables

import slick.jdbc.MySQLProfile.api.*

class GameTable2(tag: Tag) extends Table[(Int, Int, Int, Int, Int, Int)](tag, "GAME2"):
  
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def matrix = column[Int]("MATRIXID")
  def hmatrix = column[Int]("HMATRIXID")
  def code = column[Int]("CODEID")
  def turn = column[Int]("TURNID")
  def state = column[Int]("STATEID")

  def * = (id, matrix, hmatrix, code, turn, state)