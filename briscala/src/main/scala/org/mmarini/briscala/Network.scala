/**
 *
 */
package org.mmarini.briscala

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.numerics.sigmoid

/**
 * @author us00852
 *
 */
class Network(w1: DenseMatrix[Double], w2: DenseMatrix[Double], w3: DenseMatrix[Double]) {
  private val one = DenseVector.ones[Double](1)

  /**
   *
   */
  def apply(x: DenseVector[Double]): DenseVector[Double] = compute(x)._3

  /**
   *
   */
  def learn(x: DenseVector[Double], y: DenseVector[Double], c: Double, alpha: Double): (Network, Double) = {
    val (cost, (g1, g2, g3)) = costAndGrad(x, y, c)
    val nw1 = w1 - g1 * alpha
    val nw2 = w2 - g2 * alpha
    val nw3 = w3 - g3 * alpha
    (new Network(nw1, nw2, nw3), cost)
  }

  /**
   *
   */
  private def compute(x: DenseVector[Double]): (DenseVector[Double], DenseVector[Double], DenseVector[Double]) = {
    val x2 = sigmoid(w1 * DenseVector.vertcat(one, x))
    val x3 = sigmoid(w2 * DenseVector.vertcat(one, x2))
    val x4 = sigmoid(w3 * DenseVector.vertcat(one, x3))
    (x2, x3, x4)
  }

  /**
   *
   */
  def costAndGrad(x: DenseVector[Double], y: DenseVector[Double], c: Double): (Double, (DenseMatrix[Double], DenseMatrix[Double], DenseMatrix[Double])) = {
    val (x2, x3, x4) = compute(x)

    val delta4 = x4 - y;
    val regw1 = w1(::, 1 to -1)
    val regw2 = w2(::, 1 to -1)
    val regw3 = w3(::, 1 to -1)
    val cost = (delta4.t * delta4 * c + (regw1 :* regw1).sum + (regw2 :* regw2).sum + (regw3 :* regw3).sum) / 2

    val dz4 = delta4 :* x4 :* (DenseVector.ones[Double](x4.size) - x4)
    val gradW3 = dz4 * DenseVector.vertcat(one, x3).t * c;
    gradW3(::, 1 to -1) += regw3

    val delta3 = w3(::, 1 to -1) * dz4;
    val dz3 = delta3 :* x3 :* (DenseVector.ones[Double](x3.size) - x3)
    val gradW2 = dz3 * DenseVector.vertcat(one, x2).t * c;
    gradW2(::, 1 to -1) += regw2

    val delta2 = w2(::, 1 to -1) * dz3;
    val dz2 = delta2 :* x2 :* (DenseVector.ones[Double](x2.size) - x2)
    val gradW1 = dz2 * DenseVector.vertcat(one, x).t * c;
    gradW1(::, 1 to -1) += regw1

    (cost, (gradW1, gradW2, gradW3))
  }

}