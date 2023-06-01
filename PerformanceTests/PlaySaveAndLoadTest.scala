
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class PlaySaveAndLoadTest extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")
  
  private val headers_0 = Map(
  		"Sec-Fetch-Dest" -> "document",
  		"Sec-Fetch-Mode" -> "navigate",
  		"Sec-Fetch-Site" -> "same-origin",
  		"Sec-Fetch-User" -> "?1",
  		"sec-ch-ua" -> """Google Chrome";v="113", "Chromium";v="113", "Not-A.Brand";v="24""",
  		"sec-ch-ua-mobile" -> "?0",
  		"sec-ch-ua-platform" -> "Windows"
  )
  
  private val headers_4 = Map(
  		"Sec-Fetch-Dest" -> "document",
  		"Sec-Fetch-Mode" -> "navigate",
  		"Sec-Fetch-Site" -> "none",
  		"Sec-Fetch-User" -> "?1",
  		"sec-ch-ua" -> """Google Chrome";v="113", "Chromium";v="113", "Not-A.Brand";v="24""",
  		"sec-ch-ua-mobile" -> "?0",
  		"sec-ch-ua-platform" -> "Windows"
  )


  private val scn = scenario("PlaySaveAndLoadTest")
    .exec(
      http("request_0")
        .get("/controller/tui")
        .headers(headers_0)
    )
    .pause(5)
    .exec(
      http("request_1")
        .get("/controller/handleMultiCharReq/rgbr/0")
        .headers(headers_0)
    )
    .pause(2)
    .exec(
      http("request_2")
        .get("/controller/handleMultiCharReq/gggg/0")
        .headers(headers_0)
    )
    .pause(2)
    .exec(
      http("request_3")
        .get("/controller/handleMultiCharReq/bbbb/0")
        .headers(headers_0)
    )
    .pause(4)
    .exec(
      http("request_4")
        .get("/")
        .headers(headers_4)
    )
    .pause(5)
    .exec(
      http("request_5")
        .get("/controller/handleMultiCharReq/dbsave/DBSaveTestName")
        .headers(headers_0)
    )
    .pause(3)
    .exec(
      http("request_6")
        .get("/")
        .headers(headers_4)
    )
    .pause(1)
    .exec(
      http("request_7")
        .get("/controller/tui")
        .headers(headers_0)
    )
    .pause(3)
    .exec(
      http("request_8")
        .get("/controller/handleMultiCharReq/rrrr/0")
        .headers(headers_0)
    )
    .pause(4)
    .exec(
      http("request_9")
        .get("/controller/handleMultiCharReq/bbbb/0")
        .headers(headers_0)
    )
    .pause(3)
    .exec(
      http("request_10")
        .get("/")
        .headers(headers_4)
    )
    .pause(3)
    .exec(
      http("request_11")
        .get("/controller/handleMultiCharReq/dbload/1")
        .headers(headers_0)
    )
    .pause(4)
    .exec(
      http("request_12")
        .get("/")
        .headers(headers_4)
        .resources(
          http("request_13")
            .get("/controller/handleMultiCharReq/dbsave/1")
            .headers(headers_0)
        )
    )
    .pause(3)
    .exec(
      http("request_14")
        .get("/")
        .headers(headers_4)
    )
    .pause(1)
    .exec(
      http("request_15")
        .get("/controller/tui")
        .headers(headers_0)
    )
    .pause(2)
    .exec(
      http("request_16")
        .get("/controller/handleMultiCharReq/gggg/0")
        .headers(headers_0)
    )
    .pause(2)
    .exec(
      http("request_17")
        .get("/controller/handleMultiCharReq/rrrr/0")
        .headers(headers_0)
    )
    .pause(2)
    .exec(
      http("request_18")
        .get("/controller/handleMultiCharReq/gggg/0")
        .headers(headers_0)
    )
    .pause(3)
    .exec(
      http("request_19")
        .get("/")
        .headers(headers_4)
    )

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
