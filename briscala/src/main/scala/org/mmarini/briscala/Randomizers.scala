/**
 *
 */
package org.mmarini.briscala

import breeze.stats.distributions.RandBasis

/**
 * @author us00852
 *
 */
trait Randomizers {
  /**
   *
   */
  def trainRand: RandBasis

  /**
   *
   */
  def validationRand: RandBasis

  /**
   *
   */
  def policyRand: RandBasis

  /**
   *
   */
  def testRand: RandBasis
}