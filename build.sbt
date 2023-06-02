import sbt.Keys.libraryDependencies
import dependencies._

val scala3Version = "3.1.2"
val scalaTestVersion = "3.2.15"

lazy val allDependencies = Seq(
  scalameta,
  scalatic,
  scalatest,
  guice,
  sguice,
  xml,
  upickle,
  yaml,
  json,
  akkaHttp,
  akkaHttpSpray,
  akkaHttpCore,
  akkaActorTyped,
  akkaStream,
  akkaActor,
  slf4jNop,
  slick,
  mysql,
  mockito,
  mongodb
)

/** Gatling **/
lazy val gatlingDependencies = Seq(
  "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.9.5" % "test" exclude("com.typesafe.scala-logging", "scala-logging_2.13"),
  "io.gatling" % "gatling-test-framework" % "3.9.5" % "test" exclude("com.typesafe.scala-logging", "scala-logging_2.13"),
)
/** Docker **/
val dockerAppPath = "/app/"

/** Modules **/
lazy val core: Project = Project(id = "Mastermind-Core-Module", base = file("Core"))
  .dependsOn(model, tools, persistence)
  .settings(
    name := "Mastermind-Core-Module",
    version := "0.1.0-SNAPSHOT",
    dockerExposedPorts := Seq(8080),
    scalaVersion := scala3Version,
    commonSettings,
    libraryDependencies ++= allDependencies
  ).enablePlugins(JacocoPlugin, JavaAppPackaging, DockerPlugin)

lazy val model: Project = Project(id = "Mastermind-Model-Module", base = file("Model"))
  .dependsOn(tools)
  .settings(
    name := "Mastermind-Model-Module",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    commonSettings,
    libraryDependencies ++= allDependencies,
  ).enablePlugins(JacocoPlugin)

lazy val persistence: Project = Project(id = "Mastermind-Persistence-Module", base = file("Persistence"))
  .dependsOn(model, tools)
  .settings(
    name := "Mastermind-Persistence-Module",
    version := "0.1.0-SNAPSHOT",
    dockerExposedPorts := Seq(8081),
    scalaVersion := scala3Version,
    commonSettings,
    libraryDependencies ++= allDependencies
  ).enablePlugins(JacocoPlugin, JavaAppPackaging, DockerPlugin)

lazy val tools: Project = Project(id = "Mastermind-Tools-Module", base = file("Tools"))
  .settings(
    name := "Mastermind-Tools-Module",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    commonSettings,
    libraryDependencies ++= allDependencies
  ).enablePlugins(JacocoPlugin)

lazy val ui: Project = Project(id = "Mastermind-UI-Module", base = file("UI"))
  .dependsOn(core, model, tools)
  .settings(
    name := "Mastermind-UI-Module",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    commonSettings,
    libraryDependencies ++= allDependencies
  ).enablePlugins(JacocoPlugin)

lazy val root: Project = Project(id = "Mastermind-Root-Module", base = file("."))
  .dependsOn(ui, core, model, tools, persistence)
  .aggregate(ui, core, model, tools, persistence)
  .settings(
    run / connectInput := true,
    name := "Mastermind-Root-Module",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    commonSettings,
    libraryDependencies ++= allDependencies
  ).enablePlugins(JacocoPlugin, JavaAppPackaging, DockerPlugin)

lazy val commonSettings: Seq[Def.Setting[_]] = Seq(
  scalaVersion := scala3Version,

  jacocoCoverallsServiceName := "github-actions",
  jacocoCoverallsBranch := sys.env.get("CI_BRANCH"),
  jacocoCoverallsPullRequest := sys.env.get("GITHUB_EVENT_NAME"),
  jacocoCoverallsRepoToken := sys.env.get("COVERALLS_REPO_TOKEN"),

  jacocoReportSettings := JacocoReportSettings(
    "Jacoco Coverage Report",
    None,
    JacocoThresholds(),
    Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML),
    "utf-8"),

    jacocoExcludes := Seq(
    "*aview.*",
    "*Mastermind.*",
    "*MastermindModule.*"
    ),

  libraryDependencies ++= {
  // Determine OS version of JavaFX binaries
    lazy val osName = System.getProperty("os.name") match {
      case n if n.startsWith("Linux") => "linux"
      case n if n.startsWith("Mac") => "mac"
      case n if n.startsWith("Windows") => "win"
      case _ => throw new Exception("Unknown platform!")
    }
    Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
      .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier osName)
  },

  libraryDependencies += "org.scalafx" %% "scalafx" % "16.0.0-R24",

)