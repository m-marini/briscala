package org.mmarini.briscala

import scala.util.Random
import org.mmarini.briscala._
import breeze.stats.distributions.Rand
import breeze.stats.distributions.RandBasis

/**
 *
 */
object Deck {
  val sorted = (0 to 39).map(new Card(_))

  /**
   * Create a shuffled deck
   */
  def shuffle(random: RandBasis): IndexedSeq[Card] =
    random.permutation(40).draw.map(i => new Card(i))

  /**
   *
   */
  def force(deck: IndexedSeq[Card]): IndexedSeq[Card] = deck ++ sorted.filterNot(deck.contains(_))

}