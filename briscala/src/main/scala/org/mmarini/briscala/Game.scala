package org.mmarini.briscala

import scala.util.Random

object Game {

  def create(random: Random): List[Status] = {
    def next(l: List[Status], s: Status): List[Status] =
      if (s.isCompleted)
        s :: l
      else
        next(s :: l, s.nextStatus(random.nextInt(s.numOfChoice)))

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