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
import breeze.linalg.operators.DenseVector_HashVector_Ops

class TracedNetworkPropTest extends PropSpec with PropertyChecks with Matchers {

  val cGen = Gen.chooseNum(0.0, 1e3)

  val xGen = for {
    x1 <- Gen.chooseNum(-1.0, 1.0)
    x2 <- Gen.chooseNum(-1.0, 1.0)
  } yield DenseVector(x1, x2)

  val yGen = for {
    y <- Gen.chooseNum(0.0, 1.0)
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

  property("The cost of traced network should not increase learning step by step") {
    forAll((pGen, "p"), (xGen, "x"), (yGen, "y"), (cGen, "c")) { (p, x, y, c) =>
      {
        val sample = (x, y)
        val alpha = 1e-3;
        val lambda = 0.0;
        val n = 100;

        def loop(i: Int, net: TracedNetwork, prevCost: Double): Unit =
          if (i > 0) {
            val (n1, cost, _) = net.learn(sample, c)
            withClue(s"at step #${n - i + 1}: ") {
              cost should be <= prevCost
            }
            loop(i - 1, n1.update(alpha), cost)
          }

        val w1 = DenseMatrix((p(0), p(1), p(2)))
        val w2 = DenseMatrix((p(3), p(4)))
        val w3 = DenseMatrix((p(5), p(6)))

        loop(10, new TracedNetwork(w1, w2, w3, lambda), 1e308);
      }
    }
  }
}