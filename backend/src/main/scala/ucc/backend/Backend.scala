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

/**
  * Created by ptx on 9/11/16.
  */

// TODO: use a better JSON library

case class HelloReply(str: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val helloReplyFormat = jsonFormat1(HelloReply)
}


object Backend extends JsonSupport {


  def main(args: Array[String]): Unit = {


    implicit val system = ActorSystem("ucc-backend")
    implicit val materializer = ActorMaterializer()

    val route: Route = cors() {
      path("hello") {
        get {
          println("Got request!")
          complete(HelloReply("Hello world"))
        }
      }
    }


    Http().bindAndHandle(route, "localhost", 8085)

  }

}