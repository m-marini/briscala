/**
 *
 */
package org.mmarini.briscala.actor

import org.mmarini.briscala.LearningParameters
import scala.math.min
import org.mmarini.briscala.Network
import org.mmarini.briscala.TrainingAgent
import org.mmarini.briscala.TrainingAgent
import org.mmarini.briscala.TrainingAgent
import org.mmarini.briscala.ValidationAgent
import com.typesafe.scalalogging.LazyLogging
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import breeze.stats.distributions.RandBasis
import org.mmarini.briscala.TDPolicy
import org.mmarini.briscala.SelectionParameters
import breeze.stats.distributions.Bernoulli
import org.mmarini.briscala.CommonRandomizers
import org.mmarini.briscala.RandomPolicy
import org.mmarini.briscala.Game
import breeze.stats.distributions.Poisson

/**
 * @author us00852
 *
 */
class SelectionActor(parms: SelectionParameters, callbacks: SelectionCallbacks) extends Actor with LazyLogging {
  private val random = CommonRandomizers.selectionRand
  private var playersActors: IndexedSeq[ActorRef] = IndexedSeq()
  private var results: IndexedSeq[(Int, Int, TDPolicy)] = IndexedSeq()
  private var playerCount: Int = 0
  private val mutationGen = new Poisson(parms.mutationMean)(random)
  private val randomGen = CommonRandomizers.policyRand
  private val randomPolicy = new RandomPolicy(randomGen)

  /**
   * Handle received message
   */
  def receive = {

    case StartCompetitionMessage(players) => startCompetition(players)

    case EndCompetitionMessage(p0, p1) => registerEndCompetition(p0, p1)

    case RandomPolicyGameMessage(0, (won, _)) => select(won)

    case RandomPolicyGameMessage(n, (won, policy)) => {
      callbacks.testing(n)
      val finalState = Game.createGame(policy, randomPolicy, randomGen).head._1
      self ! RandomPolicyGameMessage(n - 1, (won + (if (finalState.isWinner0) 1 else 0), policy))
    }

    case ShutdownMessage => {
      playersActors.foreach(context.stop(_))
      context.stop(self)
    }

  }

  /**
   * Start the competition.
   * Create the necessary Playing Actor, create the player pairs and send
   * message to actor to start the training phase
   */
  private def startCompetition(players: IndexedSeq[TDPolicy]) = {
    val pp =
      if (parms.populationCount > players.size) {
        players ++: (
          for (i <- players.size until parms.populationCount)
            yield createRandPolicy)
      } else
        players
    require(pp.size > 0 && pp.size % 2 == 0)

    playerCount = pp.size

    val na = playerCount / 2;
    if (playersActors.size < na) {
      val p = for (i <- playersActors.size until na)
        yield context.actorOf(PlayingActor.props(
        i,
        new TrainingAgent(parms.learningParms, parms.learningIter),
        new ValidationAgent(parms.validationGameCount),
        callbacks))
      playersActors ++= p
    }

    val shuffled = random.permutation(playerCount).draw.map(pp(_))
    val pairs = for (i <- 0 until shuffled.size by 2) yield (shuffled(i), shuffled(i + 1))
    logger.debug(s"Starting competition ...")
    results = IndexedSeq()

    callbacks.startCompetition()

    for (i <- 0 until na)
      playersActors(i) ! TrainingGameMessage(parms.trainGameCount, (0, pairs(i)._1), (0, pairs(i)._2))
  }

  /**
   * Register the end of competition between a pair of players.
   * If it is the last pair it runs the selection for next competition iteration
   */
  private def registerEndCompetition(p0: (Int, Int, TDPolicy), p1: (Int, Int, TDPolicy)) = {
    results = p0 +: p1 +: results
    if (results.size >= playerCount)
      runTest
  }

  /**
   * Sort the networks by validation results and start the test phase on best player
   */
  private def runTest = {
    results = results.sortWith((a, b) => (a, b) match {
      case ((_, v0, _), (_, v1, _)) => v0 >= v1
    })
    callbacks.selectedPopulation(results.map {
      case (t, v, p) => (t.toDouble / parms.trainGameCount, v.toDouble / parms.validationGameCount, p)
    })
    self ! RandomPolicyGameMessage(parms.randomGameCount, (0, results.head._3))
  }

  /**
   * Replaces the worst players with the best players and randomly substitutes worst player with
   * random player
   */
  private def select(randWon: Int) = {
    logger.debug("Selecting ...")

    val best = results.head

    callbacks.selectedResult(
      best._1.toDouble / parms.trainGameCount,
      best._2.toDouble / parms.validationGameCount,
      randWon.toDouble / parms.randomGameCount)

    val sortedPop = results.map { case (_, _, policy) => policy }

    // Substitute the worst policies with a copy of best ones
    val n = sortedPop.size
    val newPop = sortedPop.take(n - parms.eliminationCount) ++: sortedPop.take(parms.eliminationCount)

    // Randomize networks
    val m = min(mutationGen.draw, n - 1)
    logger.debug(f"Randomizing last ${m}%d policies")
    val newPop1 = newPop.take(n - m) ++ ((1 to m).map(_ => createRandPolicy))
    self ! StartCompetitionMessage(newPop1)
  }

  /**
   * Generate a new random policy
   */
  private def createRandPolicy: TDPolicy = {
    val rnd = CommonRandomizers.policyRand
    val nh = rnd.randInt(1, parms.hiddenNeuros + 1).draw
    TDPolicy.rand(nh, parms.epsilonGreedy, rnd)
  }
}

/**
 *
 */
object SelectionActor {
  def props(parms: SelectionParameters, callbacks: SelectionCallbacks) =
    Props(new SelectionActor(parms, callbacks))
}