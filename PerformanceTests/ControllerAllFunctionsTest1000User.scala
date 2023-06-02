
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class ontrollerAllFunctionsTest1000User extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")

  private val headers_0 = Map(
  		"Cache-Control" -> "max-age=0",
  		"Sec-Fetch-Dest" -> "document",
  		"Sec-Fetch-Mode" -> "navigate",
  		"Sec-Fetch-Site" -> "none",
  		"Sec-Fetch-User" -> "?1",
  		"sec-ch-ua" -> """Not.A/Brand";v="8", "Chromium";v="114", "Google Chrome";v="114""",
  		"sec-ch-ua-mobile" -> "?0",
  		"sec-ch-ua-platform" -> "Windows"
  )


  private val scn = scenario("ontrollerAllFunctionsTest1000User")
    .exec(
      http("request_GoToControllerAPI")
        .get("/")
        .headers(headers_0)
    )
    .pause(1)
    .exec(
      http("request_WebTui")
        .get("/controller/tui")
        .headers(headers_0)
    )
    .pause(1)
    .exec(
      http("request_GameAsJSON")
        .get("/controller/tuiJSON")
        .headers(headers_0)
    )
    .pause(1)
    .exec(
      http("request_PlaceGuessAndHints")
        .get("/controller/placeGuessAndHints/bbbb/rrrr/0")
        .headers(headers_0)
    )
    .pause(1)
    .exec(
      http("request_GameLoad")
        .get("/controller/load")
        .headers(headers_0)
    )
    .pause(1)
    .exec(
      http("request_GameRedo")
        .get("/controller/redo")
        .headers(headers_0)
    )
    .pause(1)
    .exec(
      http("request_GameReset")
        .get("/controller/reset")
        .headers(headers_0)
    )
    .pause(1)
    .exec(
      http("request_DbSaveByID")
        .get("/controller/handleMultiCharReq/dbsave/1")
        .headers(headers_0)
    )
    .pause(1)
    .exec(
      http("request_DbSaveByName")
        .get("/controller/handleMultiCharReq/dbsave/DBSaveTestName")
        .headers(headers_0)
    )
    .pause(1)
    .exec(
      http("request_DbLoadByID")
        .get("/controller/handleMultiCharReq/dbload/1")
        .headers(headers_0)
    )
    .pause(1)
    .exec(
      http("request_DbLoadByName")
        .get("/controller/handleMultiCharReq/dbloadname/DBSaveTestName")
        .headers(headers_0)
    )
    .pause(1)
    .exec(
      http("request_DbListAllSaves")
        .get("/controller/handleMultiCharReq/dblist/0")
        .headers(headers_0)
    )

	setUp(scn.inject(atOnceUsers(1000))).protocols(httpProtocol)
}
