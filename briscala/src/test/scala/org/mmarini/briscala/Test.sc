
import org.mmarini.briscala._
import scalax.io._
import scala.util.matching.Regex
import scala.util.matching.Regex.MatchIterator
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix
import scalax.file.Path
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTime

object Test {
ISODateTimeFormat.dateTime().print(new DateTime)  //> res0: String = 2015-02-11T11:24:42.167+01:00
}