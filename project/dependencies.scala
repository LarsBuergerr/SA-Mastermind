import sbt._
import Keys._

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

    //test
    val cov1 = "org.scoverage" %% "scalac-scoverage-plugin" % "1.7.0"
    val cov11 = "org.scoverage" %% "scoverage-runtime" % "1.7.0"
    val cov111 = "org.scoverage" %% "scalac-scoverage-runtime" % "1.7.0"
    val cov1111 = "com.github.vlachjosef" %% "scalac-scapegoat-plugin" % "1.4.10"
}