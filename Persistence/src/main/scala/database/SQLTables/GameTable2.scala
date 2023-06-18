package SQLTables

import slick.jdbc.MySQLProfile.api.*

/**
 * A class representing the definition of a database table for the "GAME" data type.
 *
 * @param tag The tag for the table.
 */
class GameTable2(tag: Tag) extends Table[(Int, Int, Int, Int, Int, Int, String)](tag, "GAME2"):
  
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def matrix = column[Int]("MATRIXID")
  def hmatrix = column[Int]("HMATRIXID")
  def code = column[Int]("CODEID")
  def turn = column[Int]("TURNID")
  def state = column[Int]("STATEID")
  def save_name = column[String]("SAVE_NAME")

  def * = (id, matrix, hmatrix, code, turn, state, save_name)