package org.mmarini.briscala
/**
 *
 */
case class Card(figure: Figure.Value, seed: Seed.Value) {

  /**
   * Return the score of the card
   */
  def score: Int = figure match {
    case Figure.Ace => 11
    case Figure.Three => 10
    case Figure.Jack => 2
    case Figure.Knight => 3
    case Figure.King => 4
    case _ => 0
  }

  /**
   * return true if card is a trump
   */
  def isTrump: Boolean = seed == Seed.Trump
}
