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
  oppositeCards: IndexedSeq[Card],
  wonCards: Set[Card],
  lostCards: Set[Card],
  played: Option[Card],
  trump: Card,
  deck: IndexedSeq[Card]) {

  /**
   *
   */
  def this(playerCards: IndexedSeq[Card],
    oppositeCards: IndexedSeq[Card],
    wonCards: Set[Card],
    lostCards: Set[Card],
    played: Option[Card],
    trump: Card) = this(playerCards,
    oppositeCards,
    wonCards,
    lostCards,
    played,
    trump,
    Deck.allCards.filterNot(card => playerCards.contains(card) ||
      oppositeCards.contains(card) ||
      wonCards.contains(card) ||
      lostCards.contains(card) ||
      trump == card ||
      played.contains(card)))

  /**
   *
   */
  def this(playerCards: IndexedSeq[Card],
    oppositeCards: IndexedSeq[Card],
    wonCards: Set[Card],
    lostCards: Set[Card],
    played: Option[Card],
    trump: Card,
    random: Random) = this(playerCards,
    oppositeCards,
    wonCards,
    lostCards,
    played,
    trump,
    Deck.shuffle(random).filterNot(card => playerCards.contains(card) ||
      oppositeCards.contains(card) ||
      wonCards.contains(card) ||
      lostCards.contains(card) ||
      trump == card ||
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
   * Return if the player wins the game
   */
  def isWinner: Boolean = playerScore > 60

  /**
   * Return if the player wins the game
   */
  def isLooser: Boolean = oppositeScore > 60

  /**
   *
   */
  def isDraw: Boolean = playerScore == 60 && oppositeScore == 60

  /**
   * Return if the game is completed
   */
  def isCompleted: Boolean = isWinner || isLooser || isDraw

  /**
   * Generate the status when played a card
   */
  def nextStatus(choice: Int): Status = {
    val card = playerCards(choice)
    if (played.contains(None))
      Status(
        oppositeCards,
        playerCards.filter(_ == card),
        lostCards,
        wonCards,
        Some(card),
        trump,
        deck)
    else if (deck.isEmpty) {
      if (played.get.versus(card))
        // Finale mano dell'avversario che vince 
        Status(
          oppositeCards,
          playerCards.filter(_ == card),
          lostCards + played.get + card,
          wonCards,
          None,
          trump,
          deck)
      else
        // Finale mano dell'avversario che perde 
        Status(
          playerCards.filter(_ == card),
          oppositeCards,
          wonCards + played.get + card,
          lostCards,
          None,
          trump,
          deck)
    } else if (deck.length == 1) {
      if (played.get.versus(card))
        // Ultimo deal mano dell'avversario che vince 
        Status(
          oppositeCards :+ deck(0),
          playerCards.filter(_ == card) :+ trump,
          lostCards + played.get + card,
          wonCards,
          None,
          trump,
          IndexedSeq())
      else
        // Ultimo deal mano dell'avversario che perde 
        Status(
          playerCards.filter(_ == card) :+ deck(0),
          oppositeCards :+ trump,
          wonCards + played.get + card,
          lostCards,
          None,
          trump,
          IndexedSeq())
    } else if (played.get.versus(card))
      // Durante il gioco mano dell'avversario che vince 
      Status(
        oppositeCards :+ deck(1),
        playerCards.filter(_ == card) :+ deck(0),
        lostCards + played.get + card,
        wonCards,
        None,
        trump,
        deck.drop(2))
    else
      // Durante il gioco mano dell'avversario che perde 
      Status(
        playerCards.filter(_ == card) :+ deck(0),
        oppositeCards :+ deck(1),
        wonCards + played.get + card,
        lostCards,
        None,
        trump,
        deck.drop(2))
  }
}