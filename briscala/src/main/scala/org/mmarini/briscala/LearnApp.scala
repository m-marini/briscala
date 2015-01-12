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

  val SaveInterval = 10000

  val n = argForInt("--n").getOrElse(1)
  val seed = argForInt("--seed")
  val c = argForDouble("--c").getOrElse(1e3)
  val alpha = argForDouble("--alpha").getOrElse(3e-6)
  val lambda = argForDouble("--lambda").getOrElse(0.9)
  val epsilon = argForDouble("--epsilon").getOrElse(0.1)
  val hiddens = argForInt("--hiddens").getOrElse(16)
  val iterations = argForInt("--iter").getOrElse(5)
  val file = argFor("--file");
  val out = argFor("--out");

  val gen = new JDKRandomGenerator()
  if (!seed.isEmpty) {
    println(s"Seed = $seed.get")
    gen.setSeed(seed.get);
  }

  val random = new RandBasis(gen)

  /*
   * Load agent 
   */

  if (file.isEmpty)
    throw new Error("Missing file")

  val filename = file.get

  val initAgent =
    if (Path(filename).canRead) {
      println(s"Loading $filename")
      println(s" iterations = $iterations")
      println(s"          c = $c")
      println(s"      alpha = $alpha")
      println(s"    epsilon = $epsilon")
      LearningAgent.load(filename, c, alpha, iterations, epsilon, random)
    } else {
      println(s"Creating $filename")
      println(s"    hiddens = $hiddens")
      println(s" iterations = $iterations")
      println(s"          c = $c")
      println(s"      alpha = $alpha")
      println(s"     lambda = $lambda")
      println(s"    epsilon = $epsilon")
      LearningAgent.rand(hiddens, c, alpha, iterations, lambda, epsilon, random)
    }

  // Run learning
  def loop(i: Int, agent: LearningAgent, costs: List[Double], to: Long): (LearningAgent, List[Double]) =
    if (i <= 0) {
      agent.save(filename)
      (agent, costs)
    } else {
      print(s"Game #${n - i}\r")
      val (a, cost) = agent.learn
      val now = System.currentTimeMillis()
      val nto = if (now > to) {
        a.save(filename)
        now + SaveInterval
      } else
        to
      loop(i - 1, a, cost :: costs, nto)
    }

  val (_, costs) = loop(n, initAgent, List(), 0)

  // Save costs
  if (!out.isEmpty) {
    println(s"Writing ${out.get}...")
    Path(out.get).deleteIfExists()
    MathFile.save(Resource.fromFile(out.get), Map("cost" -> DenseVector(costs.reverse.toArray).toDenseMatrix.t))
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