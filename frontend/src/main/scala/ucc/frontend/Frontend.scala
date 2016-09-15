package ucc.frontend

import google.maps.{MouseEvent => _, _}

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.{HTMLInputElement, HTMLStyleElement}
import ucc.shared.API.{DatasetListReply, DatasetReply}

import scala.collection.mutable
import scala.concurrent.Future
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
    boxShadow := "0 2px 6px rgba(0, 0, 0, 0.3)" // ScalaCSS isn't complete

    //cursor.pointer
  )


  val controlText = style(
    color(rgb(25, 25, 25)),
    fontFamily := "Roboto, Arial, sans-serif",
    fontSize(16 px),
    paddingLeft(5 px),
    paddingRight(5 px)
  )

}

import scalatags.JsDom.all._

class BackendAPI(base: String) {
  def callAPI(method: String): Future[String] = {
    Ajax.get(base + method).map(_.responseText)
  }

  def listDatasets: Future[DatasetListReply] = callAPI("datasets").map(upickle.default.read[DatasetListReply])

  def getDataset(id: String): Future[DatasetReply] = callAPI("datasets/" + id).map(upickle.default.read[DatasetReply])

}

@JSExport
class Frontend(base: String) {

  val api = new BackendAPI(base)

  var gmap: google.maps.Map = null // TODO option instead

  def fetchDatasets(): Unit = {
    for (reply <- api.listDatasets) {

      layerList.innerHTML = ""
      val elm = div(
        for (set <- reply.sets) yield {
          div(
            label(
              input(`type` := "checkbox",
                value := set.id,
                onchange := toggleLayerHandler _
              ),
              set.name
            )
          )
        }
      )
      layerList.appendChild(elm.render)

    }
  }

  val layers = mutable.Map.empty[String, Seq[Marker]]

  var lastInfoWindow: Option[InfoWindow] = None

  def toggleLayerHandler(event: Event): Unit = {

    event.target match {
      case input: HTMLInputElement =>
        val id = input.value
        val enabled = input.checked

        if (enabled) {
          // Get set
          for (reply <- api.getDataset(id)) {
            val markers = for (elm <- reply.elements) yield {

              val content = div(elm.name).toString()

              val infowindow = new InfoWindow(InfoWindowOptions(
                content
              ))

              val pos = new LatLng(elm.location.lat, elm.location.lon)

              println(s"pos: $pos")

              val marker = new Marker(MarkerOptions(
                position = pos,
                map = gmap,
                title = elm.name
              ))

              google.maps.event.addListener(marker, "click", () => {
                lastInfoWindow.foreach(_.close())
                infowindow.open(gmap, marker)
                lastInfoWindow = Some(infowindow)
              })

              marker
            }
            layers(id) = markers
          }
        } else {
          for (marker <- layers.getOrElse(id, Seq())) {
            marker.setMap(null)
          }
          layers(id) = Seq()
        }

      // Tod stuff here

      case _ => // TODO error handling

    }
  }


  def callAPI() = {

    fetchDatasets()
  }

  val layerList = div().render

  val controlText: Div = {
    div(Style.controlText,
      input(`type` := "button", value := "Reload", onclick := { () => fetchDatasets() }),
      layerList
    ).render
  }

  val layerControl: Div = {
    val layer = div(
      div(Style.controlUI,
        controlText.render
      )
    )
    layer.render
  }

  /**
    * Initialize the Google Map API
    */
  def initializeMap() = js.Function {

    import google.maps._

    val opts = MapOptions(
      center = new LatLng(56.156373, 10.207897), // Obviously the center of Aarhus
      zoom = 14
    )
    gmap = new Map(document.getElementById("map"), opts)

    event.addDomListener(gmap, "click", (e: MouseEvent) => {
      println(s"clicky: ${e.latLng} zoom: ${gmap.getZoom()}")
    })

    val idx = ControlPosition.TOP_RIGHT.asInstanceOf[Int]
    gmap.controls(idx).push(layerControl)
    setTimeout(3000) {
      callAPI()
    }
    ""
  }


  @JSExport
  def main(): Unit = {
    println("Hello world!!")

    // Add styles
    val head = document.getElementsByTagName("head")(0)
    head.appendChild(Style.render[TypedTag[HTMLStyleElement]].render)


    google.maps.event.addDomListener(window, "load", initializeMap)

  }

}
