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
import model.GameComponent.GameBaseImpl.{HStone, HintStone, Stone}
import model.GameComponent.GameInterface
import play.api.libs.json.*
import scalafx.scene.input.KeyCode.G

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.*
import scala.util.{Failure, Success, Try}

/**
 * The UIController class handles user interaction with the game's UI and communicates with the backend.
 */
class UIController {

    val fio = new FileIO()
    var game: GameInterface = null

    /**
     * Fetches data from the API endpoint.
     *
     * @param apiEndpoint The API endpoint to fetch data from.
     */
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

    /**
     * Fetches the current game state from the backend.
     */
    def fetchGame() = {
        val endpoint = "get"
        fetchData(endpoint)
    }

    /**
     * Sends a request to the backend to undo the last move.
     */
    def undo() = {
        val endpoint = "undo"
        fetchData(endpoint)
    }

    /**
     * Sends a request to the backend to redo the last undone move.
     */
    def redo() = {
        val endpoint = "redo"
        fetchData(endpoint)
    }

    /**
     * Sends a request to the backend to load a saved game.
     */
    def load() = {
        val endpoint = "load"
        fetchData(endpoint)
    }

    /**
     * Sends a request to the backend to save the current game.
     */
    def save() = {
        implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext

        val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = "http://localhost:8080/controller/save/",
        entity = fio.gameToJson(game).toString()))
    }

    /**
     * Sends a request to the backend to reset the game.
     */
    def reset() = {
        val endpoint = "reset"
        fetchData(endpoint)
    }

    /**
     * Sends a request to the backend for a specific action.
     *
     * @param req The specific action to request.
     */
    def request(req: String) = {
        val endpoint = "request/" + req
        fetchData(endpoint)
    }

    /**
     * Sends a single character request to the backend for handling.
     *
     * @param req The single character request.
     */
    def handleSingleCharReq(req: String) = {
        val endpoint = "handleSingleCharReq/" + req
        fetchData(endpoint)
    }

    /**
     * Sends a multi-character request to the backend for handling.
     *
     * @param req The multi-character request.
     */
    def handleMultiCharReq(req: String) = {
        val splitted_req = req.split(" ")
        var action = ""
        var value = "0"

        if (splitted_req.length == 2) {
            action = splitted_req(0)
            value = splitted_req(1)
        } else {
            action = splitted_req(0)
        }

        val endpoint = "handleMultiCharReq/" + action + "/" + value
        fetchData(endpoint)
    }

    /**
     * Sends a request to the backend to place guesses and hints in the game.
     *
     * @param stoneVector The vector of stones representing the guesses.
     * @param hints       The vector of hint stones.
     * @param turn        The current turn number.
     */
    def placeGuessAndHints(stoneVector: Vector[Stone], hints: Vector[HStone], turn: Int) =
        val stoneString = stoneVector.map(stone => stone.toString).mkString("")
        val hintString = hints.map(hint => hint.toString).mkString("")
        val turnString = turn.toString

        val endpoint = "placeGuessAndHints/" + stoneString + "/" + hintString + "/" + turnString
        fetchData(endpoint)
}