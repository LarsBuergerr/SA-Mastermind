/**
  * Field.scala
  */

//****************************************************************************** PACKAGE
package de.htwg.se.mastermind
package model
package GameComponent
package GameBaseImpl


//****************************************************************************** IMPORTS


//****************************************************************************** CLASS DEFINITION
/* Don't wanna use the stings all the time */
private val plus = "+"
private val minus = "-"
private val verLine = "|"
private val space = " "
private val rbracket = "["
private val lbracket = "]"
private val eol = sys.props("line.separator")


/**
  * A Mastermind field
  *
  * @param matrix   Matrix with the actual player stones
  * @param hmatrix  Matrix with the hint stones
  */
case class Field(val matrix: Matrix[Stone], val hmatrix: Matrix[HStone]):

  def this(rows: Int = 6, cols: Int = 4, filling: Stone = Stone("E"), hfilling: HStone = HintStone("E")) =
    this(new Matrix(rows, cols, filling), new Matrix(rows, cols, hfilling))
  
  val rows = matrix.rows
  val cols = matrix.cols

  def bar(cellWidth: Int = 3, cellCount: Int = this.cols): String = 
    (plus + (minus * cellWidth)) * cellCount + plus + eol

  def cellsX(row: Int = 0, cellWidth: Int = 3, cellCount: Int = this.cols): String =
    matrix.row(row).map(_.toString).map(" " * ((cellWidth - 1) / 2) + _ + " " * ((cellWidth - 1) / 2)).mkString("|", "|", "|") + 
    space * 3 + hmatrix.row(row).map(_.toString).map(" " + _ + " ").mkString("[", "|", "]") + eol

//Die cells-Methode verwendet Closure, um die Formatierungsfunktionen für die Spielsteine und Hinweise zu definieren.
  def cells(row: Int = 0, cellWidth: Int = 3, cellCount: Int = this.cols): String = {
    val formatCell: Stone => String = stone =>
      " " * ((cellWidth - 1) / 2) + stone.toString + " " * ((cellWidth - 1) / 2)

    val formatHStone: HStone => String = hstone =>
      " " + hstone.toString + " "

    val formattedCells = matrix.row(row).map(formatCell)
    val formattedHStones = hmatrix.row(row).map(formatHStone)
    formattedCells.mkString("|", "|", "|") + space * 3 + formattedHStones.mkString(rbracket, verLine, lbracket) + eol
  }

  def mesh(cellWidth: Int = 3, rows: Int = this.rows, colls: Int = this.cols):String = 
    (0 until rows).map(cells(_)).mkString(bar(cellWidth, colls), bar(cellWidth, colls), bar(cellWidth, colls))

  //Die mesh-Methode verwendet Partially Applied Functions, um eine Funktion mit vorgegebenen Argumenten zu erstellen und später auf Daten anzuwenden.
  def meshX(cellWidth: Int = 3, rows: Int = this.rows, colls: Int = this.cols): String = {
    val formattedCells = (0 until rows).map(row => cells(row, cellWidth, colls))
    formattedCells.mkString(bar(cellWidth, colls), bar(cellWidth, colls), bar(cellWidth, colls))
  }

  def placeGuessAndHints(stone: Vector[Stone])(hints: Vector[HStone])(row: Int) =
    copy(matrix.replaceRow(row, stone), hmatrix.replaceRow(row, hints))

  def placeGuessAndHintsX(stone: Vector[Stone])(hints: Vector[HStone])(row: Int): Field =
    copy(matrix.replaceRow(row, stone), hmatrix.replaceRow(row, hints))

  override def toString = mesh(3, rows, cols)



