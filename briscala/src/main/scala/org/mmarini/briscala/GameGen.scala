package org.mmarini.briscala

import scala.util.Random
import org.mmarini.briscala._

/**
 *
 */
class GameGen(random: Random) {

  /**
   * Suffle deck
   */
  def shuffle(deck: IndexedSeq[Card], n: Int): IndexedSeq[Card] =
    if (n <= 1)
      deck
    else {
      val i = random.nextInt(n)
      if (i == n - 1)
        deck
      else
        shuffle(deck.updated(i, deck(n - 1)).updated(n - 1, deck(i)), n - 1)
    }

  def initDeck: IndexedSeq[Card] =
    (for {
      i <- Figure.values
      j <- Seed.values
    } yield Card(i, j)).toIndexedSeq

  /**
   * Create a random inital game Status
   */
  def create(): Status = {

    val deck = shuffle(initDeck, 40);
    Status(deck.take(3).toSet, Set(), Set(), None, deck(3))
  }
}