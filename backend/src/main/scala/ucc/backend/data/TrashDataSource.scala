package ucc.backend.data

import java.io.InputStreamReader

import com.github.tototoshi.csv.CSVReader
import ucc.shared.API.{Element, Location}


class TrashDataSource extends DataSource {
  override val name: String = "Trash"


  override val elements: Seq[Element] = {

    val stream = getClass.getResourceAsStream("Affaldsspande2016.csv")


    val reader = CSVReader.open(new InputStreamReader(stream))

    val cans = reader.allWithHeaders()

    cans.map(map => Element(map("Adresse"), Location(map("northing").toDouble, map("easting").toDouble)))
  }

  override val icon: String = "green_MarkerT"

}

