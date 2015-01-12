package org.mmarini.briscala

import scala.util.Random
import scala.math._
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
  val n = argForInt("--n").getOrElse(1000)
  val train = argForInt("--train").getOrElse(100)
  val test = argForInt("--test").getOrElse(50)
  val seed = argForInt("--seed")
  val c = argForDouble("--c").getOrElse(1e3)
  val alpha = argForDouble("--alpha").getOrElse(3e-6)
  val lambda = argForDouble("--lambda").getOrElse(0.8)
  val epsilon = argForDouble("--epsilon").getOrElse(0.1)
  val hiddens = argForInt("--hiddens").getOrElse(16)
  val iterations = argForInt("--iter").getOrElse(5)
  val file = argFor("--file").getOrElse("networks.mat");
  val out = argFor("--out").getOrElse("out.mat");

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

  val initAgent =
    if (Path(file).canRead) {
      println(s"Loading $file")
      println(s" iterations = $iterations")
      println(s"          c = $c")
      println(s"      alpha = $alpha")
      println(s"    epsilon = $epsilon")
      LearningAgent.load(file, c, alpha, iterations, epsilon, random)
    } else {
      println(s"Creating $file")
      println(s"    hiddens = $hiddens")
      println(s" iterations = $iterations")
      println(s"          c = $c")
      println(s"      alpha = $alpha")
      println(s"     lambda = $lambda")
      println(s"    epsilon = $epsilon")
      LearningAgent.rand(hiddens, c, alpha, iterations, lambda, epsilon, random)
    }

  println()

  def kpiLoop(i: Int, ctx: (LearningAgent, List[(Double, Double, Double)])): (LearningAgent, List[(Double, Double, Double)]) = if (i <= 0)
    ctx
  else {

    def trainLoop(j: Int, ctx: (LearningAgent, Double, Double)): (LearningAgent, Double, Double) = if (j <= 0)
      ctx
    else {
      print(s"Training #${train - j + (n / train - i) * train + 1}\r")
      val (a, cost, err) = ctx
      trainLoop(j - 1, a.learn match {
        case (a1, c1, e1) => (a1, cost + c1, err + e1)
      })
    }

    val (a, kpis) = ctx
    val (a1, cost, trainErr) = trainLoop(train, (a, 0.0, 0.0))

    def testErrors(i: Int, err: Double): Double = if (i <= 0)
      err
    else {
      print(s"Testing #${test - i + 1}\r")
      testErrors(i - 1, err + a1.play)
    }

    a1.save(file)

    println()
    val kpi = (cost / train,
      sqrt(trainErr / train),
      sqrt(testErrors(test, 0.0) / test))

    println()
    println(s"Cost=${kpi._1}, Training error=${kpi._2}, Test error=${kpi._3}")

    kpiLoop(i - 1, (a1, kpi :: kpis))
  }

  println(s"Training $n total samples")
  println(s"         $train training samples")
  println(s"         $test test samples")

  val (costs, trainErrs, testErrs) = kpiLoop(n / train, (initAgent, List())) match {
    case (_, kpis) => kpis.unzip3
  }

  // Save costs
  println(s"Writing ${out}...")
  Path(out).deleteIfExists()
  MathFile.save(Resource.fromFile(out),
    Map(("costs" -> DenseVector(costs.reverse.toArray).toDenseMatrix.t),
      ("trainErrs" -> DenseVector(trainErrs.reverse.toArray).toDenseMatrix.t),
      ("testErrs" -> DenseVector(testErrs.reverse.toArray).toDenseMatrix.t)))

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