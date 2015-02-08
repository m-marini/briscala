/**
 *
 */
package org.mmarini.briscala

import org.apache.commons.math3.random.MersenneTwister

import breeze.stats.distributions.RandBasis

/**
 * @author us00852
 *
 */
object CommonRandomizers extends Randomizers {
  val trainSeed = 1
  val validationSeed = 2
  val testSeed = 3

  /**
   * Create a random generator for selection
   */
  def selectionRand = new RandBasis(new MersenneTwister)

  /**
   * Create a random generator for policies
   */
  def policyRand = new RandBasis(new MersenneTwister)

  /**
   * Create a random generator for training data sets
   */
  def trainRand = new RandBasis(new MersenneTwister)

  /**
   * Create a random generator for validation data sets
   */
  def validationRand = new RandBasis(new MersenneTwister)

  /**
   * Create a random generator for test data sets
   */
  def testRand: RandBasis = new RandBasis(new MersenneTwister)
}