/**
 *
 */
package org.mmarini.briscala

import scalax.io.LongTraversable
import breeze.linalg.DenseMatrix
import scala.util.matching.Regex
import scalax.io.Output

/**
 * @author us00852
 *
 */
object MathFile {

  /**
   *
   */
  def seekForRegex(in: LongTraversable[String], regex: Regex): Option[(LongTraversable[String], String)] = if (in.isEmpty)
    None
  else
    in.head match {
      case regex(name) => Some(in.tail, name)
      case _ => seekForRegex(in.tail, regex)
    }

  /**
   *
   */
  def matchForRegex(in: LongTraversable[String], regex: Regex): Option[String] =
    if (in.isEmpty)
      None
    else
      in.head match {
        case regex(value) => Some(value)
        case _ => None
      }

  /**
   *
   */
  def loadMatrix(in: LongTraversable[String], rowCount: Int, colCount: Int): Option[(LongTraversable[String], DenseMatrix[Double])] = {
    val array = new Array[Double](rowCount * colCount)

    def loadRawsLoop(in: LongTraversable[String], n: Int, offset: Int): Option[LongTraversable[String]] =
      if (n <= 0)
        Some(in)
      else if (in.isEmpty)
        None
      else {
        val line = in.head
        val cols = line.trim.split("\\s")
        if (colCount != cols.size)
          None
        else
          for { i <- 0 until colCount } {
            array.update(offset + i * rowCount, cols(i).toDouble)
          }
        loadRawsLoop(in.tail, n - 1, offset + 1)
      }

    loadRawsLoop(in, rowCount, 0) match {
      case Some(in1) => Some(in1, new DenseMatrix(rowCount, colCount, array))
      case _ => None
    }
  }

  /**
   *
   */
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
  }

  /**
   *
   */
  def seekForScalar(in: LongTraversable[String]): Option[(LongTraversable[String], Double)] = if (in.isEmpty)
    None
  else
    Some(in.tail, in.head.trim.toDouble)

  /**
   *
   */
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
    }

  /**
   *
   */
  def load(in: LongTraversable[String]): Map[String, Any] = {

    def loadVars(in: LongTraversable[String], map: Map[String, Any]): Map[String, Any] =
      seekForVar(in) match {
        case None => map
        case Some((in1, varName, value)) => loadVars(in1, map + (varName -> value))
      }

    loadVars(in, Map())
  }

  /**
   *
   */
  def save(out: Output, vars: Map[String, Any]): Unit = {

    def writeMatrix(name: String, m: DenseMatrix[Double]): Unit = {
      out.write(s"# name: $name\n");
      out.write("# type: matrix\n")
      out.write(s"# rows: ${m.rows}\n")
      out.write(s"# columns: ${m.cols}")
      for {
        i <- 0 until m.rows
        j <- 0 until m.cols
      } {
        if (j == 0)
          out.write("\n")
        out.write(s" ${m(i, j)}")
      }
      out.write("\n")
      out.write("\n")
    }

    def writeScalar(name: String, value: Double): Unit = {
      out.write(s"# name: $name\n");
      out.write("# type: scalar\n")
      out.write(s" $value\n\n")
    }

    vars.foreach {
      case (name, value) if (value.isInstanceOf[DenseMatrix[Double]]) => writeMatrix(name, value.asInstanceOf[DenseMatrix[Double]])
      case (name, value) if (value.isInstanceOf[Double]) => writeScalar(name, value.asInstanceOf[Double])
    }

  }

}