package org.mmarini.briscala

import breeze.stats.distributions.RandBasis

/**
 *
 */
case class SelectionParameters(
  hiddenNeuros: Int,
  epsilonGreedy: Double,
  learningParms: LearningParameters,
  learningIter: Int,
  trainGameCount: Int,
  validationGameCount: Int,
  randomGameCount: Int,
  populationCount: Int,
  eliminationCount: Int,
  mutationMean: Double) {

}