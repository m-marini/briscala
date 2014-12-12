package org.mmarini.briscala

import scala.util.Random

object Game {

  def create(random: Random): List[(Status, Option[Int])] = {
    def next(l: List[(Status, Option[Int])], s: Status): List[(Status, Option[Int])] =
      if (s.isCompleted)
        (s, None) :: l
      else {
        val c = random.nextInt(s.numOfChoice)
        next((s, Some(c)) :: l, s.nextStatus(c))
      }

    next(List(), createInitStatus(random)).reverse;
  }

  /**
   *
   */
  private def createInitStatus(random: Random): Status = {
    val deck = Deck.shuffle(random)
    val Some(trump) = deck.drop(6).find(_.isTrump)
    Status(
      true,
      deck.take(3),
      deck.drop(3).take(3),
      Set(),
      Set(),
      None,
      trump,
      deck.drop(6).filterNot(_ == trump))
  }
}