package telemetry

import com.github.nscala_time.time.Imports._
import org.json4s._
import org.json4s.native.JsonMethods._
import scala.collection.JavaConverters._

case class Partitioning(dimensions: List[Dimension]) {
  def partitionPrefix(prefix: String): String = {
    val path = prefix.split("/")
    assert(path.length - 1 == dimensions.length, "Invalid partitioning")

    path(0) + "/" + dimensions
      .zip(path.drop(1))
      .map(x => x._1.fieldName + "S3=" + x._2)
      .mkString("/")
  }
}

case class Dimension(fieldName: String, allowedValues: String)

object Partitioning{
  private implicit val formats = DefaultFormats

  def apply(rawSchema: String): Partitioning = {
    val schema = parse(rawSchema)
    schema.camelizeKeys.extract[Partitioning]
  }
}