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
import model.GameComponent.GameBaseImpl.{Stone, HintStone, HStone}
import scalafx.scene.input.KeyCode.G
import akka.stream.ActorMaterializer
import _root_.util.Event
import akka.http.javadsl.model.StatusCodes


class UIController {

    val fio = new FileIO()
    var game: GameInterface = null

    def fetchData(apiEndpoint: String) = {

        implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext

        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://localhost:8080/controller/" + apiEndpoint))
        val res = responseFuture.flatMap { response =>
        response.status match {
            case StatusCodes.OK =>
            Unmarshal(response.entity).to[String].map { jsonStr =>
                val loadedGame = Json.parse(jsonStr)
                this.game = fio.jsonToGame(loadedGame)
            }
            case _ =>
            Future.failed(new RuntimeException(s"HTTP request to Controller API failed with status ${response.status} and entity ${response.entity}"))
            }
        }
        // Wait for the future to complete and get the result
        Await.result(res, 10.seconds)
    }

    def fetchGame() = {
        val endpoint = "get"
        fetchData(endpoint)
    }

    def undo() = {
        val endpoint = "undo"
        fetchData(endpoint)
    }

    def redo() = {
        val endpoint = "redo"
        fetchData(endpoint)
    }

    def load() = {
        val endpoint = "load"
        fetchData(endpoint)
    }
    def save() = {
        implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext

        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = "http://localhost:8080/controller/save",
        entity = fio.gameToJson(game).toString()))
    }

    def reset() = {
        val endpoint = "reset"
        fetchData(endpoint)
    }

    def request(req: String) = {
        val endpoint = "request/" + req
        fetchData(endpoint)
    }

    def handleSingleCharReq(req: String) = {
        val endpoint = "handleSingleCharReq/" + req
        fetchData(endpoint)
    }

    def handleMultiCharReq(req: String) = {
        val endpoint = "handleMultiCharReq/" + req
        fetchData(endpoint)
    }

    def placeGuessAndHints(stoneVector: Vector[Stone], hints: Vector[HStone], turn: Int) =
        val stoneString = stoneVector.map(stone => stone.toString).mkString("")
        val hintString = hints.map(hint => hint.toString).mkString("")
        val turnString = turn.toString

        val endpoint = "placeGuessAndHints/" + stoneString + "/" + hintString + "/" + turnString
        fetchData(endpoint)
}