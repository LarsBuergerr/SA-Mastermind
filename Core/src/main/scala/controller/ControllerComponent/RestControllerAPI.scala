package controller.ControllerComponent

import FileIOComponent.fileIOJsonImpl.FileIO
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, *}
import akka.protobufv3.internal.compiler.PluginProtos.CodeGeneratorResponse.File
import akka.stream.ActorMaterializer
import controller.ControllerComponent.ControllerInterface
import model.GameComponent.GameBaseImpl.{HStone, HintStone, PlayerInput, Stone}
import model.GameComponent.GameInterface
import util.*
import play.api.libs.json.*

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

/**
 * The RestControllerAPI class is responsible for handling the REST API endpoints for the Mastermind game.
 *
 * @param controller The controller interface implementation to interact with the game.
 */
class RestControllerAPI(using controller: ControllerInterface):

  // Create the actor system and execution context for handling HTTP requests
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val RestUIPort = 8080
  // HTML routes for different actions and endpoints
  val routes: String =
    """
        <h1>Welcome to the Mastermind REST Controller API service!</h1>
        <h2>Available routes:</h2>

          <h3>Game Actions:</h3>
            <p><a href="controller/tui">Go to the Web TUI</a></p>
            <p><a href="controller/tuiJSON">JSON presentation of the Game</a></p>
            <p><a href="controller/placeGuessAndHints/rrrr/wwww/0">placeGuessAndHints</a></p>
            <p><a href="controller/handleSingleCharReq/h">handleSingleCharReq</a></p>
            <p><a href="controller/handleMultiCharReq/rrrr/wwww/0">handleMultiCharReq</a></p>

          <h3>JSON Actions:</h3>
            <p><a href="controller/save">Game JSON save</a></p>
            <p><a href="controller/load">Game JSON load</a></p>
            <p><a href="controller/undo">Game undo</a></p>
            <p><a href="controller/redo">Game redo</a></p>
            <p><a href="controller/reset">Game reset</a></p>

          <h3>DB Actions:</h3>
            <p><a href="controller/handleMultiCharReq/dbsave/1"> Save the game to the DB</a></p>
            <p><a href="controller/handleMultiCharReq/dbsave/DBSaveTestName"> Save the game to the DB whit Name</a></p>
            <p><a href="controller/handleMultiCharReq/dbload/1"> Load the game from the DB by ID</a></p>
            <p><a href="controller/handleMultiCharReq/dbloadname/DBSaveTestName"> Load the game from the DB by Name</a></p>
            <p><a href="controller/handleMultiCharReq/dblist/1"> List all DB Saves</a></p>
            <p><a href="controller/handleMultiCharReq/dbupdate/1"> update the game save in the db with id 1</a></p>
            <p><a href="controller/handleMultiCharReq/dbdelete/1"> delete the game save in the db with id 1</a></p>
        """.stripMargin
  // Define the main route for handling HTTP requests
  val route = get {
    concat(
      // Handle root path request
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
      },
      // Handle TUI path request
      path("controller" / "tui") {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, HTMLGameboardString))
      },
      // Handle TUI JSON path request
      path("controller" / "tuiJSON") {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Mastermind\n\n" + controller.gameToJson(controller.game)))
      },
      // Handle request path with command parameter
      path("controller" / "request" / Segment) { command => {
        val event =
          command match {
            case "init" => InitStateEvent()
            case "menu" => MenuStateEvent()
            case "play" => PlayStateEvent()
            case "quit" => QuitStateEvent()
            case "help" => HelpStateEvent()

            case "pInp" => PlayerInputStateEvent()
            case "pLos" => PlayerLoseStateEvent()
            case "pWin" => PlayerWinStateEvent()
            case "pAna" => PlayerAnalyzeEvent()
            case _ => HelpStateEvent()
          }
        controller.request(event)
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
        }
      },
      // Handle single character request path
      path("controller" / "handleSingleCharReq" / Segment) { str => {
        str.size match
          case 0 => // Handles no user input -> stay in current state
            controller.request(controller.handleRequest(SingleCharRequest(" ")))
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))

          case 1 => //Handles single char user input (first with CoR, then with State Pattern)
            val currentRequest = controller.handleRequest(SingleCharRequest(str))
            currentRequest match
              case undo: UndoStateEvent =>
                controller.undo
                controller.request(PlayerInputStateEvent())

              case redo: RedoStateEvent =>
                controller.redo
                controller.request(PlayerInputStateEvent())

              case save: SaveStateEvent =>
                controller.save(controller.game)
                controller.request(PlayerInputStateEvent())

              case load: LoadStateEvent =>
                controller.load
                controller.request(PlayerInputStateEvent())

              case _ =>
                controller.request(currentRequest)

            //controller.request(controller.handleRequest(SingleCharRequest(str)))
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
        }
      },
      // Handle multi-character request path
      path("controller" / "handleMultiCharReq" / Segments) { input => {
        val action = input(0)
        val num = input(1)

        if (action == "dbload") then
          controller.dbload(num.toInt)
          controller.request(PlayerInputStateEvent())
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
        else if (action == "dbloadname") then
          controller.dbloadname(num)
          controller.request(PlayerInputStateEvent())
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
        else if (action == "dbsave") then
          controller.dbsave(controller.game, num)
          controller.request(PlayerInputStateEvent())
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
        else if (action == "dblist") then
          controller.dblist
          controller.request(PlayerInputStateEvent())
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, (controller.dblist).toString))
        else if (action == "dbupdate") then
          controller.dbupdate(controller.game, num.toInt)
          controller.request(PlayerInputStateEvent())
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
        else if (action == "dbdelete") then
          controller.dbdelete(num.toInt)
          controller.request(PlayerInputStateEvent())
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
        else
          val emptyVector: Vector[Stone] = Vector()
          val currentRequest = controller.handleRequest(MultiCharRequest(action))
          if (currentRequest.isInstanceOf[PlayerAnalyzeEvent]) then

            val codeVector: Vector[Stone] =
              Try(controller.game.buildVector(emptyVector)(action.toCharArray())) match
                case Success(vector) => vector.asInstanceOf[Vector[Stone]]
                case Failure(e) =>
                  controller.request(controller.game.getDefaultInputRule(action))
                  Vector.empty[Stone]

            val hints = controller.game.getCode().compareTo(codeVector)
            controller.placeGuessAndHints(codeVector)(hints)(controller.game.currentTurn)
            if hints.forall(p => p.stringRepresentation.equals("R")) then
              controller.request(PlayerWinStateEvent())
            else if (controller.game.field.matrix.rows - controller.game.currentTurn) == 0 then
              controller.reset
              controller.request(PlayerLoseStateEvent())
            else
              controller.request(PlayerInputStateEvent())
          else //Invalid input -> stay in current state
            controller.request(currentRequest)
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
              controller.gameToJson(controller.game)
              //HTMLGameboardString
            ))
        }
      },

      path("controller" / "htmlGameboard" / Segment) { stones => {
        val emptyVector: Vector[Stone] = Vector()
        val currentRequest = controller.handleRequest(MultiCharRequest(stones))
        if (currentRequest.isInstanceOf[PlayerAnalyzeEvent]) then

          val codeVector: Vector[Stone] =
            Try(controller.game.buildVector(emptyVector)(stones.toCharArray())) match
              case Success(vector) => vector.asInstanceOf[Vector[Stone]]
              case Failure(e) =>
                controller.request(controller.game.getDefaultInputRule(stones))
                Vector.empty[Stone]

          val hints = controller.game.getCode().compareTo(codeVector)
          controller.placeGuessAndHints(codeVector)(hints)(controller.game.currentTurn)
          if hints.forall(p => p.stringRepresentation.equals("R")) then
            controller.request(PlayerWinStateEvent())
          else if (controller.game.field.matrix.rows - controller.game.currentTurn) == 0 then
            controller.reset
            controller.request(PlayerLoseStateEvent())
          else
            controller.request(PlayerInputStateEvent())
        else //Invalid input -> stay in current state
          controller.request(currentRequest)
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
            HTMLGameboardString
          ))
        }
      },

      path("controller" / "placeGuessAndHints" / Segments) { command => {
        //make Vector of Stones
        val stonesVector = command(0).split("").toVector.map(stone => Stone(stone))
        val hintsVector = command(1).split("").toVector.map(hint => HintStone(hint))
        val turn = command(2).toInt

        controller.placeGuessAndHints(stonesVector)(hintsVector)(turn)
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
        }
      },

      path("controller" / "get") {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
      },

      path("controller" / "redo") {
        controller.redo
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
      },

      path("controller" / "undo") {
        controller.undo
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
      },

      path("controller" / "load") {
        controller.game = controller.load
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
      },

      path("controller" / "reset") {
        controller.reset
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
      },

      // POST route for saving the game state as JSON
      path("controller" / "save") {
        post {
          entity(as[String]) { saveGame =>
            //turn String to Json
            val jsonGame = Json.parse(saveGame)
            //turn Json to Game
            val fio = new FileIO()
            val game = fio.jsonToGame(jsonGame)
            controller.save(game)
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
          }
        }
      },
    )
  }
  // Starts the HTTP server
  def start(): Unit = {
    val binding = Http().newServerAt("0.0.0.0", RestUIPort).bind(route)

    binding.onComplete {
      case Success(binding) =>
        println(s"Mastermind ControllerAPI service online at http://localhost:$RestUIPort/")
      case Failure(exception) =>
        println(s"Mastermind ControllerAPI service failed to start: ${exception.getMessage}")
    }
  }

  /**
   * Generates an HTML string representing the game board.
   *
   * @return The HTML string of the game board.
   */
  private def HTMLGameboardString: String = {
    "<h2>Mastermind</h2><br>"
      + formatGameBoard(colorizeLetters(controller.game.field.toString()))
      + "<br>"
      + "Remaining Turns: " + (10 - controller.game.currentTurn).toString
      + "<br>"
      + "<form> <input type=\"text\" id=\"inputValue\">"
      + "<input type=\"button\" value=\"Send\" onclick=\"redirectToURI()\"> </form>"
      + "<script>"
      + "function redirectToURI() {var inputValue = document.getElementById('inputValue').value;"
      + "var url = '/controller/htmlGameboard/' + inputValue; window.location.href = url; }"
      + "</script>"

  }

  /**
   * Formats the game board string by adding HTML tags for proper display.
   *
   * @param gameBoard The game board string to be formatted.
   * @return The formatted game board string.
   */
  private def formatGameBoard(gameBoard: String): String = {
    val gameBoardRows = gameBoard.split("\n") // Split into rows
    val formattedGameBoard = gameBoardRows.map { row =>
      if (row.startsWith("|")) {
        val cells = row.split("\\|").map(_.trim) // Split into cells
        val formattedCells = cells.map(cell => s" <span>$cell</span> ") // Add span tags
        formattedCells.mkString("|", "|", "|") // Join cells back together
      } else {
        row // Keep unchanged row
      }
    }
    formattedGameBoard.mkString("<br>") // Join formatted text back together with line breaks
  }

  /**
   * Colorizes the letters in the given text using predefined colors.
   *
   * @param text The text to be colorized.
   * @return The colorized text with HTML span tags.
   */
  private def colorizeLetters(text: String): String = {
    val colorMap = Map(
      'R' -> "red",
      'G' -> "green",
      'B' -> "blue",
      'Y' -> "yellow",
      'W' -> "darkgray",
      'P' -> "purple"
    )
    // Colorize each letter using the color mapping
    val coloredText = text.map { letter =>
      val color = colorMap.getOrElse(letter, "black")
      s"<span style='color: $color;'>$letter</span>"
    }

    coloredText.mkString
  }
//TODO observer fehlt? bzw. keine aktualisierung in der tui bei uri eingabe
