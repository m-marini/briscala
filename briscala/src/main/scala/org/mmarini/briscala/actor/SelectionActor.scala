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
  private var results: Seq[(TDPolicy, Int, Int)] = Seq()
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
    require(players.size > 0 && players.size % 2 == 0)

    playerCount = players.size

    val na = playerCount / 2;
    if (playersActors.size < na) {
      val p = for (i <- playersActors.size to na)
        yield context.actorOf(PlayingActor.props(
        new TrainingAgent(parms.learningParms, parms.learningIter),
        new ValidationAgent(parms.validationGameCount)))
      playersActors ++= p
    }

    val shuffled = random.permutation(playerCount).draw.map(players(_))
    val pairs = for (i <- 0 to shuffled.size by 2) yield (shuffled(i), shuffled(i + 1))
    results = IndexedSeq()
    for (i <- 0 to players.size)
      playersActors(i) ! TrainingGameMessage(parms.trainGameCount, (pairs(i)._1, 0), (pairs(i)._2, 0))
  }

  /**
   * Register the end of competition between a pair of players.
   * If it is the last pair it runs the selection for next competition iteration
   */
  def registerEndCompetition(p0: (TDPolicy, Int, Int), p1: (TDPolicy, Int, Int)) = {
    results = p0 +: p1 +: results
    if (playerCount >= results.size)
      select
  }

  /**
   * Sort the networks by validation results.
   * Then replaces the worst player with the best players and randomly substitutes worst player with
   * random player
   */
  def select = {
    val sortedResults = results.sortWith((a, b) => (a, b) match {
      case ((_, _, v0), (_, _, v1)) => v0 >= v1
    })

    val best = sortedResults.head
    if (!callbacks.selectedResult.isEmpty)
      callbacks.selectedResult.get(best._2, best._3)

    val sortedPop = sortedResults.map { case (policy, _, _) => policy }
    if (!callbacks.selectedPopulation.isEmpty)
      callbacks.selectedPopulation.get(sortedPop)

    val n = sortedPop.size
    val newPop = sortedPop.take(n - parms.eliminationCount) ++: sortedPop.take(parms.eliminationCount)
    val newPop1 = if (mutationGen.draw)
      newPop.take(n - 1) :+ TDPolicy.rand(parms.hiddenNeuros, parms.epsilonGreedy, CommonRandomizers.policyRand)
    else
      newPop
    self ! StartCompetitionMessage(newPop1.toIndexedSeq)
  }
}

/**
 *
 */
object SelectionActor {
  def props(parms: SelectionParameters, callbacks: SelectionCallbacks) =
    Props(new SelectionActor(parms, callbacks))
}