/**
  * Code.scala
  */

//****************************************************************************** PACKAGE  
package de.htwg.se.mastermind
package model
package GameComponent
package GameBaseImpl


//****************************************************************************** IMPORTS
import scala.util.{Try,Success,Failure}


//****************************************************************************** CLASS DEFINITION
/**
  * Code that has to be solved in game
  * Default Constructor: for test and when in 2 Player Mode
  * Auxiliary Constructor: for Single Player Mode (generates Random code)
  *
  * @param code
  */
case class Code(code: Vector[Stone]):
  
  /* AUX CON: used to generate a vector with random values*/
  def this(size: Int = 4) = this(Vector.fill(size)(Stone.random))
  
  val size = code.size
  
  override def toString(): String = code.map(_.toString()).mkString(" | ")

  /**
    * Compares a generated code with the input code done by the user
    *
    * @param userInput  UserInput as Stone Vector
    * @return HintStone Vector (All black: code are equal)
    */
  def compareTo(userInput: Vector[Stone]):Vector[HStone] =
    val equalsList = compareToEqual(userInput, 0, List())
    val presentList = compareToPresent(userInput, 0 , 0, equalsList, List())
    //buildVector(Vector(), this.size, equalsList.size, presentList.size)
    //Partially Applied Function, zusammen mit gecurrieten buildVector
    buildVector(size)(equalsList.size, presentList.size)

  //Alte Version
  def buildVector(returnVector: Vector[HStone], vectorSize: Int, equalCount: Int, presentCount: Int): (Vector[HStone]) =
  
    if(equalCount != 0) then
      return buildVector(returnVector.appended(HintStone("R")), (vectorSize - 1), (equalCount - 1), presentCount)

    if(presentCount != 0) then
      return buildVector(returnVector.appended(HintStone("W")), (vectorSize - 1), equalCount, (presentCount - 1))
    
    if(vectorSize > 0) then
      return buildVector(returnVector.appended(HintStone("E")), (vectorSize - 1), equalCount, presentCount)
    else
      return returnVector

  //Currying: Funktion wurde in eine Currying-Funktion umgewandelt,
  // bei der der vectorSize-Parameter als erster Parameter an die Funktion gebunden ist
  // und die Funktion dann mit equalCount und presentCount aufgerufen wird.
  def buildVector(vectorSize: Int)(equalCount: Int, presentCount: Int, returnVector: Vector[HStone] = Vector()): Vector[HStone] =

    if (equalCount != 0) buildVector(vectorSize - 1)(equalCount - 1, presentCount, returnVector :+ HintStone("R"))

    else if (presentCount != 0) buildVector(vectorSize - 1)(equalCount, presentCount - 1, returnVector :+ HintStone("W"))

    else if (vectorSize > 0) buildVector(vectorSize - 1)(equalCount, presentCount, returnVector :+ HintStone("E"))

    else returnVector


  def compareToEqual(inputUser: Vector[Stone], currentPos: Int, equalsList: List[Int]): (List[Int]) =
    
    if(currentPos >= size) then
      return equalsList
    
    if(this.code(currentPos).stringRepresentation.equals(inputUser(currentPos).stringRepresentation)) then
      return compareToEqual(inputUser, (currentPos + 1), equalsList.appended(currentPos))
    else
      return compareToEqual(inputUser, (currentPos + 1), equalsList)

  
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

