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
class Policy(val vNet: TracedNetwork, val qNet: TracedNetwork, epsilonGreedy: Double, random: RandBasis) {

  private val greedyProb = new Bernoulli(epsilonGreedy, random)

  /**
   *
   */
  def greedy = new Policy(vNet, qNet, 0.0, random)

  /**
   *
   */
  def selectAction(s: HiddenStatus): Int =
    if (s.numOfChoice == 1)
      0
    else if (greedyProb.draw)
      selectActionExploring(s)
    else
      selectActionExploiting(s)

  /**
   *
   */
  def selectActionExploring(s: HiddenStatus): Int = random.randInt(s.numOfChoice).draw

  /**
   *
   */
  def learnV(samples: List[Sample], p: LearningParameters): (Policy, Double, Double) =
    vNet.learn(samples, p.c, p.lambda) match {
      case (net, cost, err) =>
        (new Policy(net.update(p.alpha).clearTraces, qNet, epsilonGreedy, random), cost, err)
    }

  /**
   *
   */
  def learnQ(samples: List[Sample], p: LearningParameters): (Policy, Double, Double) =
    qNet.learn(samples, p.c, p.lambda) match {
      case (net, cost, err) =>
        (new Policy(vNet, net.update(p.alpha).clearTraces, epsilonGreedy, random), cost, err)
    }

  /**
   *
   */
  def selectActionExploiting(s: HiddenStatus): Int =
    valueByAction(s).maxBy { case (_, v) => v }._1

  /**
   *
   */
  def valueByAction(s: HiddenStatus): Map[Int, Double] =
    (for (action <- 0 until s.numOfChoice) yield {
      (action -> (if (s.isFirstHand)
        vNet(s.afterState(action).statusFeatures)(0)
      else
        qNet(s.actionFeatures(action))(0)))
    }).toMap

  /**
   *
   */
  def errV(samples: List[Sample]): Double =
    samples.foldLeft(0.0)((err, samples) => {
      val d = (vNet(samples._1) - samples._2)
      err + d.t * d
    })

  /**
   *
   */
  def errQ(samples: List[Sample]): Double =
    samples.foldLeft(0.0)((err, samples) => {
      val d = (qNet(samples._1) - samples._2)
      err + d.t * d
    })

  /**
   *
   */
  def save(filename: String) = {
    val out = Path(filename).delete().outputStream(WriteAppend: _*)
    MathFile.save(out, Map(
      "vw1" -> vNet.w1,
      "vw2" -> vNet.w2,
      "vw3" -> vNet.w3,
      "qw1" -> qNet.w1,
      "qw2" -> qNet.w2,
      "qw3" -> qNet.w3))
  }
}

object Policy {
  /**
   *
   */
  def defaultPolicy(hiddenNeuros: Int, epsilonGreedy: Double, random: RandBasis): Policy =
    new Policy(
      TracedNetwork.defaultNetwork(HiddenStatus.statusFeatureSize, hiddenNeuros, hiddenNeuros, 1),
      TracedNetwork.defaultNetwork(HiddenStatus.actionFeatureSize, hiddenNeuros, hiddenNeuros, 1),
      epsilonGreedy, random)

  /**
   *
   */
  def rand(hiddenNeuros: Int, epsilonGreedy: Double, random: RandBasis): Policy =
    new Policy(
      TracedNetwork.rand(HiddenStatus.statusFeatureSize, hiddenNeuros, hiddenNeuros, 1, random),
      TracedNetwork.rand(HiddenStatus.actionFeatureSize, hiddenNeuros, hiddenNeuros, 1, random),
      epsilonGreedy, random)

  /**
   *
   */
  def load(filename: String, epsilonGreedy: Double, random: RandBasis): Policy = {
    val vars = MathFile.load(Resource.fromFile(filename).lines())
    if (!(vars.contains("vw1") &&
      vars.contains("vw2") &&
      vars.contains("vw3") &&
      vars.contains("qw1") &&
      vars.contains("qw2") &&
      vars.contains("qw3")))
      throw new IOException("Missing variables")
    else {
      val vNet = new TracedNetwork(
        vars("vw1").asInstanceOf[DMatrix],
        vars("vw2").asInstanceOf[DMatrix],
        vars("vw3").asInstanceOf[DMatrix])
      val qNet = new TracedNetwork(
        vars("qw1").asInstanceOf[DMatrix],
        vars("qw2").asInstanceOf[DMatrix],
        vars("qw3").asInstanceOf[DMatrix])
      new Policy(vNet, qNet, epsilonGreedy, random)
    }
  }
}
