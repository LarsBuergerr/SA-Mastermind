package model.GameComponent.GameBaseImpl

/**
 * Represents a matrix of type T.
 *
 * @param m The underlying vector of vectors that stores the matrix elements.
 */
case class Matrix[T](m: Vector[Vector[T]]) {
  def this(rows: Int, cols: Int, filling: T) = this(Vector.tabulate(rows, cols) { (_, _) => filling })

  val rows: Int = m.size
  val cols: Int = m.headOption.map(_.size).getOrElse(0)

  /**
   * Retrieves the element at the specified row and column.
   *
   * @param row The row index.
   * @param col The column index.
   * @return The element at the specified position.
   */
  def cell(row: Int, col: Int): T = m(row)(col)

  /**
   * Retrieves the row at the specified index.
   *
   * @param row The row index.
   * @return The row at the specified index.
   */
  def row(row: Int): Vector[T] = m(row)

  /**
   * Creates a new matrix with all elements filled with the specified value.
   *
   * @param filling The value used to fill the matrix.
   * @return The new matrix with all elements filled with the specified value.
   */
  def fill(filling: T): Matrix[T] = copy(Vector.tabulate(rows, cols) { (_, _) => filling })

  /**
   * Replaces the element at the specified row and column with the new value.
   *
   * @param row  The row index.
   * @param col  The column index.
   * @param cell The new value to be replaced.
   * @return The new matrix with the element replaced.
   */
  def replaceCell(row: Int, col: Int, cell: T): Matrix[T] = copy(m.updated(row, m(row).updated(col, cell)))

  /**
   * Replaces the entire row at the specified index with a new vector of values.
   *
   * @param row The row index.
   * @param vec The new vector to replace the row.
   * @return The new matrix with the row replaced.
   */
  def replaceRow(row: Int, vec: Vector[T]): Matrix[T] = copy(m.updated(row, vec))
}
