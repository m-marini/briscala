package org.mmarini.briscala

import org.scalatest._

class StatusTest extends FlatSpec with Matchers {
  "A Status with an ace and a three won" should " have player score 11 + 10" in {
    val status = new Status(
      IndexedSeq(),
      IndexedSeq(),
      Set(Card(Figure.Ace, Seed.Seed1), Card(Figure.Three, Seed.Seed1)),
      Set(),
      None,
      Card(Figure.Three, Seed.Trump))
    status.playerScore should be(21)
  }

  "A Status with an ace and a king lost" should " have opposite score 11 + 4" in {
    val status = new Status(
      IndexedSeq(),
      IndexedSeq(),
      Set(),
      Set(Card(Figure.Ace, Seed.Seed1), Card(Figure.King, Seed.Seed1)),
      None,
      Card(Figure.Three, Seed.Trump))
    status.oppositeScore should be(15)
  }

  "A Status" should " have the deck with all cards except the player cards, the opposite cards, the trump card, the won cards and lost cards" in {
    val status = new Status(
      IndexedSeq(new Card(0)),
      IndexedSeq(new Card(1)),
      Set(new Card(2)),
      Set(new Card(3)),
      Some(new Card(4)),
      new Card(5))
    status.deck should have length (34)
  }
}