package org.mmarini.briscala

import scala.util.Random
import org.mmarini.briscala._

/**
 *
 */
object Deck {
  val sorted = (0 to 39).map(new Card(_))

  /**
   * Shuffle deck
   */
  private def shuffle(deck: IndexedSeq[Card], n: Int, random: Random): IndexedSeq[Card] =
    if (n <= 1)
      deck
    else {
      val i = random.nextInt(n)
      if (i == n - 1)
        deck
      else
        shuffle(deck.updated(i, deck(n - 1)).updated(n - 1, deck(i)), n - 1, random)
    }

  /**
   * Create a shuffled deck
   */
  def shuffle(random: Random): IndexedSeq[Card] = shuffle(sorted, 40, random);

  /**
   *
   */
  def force(deck: IndexedSeq[Card]): IndexedSeq[Card] = deck ++ sorted.filterNot(deck.contains(_))

  /**
   *
   */
  def createInitStatus(random: Random) = {
    val deck = shuffle(random)
    val Some(trump) = deck.drop(6).find(_.isTrump)
    Status(
      random.nextBoolean,
      deck.take(3),
      deck.drop(3).take(3),
      Set(),
      Set(),
      None,
      trump,
      deck.drop(6).filterNot(_ == trump)).optimize
  }

  /**
   *
   */
  def createStatus(
    player0Turn: Boolean,
    playerCards: IndexedSeq[Card],
    oppositeCards: IndexedSeq[Card],
    wonCards: Set[Card],
    lostCards: Set[Card],
    played: Option[Card],
    trump: Card,
    init: IndexedSeq[Card]) = Status(
    player0Turn,
    playerCards,
    oppositeCards,
    wonCards,
    lostCards,
    played,
    trump,
    force(init).filterNot(card => playerCards.contains(card) ||
      oppositeCards.contains(card) ||
      wonCards.contains(card) ||
      lostCards.contains(card) ||
      trump == card ||
      trump == card ||
      played.contains(card))).optimize
}