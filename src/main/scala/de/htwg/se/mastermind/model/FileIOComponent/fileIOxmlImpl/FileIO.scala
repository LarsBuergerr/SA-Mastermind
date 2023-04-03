package de.htwg.se.mastermind

package model
package FileIOComponent
package fileIOxmlImpl

import GameComponent.GameInterface
import GameComponent.GameBaseImpl.{Field, Stone, Matrix, HintStone, HStone, Code, Play}
import GameComponent.GameBaseImpl.Game

import java.io._
import scala.xml._

class FileIO extends FileIOInterface:

  override def load(game: GameInterface): GameInterface = 
    import java.io._
    import scala.xml._

    val source = scala.io.Source.fromFile("game.xml")
    val xml = XML.loadString(source.mkString)
    source.close()

    val curr_turn = (xml \ "turns").text.trim.toInt
    val code = Code((xml \ "code").text.trim.map(c => Stone(c.toString)).toVector)
    val matrix = loadMatrix(xml, "matrix").asInstanceOf[Matrix[Option[Stone]]]
    val hint_matrix = loadMatrix(xml, "hint_matrix").asInstanceOf[Matrix[Option[HStone]]]

    val game = new Game(new Field(matrix, hint_matrix), code, curr_turn, Play())

    return game


  def loadMatrix(xml: NodeSeq, m_type: String): Matrix[Object] =
    val rows = (xml \ "rows").text.trim.toInt
    val cols = (xml \ "cols").text.trim.toInt

    def createMatrix(row: Int, col: Int): Matrix[Object] = {
      if (m_type == "matrix") new Matrix(row, col, Stone(" "))
      else new Matrix(row, col, HintStone(" "))
    }

    def updateMatrix(matrix: Matrix[Object], row: Int, cell: Node): Matrix[Object] = {
      val cell_y = (cell \ "@col").text.trim.toInt
      val cell_x = (cell \ "@row").text.trim.toInt
      val cell_value = (cell \ "value").text.trim

      if (m_type == "matrix") matrix.replaceCell(cell_x, cell_y, Stone(cell_value))
      else matrix.replaceCell(cell_x, cell_y, HintStone(cell_value))
    }

    def loop(matrix: Matrix[Object], rows: Seq[Node], rowNum: Int): Matrix[Object] = {
      if (rows.isEmpty) matrix
      else {
        val row = rows.head
        val row_num = (row \ "@row").text.trim.toInt
        val all_cells = (row \ "cell")

        val updatedMatrix = all_cells.foldLeft(matrix)((acc, cell) => {
          updateMatrix(acc, row_num, cell)
        })

        loop(updatedMatrix, rows.tail, rowNum + 1)
      }
    }

    val all_rows = (xml \ m_type \ "row")
    val initialMatrix = createMatrix(rows, cols)

    loop(initialMatrix, all_rows, 0)

  override def save(game: GameInterface): Unit =
    import java.io._
    import scala.xml._
    val pw = new PrintWriter(new File("game.xml"))
    pw.write(gameToXml(game).toString())
    pw.close()

  def cellToXml(matrix: Matrix[Option[Object]], row: Int, col: Int) =
    <cell row={row.toString} col={col.toString}>
      <value>
        {matrix.cell(row, col).getOrElse(" ").toString()}
      </value>
    </cell>

  def rowToXml(matrix: Matrix[Option[Object]], row: Int) =
    <row row={row.toString}>
      {
        (0 until matrix.cols).map(col => cellToXml(matrix, row, col))
      }
    </row>

  def matrixToXml(matrix: Matrix[Option[Object]]) =
    <matrix>
      {
        (0 until matrix.rows).map(row => rowToXml(matrix, row))
      }
    </matrix>

  def hmatrixToXml(hmatrix: Matrix[Option[Object]]) =
    <hint_matrix>
      {
        (0 until hmatrix.rows).map(row => rowToXml(hmatrix, row))
      }
    </hint_matrix>

  def gameToXml(game: GameInterface) =
    <game>
      <rows>
        {game.field.matrix.rows}
      </rows>
      <cols>
        {game.field.matrix.cols}
      </cols>
      {matrixToXml(game.field.matrix.asInstanceOf[Matrix[Option[Object]]])}
      {hmatrixToXml(game.field.hmatrix.asInstanceOf[Matrix[Option[Object]]])}
      <turns>
        {game.currentTurn}
      </turns>
      <code>
        {game.getCode().code}
      </code>
    </game>