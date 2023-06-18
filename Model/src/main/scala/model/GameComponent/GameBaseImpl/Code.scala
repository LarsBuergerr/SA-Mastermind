package model.GameComponent.GameBaseImpl

import scala.util.{Failure, Success, Try}

/**
  * This case class represents the code that needs to be solved in the game.
  * Default Constructor: for test and when in 2 Player Mode
  *
  * @param code The vector of stones representing the code.
  *
  */
case class Code(code: Vector[Stone]):

  /**
   * Auxiliary constructor used for single player mode to generate a random code.
   *
   * @param size The size of the code vector to generate.
   */
  def this(size: Int = 4) = this(Vector.fill(size)(Stone.random))

  val size = code.size
  
  override def toString(): String = code.map(_.toString()).mkString(" | ")

  /**
   * Compares the generated code with the user's input code and returns a vector of hint stones.
   *
   * @param userInput The user's input as a vector of stones.
   * @return A vector of hint stones (black for equal stones, white for present but not equal stones, empty for not present stones).
   */
  def compareTo(userInput: Vector[Stone]):Vector[HStone] =
    //val equalsList = compareToEqual(userInput, 0, List())
    val equalsList = compareToEqual(userInput)
    val presentList = compareToPresent(userInput, 0 , 0, equalsList, List())
    //buildVector(Vector(), this.size, equalsList.size, presentList.size)

    //currying buildVector call
    buildVector(size)(equalsList.size, presentList.size)

  /**
   * Builds a vector of hint stones based on the counts of equal and present stones.
   *
   * @param vectorSize   The size of the resulting vector.
   * @param equalCount   The count of equal stones.
   * @param presentCount The count of present but not equal stones.
   * @param returnVector The accumulator vector for building the result.
   * @return The vector of hint stones.
   */
  //Currying:
  def buildVector(vectorSize: Int)(equalCount: Int, presentCount: Int, returnVector: Vector[HStone] = Vector()): Vector[HStone] =
    if (equalCount != 0) buildVector(vectorSize - 1)(equalCount - 1, presentCount, returnVector :+ HintStone("R"))
    else if (presentCount != 0) buildVector(vectorSize - 1)(equalCount, presentCount - 1, returnVector :+ HintStone("W"))
    else if (vectorSize > 0) buildVector(vectorSize - 1)(equalCount, presentCount, returnVector :+ HintStone("E"))
    else returnVector

//OLD
/*
  def compareToEqual(inputUser: Vector[Stone], currentPos: Int, equalsList: List[Int]): (List[Int]) =
    if(currentPos >= size) then
      return equalsList
    if(this.code(currentPos).stringRepresentation.equals(inputUser(currentPos).stringRepresentation)) then
      return compareToEqual(inputUser, (currentPos + 1), equalsList.appended(currentPos))
    else
      return compareToEqual(inputUser, (currentPos + 1), equalsList)
*/

  /**
   * Compares the user's input code with the generated code and returns a list of positions where the stones are equal.
   *
   * @param inputUser The user's input code as a vector of stones.
   * @return A list of positions where the stones are equal.
   */
//Innere Closure, Currying
  def compareToEqual(inputUser: Vector[Stone]): List[Int] = {
    def compareEqual(currentPos: Int, equalsList: List[Int]): List[Int] =
      if (currentPos >= size) then
        equalsList
      else if (this.code(currentPos).stringRepresentation.equals(inputUser(currentPos).stringRepresentation)) then
        compareEqual(currentPos + 1, equalsList :+ currentPos)
      else
        compareEqual(currentPos + 1, equalsList)

    compareEqual(0, List())
  }

  /**
   * Compares the user's input code with the generated code and returns a list of positions where the stones are present but not equal.
   *
   * @param inputUser   The user's input code as a vector of stones.
   * @param currentPos  The current position being checked in the generated code.
   * @param secondPos   The current position being checked in the user's input code.
   * @param equalsList  The list of positions where the stones are equal.
   * @param presentList The list of positions where the stones are present but not equal.
   * @return A list of positions where the stones are present but not equal.
   */
  def compareToPresent(inputUser: Vector[Stone], currentPos: Int, secondPos: Int, equalsList: List[Int], presentList: List[Int]): (List[Int]) =
    if(currentPos >= size) then
      return presentList
    else
      if(equalsList.contains(currentPos)) then
        return compareToPresent(inputUser, (currentPos + 1), 0, equalsList, presentList) 
      else
        if(!equalsList.contains(secondPos) && !presentList.contains(secondPos) && (secondPos != currentPos)) then
          if(inputUser(currentPos).stringRepresentation.equals(this.code(secondPos).stringRepresentation)) then
            return compareToPresent(inputUser, (currentPos + 1), 0, equalsList, presentList.appended(secondPos))
        if(secondPos >= size - 1) then
          return compareToPresent(inputUser, (currentPos + 1), 0, equalsList, presentList)
        else
          return compareToPresent(inputUser, currentPos, secondPos + 1, equalsList, presentList)
