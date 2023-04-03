/**
  * FILENAME.scala
  */

//****************************************************************************** PACKAGE  
package de.htwg.se.mastermind
package model
package GameComponent
package GameBaseImpl


//****************************************************************************** IMPORTS
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._


//****************************************************************************** CLASS DEFINITION
class StoneSpec extends AnyWordSpec{
  "A Stone object" should {
    "be only instanced one time (singleton pattern)" in {
      Stone should be(Stone)
    }
    "have a function to generate random stones that are not Empty" in {
      for(i <- 1 to 1000){
        Stone.random should not be(Stone("E"))
      }
    }
    "have a string representation" in {
      Stone("R").toString() should be(Stone("R").toString)
    }
  }
  "A HintStone object" should {
    "be only instanced one time (singleton pattern)" in {
      HintStone should be(HintStone)
    }
    "have a string representation" in {
      HintStone("R").toString() should be(HintStone("R").toString)
    }
  }
  
  "A Game-Stone" should {
    "have a String representation of its color [Initial letter]" in {
      Stone.apply("R").getOrElse(" ").toString should be("R")
      Stone.apply("G").getOrElse(" ").toString should be("G")
      Stone.apply("B").getOrElse(" ").toString should be("B")
      Stone.apply("Y").getOrElse(" ").toString should be("Y")
      Stone.apply("P").getOrElse(" ").toString should be("P")
      Stone.apply("W").getOrElse(" ").toString should be("W")
      Stone.apply(" ").getOrElse(" ").toString should be(" ")
      Stone.apply("E").getOrElse(" ").toString should be("E")
    }
  }
  "A Hint-Stone" should {
    "have a String representation of its color [Initial letter]" in {
      HintStone.apply("R").getOrElse(" ").toString() should be("R")
      HintStone.apply("W").getOrElse(" ").toString() should be("W")
      HintStone.apply("E").getOrElse(" ").toString() should be("E")
      HintStone.apply(" ").getOrElse(" ").toString() should be(" ")
    }  
  }
}