/**
 *
 */
package org.mmarini.briscala

import scala.math.sqrt
import scala.util.Random
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.numerics.sigmoid
import breeze.stats.distributions.Rand
import breeze.stats.distributions.RandBasis
import org.apache.commons.math3.random.RandomGenerator
import breeze.stats.distributions.Uniform

/**
 * @author us00852
 *
 */
class Network(w1: DMatrix, w2: DMatrix, w3: DMatrix) {

  /**
   *
   */
  def apply(x: DVector): DVector = compute(x)._3

  /**
   *
   */
  private def compute(x: DVector): (DVector, DVector, DVector) = {
    val x2 = sigmoid(w1 * DenseVector.vertcat(One, x))
    val x3 = sigmoid(w2 * DenseVector.vertcat(One, x2))
    val x4 = sigmoid(w3 * DenseVector.vertcat(One, x3))
    (x2, x3, x4)
  }

  /**
   *
   */
  def costAndGrad(sample: Sample, c: Double): (Double, Double, (DMatrix, DMatrix, DMatrix)) = {
    val (x, y) = sample
    val (x2, x3, x4) = compute(x)

    val delta4 = x4 - y;
    val regw1 = w1(::, 1 to -1)
    val regw2 = w2(::, 1 to -1)
    val regw3 = w3(::, 1 to -1)
    val cost = (delta4.t * delta4 * c + (regw1 :* regw1).sum + (regw2 :* regw2).sum + (regw3 :* regw3).sum) / 2

    val dz4 = delta4 :* x4 :* (DenseVector.ones[Double](x4.size) - x4)
    val gradW3 = dz4 * DenseVector.vertcat(One, x3).t * c;
    gradW3(::, 1 to -1) += regw3

    val delta3 = regw3.t * dz4;
    val dz3 = delta3 :* x3 :* (DenseVector.ones[Double](x3.size) - x3)
    val gradW2 = dz3 * DenseVector.vertcat(One, x2).t * c;
    gradW2(::, 1 to -1) += regw2

    val delta2 = regw2.t * dz3;
    val dz2 = delta2 :* x2 :* (DenseVector.ones[Double](x2.size) - x2)
    val gradW1 = dz2 * DenseVector.vertcat(One, x).t * c;
    gradW1(::, 1 to -1) += regw1

    val err = delta4.t * delta4
    (cost, err, (gradW1, gradW2, gradW3))
  }
}
