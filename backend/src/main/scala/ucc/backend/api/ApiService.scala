package ucc.backend.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

import ch.megard.akka.http.cors.CorsDirectives._

import ucc.backend.data.DataSource
import ucc.shared.API._


/**
  * The Akka HTTP route that serves the API
  *
  * Created as a mix in to ease testing
  */
trait ApiService {

  val sources: Map[String, DataSource]

  def apiRoute: Route = cors() {

    // Automatically encode case classes using upickle
    import de.heikoseeberger.akkahttpupickle.UpickleSupport._

    pathPrefix("api" / "v1") {
      path("datasets") {
        get {
          complete {

            DatasetListReply(
              sources.map { case (key, source) => DatasetInfo(source.name, key, source.icon) }.toSeq
            )
          }
        }
      } ~ path("datasets" / Segment) { name =>
        // Get dataset
        rejectEmptyResponse {
          complete {
            // new LatLng(56.156373, 10.207897), // Obviously the center of Aarhus
            sources.get(name).map(s => DatasetReply(s.elements))
          }
        }
      }
    }
  }
}
