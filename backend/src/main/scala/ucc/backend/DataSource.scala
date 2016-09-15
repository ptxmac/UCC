package ucc.backend

import ucc.shared.API.Element

/**
  * Created by ptx on 9/11/16.
  */


trait DataSource {
  val name: String

  def elements: Seq[Element]
}
