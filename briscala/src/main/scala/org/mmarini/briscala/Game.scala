package org.mmarini.briscala

import scala.util.Random

object Game {

  def create(random: Random): List[(Status, Option[Int])] = {
    def next(l: List[(Status, Option[Int])], s: Status): List[(Status, Option[Int])] =
      if (s.isCompleted)
        (s, None) :: l
      else {
        val c = random.nextInt(s.numOfChoice)
        next((s, Some(c)) :: l, s.nextStatus(c))
      }

    next(List(), createInitStatus(random))
  }

  /**
   *
   */
  def createValues(random: Random): ValueFunctions = {
    val game = create(random)

    val (end, _) = game.head;

    val winner0 = end.isWinner0
    val winner1 = end.isWinner1

    // filter state for response turn (played != None) and with choice
    val state = game.filter { case (status, _) => !status.played.isEmpty && status.numOfChoice > 1 }

    // the after states are the state view of the opposite player
    val lastAfterState =
      (game.drop(3).head match {
        case (status, _) =>
          (end.toRow(!status.player0Turn), if (status.player0Turn) winner1 else winner0);
      })
    val afterStates = lastAfterState :: state.map {
      case (status, _) =>
        (status.toRow(!status.player0Turn), if (status.player0Turn) winner1 else winner0);
    }

    // the state action are the state, action of the player
    val stateActions = state.map {
      case (status, action) =>
        (status.toRow(status.player0Turn, action.get), if (status.player0Turn) winner0 else winner1);
    }

    val v: StateValue = afterStates.groupBy { case (state, ret) => state }.
      map {
        case (state, list) => {
          val win = list.count {
            case (_, ret) => ret
          }
          (state -> (win, list.size))
        }
      }
    val q: ActionValue = stateActions.groupBy {
      case (key, ret) => key
    }.map {
      case (key, list) => {
        val win = list.count {
          case (_, ret) => ret
        }
        (key -> (win, list.size))
      }
    }
    ValueFunctions(v, q)
  }

  /**
   *
   */
  private def createInitStatus(random: Random): Status = {
    val deck = Deck.shuffle(random)
    val Some(trump) = deck.drop(6).find(_.isTrump)
    Status(
      true,
      deck.take(3),
      deck.drop(3).take(3),
      Set(),
      Set(),
      None,
      trump,
      deck.drop(6).filterNot(_ == trump))
  }
}