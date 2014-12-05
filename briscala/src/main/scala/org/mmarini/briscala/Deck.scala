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
      shuffle(
        if (i == n - 1)
          deck
        else
          deck.updated(i, deck(n - 1)).updated(n - 1, deck(i)),
        n - 1, random)
    }

  /**
   * Create a shuffled deck
   */
  def shuffle(random: Random): IndexedSeq[Card] = shuffle(sorted, 40, random);

  /**
   *
   */
  def force(deck: IndexedSeq[Card]): IndexedSeq[Card] = deck ++ sorted.filterNot(deck.contains(_))

}