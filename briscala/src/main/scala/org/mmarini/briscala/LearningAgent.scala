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
class LearningAgent(policy: Policy, parms: LearningParameters, learningIter: Int, random: RandBasis) {

  /**
   *
   */
  def learn: (LearningAgent, Double, Double) = improve(createGame)

  /**
   *
   */
  def play: Double = computeErrors(createGame)

  /**
   *
   */
  def computeErrors(game: List[(Status, Option[Int])]): Double = {
    val (vSamples, qSamples) = extrapolateSamples(game)

    val vErr = vSamples.foldLeft(0.0)((err, samples) => err + policy.errV(samples))

    qSamples.foldLeft(vErr)((err, samples) => err + policy.errQ(samples)) / (19 * 2)
  }

  /**
   *
   */
  def createGame: List[(Status, Option[Int])] = {
    def next(l: List[(Status, Option[Int])], s: Status): List[(Status, Option[Int])] =
      if (s.isCompleted)
        (s, None) :: l
      else {
        val h = s.playerHidden
        val c = policy.selectAction(h)
        next((s, Some(c)) :: l, h.next(c))
      }
    next(List(), Game.createInitStatus(random))
  }

  /**
   *
   */
  def extrapolateSamples(game: List[(Status, Option[Int])]): (List[List[Sample]], List[List[Sample]]) = {

    // remove banal step (the ones with no choice or first hand steps)
    val noBanalSteps = game.filter { case (step, _) => step.numOfChoice > 1 && !step.isFirstHand }
    // Split to player0 hand steps and player1 hand step
    val (p0, p1) = noBanalSteps.partition { case (step, _) => step.isPlayer0 }

    //generate returns for players
    val (finalState, _) = game.head
    val winner = if (finalState.isWinner) One else Zero
    val looser = if (finalState.isLooser) One else Zero

    val r0 = if (finalState.isPlayer0) winner else looser
    val r1 = if (!finalState.isPlayer0) winner else looser

    //generate action features for player
    val qx0 = p0.map { case (s, a) => (s.playerHidden.actionFeatures(a.get), r0) }
    val qx1 = p1.map { case (s, a) => (s.playerHidden.actionFeatures(a.get), r1) }

    //generate state features for players
    val vx0 = p0.map { case (s, a) => (s.oppositeHidden.statusFeatures, r1) }
    val vx1 = p1.map { case (s, a) => (s.oppositeHidden.statusFeatures, r0) }
    (List(vx0.reverse, vx1.reverse), List(qx0.reverse, qx1.reverse))
  }

  /**
   *
   */
  def improve(game: List[(Status, Option[Int])]): (LearningAgent, Double, Double) = {
    val (vSamples, qSamples) = extrapolateSamples(game)

    def loop(ctx: (Policy, Double, Double), i: Int): (Policy, Double, Double) =
      if (i <= 0)
        ctx
      else {
        val (a, _, _) = ctx
        val ctx1 = vSamples.foldLeft(a, 0.0, 0.0)((ctx, samples) => {
          val (policy, cost, err) = ctx
          policy.learnV(samples, parms) match {
            case (p, c, e) => (p, c + cost, e + err)
          }
        })
        qSamples.foldLeft(ctx)((ctx, samples) => {
          val (policy, cost, err) = ctx
          policy.learnQ(samples, parms) match {
            case (p, c, e) => (p, c + cost, e + cost)
          }
        })
      }

    loop((policy, 0.0, 0.0), learningIter) match {
      case (a, c, e) => (new LearningAgent(policy, parms, learningIter, random), c / (19 * 2), e / (19 * 2))
    }
  }

  /**
   *
   */
  def save(filename: String) = policy.save(filename)
}
