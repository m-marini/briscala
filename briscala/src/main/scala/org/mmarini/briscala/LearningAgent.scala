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
class LearningAgent(parms: LearningParameters, trainingCount: Int, testCount: Int, learningIter: Int) {
  val trainRand = CommonRandomizers.trainRand
  val validationRand = CommonRandomizers.validationRand
  val testRand = CommonRandomizers.testRand
  val randomPolicy = new RandomPolicy(CommonRandomizers.policyRand)

  /**
   *
   */
  def learn(p: TDPolicy, p0: TDPolicy): (TDPolicy, TDPolicy, (Double, Double, Double, Double)) = {

    def learnLoop(i: Int, ctx: (TDPolicy, Double)): (TDPolicy, Double) =
      if (i <= 0)
        ctx
      else {
        val (p, cost) = ctx
        learnLoop(i - 1, playToLearn(p, p0) match {
          case (np, c) => (np, cost + c)
        })
      }

    val (np, cost) = learnLoop(trainingCount, (p, 0.0))

    def validate(i: Int, playF: () => (Int, Int), ctx: (Int, Int)): (Int, Int) =
      if (i <= 0)
        ctx
      else {
        val (trainWon, testWon) = ctx
        validate(i - 1, playF, playF() match {
          case (trw, tsw) => (trainWon + trw, testWon + tsw)
        })
      }

    val (trainWon, testWon) = validate(testCount, playToValidate(np, p0, validationRand), (0, 0))

    val (randTrainWon, randWon) = validate(testCount, playToValidate(np, randomPolicy, testRand), (0, 0))

    if (trainWon > testWon)
      (np, np.greedy,
        (cost / trainingCount,
          trainWon.toDouble / testCount,
          testWon.toDouble / testCount,
          randTrainWon.toDouble / testCount))
    else
      (np, p0,
        (cost / trainingCount,
          trainWon.toDouble / testCount,
          testWon.toDouble / testCount,
          randTrainWon.toDouble / testCount))
  }

  /**
   *
   */
  def playToLearn(p: TDPolicy, p0: TDPolicy): (TDPolicy, Double) = {
    val game = Game.createGame(p, p0, trainRand)
    val last = game.last._1
    val (vSamples, qSamples) = extrapolateSamples(game)

    val n = vSamples.map(_.size).sum + qSamples.map(_.size).sum

    val ctx = vSamples.foldLeft(p, 0.0)((ctx, samples) => ctx match {
      case (p, cost) => p.learnV(samples, parms) match {
        case (np, c, _) => (np, cost + c)
      }
    })
    qSamples.foldLeft(ctx)((ctx, samples) => ctx match {
      case (p, cost) => p.learnQ(samples, parms) match {
        case (np, c, _) => (np, cost + c)
      }
    }) match {
      case (p, c) => (p, c / n)
    }
  }

  /**
   *
   */
  def playToValidate(p: Policy, p0: Policy, random: RandBasis)(): (Int, Int) = {
    val finalState = Game.createGame(p, p0, random).head._1
    val won = if (finalState.isWinner) 1 else 0
    val lost = if (finalState.isLooser) 1 else 0
    if (finalState.isPlayer0)
      (won, lost)
    else
      (lost, won)
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
}
