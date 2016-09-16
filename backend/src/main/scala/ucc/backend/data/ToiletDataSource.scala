package ucc.backend.data

import ucc.backend.utils.GeoJSONLoader
import ucc.shared.API.{Element, Location}

import scala.io.Source

/**
  * Created by ptx on 9/15/16.
  */
class ToiletDataSource extends DataSource {
  /**
    * The human readable name
    */
  override val name: String = "Toilets"

  /**
    * Returns a collection of Elements in the datasource
    */
  override val elements: Seq[Element] = {

    val stream = getClass.getResourceAsStream("BytoiletterWGS84.json")
    val geojson = new GeoJSONLoader(stream, "ISO-8859-1")

    geojson.getFeatures.map { feature =>

      Element(feature.properties("Adresse"), GeoJSONLoader.extractToLocation(feature))
    }
  }
}
