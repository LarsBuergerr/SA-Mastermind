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


abstract class SimulationSkeleton extends Simulation {

  val operations: List[ChainBuilder]

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")

  val headers = Map(
    "Cache-Control" -> "max-age=0",
    "Sec-Fetch-Dest" -> "document",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "none",
    "Sec-Fetch-User" -> "?1",
    "sec-ch-ua" -> """Not.A/Brand";v="8", "Chromium";v="114", "Google Chrome";v="114""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "Windows"
  )

  def buildOperation(name: String, request: String, operation: String, body: Body): ChainBuilder = {
    exec(
      http(name)
        .httpRequest(request, operation)
        .body(body)
    )
  }
  val allowedStones = List("r", "g", "b", "y", "w", "p")
  val allowedHints = List("r", "w")
  val allowedChars = List("h", "m", "q")

  val randomStoneFeeder = Iterator.continually(Map("stones" -> Random.shuffle(allowedStones).take(4).mkString))
  val randomHintFeeder = Iterator.continually(Map("hints" -> Random.shuffle(allowedHints).take(4).mkString))
  val randomTurnFeeder = Iterator.continually(Map("turn" -> Random.nextInt(10).toString))
  val randomCharFeeder = Iterator.continually(Map("char" -> Random.shuffle(allowedChars).take(1).mkString))

  def buildScenario(name: String) =
    scenario(name)
      .feed(randomStoneFeeder)
      .feed(randomHintFeeder)
      .feed(randomTurnFeeder)
      .feed(randomCharFeeder)
      .exec(
        //exec the operations and between each pause a second
        operations.reduce((a, b) => a.pause(1.second).exec(b))
      )

  def executeOperations(): Unit
}