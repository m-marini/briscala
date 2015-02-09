/**
 *
 */
package org.mmarini.briscala.actor

import org.mmarini.briscala.LearningParameters
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

/**
 * @author us00852
 *
 */
class SelectionActor(parms: SelectionParameters, callbacks: SelectionCallbacks) extends Actor with LazyLogging {
  private val random = CommonRandomizers.selectionRand
  private var playersActors: IndexedSeq[ActorRef] = IndexedSeq()
  private var results: Seq[(Int, Int, TDPolicy)] = Seq()
  private var playerCount: Int = 0
  private val mutationGen = new Bernoulli(parms.mutationProb, random)

  /**
   * Handle received message
   */
  def receive = {

    case StartCompetitionMessage(players) => startCompetition(players)

    case EndCompetitionMessage(p0, p1) => registerEndCompetition(p0, p1)

  }

  /**
   * Start the competition.
   * Create the necessary Playing Actor, create the player pairs and send
   * message to actor to start the training phase
   */
  def startCompetition(players: IndexedSeq[TDPolicy]) = {
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
  def registerEndCompetition(p0: (Int, Int, TDPolicy), p1: (Int, Int, TDPolicy)) = {
    results = p0 +: p1 +: results
    if (results.size >= playerCount)
      select
  }

  /**
   * Sort the networks by validation results.
   * Then replaces the worst player with the best players and randomly substitutes worst player with
   * random player
   */
  def select = {
    logger.debug("Selecting ...")
    val sortedResults = results.sortWith((a, b) => (a, b) match {
      case ((_, v0, _), (_, v1, _)) => v0 >= v1
    })

    val best = sortedResults.head
    callbacks.selectedResult(
      best._1.toDouble / parms.trainGameCount,
      best._2.toDouble / parms.validationGameCount,
      0.5)

    val sortedPop = sortedResults.map { case (_, _, policy) => policy }
    callbacks.selectedPopulation(sortedPop)

    val n = sortedPop.size
    val newPop = sortedPop.take(n - parms.eliminationCount) ++: sortedPop.take(parms.eliminationCount)
    val newPop1 = if (mutationGen.draw) {
      logger.debug("Create mutated policy")
      newPop.take(n - 1) :+ createRandPolicy
    } else
      newPop
    self ! StartCompetitionMessage(newPop1.toIndexedSeq)
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