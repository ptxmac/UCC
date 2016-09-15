scalaVersion in ThisBuild := "2.11.8"

version in ThisBuild := "0.0.1-SNAPSHOT" // TODO get from git
organization in ThisBuild := "dk.ptx"


lazy val root = (project in file(".")).aggregate(frontend, backend)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).settings(
  libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.4.2"

)

lazy val sharedJS = shared.js
lazy val sharedJVM = shared.jvm

lazy val backend = (project in file("backend")).
  settings(
    libraryDependencies ++= {

      val akkaVersion = "2.4.10"

      Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,


        // CSV
        "com.github.tototoshi" %% "scala-csv" % "1.3.3",

        // CORS directives
        "ch.megard" %% "akka-http-cors" % "0.1.6"

      )
    }
  ).dependsOn(sharedJVM).
  enablePlugins(JavaAppPackaging)

lazy val frontend = (project in file("frontend")).
  settings(workbenchSettings: _*). // Add workbench to frontend for development
  settings(
  persistLauncher in Compile := true,
  libraryDependencies ++= Seq(
    // Scala.js Dom
    "org.scala-js" %%% "scalajs-dom" % "0.9.1",
    "com.lihaoyi" %%% "scalatags" % "0.6.0",
    "com.github.japgolly.scalacss" %%% "core" % "0.5.0",
    "com.github.japgolly.scalacss" %%% "ext-scalatags" % "0.5.0"
  ),
  bootSnippet := "ucc.frontend.Frontend().main()",
  updateBrowsers <<= updateBrowsers.triggeredBy(fastOptJS in Compile)

).enablePlugins(ScalaJSPlugin).dependsOn(sharedJS)



