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
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future, Promise}
import scalacss.ScalatagsCss._
import scalacss.Defaults._
import scala.scalajs.js
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalatags.JsDom.TypedTag
import scala.scalajs.js.timers.setTimeout
import scala.language.postfixOps
import scala.concurrent.duration._
import scala.util.{Failure, Success}

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

  /**
    * Delay a future
    */
  def delay[T](f: => Future[T], delay: FiniteDuration): Future[T] = {
    val p = Promise[T]()
    setTimeout(delay) {

      f.onComplete {
        case Success(res) => p.success(res)
        case Failure(ex) => p.failure(ex)
      }
    }
    p.future
  }

  /**
    * Retry a future a number of times if it fails
    *
    * @param f          a expresion returning a Future[T]. Note: call-by-name
    * @param retryDelay wait time between retires
    * @param retries    number of times the future should be retired before giving up
    * @return a new future with will have the result of the original future
    */
  def retry[T](f: => Future[T], retryDelay: FiniteDuration, retries: Int): Future[T] = {
    f recoverWith { case _ if retries > 0 =>
      delay({
        retry(f, retryDelay, retries - 1)
      }, retryDelay)
    }
  }

  /**
    * Make GET request to the api. if the call fails retry it up to 10 times with a 1 second delay between
    *
    * @tparam T The return type of the API call
    * @param method name of the API to call
    * @return A future wrapping the exepcted result
    */
  def callAPI[T: upickle.default.Reader](method: String): Future[T] = {
    retry({
      println(s"call: $method")
      Ajax.get(base + method).map(xhr => upickle.default.read[T](xhr.responseText))
    }, 1 second, 10)
  }

  def listDatasets = callAPI[DatasetListReply]("datasets")

  def getDataset(id: String) = callAPI[DatasetReply]("datasets/" + id)

}

@JSExport
class Frontend(apiBase: String, cdnBase: String) {

  val api = new BackendAPI(apiBase)

  var gmap: google.maps.Map = null // TODO option instead

  val layers = mutable.Map.empty[String, Seq[Marker]]


  def fetchDatasets(): Unit = {
    for (reply <- api.listDatasets) {
      // clear all existing layers
      layers.foreach { case (_, markers) =>
        markers.foreach(_.setMap(null))
      }
      layers.clear()

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


              val icon = MarkerImage(
                //scaledSize = new Size(32, 32),
                size = new Size(20, 34),
                //anchor = new Point(16, 16),
                url = cdnBase + "markers/blue_MarkerA.png"
              )

              val marker = new Marker(MarkerOptions(
                position = pos,
                map = gmap,
                icon = icon,
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
    * Initialize the Google Map API￼Uber
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

    fetchDatasets()

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
