package ucc.backend.data

import ucc.shared.API.Element

/**
  * Interface for data soruces
  */
trait DataSource {

  /**
    * The human readable name
    */
  def name: String

  /**
    * Returns a collection of Elements in the datasource
    */
  def elements: Seq[Element]
}
