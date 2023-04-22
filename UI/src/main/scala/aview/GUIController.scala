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
import scalafx.scene.input.KeyCode.G
import akka.stream.ActorMaterializer


class GUIController {

    val fio = new FileIO()
    var game: GameInterface = null

    def fetchGame() = {
        implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext
        val responseFuture = Http().singleRequest(HttpRequest(uri = "http://localhost:8080/controller/get"))

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

    def undo() = {
        implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext
        val responseFuture = Http().singleRequest(HttpRequest(uri = "http://localhost:8080/controller/undo"))

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

    def redo() = {
        implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext
        val responseFuture = Http().singleRequest(HttpRequest(uri = "http://localhost:8080/controller/redo"))

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

    def load() = {
        implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext
        val responseFuture = Http().singleRequest(HttpRequest(uri = "http://localhost:8080/controller/load"))

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

    def reset() = {
        implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
        implicit val executionContext = system.executionContext
        val responseFuture = Http().singleRequest(HttpRequest(uri = "http://localhost:8080/controller/reset"))

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
}