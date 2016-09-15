package ucc.backend.data

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

    val stream = getClass.getClassLoader.getResourceAsStream("BytoiletterWGS84.json")
    val text = Source.fromInputStream(stream, "ISO-8859-1").mkString
    val json = upickle.json.read(text)

    val features = json("features").arr

    features.map { feature =>
      val coord = feature("geometry")("coordinates")
      val name = feature("properties")("Adresse").str
      Element(name, Location(coord(1).num, coord(0).num))
    }
  }
}
