
import org.mmarini.briscala._
import scalax.io._
import scala.util.matching.Regex
import scala.util.matching.Regex.MatchIterator
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix
import scalax.file.Path

object Test {
  val re1 = """(.*)-\d+\..*""".r                  //> re1  : scala.util.matching.Regex = (.*)-\d+\..*
  val re2 = """(.*)\..*""".r                      //> re2  : scala.util.matching.Regex = (.*)\..*

  val source = "../aaa.aaa/aaa31111auaua"         //> source  : String = ../aaa.aaa/aaa31111auaua

  Path.fromString(source).parent                  //> res0: Option[scalax.file.defaultfs.DefaultPath] = Some(Path(../aaa.aaa))
  val name =Path.fromString(source).name          //> name  : String = aaa31111auaua

  val a = name match {
    case re1(prefix) => prefix
    case re2(prefix) => prefix
    case _ => name
  }                                               //> a  : String = aaa31111auaua
  
  a                                               //> res1: String = aaa31111auaua
}