package org.mmarini.briscala

import org.scalatest._
import scala.util.Random
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import scala.math._
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import breeze.linalg.operators.DenseVector_GenericOps

class NetworkPropTest extends PropSpec with PropertyChecks with Matchers {

  property("The gradient of cost should be similar to the increment computed gradient") {

    def cGen = Gen.chooseNum(0.0, 1000.0)
    val xGen = for {
      x1 <- Gen.chooseNum(-1.0, 1.0)
      x2 <- Gen.chooseNum(-1.0, 1.0)
    } yield DenseVector(x1, x2)
    val yGen = for {
      y <- Gen.chooseNum(0.0, 0.0)
    } yield DenseVector(y)

    val pGen = for {
      p1 <- Gen.chooseNum(0.0, 10.0)
      p2 <- Gen.chooseNum(0.0, 10.0)
      p3 <- Gen.chooseNum(0.0, 10.0)
      p4 <- Gen.chooseNum(0.0, 10.0)
      p5 <- Gen.chooseNum(0.0, 10.0)
      p6 <- Gen.chooseNum(0.0, 10.0)
      p7 <- Gen.chooseNum(0.0, 10.0)
    } yield DenseVector(p1, p2, p3, p4, p5, p6, p7)

    forAll((pGen, "p"), (xGen, "x"), (yGen, "y"), (cGen, "c")) { (p, x, y, c) =>
      whenever(c == 1.0) {
        val w1 = DenseMatrix((p(0), p(1), p(2)))
        val w2 = DenseMatrix((p(3), p(4)))
        val w3 = DenseMatrix((p(5), p(6)))

        val (cost, grad) = new Network(w1, w2, w3).costAndGrad(x, y, c)

        val epsilon = 1e-5;

        val (cost1, _) = new Network(DenseMatrix((p(0) + epsilon, p(1), p(2))), w2, w3).costAndGrad(x, y, c)
        val (cost2, _) = new Network(DenseMatrix((p(0), p(1) + epsilon, p(2))), w2, w3).costAndGrad(x, y, c)
        val (cost3, _) = new Network(DenseMatrix((p(0), p(1), p(2) + epsilon)), w2, w3).costAndGrad(x, y, c)

        val (cost4, _) = new Network(w1, DenseMatrix((p(3) + epsilon, p(4))), w3).costAndGrad(x, y, c)
        val (cost5, _) = new Network(w1, DenseMatrix((p(3), p(4) + epsilon)), w3).costAndGrad(x, y, c)

        val (cost6, _) = new Network(w1, w2, DenseMatrix((p(5) + epsilon, p(6)))).costAndGrad(x, y, c)
        val (cost7, _) = new Network(w1, w2, DenseMatrix((p(5), p(6) + epsilon))).costAndGrad(x, y, c)

        val c1 = DenseVector(cost1, cost2, cost3, cost4, cost5, cost6, cost7)
        val cs = DenseVector.fill(7) { cost }
        val g1 = (c1 - cs) / epsilon

        val g = DenseVector(grad._1(0, 0),
          grad._1(0, 1),
          grad._1(0, 2),
          grad._2(0, 0),
          grad._2(0, 1),
          grad._3(0, 0),
          grad._3(0, 1))

        val errs = (g1 - g)

        val err = errs.t * errs

        //        val avg = g.t * g

        err should be <= 1e-5
      }
    }
  }
}