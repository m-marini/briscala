
import org.mmarini.briscala._
import scala.util.Random
import breeze.linalg.Vector
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix
import breeze.numerics._
import breeze.linalg.operators.DenseVector_SpecialOps
import breeze.linalg.operators.DenseVector_HashVector_Ops
import breeze.stats.distributions.RandBasis
import breeze.stats.distributions.Rand
import breeze.stats.distributions.Uniform
import org.apache.commons.math3.random.JDKRandomGenerator

object Test {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(520); 

  val g = new JDKRandomGenerator();System.out.println("""g  : org.apache.commons.math3.random.JDKRandomGenerator = """ + $show(g ));$skip(15); 
  g.setSeed(1);$skip(28); 

  val r = new RandBasis(g);System.out.println("""r  : breeze.stats.distributions.RandBasis = """ + $show(r ));$skip(25); val res$0 = 

 r.permutation(40).draw;System.out.println("""res0: IndexedSeq[Int] = """ + $show(res$0))}
}
