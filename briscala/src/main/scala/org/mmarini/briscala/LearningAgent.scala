package org.mmarini.briscala

import scala.util.Random
import breeze.linalg.DenseMatrix

/**
 *
 */
class LearningAgent(vNet: Network, qNet: Network, c: Double, alpha: Double, epsilonGreedy: Double, lambda: Double, random: Random) {

  /**
   *
   */
  def learn: (Network, Network) = improveNetwork(createGame)

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
    else if (random.nextDouble < epsilonGreedy)
      selectActionExploring(s)
    else
      selectActionExploiting(s)

  /**
   *
   */
  def selectActionExploring(s: Status): Int = random.nextInt(s.numOfChoice)

  /**
   *
   */
  def selectActionExploiting(s: Status): Int =
    if (s.played.isEmpty)
      // use after state
      selectActionByV(s)
    else
      // use action values
      selectActionByQ(s)

  /**
   * Select the best action by V
   *
   * For each action evaluate V(s') in the after-state e and choose the higher value
   */
  def selectActionByV(status: Status): Int = {
    ???
  }

  /**
   * Select the best action by q
   *
   * For each action evaluate Q(s, a') and choose the higher value
   */
  def selectActionByQ(status: Status): Int = {
    ???
  }

  /**
   *
   */
  def improveNetwork(game: List[(Status, Option[Int])]): (Network, Network) = ???
}

object LearningAgent {

  def rand(hiddenNeuros: Int, c: Double, alpha: Double, epsilonGreedy: Double, lambda: Double, random: Random): LearningAgent = {
    val s1 = HiddenStatus.statusFeatureSize
    val s2 = hiddenNeuros
    val s3 = hiddenNeuros
    val s4 = 1

    val vw1 = DenseMatrix.rand(s2, s1 + 1);
    val vw2 = DenseMatrix.rand(s3, s2 + 1);
    val vw3 = DenseMatrix.rand(s4, s3 + 1);

    val vNet = new Network(vw1, vw2, vw3)

    val qw1 = DenseMatrix.rand(s2, s1 + 1);
    val qw2 = DenseMatrix.rand(s3, s2 + 1);
    val qw3 = DenseMatrix.rand(s4, s3 + 1);

    val qNet = new Network(qw1, qw2, qw3)

    new LearningAgent(vNet, qNet, c, alpha, epsilonGreedy, lambda, random)
  }
}
