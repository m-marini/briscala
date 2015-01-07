/**
 *
 */
package org.mmarini.briscala

import scala.collection.immutable.Map
import breeze.linalg.DenseVector
import breeze.linalg.operators.DenseVector_GenericOps

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
   * Return the player cards
   */
  lazy val playerCards = if (player0Turn) player0Cards else player1Cards

  /**
   * Return the player cards
   */
  lazy val oppositeCards = if (player0Turn) player1Cards else player0Cards

  /**
   * Return the player cards
   */
  lazy val wonCards = if (player0Turn) won0Cards else won1Cards

  /**
   * Return the player cards
   */
  lazy val lostCards = if (player0Turn) won1Cards else won0Cards

  /**
   * Return player score
   */
  lazy val playerScore = if (player0Turn) player0Score else player1Score

  /**
   * Return player score
   */
  lazy val oppositeScore = if (player0Turn) player1Score else player0Score

  /**
   * Return the number of possible choices
   */
  lazy val numOfChoice = playerCards.size

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
            deck)
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
              deck)

            // player 0 perde last in-game
            case (true, 1) => Status(
              false,
              player0Cards.filterNot(_ == card) :+ trump,
              player1Cards :+ deck.head,
              won0Cards,
              won1Cards + card + played.get,
              None,
              trump,
              Vector())

            // player 0 perde in-game
            case (true, _) => Status(
              false,
              player0Cards.filterNot(_ == card) :+ deck(1),
              player1Cards :+ deck(0),
              won0Cards,
              won1Cards + card + played.get,
              None,
              trump,
              deck.drop(2))

            // player 0 vince in finale
            case (false, 0) => Status(
              true,
              player0Cards.filterNot(_ == card),
              player1Cards,
              won0Cards + card + played.get,
              won1Cards,
              None,
              trump,
              deck)

            // player 0 vince last in-game
            case (false, 1) => Status(
              true,
              player0Cards.filterNot(_ == card) :+ deck.head,
              player1Cards :+ trump,
              won0Cards + card + played.get,
              won1Cards,
              None,
              trump,
              Vector())

            // player 0 vince in-game
            case (false, _) => Status(
              true,
              player0Cards.filterNot(_ == card) :+ deck(0),
              player1Cards :+ deck(1),
              won0Cards + card + played.get,
              won1Cards,
              None,
              trump,
              deck.drop(2))
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
          deck)
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
            deck)

          // player 1 perde last in-game
          case (true, 1) => Status(
            true,
            player0Cards :+ deck.head,
            player1Cards.filterNot(_ == card) :+ trump,
            won0Cards + card + played.get,
            won1Cards,
            None,
            trump,
            Vector())

          // player 1 perde in-game
          case (true, _) => Status(
            true,
            player0Cards :+ deck(0),
            player1Cards.filterNot(_ == card) :+ deck(1),
            won0Cards + card + played.get,
            won1Cards,
            None,
            trump,
            deck.drop(2))

          // player 1 vince in finale
          case (false, 0) => Status(
            false,
            player0Cards,
            player1Cards.filterNot(_ == card),
            won0Cards,
            won1Cards + card + played.get,
            None,
            trump,
            deck)

          // player 1 vince last in-game
          case (false, 1) => Status(
            false,
            player0Cards :+ trump,
            player1Cards.filterNot(_ == card) :+ deck.head,
            won0Cards,
            won1Cards + card + played.get,
            None,
            trump,
            Vector())

          // player 1 vince in-game
          case (false, _) => Status(
            false,
            player0Cards :+ deck(1),
            player1Cards.filterNot(_ == card) :+ deck(0),
            won0Cards,
            won1Cards + card + played.get,
            None,
            trump,
            deck.drop(2))
        }
    }
}
