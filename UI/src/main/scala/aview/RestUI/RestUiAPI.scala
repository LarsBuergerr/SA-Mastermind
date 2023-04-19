/**
 * RestUiAPI.scala
 * Class for the REST Akaa HTTP Server Interface of the Mastermind game.
 */

//****************************************************************************** PACKAGE
package aview.RestUI

//****************************************************************************** IMPORTS
import FileIOComponent.fileIOyamlImpl.FileIO
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Directives.{entity, *}
import akka.stream.ActorMaterializer
import controller.ControllerComponent.ControllerInterface
import model.GameComponent.GameBaseImpl.PlayerInput
import util.{MultiCharRequest, Observer}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

//****************************************************************************** CLASS DEFINITION
class RestUiAPI(using controller: ControllerInterface):
  //controller.add(this)

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val RestUIPort = 8081
  val routes: String =
    """
        <h1>Welcome to the Mastermind Game REST service!</h1>
        <h2>Available routes:</h2>
          <p><a href="/tui">GET   /tui</a></p>
          <p><a href="/load">GET   /load</a></p>
            <p><a href="/undo">GET   /undo</a></p>
            <p><a href="/redo">GET   /redo</a></p>
            <p><a href="/reset">GET   /reset</a></p>
          <br>
        <p><a href="/save">POST   /save</a></p>
        """.stripMargin

  val route =
    concat(
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
      },
      //get maps read
      get {
        path("hello") {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      },
      get {
        path("tui") {
          complete(HttpEntity(ContentTypes.`application/json`, "Mastermind\n\n" +controller.game.field.toString()))
        }
      },
      get {
        path("load") {
          controller.load
          complete(HttpEntity(ContentTypes.`application/json`, "Your save has been loaded"))
        }
      },
      get {
        path("undo") {
          controller.undo
          complete(HttpEntity(ContentTypes.`application/json`, "Your move has been undone"))
        }
      },
      get {
        path("redo") {
          controller.redo
          complete(HttpEntity(ContentTypes.`application/json`, "Your move has been done again"))
        }
      },
      get {
        path("reset") {
          controller.reset
          complete(HttpEntity(ContentTypes.`application/json`, "Your reset your game"))
        }
      },
      //user input via url
      path("input" / Segment) { command => {

        var x = controller.request(controller.handleRequest(MultiCharRequest(command)))
        println(x)
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, command))
      }
      },
      path("state" / Segment) { command => {

        var x = controller.request(controller.handleRequest(MultiCharRequest(command)))
        println(x)
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, command))
      }
      },
      path("api" / "myResource") {
        post {
          entity(as[String]) { data =>

            println(s"Post-Body als JSON-Text: $data")
            complete(HttpResponse(StatusCodes.OK, entity  = "POST erfolgreich verarbeitet"))
          }
        }
      },
      //post maps create
      //TODO
      post {
        path("save") {
          entity(as[String]) { game =>
            //controller.save
            val fileIO = new FileIO()
            fileIO.save(controller.game)
            complete(HttpEntity(ContentTypes.`application/json`, "Your Game has been saved"))
          }
        }
      }
    )

  def start(): Unit = {
    val binding = Http().newServerAt("localhost", RestUIPort).bind(route)

    binding.onComplete {
      case Success(binding) => {
        println(s"Mastermind UiAPI service online at http://localhost:$RestUIPort/")
      }
      case Failure(exception) => {
        println(s"Mastermind UiAPI service failed to start: ${exception.getMessage}")
      }
    }
  }
