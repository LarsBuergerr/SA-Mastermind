package model.GameComponent.GameBaseImpl

/* Don't wanna use the stings all the time */
private val plus = "+"
private val minus = "-"
private val verLine = "|"
private val space = " "
private val rbracket = "["
private val lbracket = "]"
private val eol = sys.props("line.separator")


/**
 * A Mastermind field representing the game board.
 *
 * @param matrix   Matrix with the actual player stones.
 * @param hmatrix  Matrix with the hint stones.
 */
case class Field(var matrix: Matrix[Stone], var hmatrix: Matrix[HStone]):

  /**
   * Auxiliary constructor to create a field with specified dimensions and filling values.
   *
   * @param rows     The number of rows in the field.
   * @param cols     The number of columns in the field.
   * @param filling  The filling value for the player stones.
   * @param hfilling The filling value for the hint stones.
   */
  def this(rows: Int = 6, cols: Int = 4, filling: Stone = Stone("E"), hfilling: HStone = HintStone("E")) = {
    this(new Matrix(rows, cols, filling), new Matrix(rows, cols, hfilling))
  }

  val rows = matrix.rows
  val cols = matrix.cols

  /**
   * Generates a string representing the horizontal bar separator between rows.
   *
   * @param cellWidth The width of each cell in the field.
   * @param cellCount The number of cells in a row.
   * @return The horizontal bar string.
   */
  def bar(cellWidth: Int = 3, cellCount: Int = this.cols): String = {
    (plus + (minus * cellWidth)) * cellCount + plus + eol
  }

  /**
   * Generates a string representing the cells of a specific row, including player stones and hint stones.
   *
   * @param row       The row index.
   * @param cellWidth The width of each cell in the field.
   * @param cellCount The number of cells in a row.
   * @return The string representing the cells of the row.
   */
  def cells(row: Int = 0, cellWidth: Int = 3, cellCount: Int = this.cols): String = {
    matrix.row(row).map(_.toString).map(" " * ((cellWidth - 1) / 2) + _ + " " * ((cellWidth - 1) / 2)).mkString("|", "|", "|") +
      space * 3 + hmatrix.row(row).map(_.toString).map(" " + _ + " ").mkString("[", "|", "]") + eol
  }

  /**
   * Generates a string representing the entire game board with player stones and hint stones.
   *
   * @param cellWidth The width of each cell in the field.
   * @param rows      The number of rows in the field.
   * @param cols      The number of columns in the field.
   * @return The string representing the game board.
   */
  def mesh(cellWidth: Int = 3, rows: Int = this.rows, colls: Int = this.cols):String =
  {
    (0 until rows).map(cells(_)).mkString(bar(cellWidth, colls), bar(cellWidth, colls), bar(cellWidth, colls))
  }

  /**
   * Places the player stones and hint stones in the specified row of the field.
   *
   * @param stone The player stones to be placed.
   * @param hints The hint stones to be placed.
   * @param row   The row index where the stones should be placed.
   * @return A new field with the stones placed in the specified row.
   */
  def placeGuessAndHints(stone: Vector[Stone])(hints: Vector[HStone])(row: Int) = copy(matrix.replaceRow(row, stone), hmatrix.replaceRow(row, hints))

  override def toString = mesh(3, rows, cols)
