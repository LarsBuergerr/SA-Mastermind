import sbt.*

object dependencies{
    val scalameta = "org.scalameta" %% "munit" % "0.7.29" % Test
    val scalatic = "org.scalactic" %% "scalactic" % "3.2.15"
    val scalatest = "org.scalatest" %% "scalatest" % "3.2.15" % "test"
    val guice = "com.google.inject" % "guice" % "4.2.3"
    val sguice = ("net.codingwell" %% "scala-guice" % "5.0.2").cross(CrossVersion.for3Use2_13)
    val xml = "org.scala-lang.modules" %% "scala-xml" % "2.0.1" // XML
    val upickle = "com.lihaoyi" %% "upickle" % "1.4.4" // JSON upickle
    val yaml = ("net.jcazevedo" %% "moultingyaml" % "0.4.2").cross(CrossVersion.for3Use2_13) //YAML
    val json = ("com.typesafe.play" %% "play-json" % "2.9.3").cross(CrossVersion.for3Use2_13) // JSON

    val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.5.0"
    val akkaHttpSpray = "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.0"
    val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core" % "10.5.0"
    val akkaActorTyped = "com.typesafe.akka" %% "akka-actor-typed" % "2.8.0"
    val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.8.0"
    val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.8.0"
    val slf4jNop = "org.slf4j" % "slf4j-nop" % "2.0.5"
    val mysql = "mysql" % "mysql-connector-java" % "8.0.32"
    val slick = ("com.typesafe.slick" %% "slick" % "3.5.0-M3").cross(CrossVersion.for3Use2_13)
    val mockito = "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test
    val mongodb = ("org.mongodb.scala" %% "mongo-scala-driver" % "4.9.1").cross(CrossVersion.for3Use2_13)
}