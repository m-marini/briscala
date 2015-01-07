package org.mmarini.briscala

import org.scalatest._
import scala.util.Random

class LearningAgentTest extends FunSpec with Matchers {
  describe("A LearningAgent") {
    val hiddens = 20
    val c = 1e3
    val alpha = 10e-3
    val epsilonGreedy = 0.0
    val lambda = 0.8
    val random = new Random(1)

    val agent = LearningAgent.rand(hiddens, c, alpha, epsilonGreedy, lambda, random)

    describe("when selecting an action by V") {
      val s = Game.createInitStatus(random)
      val action = agent.selectActionByV(s);

      it("should select the best action") {

      }
    }
  }

}