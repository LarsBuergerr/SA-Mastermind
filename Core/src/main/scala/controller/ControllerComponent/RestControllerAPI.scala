/**
 * RootSevice.scala
 *  implementation for AKKA RestControllerAPI
 */

//****************************************************************************** PACKAGE
package controller.ControllerComponent

//****************************************************************************** IMPORTS

import FileIOComponent.fileIOyamlImpl.FileIO
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, *}
import akka.stream.ActorMaterializer
import controller.ControllerComponent.ControllerInterface
import model.GameComponent.GameBaseImpl.PlayerInput
import util.{MultiCharRequest, Observer}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

//****************************************************************************** CLASS DEFINITION
class RestControllerAPI(using controller: ControllerInterface):
  //controller.add(this)

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val RestUIPort = 8081
  val routes: String =
    """
        <h1>Welcome to the Mastermind REST Controller API service!</h1>
        <h2>Available routes:</h2>
          <p><a href="controller/tui">GET ->    controller/tui</a></p>
          <p><a href="controller/load">GET  ->   controller/load</a></p>
            <p><a href="controller/undo">GET  ->    controllerundo</a></p>
            <p><a href="controller/redo">GET  ->    controller"/redo</a></p>
            <p><a href="controller/reset">GET ->     controller"/reset</a></p>
          <br>
        <p><a href=""controller"/ /save">POST ->     controller/save</a></p>
        """.stripMargin

  val route =
    concat(
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
      },
      //Todo
      path("controller"/ "request"/ Segment) { command => {

        controller.request(controller.handleRequest(MultiCharRequest(command)))
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, command))
      }
      },
      //Todo
      path("controller"/ "handleRequest" / Segment) { command => {

        controller.request(controller.handleRequest(MultiCharRequest(command)))
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, command))
      }
      },
      //Todo
      path("controller"/ "placeGuessAndHints" / Segment) { command => {

        controller.request(controller.handleRequest(MultiCharRequest(command)))
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, command))
      }
      },
      get {
        path("controller"/ "redo") {
          controller.redo
          complete(HttpEntity(ContentTypes.`application/json`, "Your move has been done again"))
        }
      },
      get {
        path("controller"/ "undo") {
          controller.undo
          complete(HttpEntity(ContentTypes.`application/json`, "Your move has been undone"))
        }
      },
      //post maps create
      //TODO
      post {
        path("controller"/ "save") {
          entity(as[String]) { game =>
            //controller.save
            val fileIO = new FileIO()
            fileIO.save(controller.game)
            complete(HttpEntity(ContentTypes.`application/json`, "Your Game has been saved"))
          }
        }
      },
      get {
        path("controller"/ "load") {
          controller.load
          complete(HttpEntity(ContentTypes.`application/json`, "Your save has been loaded"))
        }
      },
      get {
        path("controller"/ "reset") {
          controller.reset
          complete(HttpEntity(ContentTypes.`application/json`, "Your reset your game"))
        }
      },
    )

  def start(): Unit = {
    val binding = Http().newServerAt("localhost", RestUIPort).bind(route)

    binding.onComplete {
      case Success(binding) => {
        println(s"Mastermind ControlerAPI service online at http://localhost:$RestUIPort/")
      }
      case Failure(exception) => {
        println(s"Mastermind ControlerAPI service failed to start: ${exception.getMessage}")
      }
    }
  }
