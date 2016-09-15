scalaVersion in ThisBuild := "2.11.8"

version in ThisBuild := "0.0.1-SNAPSHOT" // TODO get from git
organization in ThisBuild := "dk.ptx"


lazy val root = (project in file(".")).settings(

)
  .aggregate(frontend, backend)


lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).settings(
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "upickle" % "0.4.2"
    //    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  )
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

        // upickle for akka http
        "de.heikoseeberger" %% "akka-http-upickle" % "1.10.0",


        // Testing
        "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion % "test",
        "org.scalatest" %% "scalatest" % "3.0.0" % "test",


        // CSV
        "com.github.tototoshi" %% "scala-csv" % "1.3.3",

        // CORS directives
        "ch.megard" %% "akka-http-cors" % "0.1.6"

      )
    }
    ,
    (resourceGenerators in Compile) <+=
      (fastOptJS in Compile in frontend, packageScalaJSLauncher in Compile in frontend)
        .map((f1, f2) => Seq(f1.data, f2.data)),
    (resources in Compile) ++= (resources in(frontend, Compile)).value
    //    watchSources <++= (watchSources in frontend)


  ).dependsOn(sharedJVM)
  .enablePlugins(JavaAppPackaging)

lazy val frontend = (project in file("frontend")).
  settings(workbenchSettings: _*). // Add workbench to frontend for development
  settings(
  //persistLauncher in Compile := true,
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



