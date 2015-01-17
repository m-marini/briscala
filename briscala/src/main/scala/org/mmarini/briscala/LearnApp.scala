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

  val initPolicy =
    if (Path(file).canRead) {
      println(s"Loading $file")
      println(s" iterations = $iterations")
      println(s"          c = $c")
      println(s"      alpha = $alpha")
      println(s"    epsilon = $epsilon")
      TDPolicy.load(file, epsilon, random)
    } else {
      println(s"Creating $file")
      println(s"    hiddens = $hiddens")
      println(s" iterations = $iterations")
      println(s"          c = $c")
      println(s"      alpha = $alpha")
      println(s"     lambda = $lambda")
      println(s"    epsilon = $epsilon")
      TDPolicy.rand(hiddens, epsilon, random)
    }

  println()

  val cycles = (n + train - 1) / train

  /*
   * The GPI loop iterates among the learning and validate phases to improve the policy
   * In each iteration it computes the learning policy, the reference policy, the learning cost,
   * the training performance, the test performance.
   * The performances are the won games over the games 
   */
  val agent = new LearningAgent(LearningParameters(c, alpha, lambda), train, test, iterations, random)

  def gpiLoop(i: Int, ctx: (TDPolicy, TDPolicy, List[(Double, Double, Double, Double)])): (TDPolicy, TDPolicy, List[(Double, Double, Double, Double)]) =
    if (i <= 0)
      ctx
    else {
      val (p, p0, kpis) = ctx
      val (np, np0, kpi) = agent.learn(p, p0)
      if (np0 != p0) {
        println(s"New policy cost=${kpi._1}, won rate=${kpi._2} vs ${kpi._3}");
        np0.save(file)
      }
      gpiLoop(i - 1, (np, np0, kpi :: kpis))
    }

  println(s"Training $n total samples")
  println(s"         $train training samples")
  println(s"         $test test samples")

  def unzip4(
    list: List[(Double, Double, Double, Double)],
    lists: (List[Double], List[Double], List[Double], List[Double])): (List[Double], List[Double], List[Double], List[Double]) =
    if (list.isEmpty)
      lists
    else
      unzip4(list.tail,
        (list.head._1 :: lists._1,
          list.head._2 :: lists._2,
          list.head._3 :: lists._3,
          list.head._4 :: lists._4))

  val (costs, trainWonRate, testWonRate, randRate) = gpiLoop(cycles, (initPolicy, initPolicy, List())) match {
    case (_, _, kpis) => unzip4(kpis, (Nil, Nil, Nil, Nil))
  }

  // Save costs
  println(s"Writing ${out}...")
  Path(out).deleteIfExists()
  MathFile.save(Resource.fromFile(out),
    Map(("costs" -> DenseVector(costs.reverse.toArray).toDenseMatrix.t),
      ("trainWonRate" -> DenseVector(trainWonRate.reverse.toArray).toDenseMatrix.t),
      ("testWonRate" -> DenseVector(testWonRate.reverse.toArray).toDenseMatrix.t),
      ("randomRate" -> DenseVector(randRate.reverse.toArray).toDenseMatrix.t)))

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