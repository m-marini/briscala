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
class LearningAgent(vNet: TracedNetwork, qNet: TracedNetwork, parms: LearningParameters, epsilonGreedy: Double, random: RandBasis) {

  private val greedyProb = new Bernoulli(epsilonGreedy, random)

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

    val vErr = vSamples.foldLeft(0.0)((err: Double, samples: List[Sample]) => {
      samples.foldLeft(err)((err: Double, samples: Sample) => {
        val d = (vNet(samples._1) - samples._2)
        err + d.t * d
      })
    })

    qSamples.foldLeft(vErr)((err: Double, samples: List[Sample]) => {
      samples.foldLeft(err)((err: Double, samples: Sample) => {
        val d = (qNet(samples._1) - samples._2)
        err + d.t * d
      })
    })
  }

  /**
   *
   */
  def createGame: List[(Status, Option[Int])] = {
    def next(l: List[(Status, Option[Int])], s: Status): List[(Status, Option[Int])] =
      if (s.isCompleted)
        (s, None) :: l
      else {
        val c = selectAction(s)
        next((s, Some(c)) :: l, s.playerHidden.next(c))
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
      vNet(s.playerHidden.afterState(action).statusFeatures)(0)
    else
      qNet(s.playerHidden actionFeatures (action))(0)

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
    (List(vx0, vx1), List(qx0, qx1))
  }

  /**
   *
   */
  def improve(game: List[(Status, Option[Int])]): (LearningAgent, Double, Double) = {
    val (vSamples, qSamples) = extrapolateSamples(game)

    def loop(acc: (LearningAgent, Double, Double), i: Int): (LearningAgent, Double, Double) =
      if (i <= 0)
        acc
      else {
        val (a, c, e) = acc
        val acc1 = vSamples.foldLeft(a, 0.0, 0.0)((acc: (LearningAgent, Double, Double), samples: List[Sample]) => {
          val (a, c, e) = acc
          a.learnV(samples) match {
            case (a1, c1, e1) => (a1, c + c1, e + e1)
          }
        })
        qSamples.foldLeft(acc1)((acc: (LearningAgent, Double, Double), samples: List[Sample]) => {
          val (a, c, e) = acc
          a.learnQ(samples) match {
            case (a1, c1, e1) => (a1, c + c1, e + e1)
          }
        })
      }

    loop((this, 0.0, 0.0), parms.learnIterations)
  }

  /**
   *
   */
  def learnV(samples: List[Sample]): (LearningAgent, Double, Double) = {
    val (net, cost, err) = vNet.learn(samples, parms.c)
    (new LearningAgent(net.update(parms.alpha).clearTraces, qNet, parms, epsilonGreedy, random), cost, err)
  }

  /**
   *
   */
  def learnQ(samples: List[Sample]): (LearningAgent, Double, Double) = {
    val (net, cost, err) = qNet.learn(samples, parms.c)
    (new LearningAgent(vNet, net.update(parms.alpha).clearTraces, parms, epsilonGreedy, random), cost, err)
  }

  /**
   *
   */
  def save(filename: String) = {
    val out = Path(filename).delete().outputStream(WriteAppend: _*)
    MathFile.save(out, Map(
      ("vw1" -> vNet.w1),
      ("vw2" -> vNet.w2),
      ("vw3" -> vNet.w3),
      ("vLambda" -> vNet.lambda),
      ("qw1" -> qNet.w1),
      ("qw2" -> qNet.w2),
      ("qw3" -> qNet.w3),
      ("qLambda" -> qNet.lambda)))
  }
}

object LearningAgent {
  /**
   *
   */
  def defaultAgent(hiddenNeuros: Int, c: Double, alpha: Double, learnIterations: Int, lambda: Double, epsilonGreedy: Double, random: RandBasis): LearningAgent =
    new LearningAgent(
      TracedNetwork.defaultNetwork(HiddenStatus.statusFeatureSize, hiddenNeuros, hiddenNeuros, 1, lambda),
      TracedNetwork.defaultNetwork(HiddenStatus.actionFeatureSize, hiddenNeuros, hiddenNeuros, 1, lambda),
      LearningParameters(c, alpha, learnIterations), epsilonGreedy, random)

  /**
   *
   */
  def rand(hiddenNeuros: Int, c: Double, alpha: Double, learnIterations: Int, lambda: Double, epsilonGreedy: Double, random: RandBasis): LearningAgent =
    new LearningAgent(
      TracedNetwork.rand(HiddenStatus.statusFeatureSize, hiddenNeuros, hiddenNeuros, 1, lambda, random),
      TracedNetwork.rand(HiddenStatus.actionFeatureSize, hiddenNeuros, hiddenNeuros, 1, lambda, random),
      LearningParameters(c, alpha, learnIterations), epsilonGreedy, random)

  /**
   *
   */
  def load(filename: String, c: Double, alpha: Double, learnIterations: Int, epsilonGreedy: Double, random: RandBasis): LearningAgent = {
    val vars = MathFile.load(Resource.fromFile(filename).lines())
    if (!(vars.contains("vw1") &&
      vars.contains("vw2") &&
      vars.contains("vw3") &&
      vars.contains("vLambda") &&
      vars.contains("qw1") &&
      vars.contains("qw2") &&
      vars.contains("qw3") &&
      vars.contains("qLambda")))
      throw new IOException("Missing variables")
    else {
      val vNet = new TracedNetwork(
        vars("vw1").asInstanceOf[DMatrix],
        vars("vw2").asInstanceOf[DMatrix],
        vars("vw3").asInstanceOf[DMatrix],
        vars("vLambda").asInstanceOf[Double])
      val qNet = new TracedNetwork(
        vars("qw1").asInstanceOf[DMatrix],
        vars("qw2").asInstanceOf[DMatrix],
        vars("qw3").asInstanceOf[DMatrix],
        vars("qLambda").asInstanceOf[Double])
      new LearningAgent(vNet, qNet, LearningParameters(c, alpha, learnIterations), epsilonGreedy, random)
    }
  }
}
