package SQLTables

import slick.jdbc.MySQLProfile.api.*

/**
 * A class representing the definition of a database table for the "GAME" data type.
 *
 * @param tag The tag for the table.
 */
class GameTable(tag: Tag) extends Table[(Int, String, String, String, Int, String)](tag, "GAME"):
  
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def matrix = column[String]("MATRIX")
  def hmatrix = column[String]("HMATRIX")
  def code = column[String]("CODESTRING")
  def turn = column[Int]("TURN")
  def state = column[String]("STATE")

  def * = (id, matrix, hmatrix, code, turn, state)