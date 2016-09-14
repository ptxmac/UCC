package ucc.frontend

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom._
import org.scalajs.dom.ext.Ajax

import scala.scalajs.js

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

/**
  * Created by ptx on 9/14/16.
  */

object Frontend extends JSApp {

  def callAPI() = {
    println("Hello API :D !")

    for (resp <- Ajax.get("http://localhost:8085/hello")) {
      val json = js.JSON.parse(resp.responseText)
      println(json)
    }
  }

  override def main(): Unit = {
    println("Hello world!!")

    def initializeMap() = js.Function {

      import google.maps._

      val opts = MapOptions(
        center = new LatLng(56.156373, 10.207897), // Obviously the center of Aarhus
        zoom = 14
      )
      val gmap = new Map(document.getElementById("map"), opts)

      google.maps.event.addDomListener(gmap, "click", (e: MouseEvent) => {
        println(s"clicky: ${e.latLng} zoom: ${gmap.getZoom()}")
      })
      callAPI()
      ""
    }

    google.maps.event.addDomListener(window, "load", initializeMap)

  }

}
