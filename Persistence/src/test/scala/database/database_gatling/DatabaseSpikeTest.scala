package gatling

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.util.Random
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.http.request.builder.HttpRequestBuilder
import akka.http.javadsl.Http
import io.gatling.core.structure.ChainBuilder
import io.gatling.core.structure.PopulationBuilder
import io.gatling.core.body.Body
import akka.http.javadsl.model.HttpMethod

class DatabaseSpikeTest extends SimulationSkeleton {

  override val operations = List(
    buildOperation("persistence save", "POST", "/persistence/dbsave/test", ElFileBody("D:/Lars/git_projects/SA-Mastermind/Persistence/src/test/scala/gatling/example_bodies/game.json")),
    buildOperation("persistence load", "GET", "/persistence/dbload/1", StringBody("")),
    buildOperation("persistence update", "POST", "/persistence/dbupdate/1", ElFileBody("D:/Lars/git_projects/SA-Mastermind/Persistence/src/test/scala/gatling/example_bodies/game.json")),
    buildOperation("persistence delete", "POST", "/persistence/dbdelete/1", StringBody("")),
  )

  override def executeOperations(): Unit = {
    var scn = buildScenario("Scenario 1")
    var scn2 = buildScenario("Scenario 2")
    var scn3 = buildScenario("Scenario 3")

    setUp(
      scn.inject(
        //spike users 
        rampUsers(10) during (10.second),
        atOnceUsers(300),
        rampUsers(10) during (10.second),
        )
    ).protocols(httpProtocol)
  }

  executeOperations()
}