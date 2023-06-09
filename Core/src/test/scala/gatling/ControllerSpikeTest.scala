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
import io.gatling.javaapi.core.PopulationBuilder
import akka.http.javadsl.model.HttpMethod

class ControllerSpikeTest extends SimulationSkeleton {

  override val operations = List(
    buildOperation("API root", "GET", "/", StringBody("")),
    buildOperation("place Stones and Hints", "GET", "/controller/handleMultiCharReq/${stones}/${hints}/${turn}", StringBody("")),
    buildOperation("game undo", "GET", "/controller/undo", StringBody("")),
    buildOperation("game redo", "GET", "/controller/redo", StringBody("")),
    buildOperation("game reset", "GET", "/controller/reset", StringBody("")),
    buildOperation("game single char request", "GET", "/controller/handleSingleCharReq/${char}", StringBody("")),

  )

  override def executeOperations(): Unit = {
    var scn = buildScenario("Scenario 1")
    var scn2 = buildScenario("Scenario 2")
    var scn3 = buildScenario("Scenario 3")

    setUp(
      scn.inject(
        //spike users 
        rampUsers(10) during (10.second),
        atOnceUsers(1000),
        rampUsers(10) during (10.second),
        )
    ).protocols(httpProtocol)
  }

  executeOperations()
}