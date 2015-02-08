/**
 *
 */
package org.mmarini.briscala.actor

import akka.actor.Actor
import akka.actor.Props
import com.typesafe.scalalogging.LazyLogging
import org.mmarini.briscala.TrainingAgent
import org.mmarini.briscala.ValidationAgent

/**
 * @author us00852
 *
 */
class PlayingActor(trainingAgent: TrainingAgent, validationAgent: ValidationAgent) extends Actor with LazyLogging {
  def receive = {

    case TrainingGameMessage(0, (policy0, won0), (policy1, won1)) =>
      self ! ValidationGameMessage(validationAgent.validationGameCount, (policy0, won0, 0), (policy1, won1, 0))

    case TrainingGameMessage(n, (policy0, won0), (policy1, won1)) =>
      self ! (trainingAgent.play(policy0, policy1) match {
        case ((newPolicy0, hasWon0), (newPolicy1, hasWon1)) =>
          TrainingGameMessage(
            n - 1,
            (newPolicy0, won0 + (if (hasWon0) 1 else 0)),
            (newPolicy1, won1 + (if (hasWon1) 1 else 0)))
      })

    case ValidationGameMessage(0, player0, player1) =>
      context.parent ! EndCompetitionMessage(player0, player1)

    case ValidationGameMessage(n, (policy0, train0, val0), (policy1, train1, val1)) =>
      self ! (validationAgent.play(policy0, policy1) match {
        case (hasWon0, hasWon1) => ValidationGameMessage(
          n - 1,
          (policy0, train0, val0 + (if (hasWon0) 1 else 0)),
          (policy1, train1, val1 + (if (hasWon1) 1 else 0)))
      })

  }
}

/**
 *
 */
object PlayingActor {
  def props(trainingAgent: TrainingAgent, validateAgent: ValidationAgent) =
    Props(new PlayingActor(trainingAgent, validateAgent))
}