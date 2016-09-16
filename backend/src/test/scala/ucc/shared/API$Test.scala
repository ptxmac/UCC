package ucc.shared

import org.scalatest.FunSuite
import ucc.shared.API._

/**
  * Created by ptx on 9/16/16.
  */
class API$Test extends FunSuite {

  test("json encoding of DatasetReply") {
    val dsr = DatasetReply(Seq(Element("a", Location(1, 2))))
    val json = upickle.default.write(dsr)

    // This testcase validates that the json encoding creates the expected output.

    assert(json === """{"elements":[{"name":"a","location":{"lat":1,"lon":2}}]}""")
  }

}
