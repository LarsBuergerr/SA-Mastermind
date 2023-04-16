/**
 * RestUI.scala
 * Class for the REST Akaa HTTP Server Interface of the Mastermind game.
 */

//****************************************************************************** PACKAGE
package aview

//****************************************************************************** IMPORTS
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Success,Failure}

//****************************************************************************** CLASS DEFINITION
class RestUI {

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  val RestUIPort = 8080
  val routes: String =
    """
        <h1>Welcome to the Persistence REST service!</h1>
        <br>Available routes:
          <br>GET   /fileio/load
          <br>POST  /fileio/save
        """.stripMargin


  val route =
    concat(
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
      },
      get {
        path("") {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>start/h1>"))
        }
        path("hello") {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-httpX</h1>"))
        }

      },/*
      post {

        path("save") {

          controller.saveGame()
          complete(HttpEntity(ContentTypes.`application/json`, "saved"))

        }

      }
        */
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
}