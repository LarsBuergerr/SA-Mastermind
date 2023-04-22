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

import FileIOComponent.fileIOJsonImpl.FileIO
import model.GameComponent.GameInterface
import model.GameComponent.GameBaseImpl.{Stone, HintStone, HStone}
import scalafx.scene.input.KeyCode.G
import akka.stream.ActorMaterializer
import _root_.util.Event


class UIController {

    val fio = new FileIO()
    var game: GameInterface = null

    def fetchData(apiEndpoint: String) = {

        implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext
        val responseFuture = Http().singleRequest(HttpRequest(uri = "http://localhost:8080/controller/" + apiEndpoint))

        responseFuture
        .onComplete {
            case Failure(_) => sys.error("Failed getting Json")
            case Success(value) => {
            Unmarshal(value.entity).to[String].onComplete {
                case Failure(_) => sys.error("Failed unmarshalling")
                case Success(value) => {
                    val loadedGame = Json.parse(value)
                    this.game = fio.JsonToGame(loadedGame)
                    }
                }
            }
        }
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

    def reset() = {
        val endpoint = "reset"
        fetchData(endpoint)
    }

    def request(req: String) = {
        val endpoint = "request/" + req
        fetchData(endpoint)
    }

    def placeGuessAndHints(stoneVector: Vector[Stone], hints: Vector[HStone], turn: Int) =
        val stoneString = stoneVector.map(stone => stone.toString).mkString("")
        val hintString = hints.map(hint => hint.toString).mkString("")
        val turnString = turn.toString

        val endpoint = "placeGuessAndHints/" + stoneString + "/" + hintString + "/" + turnString
        fetchData(endpoint)
}