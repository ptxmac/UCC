package ucc.backend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer


import ch.megard.akka.http.cors.CorsDirectives._
import ucc.shared.API._




class TestDataSource extends DataSource {
  override val name: String = "Test source"

  override def elements: Seq[Element] = Seq(
    Element("Uber", Location(56.156373, 10.207897))
  )
}

object Backend {

  val sources: Map[String, DataSource] = Map(
    "test" -> new TestDataSource,
    "trash" -> new TrashDataSource
  )

  def route: Route = cors() {
    path("datasets") {
      get {
        complete {

          val reply = DatasetListReply(
            sources.map { case (key, source) => DatasetInfo(source.name, key) }.toSeq
          )
          upickle.default.write(reply)
        }
      }
    } ~ path("datasets" / Segment) { name =>
      // Get dataset
      complete {
        // new LatLng(56.156373, 10.207897), // Obviously the center of Aarhus
        val source = sources(name)
        val reply = DatasetReply(source.elements)

        upickle.default.write(reply)
      }
    }
  }


  def main(args: Array[String]): Unit = {


    implicit val system = ActorSystem("ucc-backend")
    implicit val materializer = ActorMaterializer()


    // Start server
    Http().bindAndHandle(route, "localhost", 8085)

  }

}