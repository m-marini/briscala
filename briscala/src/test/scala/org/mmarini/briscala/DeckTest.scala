package org.mmarini.briscala

import org.scalatest._

class DeckTest extends FunSpec with Matchers {
  val start = IndexedSeq(Card(Figure.King, Seed.Trump), Card(Figure.Two, Seed.Seed1))
  describe("A Deck") {
    describe("when forced") {
      it("should have 40 cards") {
        val deck = Deck.force(start)
        deck should have length (40)
      }
      it("should start with the forced cards") {
        val deck = Deck.force(start)
        deck.take(start.length) should contain theSameElementsInOrderAs start
      }

    }
  }

}