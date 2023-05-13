package SQLTables

import slick.jdbc.MySQLProfile.api.*

class CodeTable(tag: Tag) extends Table[(Int, String)](tag, "CODE"):
  
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def code = column[String]("CODESTRING")


  def * = (id, code)