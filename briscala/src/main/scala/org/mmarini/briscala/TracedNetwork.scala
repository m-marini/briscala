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
import scalax.io.Output
import scala.util.matching.Regex
import scalax.io.LongTraversable

/**
 * @author us00852
 *
 */
class TracedNetwork(val w1: DMatrix, val w2: DMatrix, val w3: DMatrix, e1: DMatrix, e2: DMatrix, e3: DMatrix, val lambda: Double) extends Network(w1, w2, w3) {

  /**
   *
   */
  def this(w1: DMatrix, w2: DMatrix, w3: DMatrix, lambda: Double) =
    this(w1, w2, w3,
      DenseMatrix.zeros(w1.rows, w1.cols),
      DenseMatrix.zeros(w2.rows, w2.cols),
      DenseMatrix.zeros(w3.rows, w3.cols),
      lambda)

  /**
   * Learn an output for a given input.
   * It creates a new network with eligibility traces updated and the current cost value
   */
  def learn(sample: Sample, c: Double): (TracedNetwork, Double, Double) = {
    val (cost, err, (g1, g2, g3)) = costAndGrad(sample, c)
    (new TracedNetwork(w1, w2, w3,
      e1 * lambda - g1,
      e2 * lambda - g2,
      e3 * lambda - g3,
      lambda),
      cost, err)
  }

  /**
   * Learn an output for a given input.
   * It creates a new network with eligibility traces updated and the current cost value
   */
  def learn(samples: List[(DVector, DVector)], c: Double): (TracedNetwork, Double, Double) =
    samples.foldLeft(this, 0.0, 0.0)((acc: (TracedNetwork, Double, Double), sample: (DVector, DVector)) => {
      val (net, cost, err) = acc
      val (n1, c1, e) = net.learn(sample, c)
      (n1, cost + c1, err + e)
    })

  /**
   * Update the network with the eligibility traces.
   * It creates
   */
  def update(alpha: Double): TracedNetwork =
    new TracedNetwork(
      w1 + e1 * alpha,
      w2 + e2 * alpha,
      w3 + e3 * alpha,
      e1, e2, e3,
      lambda)

  /**
   * Update the network with the eligibility traces.
   * It creates
   */
  def clearTraces: TracedNetwork = new TracedNetwork(w1, w2, w3, lambda)
}

/**
 *
 */
object TracedNetwork {
  /**
   *
   */
  def rand(s1: Int, s2: Int, s3: Int, s4: Int, lambda: Double, epsilon: Double, random: RandBasis): TracedNetwork = {
    val w1 = (DenseMatrix.rand(s2, s1 + 1, random.uniform) * 2.0 - DenseMatrix.ones[Double](s2, s1 + 1)) * epsilon
    val w2 = (DenseMatrix.rand(s3, s2 + 1, random.uniform) * 2.0 - DenseMatrix.ones[Double](s3, s2 + 1)) * epsilon
    val w3 = (DenseMatrix.rand(s4, s3 + 1, random.uniform) * 2.0 - DenseMatrix.ones[Double](s4, s3 + 1)) * epsilon
    new TracedNetwork(w1, w2, w3, lambda)
  }

  /**
   *
   */
  def rand(s1: Int, s2: Int, s3: Int, s4: Int, lambda: Double, random: RandBasis): TracedNetwork = {
    val epsilon = sqrt(6.0 / (s1 + s2 + s3 + s4));
    rand(s1, s2, s3, s4, lambda, epsilon, random)
  }

  /**
   *
   */
  def defaultNetwork(s1: Int, s2: Int, s3: Int, s4: Int, lambda: Double): TracedNetwork =
    new TracedNetwork(DenseMatrix.zeros[Double](s2, s1 + 1),
      DenseMatrix.zeros[Double](s3, s2 + 1),
      DenseMatrix.zeros[Double](s4, s3 + 1),
      lambda)
}