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
import util.*
import model.GameComponent.GameInterface
import model.GameComponent.GameBaseImpl.{HStone, HintStone, Stone}
import FileIOComponent.fileIOJsonImpl.FileIO
import SlickDB.SlickDAO
import MongoDB.MongoDAO

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import akka.protobufv3.internal.compiler.PluginProtos.CodeGeneratorResponse.File

import play.api.libs.json.*

import scala.util.{Failure, Success, Try}

//****************************************************************************** CLASS DEFINITION
class RestPersistenceAPI():

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val fileIO = new FileIO()
  val db = new MongoDAO()
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

      path("persistence" / "dbsave" / Segment) { save_name =>
         post {
          entity(as[String]) { saveGame =>
            print("saved Game")
            //turn String to Json
            val jsonGame = Json.parse(saveGame)
            //save to db
            val fio = new FileIO()
            val game = fio.jsonToGame(jsonGame)
            db.save(game, save_name)

            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
          }
        }
      },

      get {
        path("persistence" / "dbload" / IntNumber) { num =>
          val game = db.load(Some(num))          
          val unpacked_game = game.getOrElse("ERROR LOADING GAME")
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(unpacked_game.asInstanceOf[GameInterface]).toString))
        }
      },
      get {
        path("persistence" / "dbloadname" / Segment) { name =>
          val game = db.loadByName(Some(name))
          val unpacked_game = game.getOrElse("ERROR LOADING GAME")
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, fileIO.gameToJson(unpacked_game.asInstanceOf[GameInterface]).toString))
        }
      },

      path ("persistence" / "dbupdate" / Segment) { id => 
        post {
          entity(as[String]) { saveGame =>
            //turn String to Json
            val jsonGame = Json.parse(saveGame)
            //save to db
            val fio = new FileIO()
            val game = fio.jsonToGame(jsonGame)
            db.update(game, id.toInt)

            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
          }
        }
      },

      path("persistence" / "dblist") {
        post {
          entity(as[String]) { saveGame =>
            
            db.listAllGames()
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game saved"))
          }
        }
      },

      path("persistence" / "dbdelete" / Segment) { id =>
        post {
          entity(as[String]) { game =>
            db.delete(id.toInt)
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