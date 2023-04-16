import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}

import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.actor.typed.scaladsl.Behaviors
class RestUI {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val route =
    path("hello") {
      get {
        complete("Hello, world!")
      }
    }

  def start(): Future[Http.ServerBinding] =
    Http().newServerAt("localhost", 8080).bind(route)
}

@main def startRestApi(): Unit = {
  val restApi = new RestUI()
  restApi.start()
  println(s"Server online at http://localhost:8080/")
}
