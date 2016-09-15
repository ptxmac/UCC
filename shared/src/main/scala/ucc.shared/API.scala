package ucc.shared

/**
  * Created by ptx on 9/15/16.
  */
object API {

  case class DatasetInfo(name: String, id: String)

  case class DatasetListReply(sets: Seq[DatasetInfo])

  case class Location(lat: Double, lon: Double)

  case class Element(name: String, location: Location)

  case class DatasetReply(elements: Seq[Element])

}
