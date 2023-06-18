package FileIOComponent.fileIOJsonImpl

import FileIOComponent.FileIOInterface
import model.GameComponent.GameBaseImpl.*
import model.GameComponent.GameInterface

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//import json lib
import play.api.libs.json.*
//import javax.swing.filechooser.FileNameExtensionFilter

/**
 * Converts a JSON game to a GameInterface object.
 *
 * @param gameJson The JSON representation of the game.
 * @return The GameInterface object created from the JSON game.
 */
class FileIO extends FileIOInterface:

  /**
   * Loads a game from a JSON file.
   *
   * @return The loaded game as a GameInterface object.
   */
  override def load(): GameInterface = 
    import java.io.File
    import scala.io.Source
    val source: String = Source.fromFile("game.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    jsonToGame(json)

  /**
   * Saves a game to a JSON file.
   *
   * @param game The game to be saved.
   */
  override def save(game: GameInterface): Unit = 
    import java.io.*
    import scala.xml.*
    val pw = new PrintWriter(new File("game.json"))
    pw.write(gameToJson(game).toString())
    pw.close()

  /**
   * Converts a cell object to JSON format.
   *
   * @param cell The cell object to be converted.
   * @param x    The x-coordinate of the cell.
   * @param y    The y-coordinate of the cell.
   * @return The JSON representation of the cell.
   */
  def cellToJson(cell: Object, x: Int, y: Int) = 
    val cellJson = Json.obj(
      "x" -> x,
      "y" -> y,
      "value" -> cell.toString()
    )
    cellJson

  /**
   * Converts a vector of objects to JSON format.
   *
   * @param vector The vector of objects to be converted.
   * @param row    The row index of the vector.
   * @return The JSON representation of the vector.
   */
  def vectorToJson(vector: Vector[Object], row: Int) =
    Json.obj(
      "row" -> row,
      "cells" -> {
        vector.map(cell => cellToJson(cell, row, vector.indexOf(cell)))
      }
    )

  /**
   * Converts a state object to JSON format.
   *
   * @param state The state object to be converted.
   * @return The JSON representation of the state.
   */
  def stateToJson(state: State) =
    val json = Json.obj(
      "value" -> state.toString()
    )
    json

  /**
   * Converts a game object to JSON format.
   *
   * @param game The game object to be converted.
   * @return The JSON representation of the game.
   */
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


  /**
   * Converts a JSON cell to a Stone object.
   *
   * @param cellJson The JSON representation of the cell.
   * @return The Stone object created from the JSON cell.
   */
  def jsonToStone(cellJson: JsValue) = 
    val x = cellJson("x").as[Int]
    val y = cellJson("y").as[Int]
    val value = cellJson("value").as[String]
    val cell = Stone(value)
      cell

  /**
   * Converts a JSON cell to a HintStone object.
   *
   * @param cellJson The JSON representation of the cell.
   * @return The HintStone object created from the JSON cell.
   */
  def jsonToHStone(cellJson: JsValue) = 
    val x = cellJson("x").as[Int]
    val y = cellJson("y").as[Int]
    val value = cellJson("value").as[String]
    val cell = HintStone(value)
    cell

  /**
   * Converts a JSON vector to a vector of objects.
   *
   * @param vectorJson The JSON representation of the vector.
   * @param mtype      The type of the vector (matrix or hmatrix).
   * @return The vector of objects created from the JSON vector.
   */
  def jsonToVector(vectorJson: JsValue, mtype: String) =
    val row = vectorJson("row").as[Int]
    val cells = vectorJson("cells")

    val vector = 
      if mtype == "matrix" then
        cells.as[Seq[JsValue]].map(cell => jsonToStone(cell))
      else
        cells.as[Seq[JsValue]].map(cell => jsonToHStone(cell))
    vector.toVector

  /**
   * Converts a JSON state to a State object.
   *
   * @param stateJson The JSON representation of the state.
   * @return The State object created from the JSON state.
   */
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

  /**
   * Converts a JSON game to a GameInterface object.
   *
   * @param gameJson The JSON representation of the game.
   * @return The GameInterface object created from the JSON game.
   */
  def jsonToGame(gameJson: JsValue): GameInterface =

    val matrix = Matrix[Stone](gameJson("matrix").as[Seq[JsValue]].map(vector => jsonToVector(vector, "matrix")).toVector.asInstanceOf[Vector[Vector[Stone]]])
    val hmatrix = Matrix[HStone](gameJson("hmatrix").as[Seq[JsValue]].map(vector => jsonToVector(vector, "hmatrix")).toVector.asInstanceOf[Vector[Vector[HStone]]])
    val code = Code(jsonToVector(gameJson("code"), "matrix").asInstanceOf[Vector[Stone]])
    val turn = gameJson("turn").as[Int]
    val state = jsonToState(gameJson("state"))
    Game(new Field(matrix, hmatrix), code, turn, state)

