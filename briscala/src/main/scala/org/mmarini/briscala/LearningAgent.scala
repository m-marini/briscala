package org.mmarini.briscala

import scala.util.Random
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.stats.distributions.RandBasis
import breeze.stats.distributions.Bernoulli

/**
 *
 */
class LearningAgent(vNet: TracedNetwork, qNet: TracedNetwork, parms: LearningParameters, epsilonGreedy: Double, random: RandBasis) {

  private val greedyProb = new Bernoulli(epsilonGreedy, random)

  /**
   *
   */
  def learn: LearningAgent = improve(createGame)

  /**
   *
   */
  def createGame: List[(Status, Option[Int])] = {
    def next(l: List[(Status, Option[Int])], s: Status): List[(Status, Option[Int])] =
      if (s.isCompleted)
        (s, None) :: l
      else {
        val c = selectAction(s)
        next((s, Some(c)) :: l, s.hidden.next(c))
      }
    next(List(), Game.createInitStatus(random))
  }

  /**
   *
   */
  def selectAction(s: Status): Int =
    if (s.numOfChoice == 1)
      0
    else if (greedyProb.draw)
      selectActionExploring(s)
    else
      selectActionExploiting(s)

  /**
   *
   */
  def selectActionExploring(s: Status): Int = random.randInt(s.numOfChoice).draw

  /**
   *
   */
  def selectActionExploiting(s: Status): Int =
    valueByAction(s).zipWithIndex.maxBy { case (v, _) => v }._2

  /**
   *
   */
  def valueByAction(s: Status): IndexedSeq[Double] =
    for (action <- 0 until s.numOfChoice) yield if (s.played.isEmpty)
      vNet(s.hidden.afterState(action).statusFeatures)(0)
    else
      qNet(s.hidden.actionFeatures(action))(0)

  /**
   *
   */
  def improve(game: List[(Status, Option[Int])]): LearningAgent = {
    // remove banal step (the ones with no choice or first hand steps)
    val noBanalSteps = game.filter { case (step, _) => step.numOfChoice > 1 && !step.played.isEmpty }
    // Split to player0 hand steps and player1 hand step
    val (p0, p1) = noBanalSteps.partition { case (step, _) => step.player0Turn }

    //generate returns for players
    val (finalState, _) = game.head
    val r0 = DenseVector(if (finalState.isWinner0) 1.0 else 0.0)
    val r1 = DenseVector(if (finalState.isWinner1) 1.0 else 0.0)

    //generate action features for player
    val qx0 = p0.map { case (s, a) => (s.hidden.actionFeatures(a.get), r0) }
    val qx1 = p1.map { case (s, a) => (s.hidden.actionFeatures(a.get), r1) }
    //generate state features for players
    val vx0 = p0.map { case (s, a) => (s.hiddenOpposite.statusFeatures, r1) }
    val vx1 = p1.map { case (s, a) => (s.hiddenOpposite.statusFeatures, r0) }

    def learnV(agent: LearningAgent, sample: (DenseVector[Double], DenseVector[Double])): LearningAgent =
      sample match {
        case (x, y) => agent.learnV(x, y)
      }
    def learnQ(agent: LearningAgent, sample: (DenseVector[Double], DenseVector[Double])): LearningAgent =
      sample match {
        case (x, y) => agent.learnQ(x, y)
      }

    val ag1 = (vx0.reverse.foldLeft(this))(learnV).update.clearTraces
    val ag2 = (vx1.reverse.foldLeft(ag1))(learnV).update.clearTraces
    val ag3 = (qx0.foldLeft(ag2))(learnQ).update.clearTraces
    (qx1.foldLeft(ag3))(learnQ).update.clearTraces
  }

  /**
   *
   */
  def learnV(x: DenseVector[Double], y: DenseVector[Double]): LearningAgent = {
    val (nv, _) = vNet.learn(x, y, parms.c, parms.lambda)
    new LearningAgent(nv, qNet, parms, epsilonGreedy, random)
  }

  /**
   *
   */
  def learnQ(x: DenseVector[Double], y: DenseVector[Double]): LearningAgent = {
    val (nq, _) = qNet.learn(x, y, parms.c, parms.lambda)
    new LearningAgent(vNet, nq, parms, epsilonGreedy, random)
  }

  /**
   *
   */
  def learn(status: Status, action: Int, win: Boolean): LearningAgent = {
    val y = DenseVector(if (win) 1.0 else 0.0)
    if (status.played.isEmpty)
      learnV(status.hidden.afterState(action).statusFeatures, y)
    else
      learnQ(status.hidden.actionFeatures(action), y)
  }

  /**
   *
   */
  def update: LearningAgent =
    new LearningAgent(vNet.update(parms.alpha), qNet.update(parms.alpha), parms, epsilonGreedy, random)

  /**
   *
   */
  def clearTraces: LearningAgent =
    new LearningAgent(vNet.clearTraces, qNet.clearTraces, parms, epsilonGreedy, random)

}

object LearningAgent {

  /**
   *
   */
  def defaultAgent(hiddenNeuros: Int, c: Double, alpha: Double, lambda: Double, epsilonGreedy: Double, random: RandBasis): LearningAgent =
    new LearningAgent(
      TracedNetwork.defaultNetwork(HiddenStatus.statusFeatureSize, hiddenNeuros, hiddenNeuros, 1),
      TracedNetwork.defaultNetwork(HiddenStatus.actionFeatureSize, hiddenNeuros, hiddenNeuros, 1),
      LearningParameters(c, alpha, lambda), epsilonGreedy, random)

  /**
   *
   */
  def rand(hiddenNeuros: Int, c: Double, alpha: Double, epsilonGreedy: Double, lambda: Double, random: RandBasis): LearningAgent =
    new LearningAgent(
      TracedNetwork.rand(HiddenStatus.statusFeatureSize, hiddenNeuros, hiddenNeuros, 1, random),
      TracedNetwork.rand(HiddenStatus.actionFeatureSize, hiddenNeuros, hiddenNeuros, 1, random),
      LearningParameters(c, alpha, lambda), epsilonGreedy, random)
}
