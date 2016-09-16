package ucc.backend.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.FunSuite
import ucc.backend.data.{DataSource, UberDataSource}
import ucc.shared.API._

/**
  * Created by ptx on 9/15/16.
  */
class ApiServiceTest extends FunSuite with ScalatestRouteTest with ApiService {

  import de.heikoseeberger.akkahttpupickle.UpickleSupport._

  val sources = Map("test" -> new DataSource {

    override val name: String = "Test source"

    override val icon = "noIcon"

    override val elements: Seq[Element] = {

      Seq(
        Element("test", Location(1, 2))

      )
    }
  })

  test("get dataset list") {
    Get("/datasets") ~> apiRoute ~> check {
      assert(responseAs[DatasetListReply] === DatasetListReply(Seq(DatasetInfo("Test source", "test", "noIcon"))))
    }
  }

  test("get dataset") {
    Get("/datasets/test") ~> apiRoute ~> check {
      assert(responseAs[DatasetReply] === DatasetReply(Seq(
        Element("test", Location(1, 2))
      )))
    }
  }

  test("dataset not found") {
    Get("/datasets/notfound") ~> Route.seal(apiRoute) ~> check {

      assert(status === StatusCodes.NotFound)
    }
  }


}
