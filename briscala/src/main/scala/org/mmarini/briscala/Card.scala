package org.mmarini.briscala
/**
 *
 */
case class Card(figure: Figure.Value, seed: Seed.Value) {

  /**
   * Create a card from the id
   */
  def this(id: Int) = this(Figure(id % 10), Seed(id / 10))

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

  /**
   * Return true if the card win over another card
   */
  def versus(other: Card): Boolean = other match {
    case Card(of, os) if (seed == os) => figure.id > of.id
    case Card(_, os) if (seed == Seed.Trump) => true
    case Card(_, os) if (os == Seed.Trump) => false
    case _ => true
  }

  /**
   * Return the id of a card
   */
  def id: Int = figure.id + seed.id * 10
}
