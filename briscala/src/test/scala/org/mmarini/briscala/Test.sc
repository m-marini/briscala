
import org.mmarini.briscala._
import scala.util.Random
import breeze.linalg.Vector
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix
import breeze.numerics._
import breeze.linalg.operators.DenseVector_SpecialOps
import breeze.linalg.operators.DenseVector_HashVector_Ops

object Test {
  val x = DenseVector(-1.0, 0.0, 1.0, 2.0)        //> x  : breeze.linalg.DenseVector[Double] = DenseVector(-1.0, 0.0, 1.0, 2.0)
  x :* x                                          //> res0: breeze.linalg.DenseVector[Double] = DenseVector(1.0, 0.0, 1.0, 4.0)
  x.t * x                                         //> gen 05, 2015 2:46:34 PM com.github.fommil.netlib.BLAS <clinit>
                                                  //| AVVERTENZA: Failed to load implementation from: com.github.fommil.netlib.Nat
                                                  //| iveSystemBLAS
                                                  //| gen 05, 2015 2:46:34 PM com.github.fommil.netlib.BLAS <clinit>
                                                  //| AVVERTENZA: Failed to load implementation from: com.github.fommil.netlib.Nat
                                                  //| iveRefBLAS
                                                  //| res1: Double = 6.0

  val w = DenseMatrix((1, 2), (3, 4))             //> w  : breeze.linalg.DenseMatrix[Int] = 1  2  
                                                  //| 3  4  

 val ww= w :* w                                   //> ww  : breeze.linalg.DenseMatrix[Int] = 1  4   
                                                  //| 9  16  
ww.sum                                            //> res2: Int = 30

}