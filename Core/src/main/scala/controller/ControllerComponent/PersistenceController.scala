package controller.ControllerComponent

package aview

import scala.concurrent.Future
import akka.actor.typed.{ActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCode, HttpMethods, HttpResponse, HttpRequest}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import scala.util.{Try, Success, Failure}
import play.api.libs.json.*
import akka.http.scaladsl.unmarshalling.Unmarshal
import scala.concurrent.duration._
import scala.concurrent.Await

import FileIOComponent.fileIOJsonImpl.FileIO
import model.GameComponent.GameInterface
import model.GameComponent.GameBaseImpl.Game
import model.GameComponent.GameBaseImpl.{Stone, HintStone, HStone}
import scalafx.scene.input.KeyCode.G
import akka.stream.ActorMaterializer
import _root_.util.Event
import akka.http.javadsl.model.StatusCodes


class PersistenceController {

    val fio = new FileIO()
    var game: GameInterface = new Game()

    def fetchData(apiEndpoint: String) = {

        implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext

        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://persistence_service:8081/persistence/" + apiEndpoint))


        val res = responseFuture.flatMap { response =>
        response.status match {
            case StatusCodes.OK =>
            Unmarshal(response.entity).to[String].map { jsonStr =>
                val loadedGame = Json.parse(jsonStr)
                this.game = fio.jsonToGame(loadedGame)
            }
            case _ =>
            Future.failed(new RuntimeException(s"HTTP request to Persistence API failed with status ${response.status} and entity ${response.entity}"))
            }
        }
        // Wait for the future to complete and get the result
        Await.result(res, 10.seconds)
    }

    def load() = {
        val endpoint = "load"
        fetchData(endpoint)
    }


    def save(game: GameInterface) = {
       implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext

        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = "http://persistence_service:8081/persistence/save",
        entity = fio.gameToJson(game).toString()))
    }

    def dbload() = {
        val endpoint = "dbload"
        fetchData(endpoint)
    }

    def dbsave(game: GameInterface) = {
        implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext

        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = "http://persistence_service:8081/persistence/dbsave",
        entity = fio.gameToJson(game).toString()))
    }

    def dblist() = {
        try {
            implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
            implicit val executionContext = system.executionContext

            val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
            method = HttpMethods.POST,
            uri = "http://persistence_service:8081/persistence/dblist",
            entity = fio.gameToJson(game).toString()))
        } catch {
            case e: Exception => print(e.printStackTrace())
        }
    }
}