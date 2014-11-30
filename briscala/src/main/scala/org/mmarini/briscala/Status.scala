/**
 *
 */
package org.mmarini.briscala

import scala.util.Random

/**
 * @author us00852
 *
 */
case class Status(playerCards: IndexedSeq[Card],
  wonCards: Set[Card],
  lostCards: Set[Card],
  played: Option[Card],
  trump: Option[Card],
  deck: IndexedSeq[Card]) {

  /**
   *
   */
  def this(playerCards: IndexedSeq[Card],
    wonCards: Set[Card],
    lostCards: Set[Card],
    played: Option[Card],
    trump: Option[Card]) = this(playerCards,
    wonCards,
    lostCards,
    played,
    trump,
    Deck.allCards.filterNot(card => playerCards.contains(card) ||
      wonCards.contains(card) ||
      lostCards.contains(card) ||
      trump.contains(card) ||
      played.contains(card)))

  /**
   *
   */
  def this(playerCards: IndexedSeq[Card],
    wonCards: Set[Card],
    lostCards: Set[Card],
    played: Option[Card],
    trump: Option[Card],
    random: Random) = this(playerCards,
    wonCards,
    lostCards,
    played,
    trump,
    Deck.shuffle(random).filterNot(card => playerCards.contains(card) ||
      wonCards.contains(card) ||
      lostCards.contains(card) ||
      trump.contains(card) ||
      trump == card ||
      played.contains(card)))

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
  def nextStatus(choice: Int): Status = {
    val card = playerCards(choice)
    val playerRemainder = playerCards.filterNot(_ == card)
    if (deck.length > 3) {
      if (played.contains(None)) {
        // Durante il gioco mano del giocatore
        val nd = deck.tail;
      } else {
        // Durante il gioco mano dell'avversario
      }
    } else {

    }
    ???
  }
}