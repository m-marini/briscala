
import org.mmarini.briscala._
import scalax.io._
import scala.util.matching.Regex
import scala.util.matching.Regex.MatchIterator
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix

object Test {

  def seekForRegex(in: LongTraversable[String], regex: Regex): Option[(LongTraversable[String], String)] = if (in.isEmpty)
    None
  else {
    println(s"Seeking for [$regex] in - ${in.head}")
    in.head match {
      case regex(name) => Some(in.tail, name)
      case _ => seekForRegex(in.tail, regex)
    }
  }                                               //> seekForRegex: (in: scalax.io.LongTraversable[String], regex: scala.util.matc
                                                  //| hing.Regex)Option[(scalax.io.LongTraversable[String], String)]

  def matchForRegex(in: LongTraversable[String], regex: Regex): Option[String] =
    if (in.isEmpty)
      None
    else {
      println(s"Matching for [$regex] in - ${in.head}")
      in.head match {
        case regex(value) => Some(value)
        case _ => None
      }
    }                                             //> matchForRegex: (in: scalax.io.LongTraversable[String], regex: scala.util.mat
                                                  //| ching.Regex)Option[String]

  def loadRows(in: LongTraversable[String], array: Array[Double], offset: Int, size: Int): Some[(LongTraversable[String], Array[Double])] = ???
                                                  //> loadRows: (in: scalax.io.LongTraversable[String], array: Array[Double], offs
                                                  //| et: Int, size: Int)Some[(scalax.io.LongTraversable[String], Array[Double])]

  def loadMatrix(in: LongTraversable[String], rowCount: Int, colCount: Int): Option[(LongTraversable[String], DenseMatrix[Double])] = {
    val array = new Array[Double](rowCount * colCount)

    def loadRawsLoop(in: LongTraversable[String], rowCount: Int, offset: Int): Option[LongTraversable[String]] =
      if (rowCount <= 0)
        Some(in)
      else if (in.isEmpty)
        None
      else {
      val line = in.head
      
        loadRawsLoop(in.tail, rowCount - 1, offset + colCount)
      }

    loadRawsLoop(in, rowCount, 0) match {
      case Some(in1) => Some(in1, new DenseMatrix(rowCount, colCount, array))
      case _ => None
    }
  }                                               //> loadMatrix: (in: scalax.io.LongTraversable[String], rowCount: Int, colCount
                                                  //| : Int)Option[(scalax.io.LongTraversable[String], breeze.linalg.DenseMatrix[
                                                  //| Double])]

  def seekForMatrix(in: LongTraversable[String]): Option[(LongTraversable[String], DenseMatrix[Double])] = if (in.isEmpty)
    None
  else {
    matchForRegex(in, """# rows: (\d*)""".r) match {
      case Some(row) => matchForRegex(in.tail, """# columns: (\d*)""".r) match {
        case Some(col) => loadMatrix(in.drop(2), row.toInt, col.toInt)
        case _ => None
      }
      case _ => None
    }
  }                                               //> seekForMatrix: (in: scalax.io.LongTraversable[String])Option[(scalax.io.Lon
                                                  //| gTraversable[String], breeze.linalg.DenseMatrix[Double])]

  def seekForScalar(in: LongTraversable[String]): Option[(LongTraversable[String], Double)] = ???
                                                  //> seekForScalar: (in: scalax.io.LongTraversable[String])Option[(scalax.io.Lon
                                                  //| gTraversable[String], Double)]

  def seekForVar(in: LongTraversable[String]): Option[(LongTraversable[String], String, Any)] =
    seekForRegex(in, """# name: (\w*)""".r) match {
      case Some((in1, varName)) => matchForRegex(in1, """# type: (\w*)""".r) match {
        case Some("matrix") => seekForMatrix(in1.tail) match {
          case Some((in1, matrix)) => Some(in1.tail, varName, matrix)
          case _ => None
        }
        case Some("scalar") => seekForScalar(in1.tail) match {
          case Some((in1, scalar)) => Some(in1.tail, varName, scalar)
          case _ => None
        }
        case _ => None
      }
      case _ => None
    }                                             //> seekForVar: (in: scalax.io.LongTraversable[String])Option[(scalax.io.LongTr
                                                  //| aversable[String], String, Any)]

  def load(in: LongTraversable[String]): Map[String, Any] = {
    seekForVar(in) match {
      case Some((in1, varName, typ)) => {
        // println(in1.head)
        Map(varName -> typ)
      }
      case _ => Map()
    }
  }                                               //> load: (in: scalax.io.LongTraversable[String])Map[String,Any]

  val in = Resource.fromFile("/home/us00852/git/briscala/briscala/test.mat")
                                                  //> in  : scalax.io.managed.SeekableByteChannelResource[scalax.io.SeekableByteC
                                                  //| hannel] = SeekableByteChannelResource(/home/us00852/git/briscala/briscala/t
                                                  //| est.mat)
  val k = load(in.lines())                        //> Seeking for [# name: (\w*)] in - # name: vNet_w1
                                                  //| Matching for [# type: (\w*)] in - # type: matrix
                                                  //| Matching for [# rows: (\d*)] in - # rows: 3
                                                  //| Matching for [# columns: (\d*)] in - # columns: 211
                                                  //| k  : Map[String,Any] = Map(vNet_w1 -> 0.0  0.0  0.0  0.0  0.0  0.0  0.0  0.
                                                  //| 0  0.0  0.0  0.0  0.0  ... (211 total)
                                                  //| 0.0  0.0  0.0  0.0  0.0  0.0  0.0  0.0  0.0  0.0  0.0  0.0  ...
                                                  //| 0.0  0.0  0.0  0.0  0.0  0.0  0.0  0.0  0.0  0.0  0.0  0.0  ...)
}