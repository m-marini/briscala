
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

object Test {

  val g = new JDKRandomGenerator()                //> g  : org.apache.commons.math3.random.JDKRandomGenerator = org.apache.commons
                                                  //| .math3.random.JDKRandomGenerator@1dbc21fd
  g.setSeed(1)

  val r = new RandBasis(g)                        //> r  : breeze.stats.distributions.RandBasis = breeze.stats.distributions.RandB
                                                  //| asis@751dc8af

 r.permutation(40).draw                           //> res0: IndexedSeq[Int] = ArrayBuffer(38, 21, 23, 33, 35, 7, 32, 36, 15, 29, 1
                                                  //| , 24, 3, 6, 14, 37, 20, 4, 2, 22, 27, 18, 16, 31, 9, 12, 0, 28, 5, 19, 8, 30
                                                  //| , 39, 10, 34, 26, 11, 17, 13, 25)
}