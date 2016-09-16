package ucc.backend.utils

import java.io.InputStream

import com.typesafe.scalalogging.LazyLogging
import ucc.shared.API.Location
import upickle.Js

import scala.io.Source

/**
  * Very simpel GeoJSON parser
  *
  * @note Does not support nested properties
  *
  */


class GeoJSONLoader(stream: InputStream, encoding: String = "UTF-8") extends LazyLogging {

  import GeoJSONLoader._

  private val text = Source.fromInputStream(stream, encoding).mkString
  private val json = upickle.json.read(text)


  def convertProperty(value: Js.Value): String = {
    value match {
      case Js.Str(str) => str
      case Js.Num(num) => num.toString
      case other =>
        logger.warn(s"Don't know how to convert ${other}, using toString")
        other.toString()
    }
  }

  def getFeatures: Seq[Feature] = {
    val featuresJson = json("features").arr

    for (featureJson <- featuresJson) yield {
      val geometryJson = featureJson("geometry")
      val propertiesJson = featureJson("properties")
      val coordJson = geometryJson("coordinates")

      val geometry = Geometry(coordJson.arr.map(_.num))
      val properties = propertiesJson.obj.mapValues(convertProperty)

      Feature(geometry, properties)
    }
  }
}

object GeoJSONLoader {

  case class Geometry(coordinates: Seq[Double])

  case class Feature(geometry: Geometry, properties: Map[String, String])


  def extractToLocation(feature: Feature): Location = {
    val coords = feature.geometry.coordinates
    Location(coords(1), coords(0))
  }
}
