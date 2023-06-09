package FileIOComponent.fileIOJsonImpl

import model.GameComponent.GameInterface
import FileIOComponent.FileIOInterface
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import model.GameComponent.GameBaseImpl._

//import json lib
import play.api.libs.json.*
//import javax.swing.filechooser.FileNameExtensionFilter

class FileIO extends FileIOInterface:

  override def load(): GameInterface = 
    import scala.io.Source
    import java.io.File
    val source: String = Source.fromFile("game.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    jsonToGame(json)

  override def save(game: GameInterface): Unit = 
    import java.io._
    import scala.xml._
    val pw = new PrintWriter(new File("game.json"))
    pw.write(gameToJson(game).toString())
    pw.close()

  def cellToJson(cell: Object, x: Int, y: Int) = 
    val cellJson = Json.obj(
      "x" -> x,
      "y" -> y,
      "value" -> cell.toString()
    )
    cellJson

  def vectorToJson(vector: Vector[Object], row: Int) =
    Json.obj(
      "row" -> row,
      "cells" -> {
        vector.map(cell => cellToJson(cell, row, vector.indexOf(cell)))
      }
    )

  def stateToJson(state: State) =
    val json = Json.obj(
      "value" -> state.toString()
    )
    json

  def gameToJson(game: GameInterface) =
    val json = Json.obj(
      "matrix" -> {
        game.field.matrix.m.map(vector => vectorToJson(vector, game.field.matrix.m.indexOf(vector)))
      },
      "hmatrix" -> {
        game.field.hmatrix.m.map(vector => vectorToJson(vector, game.field.hmatrix.m.indexOf(vector)))
      },
      "turn" -> game.currentTurn,
      "code" -> vectorToJson(game.code.code.asInstanceOf[Vector[Object]], 0),
      "state" -> stateToJson(game.state),
    )
    json



  def jsonToStone(cellJson: JsValue) = 
    val x = cellJson("x").as[Int]
    val y = cellJson("y").as[Int]
    val value = cellJson("value").as[String]
    val cell = Stone(value)
      cell

  def jsonToHStone(cellJson: JsValue) = 
    val x = cellJson("x").as[Int]
    val y = cellJson("y").as[Int]
    val value = cellJson("value").as[String]
    val cell = HintStone(value)
    cell


  def jsonToVector(vectorJson: JsValue, mtype: String) =
    val row = vectorJson("row").as[Int]
    val cells = vectorJson("cells")

    val vector = 
      if mtype == "matrix" then
        cells.as[Seq[JsValue]].map(cell => jsonToStone(cell))
      else
        cells.as[Seq[JsValue]].map(cell => jsonToHStone(cell))
    vector.toVector

  def jsonToState(stateJson: JsValue) =
    val state = stateJson("value").as[String]
    state match
      case "Init" => Init()
      case "Menu" => Menu()
      case "Play" => Play()
      case "Help" => Help()
      case "Quit" => Quit()
      case "PlayerInput" => PlayerInput()
      case "PlayerAnalyseState" => PlayerAnalyseState()
      case "PlayerLose" => PlayerLose()
      case "PlayerWin" => PlayerWin()
      case "PlayerAnalyze" => PlayerAnalyze()
      case _ => Play()
  

  def jsonToGame(gameJson: JsValue): GameInterface =

    val matrix = Matrix[Stone](gameJson("matrix").as[Seq[JsValue]].map(vector => jsonToVector(vector, "matrix")).toVector.asInstanceOf[Vector[Vector[Stone]]])
    val hmatrix = Matrix[HStone](gameJson("hmatrix").as[Seq[JsValue]].map(vector => jsonToVector(vector, "hmatrix")).toVector.asInstanceOf[Vector[Vector[HStone]]])
    val code = Code(jsonToVector(gameJson("code"), "matrix").asInstanceOf[Vector[Stone]])
    val turn = gameJson("turn").as[Int]
    val state = jsonToState(gameJson("state"))
    Game(new Field(matrix, hmatrix), code, turn, state)

