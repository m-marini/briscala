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

    next(List(), createInitStatus(random)).reverse
  }

  /**
   *
   */
  def createValues(random: Random): (StateValue, StateActionValue) = {
    val game = create(random)

    val player0Winner = game.head._1.isWinner0
    val player1Winner = game.head._1.isWinner1

    // filter state for response turn (played != None) and with choice
    val state = create(random).filter {
      case (status, _) => !status.played.isEmpty && status.numOfChoice > 1
    }

    val (end, _) = game.head;

    // the after states are the state view of the opposite player
    val as =
      (game.drop(3).head match {
        case (status, _) =>
          (end.toRow(!status.player0Turn), if (status.player0Turn) player1Winner else player0Winner);
      }) :: state.map {
        case (status, _) =>
          (status.toRow(!status.player0Turn), if (status.player0Turn) player1Winner else player0Winner);
      }

    // the state action are the state, action of the player
    val sa = state.map {
      case (status, action) =>
        (action.get, status.toRow(status.player0Turn), if (status.player0Turn) player0Winner else player1Winner);
    }

    val asv = as.groupBy {
      case (state, ret) => state
    }.map {
      case (state, list) => {
        val win = list.count {
          case (_, ret) => ret
        }
        (state -> (win, list.size))
      }
    }
    val sav = sa.groupBy {
      case (action, state, ret) => (action, state)
    }.map {
      case (key, list) => {
        val win = list.count {
          case (_, _, ret) => ret
        }
        (key -> (win, list.size))
      }
    }
    (asv, sav)
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