package ucc.frontend

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLStyleElement
import ucc.shared.API.DatasetsReply

import scalacss.ScalatagsCss._
import scalacss.Defaults._
import scala.scalajs.js
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalatags.JsDom.TypedTag
import scala.scalajs.js.timers.setTimeout
import scala.language.postfixOps



/**
  * Created by ptx on 9/14/16.
  */

object Style extends StyleSheet.Inline {

  import dsl._

  val controlUI = style(
    backgroundColor(c"#fff"),
    border(2 px, solid, c"#fff"),
    borderRadius(3 px),
    boxShadow := "0 2px 6px rgba(0, 0, 0, 0.3)", // ScalaCSS isn't complete
    marginBottom(22 px),
    cursor.pointer
  )


  val controlText = style(
    color(rgb(25, 25, 25)),
    fontFamily := "Roboto, Arial, sans-serif",
    fontSize(16 px)
  )

}

import scalatags.JsDom.all._


object Frontend extends JSApp {

  def callAPI() = {
    println("Hello API :D !")

    for (xhr <- Ajax.get("http://localhost:8085/datasets")) {
      val reply = upickle.default.read[DatasetsReply](xhr.responseText)


      controlText.innerHTML = ""
      val elm = ul(
        for (set <- reply.sets) yield {
          li(set.toString)
        }
      )
      controlText.appendChild(elm.render)
    }
  }

  val controlText = {
    div(Style.controlText,
      "Click me"
    ).render
  }

  val layerControl = {
    val layer = div(
      div(Style.controlUI, title := "Hello?",
        controlText.render
      )
    )
    layer.render
  }


  override def main(): Unit = {
    println("Hello world!!")

    // Add styles
    val head = document.getElementsByTagName("head")(0)
    head.appendChild(Style.render[TypedTag[HTMLStyleElement]].render)

    def initializeMap() = js.Function {

      import google.maps._


      val opts = MapOptions(
        center = new LatLng(56.156373, 10.207897), // Obviously the center of Aarhus
        zoom = 14
      )
      val gmap = new Map(document.getElementById("map"), opts)

      event.addDomListener(gmap, "click", (e: MouseEvent) => {
        println(s"clicky: ${e.latLng} zoom: ${gmap.getZoom()}")
      })

      val idx = ControlPosition.TOP_CENTER.asInstanceOf[Int]
      gmap.controls(idx).push(layerControl)
      setTimeout(5000) {
        callAPI()
      }
      ""
    }

    google.maps.event.addDomListener(window, "load", initializeMap)

  }

}
