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
 * The training agent manage the training phase modifying the policies by playing game
 */
class TrainingAgent(parms: LearningParameters, learningIter: Int) extends AbstractAgent {
  private val trainRand = CommonRandomizers.trainRand

  /**
   * Play a game and modify the policy on results of game
   * Returns the new policies, costs and if the game result
   */
  def play(p0: TDPolicy, p1: TDPolicy): ((Boolean, TDPolicy), (Boolean, TDPolicy)) = {
    val game = Game.createGame(p0, p1, trainRand)
    val last = game.head._1

    val samples = extrapolateSamples(game)
    ((last.isWinner0, train(p0, samples)), (last.isWinner1, train(p1, samples)))
  }

  /**
   *
   */
  private def train(policy: TDPolicy, samples: (List[List[Sample]], List[List[Sample]])): TDPolicy = {
    val (vSamples, qSamples) = samples
    val n = vSamples.map(_.size).sum + qSamples.map(_.size).sum

    def trainLoop(ctx: (Int, TDPolicy)): (Int, TDPolicy) = ctx match {
      case (0, _) => ctx

      case (iterationCount, policy) => {
        val policy1 = vSamples.foldLeft(policy)((policy, samples) =>
          policy.learnV(samples, parms) match {
            case (policy, _, _) => policy
          })
        val policy2 = qSamples.foldLeft(policy1)((policy, samples) => policy.learnQ(samples, parms) match {
          case (policy, _, _) => policy
        })
        trainLoop(iterationCount - 1, policy2)
      }
    }

    trainLoop(learningIter, policy) match {
      case (_, policy) => policy
    }
  }
}
