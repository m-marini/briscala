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
  def learn: (LearningAgent, Double) = improve(createGame)

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

  type Sample = (DenseVector[Double], DenseVector[Double])

  import LearningAgent.One
  import LearningAgent.Zero

  /**
   *
   */
  def extrapolateSamples(game: List[(Status, Option[Int])]): (List[List[Sample]], List[List[Sample]]) = {

    // remove banal step (the ones with no choice or first hand steps)
    val noBanalSteps = game.filter { case (step, _) => step.numOfChoice > 1 && !step.played.isEmpty }
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
  def improve(game: List[(Status, Option[Int])]): (LearningAgent, Double) = {
    // remove banal step (the ones with no choice or first hand steps)
    val noBanalSteps = game.filter { case (step, _) => step.numOfChoice > 1 && !step.played.isEmpty }
    // Split to player0 hand steps and player1 hand step
    val (p0, p1) = noBanalSteps.partition { case (step, _) => step.isPlayer0 }

    //generate returns for players
    val (finalState, _) = game.head
    val winner = DenseVector(if (finalState.isWinner) 1.0 else 0.0)
    val looser = DenseVector(if (finalState.isLooser) 1.0 else 0.0)

    val r0 = if (finalState.isPlayer0) winner else looser
    val r1 = if (!finalState.isPlayer0) winner else looser

    //generate action features for player
    val qx0 = p0.map { case (s, a) => (s.playerHidden.actionFeatures(a.get), r0) }
    val qx1 = p1.map { case (s, a) => (s.playerHidden.actionFeatures(a.get), r1) }

    //generate state features for players
    val vx0 = p0.map { case (s, a) => (s.oppositeHidden.statusFeatures, r1) }
    val vx1 = p1.map { case (s, a) => (s.oppositeHidden.statusFeatures, r0) }

    val (vSamples, qSamples) = extrapolateSamples(game)

    def learnV(acc: (LearningAgent, Double), sample: (DenseVector[Double], DenseVector[Double])): (LearningAgent, Double) = {
      val (agent, cost) = acc
      sample match {
        case (x, y) => agent.learnV(x, y) match {
          case (ag2, cost2) => (ag2, cost + cost2)
        }
      }
    }

    def learnQ(acc: (LearningAgent, Double), sample: (DenseVector[Double], DenseVector[Double])): (LearningAgent, Double) = {
      val (agent, cost) = acc
      sample match {
        case (x, y) => agent.learnQ(x, y) match {
          case (ag2, cost2) => (ag2, cost + cost2)
        }
      }
    }

    def loop(a: LearningAgent, cost: Double, i: Int): (LearningAgent, Double) =
      if (i <= 0)
        (a, cost)
      else {
        val (ag1, cost1) = (vSamples.foldLeft(a, 0.0))((acc: (LearningAgent, Double), samples: List[Sample]) => {
          val (a, c) = (samples.reverse.foldLeft(acc))(learnV)
          (a.update.clearTraces, c)
        })
        val (ag2, cost2) = (qSamples.foldLeft(ag1, 0.0))((acc: (LearningAgent, Double), samples: List[Sample]) => {
          val (a, c) = (samples.reverse.foldLeft(acc))(learnQ)
          (a.update.clearTraces, c)
        })
        loop(ag2, cost2, i - 1)
      }

    loop(this, 0.0, parms.learnIterations)
  }

  /**
   *
   */
  def learnV(x: DenseVector[Double], y: DenseVector[Double]): (LearningAgent, Double) = {
    val (nv, cost) = vNet.learn(x, y, parms.c)
    (new LearningAgent(nv, qNet, parms, epsilonGreedy, random), cost)
  }

  /**
   *
   */
  def learnQ(x: DenseVector[Double], y: DenseVector[Double]): (LearningAgent, Double) = {
    val (nq, cost) = qNet.learn(x, y, parms.c)
    (new LearningAgent(vNet, nq, parms, epsilonGreedy, random), cost)
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

  val One = DenseVector(1.0)
  val Zero = DenseVector(0.0)

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
        vars("vw1").asInstanceOf[DenseMatrix[Double]],
        vars("vw2").asInstanceOf[DenseMatrix[Double]],
        vars("vw3").asInstanceOf[DenseMatrix[Double]],
        vars("vLambda").asInstanceOf[Double])
      val qNet = new TracedNetwork(
        vars("qw1").asInstanceOf[DenseMatrix[Double]],
        vars("qw2").asInstanceOf[DenseMatrix[Double]],
        vars("qw3").asInstanceOf[DenseMatrix[Double]],
        vars("qLambda").asInstanceOf[Double])
      new LearningAgent(vNet, qNet, LearningParameters(c, alpha, learnIterations), epsilonGreedy, random)
    }
  }
}
