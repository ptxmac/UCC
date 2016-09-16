package ucc.backend.data

import ucc.backend.utils.GeoJSONLoader
import ucc.shared.API.Element

/**
  * Created by ptx on 9/16/16.
  */
class TruckParkingDataSource extends DataSource {
  /**
    * The human readable name
    */
  override val name: String = "Truck parking"

  /**
    * Returns a collection of Elements in the datasource
    */
  override val elements: Seq[Element] = {
    val stream = getClass.getResourceAsStream("lastbiler-gma.json")
    val geojson = new GeoJSONLoader(stream)
    geojson.getFeatures.map { feature =>
      Element(feature.properties("description"), GeoJSONLoader.extractToLocation(feature))
    }
  }
}
