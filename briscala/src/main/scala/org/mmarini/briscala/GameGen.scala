package org.mmarini.briscala

import scala.util.Random
import scala.reflect.internal.util.Collections

/**
 *
 */
class GameGen(random: Random) {

  /**
   * Suffle deck
   */
  def shuffle(deck: IndexedSeq[Int], n: Int): IndexedSeq[Int] =
    if (n <= 1)
      deck
    else {
      val i = random.nextInt(n)
      if (i == n - 1)
        deck
      else
        shuffle(deck.updated(i, deck(n - 1)).updated(n - 1, deck(i)), n - 1)
    }

  /**
   * Create a random inital game Status
   */
  def create(): Status = {

    val deck = shuffle(0 to 39, 40);
    Status(deck.take(3).foldLeft(
      (0 to 39).map(_ => CardStatus.Deck).toVector)((s, i) => s.updated(i, CardStatus.Player)).
      updated(deck(3), CardStatus.Trump))
  }
}