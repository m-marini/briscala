package org.mmarini.briscala

import org.scalatest._

class DeckTest extends FlatSpec with Matchers {
  "A forced Deck" should "have score 40 cards" in {
    val start = IndexedSeq(Card(Figure.King, Seed.Trump), Card(Figure.Two, Seed.Seed1))
    val deck = Deck.force(start)
    deck should have length (40)
  }

  "A forced Deck" should "start with the forced cards" in {
    val start = IndexedSeq(Card(Figure.King, Seed.Trump), Card(Figure.Two, Seed.Seed1))
    val deck = Deck.force(start)
    deck.take(start.length) should contain theSameElementsInOrderAs start
  }

}