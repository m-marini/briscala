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

  describe("A default LearningAgent") {
    val hiddens = 3
    val c = 1e3
    val alpha = 10e-3
    val epsilonGreedy = 0.0
    val lambda = 0.8
    val random = randBiasis

    val agent = LearningAgent.defaultAgent(hiddens, c, alpha, 1, epsilonGreedy, lambda, random)

    describe("when saved") {
      agent.save("test.mat")
      it("should write a file") {
        new File("test.mat") should exist
      }
    }
    describe("when computing V") {
      val s = Game.createInitStatus(random)
      val v = agent.valueByAction(s)

      it("should return a list of three 0.5 values") {
        v should have size (3)
        v should be(IndexedSeq(0.5, 0.5, 0.5))
      }
    }
    describe("when computing Q") {
      val s = Game.createInitStatus(random).nextStatus(0)
      val v = agent.valueByAction(s)

      it("should return a list of three 0.5 values") {
        v should have size (3)
        v should be(IndexedSeq(0.5, 0.5, 0.5))
      }
    }
  }

  describe("A random LearningAgent") {
    val hiddens = 1
    val c = 1e4
    val alpha = 1e-4
    val epsilonGreedy = 0.0
    val lambda = 1.0
    val random = randBiasis

    val agent = LearningAgent.rand(hiddens, c, alpha, 1, lambda, epsilonGreedy, random)

    describe("when learnt for second action in status for first hand player") {
      val status = Status(
        true,
        (1 to 3).map(new Card(_)),
        (4 to 6).map(new Card(_)),
        Set(),
        Set(),
        None,
        new Card(0),
        (7 to 39).map(new Card(_)))

      val hs = status.playerHidden

      val samples = (0 to 2).map(i =>
        (hs.afterState(i).statusFeatures, DenseVector(if (i == 1) 1.0 else 0.0))).toList

      def learnLoop(n: Int, agent: LearningAgent): LearningAgent =
        if (n <= 0)
          agent
        else {
          def sampleLoop(list: List[(DenseVector[Double], DenseVector[Double])], agent: LearningAgent): LearningAgent =
            list match {
              case List() => agent
              case (x, y) :: tail => sampleLoop(tail, agent.learnV(x, y) match {
                case (na, _) => na
              })
            }
          learnLoop(n - 1, sampleLoop(samples, agent).update.clearTraces)
        }

      val betterAgent = learnLoop(3000, agent)

      it("after states should be different ") {
        val hs = status.playerHidden
        val as0 = hs.afterState(0).statusFeatures
        val as1 = hs.afterState(1).statusFeatures
        val as2 = hs.afterState(2).statusFeatures

        as0 should not be (as1)
        as0 should not be (as2)
        as1 should not be (as2)
      }

      it("should return a list where second element should be the highest") {
        val v0 = agent.valueByAction(status)
        val v1 = betterAgent.valueByAction(status)
        v1(0) should be < 0.5
        v1(1) should be > 0.5
        v1(2) should be < 0.5
      }

      it("should select the second action") {
        val action = betterAgent.selectActionExploiting(status)
        action should be(1)
      }
    }

    describe("when learnt for second action in status for second hand player") {
      val status = Status(
        false,
        (4 to 6).map(new Card(_)),
        (2 to 3).map(new Card(_)),
        Set(),
        Set(),
        Some(new Card(1)),
        new Card(0),
        (7 to 39).map(new Card(_)))

      val hs = status.playerHidden

      val samples = (0 to 2).map(i =>
        (hs.actionFeatures(i), DenseVector(if (i == 1) 1.0 else 0.0))).toList

      def learnLoop(n: Int, agent: LearningAgent): LearningAgent =
        if (n <= 0)
          agent
        else {
          def sampleLoop(list: List[(DenseVector[Double], DenseVector[Double])], agent: LearningAgent): LearningAgent =
            list match {
              case List() => agent
              case (x, y) :: tail => sampleLoop(tail, agent.learnQ(x, y) match {
                case (na, _) => na
              })
            }
          learnLoop(n - 1, sampleLoop(samples, agent).update.clearTraces)
        }

      val betterAgent = learnLoop(3000, agent)

      it("action features should be different ") {
        val as0 = hs.actionFeatures(0)
        val as1 = hs.actionFeatures(1)
        val as2 = hs.actionFeatures(2)

        as0 should not be (as1)
        as0 should not be (as2)
        as1 should not be (as2)
      }

      it("should return a list where second element should be the highest") {
        val v0 = agent.valueByAction(status)
        val v1 = betterAgent.valueByAction(status)
        v1(0) should be < 0.5
        v1(1) should be > 0.5
        v1(2) should be < 0.5
      }

      it("should select the second action") {
        val action = betterAgent.selectActionExploiting(status)
        action should be(1)
      }
    }
  }

}