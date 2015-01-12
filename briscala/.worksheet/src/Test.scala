
import org.mmarini.briscala._
import scalax.io._
import scala.util.matching.Regex
import scala.util.matching.Regex.MatchIterator
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix

object Test {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(526); 

  def seekForRegex(in: LongTraversable[String], regex: Regex): Option[(LongTraversable[String], String)] = if (in.isEmpty)
    None
  else {
    println(s"Seeking for [$regex] in - ${in.head}")
    in.head match {
      case regex(name) => Some(in.tail, name)
      case _ => seekForRegex(in.tail, regex)
    }
  };System.out.println("""seekForRegex: (in: scalax.io.LongTraversable[String], regex: scala.util.matching.Regex)Option[(scalax.io.LongTraversable[String], String)]""");$skip(280); 

  def matchForRegex(in: LongTraversable[String], regex: Regex): Option[String] =
    if (in.isEmpty)
      None
    else {
      println(s"Matching for [$regex] in - ${in.head}")
      in.head match {
        case regex(value) => Some(value)
        case _ => None
      }
    };System.out.println("""matchForRegex: (in: scalax.io.LongTraversable[String], regex: scala.util.matching.Regex)Option[String]""");$skip(155); 
    
    

  def loadRows(in: LongTraversable[String], array: Array[Double], offset: Int, size: Int): Some[(LongTraversable[String], Array[Double])] = ???;System.out.println("""loadRows: (in: scalax.io.LongTraversable[String], array: Array[Double], offset: Int, size: Int)Some[(scalax.io.LongTraversable[String], Array[Double])]""");$skip(656); 

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
  };System.out.println("""loadMatrix: (in: scalax.io.LongTraversable[String], rowCount: Int, colCount: Int)Option[(scalax.io.LongTraversable[String], breeze.linalg.DenseMatrix[Double])]""");$skip(409); 

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
  };System.out.println("""seekForMatrix: (in: scalax.io.LongTraversable[String])Option[(scalax.io.LongTraversable[String], breeze.linalg.DenseMatrix[Double])]""");$skip(99); 

  def seekForScalar(in: LongTraversable[String]): Option[(LongTraversable[String], Double)] = ???;System.out.println("""seekForScalar: (in: scalax.io.LongTraversable[String])Option[(scalax.io.LongTraversable[String], Double)]""");$skip(628); 

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
    };System.out.println("""seekForVar: (in: scalax.io.LongTraversable[String])Option[(scalax.io.LongTraversable[String], String, Any)]""");$skip(229); 

  def load(in: LongTraversable[String]): Map[String, Any] = {
    seekForVar(in) match {
      case Some((in1, varName, typ)) => {
        // println(in1.head)
        Map(varName -> typ)
      }
      case _ => Map()
    }
  };System.out.println("""load: (in: scalax.io.LongTraversable[String])Map[String,Any]""");$skip(78); 

  val in = Resource.fromFile("/home/us00852/git/briscala/briscala/test.mat");System.out.println("""in  : scalax.io.managed.SeekableByteChannelResource[scalax.io.SeekableByteChannel] = """ + $show(in ));$skip(27); 
  val k = load(in.lines());System.out.println("""k  : Map[String,Any] = """ + $show(k ))}
}
