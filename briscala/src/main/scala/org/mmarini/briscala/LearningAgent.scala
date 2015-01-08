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
        next((s, Some(c)) :: l, s.nextStatus(c))
      }

    next(List(), Game.createInitStatus(random))
  }

  /**
   *
   */
  def selectAction(s: Status): Int =
    if (s.numOfChoice == 1)
      0
    else if (new Bernoulli(epsilonGreedy, random).draw)
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
      vNet(HiddenStatus(s, s.player0Turn).afterState(action).statusFeatures)(0)
    else
      qNet(HiddenStatus(s, s.player0Turn).actionFeatures(action))(0)

  /**
   *
   */
  def improve(game: List[(Status, Option[Int])]): LearningAgent = ???

  /**
   *
   */
  def learn(status: Status, action: Int, win: Boolean): LearningAgent = {
    val y = DenseVector(if (win) 1.0 else 0.0)
    if (status.played.isEmpty) {
      val x = HiddenStatus(status, status.player0Turn).afterState(action).statusFeatures
      val (nv, _) = vNet.learn(x, y, parms.c, parms.lambda)
      new LearningAgent(nv, qNet, parms, epsilonGreedy, random)
    } else {
      val x = HiddenStatus(status, status.player0Turn).actionFeatures(action)
      val (nq, _) = qNet.learn(x, y, parms.c, parms.lambda)
      new LearningAgent(vNet, nq, parms, epsilonGreedy, random)
    }
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
