package ucc.backend

import java.io.{File, InputStreamReader}

import com.github.tototoshi.csv.CSVReader
import ucc.shared.API.{Element, Location}


class TrashDataSource extends DataSource {
  override val name: String = "Trash"


  override val elements: Seq[Element] = {

    val stream = getClass.getClassLoader.getResourceAsStream("Affaldsspande2016.csv")


    val reader = CSVReader.open(new InputStreamReader(stream))

    val cans = reader.allWithHeaders()

    cans.map(map => Element(map("Adresse"), Location(map("northing").toDouble, map("easting").toDouble)))
  }
}

