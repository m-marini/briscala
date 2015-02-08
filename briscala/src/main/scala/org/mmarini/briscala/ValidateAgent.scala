package org.mmarini.briscala

import breeze.linalg.DenseVector
import breeze.stats.distributions.Bernoulli
import breeze.stats.distributions.RandBasis
import scalax.file.Path
import scalax.io.StandardOpenOption.Val
import scalax.io.StandardOpenOption.WriteAppend
import breeze.linalg.DenseMatrix
import scalax.io.Resource
import java.io.IOException

/**
 *
 */
class ValidationAgent(val validationGameCount: Int) extends AbstractAgent {
  private val validationRand = CommonRandomizers.validationRand

  /**
   *
   */
  def play(p0: Policy, p1: Policy): (Boolean, Boolean) = {
    val finalState = Game.createGame(p0, p1, validationRand).head._1
    (finalState.isWinner0, finalState.isWinner1)
  }
}
