package org.mmarini.briscala

import scala.util.Random
import scalax.file.Path
import scalax.io.JavaConverters.asOutputUnmanagedConverter
import scalax.io.Output
import scalax.io.StandardOpenOption.WriteAppend
import org.apache.commons.math3.random.JDKRandomGenerator
import breeze.stats.distributions.RandBasis
import breeze.linalg.DenseVector
import scalax.io.Resource

/**
 *
 */
object LearnApp extends App {

  val n = argForInt("--n").getOrElse(1)
  val seed = argForInt("--seed")
  val c = argForDouble("--c").getOrElse(1e3)
  val alpha = argForDouble("--alpha").getOrElse(1e-5)
  val lambda = argForDouble("--lambda").getOrElse(0.0)
  val epsilon = argForDouble("--epsilon").getOrElse(0.1)
  val hiddens = argForInt("--hiddens").getOrElse(3)
  val file = argFor("--file");
  val out = argFor("--out");

  val gen = new JDKRandomGenerator()
  if (!seed.isEmpty)
    gen.setSeed(seed.get);

  val random = new RandBasis(gen)

  /*
   * Load agent 
   */

  if (file.isEmpty)
    throw new Error("Missing file")

  val filename = file.get

  val initAgent =
    if (Path(filename).canRead)
      LearningAgent.load(filename, c, alpha, epsilon, random)
    else
      LearningAgent.rand(hiddens, c, alpha, epsilon, lambda, random)

  // Run learning
  def loop(i: Int, agent: LearningAgent, costs: List[Double]): (LearningAgent, List[Double]) =
    if (i <= 0)
      (agent, costs)
    else {
      print(s"Game #${n - i}\r")
      val (a, cost) = agent.learn
      a.save(filename)
      loop(i - 1, a, cost :: costs)
    }

  val (_, costs) = loop(n, initAgent, List())

  // Save costs
  if (!out.isEmpty) {
    println(s"Writing ${out.get}...")
    Path(out.get).deleteIfExists()
    MathFile.save(Resource.fromFile(out.get), Map("cost" -> DenseVector(costs.toArray).toDenseMatrix.t))
  }

  /**
   *
   */
  private def argFor(name: String): Option[String] = {
    val i = args.indexOf(name)
    if (i >= 0 && i + 1 < args.length)
      Some(args(i + 1))
    else
      None
  }

  /**
   *
   */
  private def argForBoolean(name: String): Option[Boolean] = argFor(name) match {
    case None => None
    case Some(value) => Some(value.toBoolean)
  }

  /**
   *
   */
  private def argForInt(name: String): Option[Int] = argFor(name) match {
    case None => None
    case Some(value) => Some(value.toInt)
  }

  /**
   *
   */
  private def argForDouble(name: String): Option[Double] = argFor(name) match {
    case None => None
    case Some(value) => Some(value.toDouble)
  }
}