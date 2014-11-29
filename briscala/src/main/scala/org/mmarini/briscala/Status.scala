/**
 *
 */
package org.mmarini.briscala

/**
 * @author us00852
 *
 */
case class Status(cards: Vector[CardStatus.Value]) {

  /**
   * Compute the score of player
   */
  def playerScore(): Int = score(CardStatus.Won)

  /**
   * Compute the score of opposite
   */
  def oppositeScore(): Int = score(CardStatus.Lost)

  /**
   * COmpute the score for a given status
   */
  def score(status: CardStatus.Value): Int = score(cards.zipWithIndex.filter { case (a, i) => a == status }.map(_._2))

  /**
   * Compute the score of a cards set
   */
  def score(cards: Seq[Int]): Int = cards.map(score).sum

  /**
   * Compute the score of a card
   */
  def score(card: Int): Int = (card % 10) match {
    case 0 => 11
    case 2 => 10
    case 7 => 2
    case 8 => 3
    case 9 => 4
    case _ => 0
  }

  /**
   * Generate the status when played a card
   */
  def statusFor(card: Int): Status = ???
}