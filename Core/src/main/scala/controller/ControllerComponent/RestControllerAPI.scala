/**
 * RootSevice.scala
 *  implementation for AKKA RestControllerAPI
 */

//****************************************************************************** PACKAGE
package controller.ControllerComponent

//****************************************************************************** IMPORTS

import FileIOComponent.fileIOJsonImpl.FileIO
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, *}
import akka.stream.ActorMaterializer
import controller.ControllerComponent.ControllerInterface
import model.GameComponent.GameBaseImpl.PlayerInput
import util.{MultiCharRequest, Observer}
import util._
import model.GameComponent.GameInterface
import model.GameComponent.GameBaseImpl.{Stone, HintStone, HStone}
import FileIOComponent.fileIOJsonImpl.FileIO

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import akka.protobufv3.internal.compiler.PluginProtos.CodeGeneratorResponse.File
import play.api.libs.json.*
import scala.util.{Try, Success, Failure}


//****************************************************************************** CLASS DEFINITION
class RestControllerAPI(using controller: ControllerInterface):

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val RestUIPort = 8080
  val routes: String =
    """
        <h1>Welcome to the Mastermind REST Controller API service!</h1>
        <h2>Available routes:</h2>
          <p><a href="controller/tui">GET ->    controller/tui</a></p>
          <p><a href="controller/load">GET  ->   controller/load</a></p>
            <p><a href="controller/undo">GET  ->    controller/undo</a></p>
            <p><a href="controller/redo">GET  ->    controller/redo</a></p>
            <p><a href="controller/reset">GET ->     controller/reset</a></p>
            <p><a href="controller/save">GET  ->     controller/save</a></p>
            <p><a href="controller/get">GET  ->     controller/get</a></p>
            <p><a href="controller/placeGuessAndHints">GET ->    controller/placeGuessAndHints</a></p>
            <p><a href="controller/handleSingleCharReq">GET ->    controller/handleSingleCharReq</a></p>
            <p><a href="controller/handleMultiCharReq">GET ->    controller/handleMultiCharReq</a></p>

          <br>
        <p><a href=""controller"/ /save">POST ->     controller/save</a></p>
        """.stripMargin

  val route =
    concat(
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
      },
      //Todo
      path("controller"/ "tui") {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Mastermind\n\n" +controller.game.field.toString()))
      },
      get {
        path("controller"/ "request"/ Segment) { command => {
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
        }
      },
      get {
        path("controller"/ "handleSingleCharReq" / Segment) { str => { 
        str.size match
          case 0 =>  // Handles no user input -> stay in current state
            controller.request(controller.handleRequest(SingleCharRequest(" ")))
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))

          case 1 =>  //Handles single char user input (first with CoR, then with State Pattern)
            val currentRequest = controller.handleRequest(SingleCharRequest(str))
              currentRequest match
              case undo: UndoStateEvent  =>
                controller.undo
                controller.request(PlayerInputStateEvent())

              case redo: RedoStateEvent  =>
                controller.redo
                controller.request(PlayerInputStateEvent())

              case save: SaveStateEvent  =>
                controller.save(controller.game)
                controller.request(PlayerInputStateEvent())

              case load: LoadStateEvent  =>
                controller.load
                controller.request(PlayerInputStateEvent())

              case dbsave: DBSaveStateEvent  =>
                controller.dbsave(controller.game)
                controller.request(PlayerInputStateEvent())

              case dbload: DBLoadStateEvent  =>
                controller.dbload
                controller.request(PlayerInputStateEvent())

              case _ => 
                controller.request(currentRequest)
                
            //controller.request(controller.handleRequest(SingleCharRequest(str)))
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
          }
        }
      },
      get {
        path("controller"/ "handleMultiCharReq" / Segment) { str => { 
          val emptyVector: Vector[Stone] = Vector()
          val currentRequest = controller.handleRequest(MultiCharRequest(str))
          if(currentRequest.isInstanceOf[PlayerAnalyzeEvent]) then

            val codeVector: Vector[Stone] =
              Try(controller.game.buildVector(emptyVector)(str.toCharArray())) match
                case Success(vector) => vector.asInstanceOf[Vector[Stone]]
                case Failure(e) =>
                  controller.request(controller.game.getDefaultInputRule(str))
                  Vector.empty[Stone]

            val hints = controller.game.getCode().compareTo(codeVector)
            controller.placeGuessAndHints(codeVector)(hints)(controller.game.currentTurn)
            if hints.forall(p => p.stringRepresentation.equals("R")) then
              controller.request(PlayerWinStateEvent())
            else if (controller.game.field.matrix.rows - controller.game.currentTurn) == 0 then
              controller.request(PlayerLoseStateEvent())
            else
              controller.request(PlayerInputStateEvent())
          else  //Invalid input -> stay in current state
            controller.request(currentRequest)
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
          }
        }
      },
      get {
        path("controller"/ "placeGuessAndHints" / Segments) { command => {
          //make Vector of Stones
          val stonesVector = command(0).split("").toVector.map(stone => Stone(stone))
          val hintsVector = command(1).split("").toVector.map(hint => HintStone(hint))
          val turn = command(2).toInt

          controller.placeGuessAndHints(stonesVector)(hintsVector)(turn)
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
          }
        }
      },
      get {
        path("controller"/ "get") {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
        }
      },
      get {
        path("controller"/ "redo") {
          controller.redo
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
        }
      },
      get {
        path("controller"/ "undo") {
          controller.undo
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
        }
      },
      //post maps create
      //TODO
      path("controller"/ "save") {
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
      get {
        path("controller"/ "load") {
          controller.game = controller.load
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
        }
      },
      get {
        path("controller"/ "reset") {
          controller.reset
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
        }
      },
    )

  def start(): Unit = {
    val binding = Http().newServerAt("0.0.0.0", RestUIPort).bind(route)

    binding.onComplete {
      case Success(binding) => {
        println(s"Mastermind ControllerAPI service online at http://localhost:$RestUIPort/")
      }
      case Failure(exception) => {
        println(s"Mastermind ControllerAPI service failed to start: ${exception.getMessage}")
      }
    }
  }