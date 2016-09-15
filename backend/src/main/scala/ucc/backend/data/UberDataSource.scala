package ucc.backend.data

import ucc.shared.API.{Element, Location}

/**
  * Created by ptx on 9/15/16.
  */
class UberDataSource extends DataSource {
  override val name: String = "Uber"

  override def elements: Seq[Element] = Seq(
    Element("Uber aarhus", Location(56.156373, 10.207897))
  )
}
