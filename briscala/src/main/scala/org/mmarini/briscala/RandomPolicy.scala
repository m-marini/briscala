/**
 *
 */
package org.mmarini.briscala

import breeze.stats.distributions.RandBasis

/**
 * @author us00852
 *
 */
class RandomPolicy(random: RandBasis) extends Policy {

  /**
   *
   */
  def selectAction(state: HiddenStatus): Int = {
    val n = state.numOfChoice
    if (n <= 1)
      0
    else
      random.randInt(n).draw
  }
}