package ucc.backend

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import ch.megard.akka.http.cors.CorsDirectives._
import ucc.shared.API.DatasetsReply

/**
  * Created by ptx on 9/11/16.
  */


object Backend {


  def main(args: Array[String]): Unit = {


    implicit val system = ActorSystem("ucc-backend")
    implicit val materializer = ActorMaterializer()

    val route: Route = cors() {
      path("datasets") {
        get {
          complete {
            val reply = DatasetsReply(Seq("Test", "Hest"))
            upickle.default.write(reply)
          }
        }
      }
    }


    Http().bindAndHandle(route, "localhost", 8085)

  }

}