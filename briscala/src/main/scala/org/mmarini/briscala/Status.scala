/**
 *
 */
package org.mmarini.briscala

import scala.util.Random

/**
 * @author us00852
 *
 */
case class Status(
  player0Turn: Boolean,
  player0Cards: IndexedSeq[Card],
  player1Cards: IndexedSeq[Card],
  won0Cards: Set[Card],
  won1Cards: Set[Card],
  played: Option[Card],
  trump: Card,
  deck: IndexedSeq[Card]) {

  lazy val numOfChoice = if (player0Turn) player0Cards.size else player1Cards.size
  /**
   * Compute the score of player
   */
  lazy val player0Score: Int = score(won0Cards)

  /**
   * Compute the score of opposite
   */
  lazy val player1Score: Int = score(won1Cards)

  /**
   * Compute the score of a cards set
   */
  def score(cards: Set[Card]): Int = cards.toList.map(_.score).sum

  /**
   * Return if the player wins the game
   */
  lazy val isWinner0: Boolean = player0Score > 60

  /**
   * Return if the player wins the game
   */
  lazy val isWinner1: Boolean = player1Score > 60

  /**
   *
   */
  lazy val isDraw: Boolean = player0Score == 60 && player1Score == 60

  /**
   * Return if the game is completed
   */
  lazy val isCompleted: Boolean = numOfChoice == 0

  /**
   * Return if the result game is determined by now
   */
  lazy val isDetermined: Boolean = isWinner0 || isWinner1 || isDraw

  /**
   * Generate the status when played a card
   */
  def nextStatus(choice: Int): Status =
    if (player0Turn)
      if (player0Cards.isEmpty)
        this
      else {
        val card = player0Cards(choice)
        if (played.isEmpty)
          Status(
            false,
            player0Cards.filterNot(_ == card),
            player1Cards,
            won0Cards,
            won1Cards,
            Some(card),
            trump,
            deck).optimize
        else
          (played.get.versus(card), deck.size) match {
            // player 0 perde in finale
            case (true, 0) => Status(
              false,
              player0Cards.filterNot(_ == card),
              player1Cards,
              won0Cards,
              won1Cards + card + played.get,
              None,
              trump,
              deck).optimize

            // player 0 perde last in-game
            case (true, 1) => Status(
              false,
              player0Cards.filterNot(_ == card) :+ trump,
              player1Cards :+ deck.head,
              won0Cards,
              won1Cards + card + played.get,
              None,
              trump,
              Vector()).optimize

            // player 0 perde in-game
            case (true, _) => Status(
              false,
              player0Cards.filterNot(_ == card) :+ deck(1),
              player1Cards :+ deck(0),
              won0Cards,
              won1Cards + card + played.get,
              None,
              trump,
              deck.drop(2)).optimize

            // player 0 vince in finale
            case (false, 0) => Status(
              true,
              player0Cards.filterNot(_ == card),
              player1Cards,
              won0Cards + card + played.get,
              won1Cards,
              None,
              trump,
              deck).optimize

            // player 0 vince last in-game
            case (false, 1) => Status(
              true,
              player0Cards.filterNot(_ == card) :+ deck.head,
              player1Cards :+ trump,
              won0Cards + card + played.get,
              won1Cards,
              None,
              trump,
              Vector()).optimize

            // player 0 vince in-game
            case (false, _) => Status(
              true,
              player0Cards.filterNot(_ == card) :+ deck(0),
              player1Cards :+ deck(1),
              won0Cards + card + played.get,
              won1Cards,
              None,
              trump,
              deck.drop(2)).optimize
          }
      }
    else if (player1Cards.isEmpty)
      this
    else {
      val card = player1Cards(choice)
      if (played.isEmpty)
        Status(
          true,
          player0Cards,
          player1Cards.filterNot(_ == card),
          won0Cards,
          won1Cards,
          Some(card),
          trump,
          deck).optimize
      else
        (played.get.versus(card), deck.size) match {
          // player 1 perde in finale
          case (true, 0) => Status(
            true,
            player0Cards,
            player1Cards.filterNot(_ == card),
            won0Cards + card + played.get,
            won1Cards,
            None,
            trump,
            deck).optimize

          // player 1 perde last in-game
          case (true, 1) => Status(
            true,
            player0Cards :+ deck.head,
            player1Cards.filterNot(_ == card) :+ trump,
            won0Cards + card + played.get,
            won1Cards,
            None,
            trump,
            Vector()).optimize

          // player 1 perde in-game
          case (true, _) => Status(
            true,
            player0Cards :+ deck(0),
            player1Cards.filterNot(_ == card) :+ deck(1),
            won0Cards + card + played.get,
            won1Cards,
            None,
            trump,
            deck.drop(2)).optimize

          // player 1 vince in finale
          case (false, 0) => Status(
            false,
            player0Cards,
            player1Cards.filterNot(_ == card),
            won0Cards,
            won1Cards + card + played.get,
            None,
            trump,
            deck).optimize

          // player 1 vince last in-game
          case (false, 1) => Status(
            false,
            player0Cards :+ trump,
            player1Cards.filterNot(_ == card) :+ deck.head,
            won0Cards,
            won1Cards + card + played.get,
            None,
            trump,
            Vector()).optimize

          // player 1 vince in-game
          case (false, _) => Status(
            false,
            player0Cards :+ deck(1),
            player1Cards.filterNot(_ == card) :+ deck(0),
            won0Cards,
            won1Cards + card + played.get,
            None,
            trump,
            deck.drop(2)).optimize
        }
    }

  /**
   *
   */
  def optimize: Status = this

  /**
   * Transform the status in int value
   */
  def toRow: List[Int] = {
    object CardState extends Enumeration {
      val Player0 = Value
      val Player1 = Value
      val Owned0 = Value
      val Owned1 = Value
      val Played = Value
      val Deck = Value
      val Trump = Value
    }

    val cardStatus =
      player0Cards.toList.map(c => (c.id, CardState.Player0.id)) :::
        player1Cards.toList.map(c => (c.id, CardState.Player1.id)) :::
        won0Cards.toList.map(c => (c.id, CardState.Owned0.id)) :::
        won1Cards.toList.map(c => (c.id, CardState.Owned1.id)) :::
        deck.toList.map(c => (c.id, CardState.Deck.id)) :::
        played.toList.map(c => (c.id, CardState.Played.id)) :::
        (if (deck.isEmpty) List() else List((trump.id, CardState.Trump.id)))

    val map = cardStatus.foldLeft((0 to 39).toIndexedSeq) { case (map, (idx, value)) => map.updated(idx, value) }

    (if (player0Turn) 1 else 0) ::
      ((player0Turn, isWinner0, isWinner1) match {
        case (false, true, false) => -player0Score
        case (false, false, true) => player1Score
        case (true, true, false) => player0Score
        case (true, false, true) => -player1Score
        case _ => 0
      }) ::
      trump.id ::
      map.toList
  }
}