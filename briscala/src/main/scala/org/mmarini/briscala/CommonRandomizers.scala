/**
 *
 */
package org.mmarini.briscala

import breeze.stats.distributions.RandBasis
import org.apache.commons.math3.random.JDKRandomGenerator
import org.apache.commons.math3.random.MersenneTwister
import sbt.testing.TestSelector

/**
 * @author us00852
 *
 */
object CommonRandomizers extends Randomizers {
  val trainSeed = 1
  val validationSeed = 2
  val testSeed = 3

  val policyRand = new RandBasis(new MersenneTwister)

  /**
   *
   */
  //  def trainRand: RandBasis = new RandBasis(new MersenneTwister(trainSeed))
  val trainRand = policyRand

  /**
   *
   */
  //def validationRand: RandBasis = new RandBasis(new MersenneTwister(validationSeed))
  val validationRand = policyRand

  /**
   *
   */
  // def testRand: RandBasis = new RandBasis(new MersenneTwister(testSeed))
  val testRand: RandBasis = policyRand
}