
import org.mmarini.briscala._
import scala.util.Random
import breeze.linalg.Vector
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix
import breeze.numerics._
import breeze.linalg.operators.DenseVector_SpecialOps
import breeze.linalg.operators.DenseVector_HashVector_Ops

object Test {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(344); 
  val x = DenseVector(-1.0, 0.0, 1.0, 2.0);System.out.println("""x  : breeze.linalg.DenseVector[Double] = """ + $show(x ));$skip(9); val res$0 = 
  x :* x;System.out.println("""res0: breeze.linalg.DenseVector[Double] = """ + $show(res$0));$skip(10); val res$1 = 
  x.t * x;System.out.println("""res1: Double = """ + $show(res$1));$skip(39); 

  val w = DenseMatrix((1, 2), (3, 4));System.out.println("""w  : breeze.linalg.DenseMatrix[Int] = """ + $show(w ));$skip(17); 

 val ww= w :* w;System.out.println("""ww  : breeze.linalg.DenseMatrix[Int] = """ + $show(ww ));$skip(7); val res$2 = 
ww.sum;System.out.println("""res2: Int = """ + $show(res$2))}

}
