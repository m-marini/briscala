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

  /**
   * Compute the score of player
   */
  def player0Score: Int = score(won0Cards)

  /**
   * Compute the score of opposite
   */
  def player1Score: Int = score(won1Cards)

  /**
   * Compute the score of a cards set
   */
  def score(cards: Set[Card]): Int = cards.map(_.score).sum

  /**
   * Return if the player wins the game
   */
  def isWinner0: Boolean = player0Score > 60

  /**
   * Return if the player wins the game
   */
  def isWinner1: Boolean = player1Score > 60

  /**
   *
   */
  def isDraw: Boolean = player0Score == 60 && player1Score == 60

  /**
   * Return if the game is completed
   */
  def isCompleted: Boolean = isWinner0 || isWinner1 || isDraw

  /**
   * Generate the status when played a card
   */
  def nextStatus(choice: Int): Status = {
    if (player0Cards.isEmpty)
      this
    else {
      val card =
        if (player0Turn) player0Cards(choice)
        else player0Cards(choice)
      if (played.isEmpty)
        if (player0Turn)
          Status(
            !player0Turn,
            player0Cards.filterNot(_ == card),
            player1Cards,
            won0Cards,
            won1Cards,
            Some(card),
            trump,
            deck).optimize
        else
          Status(
            !player0Turn,
            player0Cards,
            player1Cards.filterNot(_ == card),
            won0Cards,
            won1Cards,
            Some(card),
            trump,
            deck).optimize
      else if (deck.isEmpty) {
        if (played.get.versus(card))
          // Finale mano dell'avversario che vince 
          Status(
            player1Cards,
            player0Cards.filterNot(_ == card),
            won1Cards + played.get + card,
            won0Cards,
            None,
            trump,
            deck).optimize
        else
          // Finale mano dell'avversario che perde 
          Status(
            player0Cards.filterNot(_ == card),
            player1Cards,
            won0Cards + played.get + card,
            won1Cards,
            None,
            trump,
            deck).optimize
      } else if (deck.length == 1) {
        if (played.get.versus(card))
          // Ultima mano dell'avversario che vince 
          Status(
            player1Cards :+ deck(0),
            player0Cards.filterNot(_ == card) :+ trump,
            won1Cards + played.get + card,
            won0Cards,
            None,
            trump,
            IndexedSeq()).optimize
        else
          // Ultima mano dell'avversario che perde 
          Status(
            player0Cards.filterNot(_ == card) :+ deck(0),
            player1Cards :+ trump,
            won0Cards + played.get + card,
            won1Cards,
            None,
            trump,
            IndexedSeq()).optimize
      } else if (played.get.versus(card))
        // Durante il gioco mano dell'avversario che vince 
        Status(
          player1Cards :+ deck(1),
          player0Cards.filterNot(_ == card) :+ deck(0),
          won1Cards + played.get + card,
          won0Cards,
          None,
          trump,
          deck.drop(2)).optimize
      else
        // Durante il gioco mano dell'avversario che perde 
        Status(
          player0Cards.filterNot(_ == card) :+ deck(0),
          player1Cards :+ deck(1),
          won0Cards + played.get + card,
          won1Cards,
          None,
          trump,
          deck.drop(2)).optimize
    }
  }

  /**
   *
   */
  def optimize: Status = this
}