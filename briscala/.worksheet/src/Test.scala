
import org.mmarini.briscala._
import scalax.io._
import scala.util.matching.Regex
import scala.util.matching.Regex.MatchIterator
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix

object Test {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(267); 
  val map: (Int => Double) = Map((0 -> 1.0), (1 -> 4.2));System.out.println("""map  : Int => Double = """ + $show(map ));$skip(10); val res$0 = 

  map(1);System.out.println("""res0: Double = """ + $show(res$0));$skip(9); val res$1 = 
  map(0);System.out.println("""res1: Double = """ + $show(res$1));$skip(9); val res$2 = 
  map(3);System.out.println("""res2: Double = """ + $show(res$2))}
}
