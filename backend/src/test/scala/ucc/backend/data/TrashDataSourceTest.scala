package ucc.backend.data

import org.scalatest.FunSuite

/**
  * Created by ptx on 9/15/16.
  */
class TrashDataSourceTest extends FunSuite {
  test("trash data source non empty") {
    val ts = new TrashDataSource
    assert(ts.elements.nonEmpty)
  }
}
