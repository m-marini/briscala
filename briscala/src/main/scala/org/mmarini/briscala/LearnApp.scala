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
import com.typesafe.scalalogging.LazyLogging

/**
 *
 */
object LearnApp extends App with LazyLogging {
  val n = 100000
  val train = 1000
  val test = 500
  val iterations = 10
  val c = 3000
  val alpha = 300E-9
  val lambda = 0.8
  val epsilon = 0.1
  val hiddens = 40
  val file = "network.mat"
  val out = "out.mat"

  /*
   * Load agent 
   */

  val initPolicy =
    if (Path(file).canRead) {
      logger.info(s"Loading $file")
      logger.info(s" iterations = $iterations")
      logger.info(s"          c = $c")
      logger.info(s"      alpha = $alpha")
      logger.info(s"    epsilon = $epsilon")
      TDPolicy.load(file, epsilon, CommonRandomizers.policyRand)
    } else {
      logger.info(s"Creating $file")
      logger.info(s"    hiddens = $hiddens")
      logger.info(s" iterations = $iterations")
      logger.info(s"          c = $c")
      logger.info(s"      alpha = $alpha")
      logger.info(s"     lambda = $lambda")
      logger.info(s"    epsilon = $epsilon")
      TDPolicy.rand(hiddens, epsilon, CommonRandomizers.policyRand)
    }

  val cycles = (n + train - 1) / train

  /*
   * The GPI loop iterates among the learning and validate phases to improve the policy
   * In each iteration it computes the learning policy, the reference policy, the learning cost,
   * the training performance, the test performance.
   * The performances are the won games over the games 
   */

  def gpiLoop(i: Int, ctx: (TDPolicy, TDPolicy, List[(Double, Double, Double, Double)])): (TDPolicy, TDPolicy, List[(Double, Double, Double, Double)]) =
    if (i <= 0)
      ctx
    else {
      val agent = new LearningAgent(LearningParameters(c, alpha, lambda), train, test, iterations)
      val (p, p0, kpis) = ctx
      val (np, np0, kpi) = agent.learn(p, p0)
      logger.info(s"#${cycles - i + 1} / $cycles: cost=${kpi._1}, won rate=${kpi._2} vs ${kpi._3} randRate=${kpi._4}");
      if (np0 != p0) {
        logger.info(s" Saveing better network")
        np0.save(file)
      }
      gpiLoop(i - 1, (np, np0, kpi :: kpis))
    }

  logger.info(s"Training $n total samples")
  logger.info(s"         $train training samples")
  logger.info(s"         $test test samples")

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
  logger.info(s"Writing ${out}...")
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