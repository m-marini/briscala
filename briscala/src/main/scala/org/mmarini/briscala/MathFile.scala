/**
 *
 */
package org.mmarini.briscala

import scalax.io.LongTraversable
import breeze.linalg.DenseMatrix
import scala.util.matching.Regex
import scalax.io.Output
import java.io.IOException

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
  def matchForRegex(in: LongTraversable[String], regex: Regex): String =
    if (in.isEmpty)
      throw new IOException(s"Missing $regex")
    else
      in.head match {
        case regex(value) => value
        case _ => throw new IOException(s"Missing $regex")
      }

  /**
   *
   */
  def loadMatrix(in: LongTraversable[String], rowCount: Int, colCount: Int): (LongTraversable[String], DenseMatrix[Double]) = {
    val array = new Array[Double](rowCount * colCount)

    def loadRawsLoop(in: LongTraversable[String], n: Int, offset: Int): LongTraversable[String] =
      if (n <= 0)
        in
      else if (in.isEmpty)
        throw new IOException("Missing rows")
      else {
        val line = in.head
        val cols = line.trim.split("\\s")
        if (colCount != cols.size)
          throw new IOException("declared columns count != read columns")
        else
          for { i <- 0 until colCount } {
            array.update(offset + i * rowCount, cols(i).toDouble)
          }
        loadRawsLoop(in.tail, n - 1, offset + 1)
      }

    (loadRawsLoop(in, rowCount, 0), new DenseMatrix(rowCount, colCount, array))
  }

  /**
   *
   */
  def seekForMatrix(in: LongTraversable[String]): (LongTraversable[String], DenseMatrix[Double]) =
    loadMatrix(in.drop(2),
      matchForRegex(in, """# rows: (\d*)""".r).toInt,
      matchForRegex(in.tail, """# columns: (\d*)""".r).toInt)

  /**
   *
   */
  def seekForScalar(in: LongTraversable[String]): Double =
    if (in.isEmpty)
      throw new IOException(s"Missing scalar value")
    else
      in.head.trim.toDouble

  /**
   *
   */
  def seekForVar(in: LongTraversable[String]): Option[(LongTraversable[String], String, Any)] =
    seekForRegex(in, """# name: (\w*)""".r) match {
      case Some((in1, varName)) =>
        matchForRegex(in1, """# type: (\w*)""".r) match {
          case "matrix" => {
            val (in, matrix) = seekForMatrix(in1.tail)
            Some(in.tail, varName, matrix)
          }
          case "scalar" => Some(in1.drop(2), varName, seekForScalar(in1.tail))
          case typ => throw new IOException(s"Unrecognize type ${typ}")
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