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
class PlayingActor(
  id: Int,
  trainingAgent: TrainingAgent,
  validationAgent: ValidationAgent,
  callbacks: SelectionCallbacks) extends Actor with LazyLogging {

  /**
   *
   */
  def receive = {

    case TrainingGameMessage(0, (won0, policy0), (won1, policy1)) =>
      self ! ValidationGameMessage(validationAgent.validationGameCount, (won0, 0, policy0), (won1, 0, policy1))

    case TrainingGameMessage(n, (won0, policy0), (won1, policy1)) => {
      callbacks.training(id, n)
      self ! (trainingAgent.play(policy0, policy1) match {
        case ((hasWon0, newPolicy0), (hasWon1, newPolicy1)) => {
          TrainingGameMessage(
            n - 1,
            (won0 + (if (hasWon0) 1 else 0), newPolicy0),
            (won1 + (if (hasWon1) 1 else 0), newPolicy1))
        }
      })
    }

    case ValidationGameMessage(0, player0, player1) =>
      context.parent ! EndCompetitionMessage(player0, player1)

    case ValidationGameMessage(n, (train0, val0, policy0), (train1, val1, policy1)) => {
      callbacks.validating(id, n)
      self ! (validationAgent.play(policy0, policy1) match {
        case (hasWon0, hasWon1) => {
          ValidationGameMessage(
            n - 1,
            (train0, val0 + (if (hasWon0) 1 else 0), policy0),
            (train1, val1 + (if (hasWon1) 1 else 0), policy1))
        }
      })
    }
  }
}

/**
 *
 */
object PlayingActor {
  def props(id: Int, trainingAgent: TrainingAgent, validateAgent: ValidationAgent, callbacks: SelectionCallbacks) =
    Props(new PlayingActor(id, trainingAgent, validateAgent, callbacks))
}