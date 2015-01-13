package org.mmarini.briscala

import org.scalatest._
import scala.util.Random
import breeze.linalg.DenseVector
import breeze.stats.distributions.RandBasis
import org.apache.commons.math3.random.JDKRandomGenerator
import java.io.File

class LearningAgentTest extends FunSpec with Matchers {
  def randBiasis = {
    val g = new JDKRandomGenerator()
    g.setSeed(1)
    new RandBasis(g)
  }

}