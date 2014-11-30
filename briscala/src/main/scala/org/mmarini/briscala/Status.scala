/**
 *
 */
package org.mmarini.briscala

/**
 * @author us00852
 *
 */
case class Status(playerCards: Set[Card], wonCards: Set[Card], lostCards: Set[Card], played: Option[Card], trump: Card) {

  /**
   * Compute the score of player
   */
  def playerScore: Int = score(wonCards)

  /**
   * Compute the score of opposite
   */
  def oppositeScore: Int = score(lostCards)

  /**
   * Compute the score of a cards set
   */
  def score(cards: Set[Card]): Int = cards.map(_.score).sum

  /**
   * Generate the status when played a card
   */
  def statusFor(choice: Int): Status = {
    ???
  }
}