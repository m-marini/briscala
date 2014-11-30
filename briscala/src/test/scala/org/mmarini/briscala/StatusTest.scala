package org.mmarini.briscala

import org.scalatest._

class StatusTest extends FlatSpec with Matchers {
  "A Status with an ace and a three won" should " have player score 11 + 10" in {
    val status = new Status(IndexedSeq(), Set(Card(Figure.Ace, Seed.Seed1), Card(Figure.Three, Seed.Seed1)), Set(), None, Some(Card(Figure.Three, Seed.Trump)))
    status.playerScore should be(21)
  }

  "A Status with an ace and a king lost" should " have opposite score 11 + 4" in {
    val status = new Status(IndexedSeq(), Set(), Set(Card(Figure.Ace, Seed.Seed1), Card(Figure.King, Seed.Seed1)), None, Some(Card(Figure.Three, Seed.Trump)))
    status.oppositeScore should be(15)
  }

  "A Status" should " have a deck with all cards except the player cards, the trump card, the won cards and lost cards" in {
    val status = new Status(IndexedSeq(Card(Figure.Ace, Seed.Trump)),
      Set(Card(Figure.Two, Seed.Trump)),
      Set(Card(Figure.Three, Seed.Trump)),
      Some(Card(Figure.Four, Seed.Trump)),
      Some(Card(Figure.Five, Seed.Trump)))
    status.deck should have length (35)
  }
}