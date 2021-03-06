package ucc.backend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import ucc.backend.api.ApiService
import ucc.backend.data._


object Backend extends ApiService with LazyLogging {

  val sources: Map[String, DataSource] = Map(
    "uber" -> new UberDataSource,
    "trash" -> new TrashDataSource,
    "toilet" -> new ToiletDataSource,
    "truck_park" -> new TruckParkingDataSource
  )

  /**
    * Helper route for serving the static files. Not needed if the frontend is hosted another place.
    */
  def staticRoute: Route =
    path(PathEnd) {
      getFromResource("index-prod.html")
    } ~ path(Segment) { name =>
      getFromResource(name)
    }

  def route: Route = apiRoute ~ staticRoute




  def main(args: Array[String]): Unit = {


    implicit val system = ActorSystem("ucc-backend")
    implicit val materializer = ActorMaterializer()

    val port = sys.env.getOrElse("PORT", "8085").toInt
    // Start server
    println(s"Start on $port")
    Http().bindAndHandle(route, "0.0.0.0", port)

  }

}