package controller.ControllerComponent

package aview

import FileIOComponent.fileIOJsonImpl.FileIO
import _root_.util.Event
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import model.GameComponent.GameBaseImpl.{Game, HStone, HintStone, Stone}
import model.GameComponent.GameInterface
import play.api.libs.json.*
import scalafx.scene.input.KeyCode.G

import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}


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

    def dbload(num: Int) = {
        val endpoint = "dbload/" + num.toString()
        fetchData(endpoint)
    }

    def dbloadByName(name: String) = {
        val endpoint = "dbloadname/" + name.toString()
        fetchData(endpoint)
    }

    def dbsave(game: GameInterface, save_name: String) = {
        implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext

        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = "http://persistence_service:8081/persistence/dbsave/" + save_name,
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
    
    def dbupdate(game: GameInterface, id: Int) = {
        implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext

        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = "http://persistence_service:8081/persistence/dbupdate/" + id.toString(),
        entity = fio.gameToJson(game).toString())
        )
    }
    
    def dbdelete(id: Int) = {
        implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext

        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = "http://persistence_service:8081/persistence/dbdelete/" + id.toString(),
        entity = fio.gameToJson(game).toString())
        )
    }
}