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
import model.GameComponent.GameInterface

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import akka.protobufv3.internal.compiler.PluginProtos.CodeGeneratorResponse.File
import play.api.libs.json.*

//****************************************************************************** CLASS DEFINITION
class RestControllerAPI(using controller: ControllerInterface):

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  //print("before redo")
  //print(controller.game)
  //print("after redo")

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
        path("controller"/ "get") {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(controller.game)))
        }
      },
      get {
        path("controller"/ "redo") {
          val game = controller.redo
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, controller.gameToJson(game)))
        }
      },
      get {
        path("controller"/ "undo") {
          print(controller.undo)
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Du kleiner HS"))
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
        println(s"Mastermind ControllerAPI service online at http://localhost:$RestUIPort/")
      }
      case Failure(exception) => {
        println(s"Mastermind ControllerAPI service failed to start: ${exception.getMessage}")
      }
    }
  }
