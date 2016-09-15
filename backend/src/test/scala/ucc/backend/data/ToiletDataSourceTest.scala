package ucc.backend.data

import org.scalatest.FunSuite

/**
  * Created by ptx on 9/15/16.
  */
class ToiletDataSourceTest extends FunSuite {

  test("toilet source non empty") {

    val ts = new ToiletDataSource

    assert(ts.elements.nonEmpty)
  }

}
