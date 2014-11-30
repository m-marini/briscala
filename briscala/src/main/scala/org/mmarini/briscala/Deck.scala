package org.mmarini.briscala

import scala.util.Random
import org.mmarini.briscala._

/**
 *
 */
object Deck {

  /**
   * Suffle deck
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
   * Create a deck qith all cards
   */
  def allCards: IndexedSeq[Card] =
    (for {
      i <- Figure.values
      j <- Seed.values
    } yield Card(i, j)).toIndexedSeq

  /**
   * Create a shuffled deck
   */
  def shuffle(random: Random): IndexedSeq[Card] = shuffle(allCards, 40, random);
}