/**
 * RestUI.scala
 * Class for the REST Akaa HTTP Server Interface of the Mastermind game.
 */

//****************************************************************************** PACKAGE
package aview

//****************************************************************************** IMPORTS
import FileIOComponent.fileIOyamlImpl.FileIO
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import controller.ControllerComponent.ControllerInterface
import util.Observer

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

//****************************************************************************** CLASS DEFINITION
class RestUI(using controller: ControllerInterface):
  //controller.add(this)

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val RestUIPort = 8080
  val routes: String =
    """
        <h1>Welcome to the Persistence REST service!</h1>
        <br>Available routes:
          <br>GET   /load
          <br>POST  /save
        """.stripMargin


  val route =
    concat(
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
      },
      get {
        path("hello") {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-httpX</h1>"))
        }
        path("load") {
          controller.load
          complete(HttpEntity(ContentTypes.`application/json`, "loaded"))
        }

      },
      post {
        path("save") {
          val fileIO = new FileIO()
          fileIO.save(controller.game)
          complete(HttpEntity(ContentTypes.`application/json`, "saved"))
        }
      }
    )
  def start(): Unit = {
    val binding = Http().newServerAt("localhost", RestUIPort).bind(route)

    binding.onComplete {
      case Success(binding) => {
        println(s"Server online at http://localhost:$RestUIPort/")
      }
      case Failure(exception) => {
        println(s"Server failed to start: ${exception.getMessage}")
      }
    }
  }
