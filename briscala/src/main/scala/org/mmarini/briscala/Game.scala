package org.mmarini.briscala

import scala.util.Random

object Game {

  def create(random: Random): List[(Status, Int)] = {
    def next(l: List[(Status, Int)], s: Status): List[(Status, Int)] =
      if (s.isCompleted)
        (s, -1) :: l
      else {
        val c = random.nextInt(s.numOfChoice)
        next((s, c) :: l, s.nextStatus(c))
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
      random.nextBoolean,
      deck.take(3),
      deck.drop(3).take(3),
      Set(),
      Set(),
      None,
      trump,
      deck.drop(6).filterNot(_ == trump)).optimize
  }
}