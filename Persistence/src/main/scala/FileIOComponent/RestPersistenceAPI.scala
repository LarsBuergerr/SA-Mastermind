

/**
 * RootSevice.scala
 *  implementation for AKKA RestControllerAPI
 */

//****************************************************************************** PACKAGE

package FileIOComponent

//****************************************************************************** IMPORTS

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, *}
import akka.stream.ActorMaterializer
import model.GameComponent.GameBaseImpl.PlayerInput
import util.{MultiCharRequest, Observer}
import util._
import model.GameComponent.GameInterface
import model.GameComponent.GameBaseImpl.{Stone, HintStone, HStone}
import FileIOComponent.fileIOJsonImpl.FileIO
import SlickDB.SlickDAO

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import akka.protobufv3.internal.compiler.PluginProtos.CodeGeneratorResponse.File
import play.api.libs.json.*
import scala.util.{Try, Success, Failure}


//****************************************************************************** CLASS DEFINITION
class RestPersistenceAPI():

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val fileIO = new FileIO()
  val slickDAO = new SlickDAO() 
  val RestUIPort = 8081
  val routes: String =
    """
        <h1>Welcome to the Mastermind REST Persistence API service!</h1>
        <h2>Available routes:</h2>

            <p><a href="persistence/save">GET  ->     persistence/save</a></p>
            <p><a href="persistence/load">GET  ->     persistence/load</a></p>


          <br>
        """.stripMargin

  val route =
    concat(
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
      },
      //post maps create
      //TODO
      path("persistence"/ "save") {
        post {
          entity(as[String]) { saveGame =>
            print("saved Game")
            //turn String to Json
            val jsonGame = Json.parse(saveGame)
            //turn Json to Game
            val fio = new FileIO()
            val game = fio.jsonToGame(jsonGame)
            fileIO.save(game)
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
          }
        }
      },
      get {
        path("persistence"/ "load") {
          val game = fileIO.load()
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(game).toString))
        }
      },

      path("persistence" / "dbsave") {
        post {
          entity(as[String]) { saveGame =>
            print("saved Game")
            //turn String to Json
            val jsonGame = Json.parse(saveGame)
            //save to db
            val fio = new FileIO()
            val game = fio.jsonToGame(jsonGame)
            slickDAO.save(game)

            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
          }
        }
      },

      get {
        path("persistence" / "dbload") {
          val game = slickDAO.load()          
          val unpacked_game = game.getOrElse("ERROR LOADING GAME")
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(unpacked_game.asInstanceOf[GameInterface]).toString))
        }
      },

      path("persistence" / "dblist") {
        post {
          entity(as[String]) { saveGame =>
            
            slickDAO.listAllGames()
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
          }
        }
      }
    )

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