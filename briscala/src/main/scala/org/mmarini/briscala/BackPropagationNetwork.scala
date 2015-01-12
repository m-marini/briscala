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
class BackPropagationNetwork(w1: DMatrix, w2: DMatrix, w3: DMatrix) extends Network(w1, w2, w3) {

  /**
   *
   */
  def learn(sample: Sample, c: Double, alpha: Double): (BackPropagationNetwork, Double, Double) = {
    val (cost, err, (g1, g2, g3)) = costAndGrad(sample, c)
    (new BackPropagationNetwork(w1 - g1 * alpha, w2 - g2 * alpha, w3 - g3 * alpha), cost, err)
  }
}

/**
 *
 */
object BackPropagationNetwork {
  /**
   *
   */
  def rand(s1: Int, s2: Int, s3: Int, s4: Int, epsilon: Double, random: RandBasis): Network = {
    val w1 = (DenseMatrix.rand(s2, s1 + 1, random.uniform) * 2.0 - DenseMatrix.ones[Double](s2, s1 + 1)) * epsilon
    val w2 = (DenseMatrix.rand(s3, s2 + 1, random.uniform) * 2.0 - DenseMatrix.ones[Double](s3, s2 + 1)) * epsilon
    val w3 = (DenseMatrix.rand(s4, s3 + 1, random.uniform) * 2.0 - DenseMatrix.ones[Double](s4, s3 + 1)) * epsilon
    new Network(w1, w2, w3)
  }

  /**
   *
   */
  def rand(s1: Int, s2: Int, s3: Int, s4: Int, random: RandBasis): Network = {
    val epsilon = sqrt(6.0 / (s1 + s2 + s3 + s4));
    rand(s1, s2, s3, s4, epsilon, random)
  }

  /**
   *
   */
  def defaultNetwork(s1: Int, s2: Int, s3: Int, s4: Int): Network =
    new Network(DenseMatrix.zeros[Double](s2, s1 + 1),
      DenseMatrix.zeros[Double](s3, s2 + 1),
      DenseMatrix.zeros[Double](s4, s3 + 1))

}