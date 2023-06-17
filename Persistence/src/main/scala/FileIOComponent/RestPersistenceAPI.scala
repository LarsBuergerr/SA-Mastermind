package FileIOComponent

import FileIOComponent.fileIOJsonImpl.FileIO
import MongoDB.MongoDAO
import SlickDB.SlickDAO
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, *}
import akka.stream.ActorMaterializer
import model.GameComponent.GameBaseImpl.*
import model.GameComponent.GameInterface
import util.*
import play.api.libs.json._

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

/**
 * RestPersistenceAPI
 * This class implements an AKKA RestController API for handling game persistence operations.
 */
class RestPersistenceAPI():

  // Implicit actor system and execution context for AKKA
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  // FileIO, MongoDAO, and SlickDAO instances for handling game data
  val fileIO = new FileIO()
  val db = new MongoDAO() // SlickDAO()

  // Port number for the REST API
  val RestUIPort = 8081

  // HTML routes for the REST API
  val routes: String =
    """
        <h1>Welcome to the Mastermind REST Persistence API service!</h1>
        <h2>Available routes:</h2>

            <p><a href="persistence/save">GET  ->     persistence/save</a></p>
            <p><a href="persistence/load">GET  ->     persistence/load</a></p>
            <p><a href="persistence/dbsave">POST  ->     persistence/dbsave/test</a></p>
            <p><a href="persistence/dbload">GET  ->     persistence/dbload/1</a></p>
            <p><a href="persistence/dbloadname">GET  ->     persistence/dbloadname/test</a></p>
            <p><a href="persistence/dbupdate">POST  ->     persistence/dbupdate/1</a></p>
            <p><a href="persistence/dbdelete">POST  ->     persistence/dbdelete/1</a></p>
          <br>
        """.stripMargin

  // Routes for the REST API
  val route =
    concat(
      get {
        concat(
          pathSingleSlash {
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
          },
          path("persistence" / "load") {
            val game = fileIO.load()
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(game).toString))
          },
          path("persistence" / "dbload" / IntNumber) { num =>
            val gameFuture = db.load(Some(num))
            complete(gameFuture.map(gameOption => fileIO.gameToJson(gameOption.getOrElse(new Game())).toString).recover {
              case _ => "ERROR LOADING GAME"
            })
          },
        )
      },

      post {
        concat(
          path("persistence" / "save") {
            entity(as[String]) { saveGame =>
              val jsonGame = Json.parse(saveGame)
              val fio = new FileIO()
              val game = fio.jsonToGame(jsonGame)
              fileIO.save(game)
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
            }
          },
          path("persistence" / "dbsave" / Segment) { save_name =>
            entity(as[String]) { saveGame =>
              val jsonGame = Json.parse(saveGame)
              val fio = new FileIO()
              val game = fio.jsonToGame(jsonGame)
              db.save(game, save_name)
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
            }
          },
          path("persistence" / "dbupdate" / Segment) { id =>
            entity(as[String]) { saveGame =>
              val jsonGame = Json.parse(saveGame)
              val fio = new FileIO()
              val game = fio.jsonToGame(jsonGame)
              db.update(game, id.toInt)
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
            }
          },
          path("persistence" / "dblist") {
            entity(as[String]) { saveGame =>
              db.listAllGames()
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
            }
          },
          path("persistence" / "dbdelete" / Segment) { id =>
            entity(as[String]) { game =>
              db.delete(id.toInt)
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
            }
          }
        )
      }
    )

  // Start the REST API server
  def start(): Unit = {
    val binding = Http().newServerAt("persistence_service", RestUIPort).bind(route)

    binding.onComplete {
      case Success(binding) => {
        println(s"Mastermind PersistenceAPI service online at http://persistence_service:$RestUIPort/")
      }
      case Failure(exception) => {
        println(s"Mastermind PersistenceAPI service failed to start: ${exception.getMessage}")
      }
    }
  }
