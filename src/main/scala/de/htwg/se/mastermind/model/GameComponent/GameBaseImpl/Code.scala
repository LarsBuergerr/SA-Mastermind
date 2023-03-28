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
  //PARTIALLY APPLIED FUNCTION: Function that fills the vector with random values
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
    //val equalsList = compareToEqual(userInput, 0, List())
    val equalsList = compareToEqual(userInput)
    //val presentList = compareToPresent(userInput, 0 , 0, equalsList, List())
    val presentList = compareToPresent(userInput)
    //buildVector(Vector(), this.size, equalsList.size, presentList.size)
    //Partially Applied Function,with currying buildVector
    buildVector(size)(equalsList.size, presentList.size)

  //Currying:
  // bei der der vectorSize-Parameter als erster Parameter an die Funktion gebunden ist
  // und die Funktion dann mit equalCount und presentCount aufgerufen wird.
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

//Innere Closure, Currying
//Diese Closure hat einen neuen Parameter equalsList, welcher als Liste zur Verfügung gestellt wird und alle Indizes enthält,
// an denen Steine der Eingabe und des Lösungscodes übereinstimmen.
// Der alte Parameter currentPos ist gebunden und der Eingabeparameter inputUser wird zur Verfügung gestellt.
// compareToEqual wird in compareToPresent durch compareToEqual(inputUser) aufgerufen.
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

//OLD
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

  //Closure, Currying
  //neuer Parameter presentList, welcher als Liste zur Verfügung gestellt wird und alle Indizes enthält,
  // an denen Steine der Eingabe an einer anderen Stelle im Lösungscode vorkommen.
  // compareToEqual wird auch hier durch compareToEqual(inputUser) aufgerufen.
  // Die anderen Parameter sind gebunden.
  def compareToPresent(inputUser: Vector[Stone]): List[Int] = {
    def comparePresent(currentPos: Int, secondPos: Int, equalsList: List[Int], presentList: List[Int]): List[Int] =
      if (currentPos >= size) then
        presentList
      else if (equalsList.contains(currentPos)) then
        comparePresent(currentPos + 1, 0, equalsList, presentList)
      else if (!equalsList.contains(secondPos) && !presentList.contains(secondPos) && (secondPos != currentPos)) then
        if (inputUser(currentPos).stringRepresentation.equals(this.code(secondPos).stringRepresentation)) then
          comparePresent(currentPos, secondPos + 1, equalsList, presentList :+ secondPos)
        else
          comparePresent(currentPos, secondPos + 1, equalsList, presentList)
      else if (secondPos >= size - 1) then
        comparePresent(currentPos + 1, 0, equalsList, presentList)
      else
        comparePresent(currentPos, secondPos + 1, equalsList, presentList)

    comparePresent(0, 0, compareToEqual(inputUser), List())
  }