package org.mmarini.briscala

import org.scalatest._
import sun.security.jgss.krb5.InitSecContextToken

class StatusTest extends FunSpec with Matchers {

  describe("A Status") {

    describe("with an ace and a three won") {

      it("should have player score 11 + 10") {
        val status = Deck.createStatus(
          IndexedSeq(),
          IndexedSeq(),
          Set(Card(Figure.Ace, Seed.Seed1), Card(Figure.Three, Seed.Seed1)),
          Set(),
          None,
          Card(Figure.Three, Seed.Trump),
          IndexedSeq())
        status.playerScore should be(21)
      }
    }

    describe("with an ace and a king lost") {

      it("should have opposite score 11 + 4") {
        val status = Deck.createStatus(
          IndexedSeq(),
          IndexedSeq(),
          Set(),
          Set(Card(Figure.Ace, Seed.Seed1), Card(Figure.King, Seed.Seed1)),
          None,
          Card(Figure.Three, Seed.Trump),
          IndexedSeq())
        status.oppositeScore should be(15)
      }
    }
  }

  val status1 = Status(
    (1 to 3).map(new Card(_)),
    (4 to 6).map(new Card(_)),
    Set(),
    Set(),
    None,
    new Card(0),
    (7 to 39).map(new Card(_)))

  val status2 = Status(
    (4 to 6).map(new Card(_)),
    Vector(1, 3).map(new Card(_)),
    Set(),
    Set(),
    Some(new Card(2)),
    new Card(0),
    (7 to 39).map(new Card(_)))

  val status3 = Status(
    (1 to 3).map(new Card(_)),
    Vector(4, 6).map(new Card(_)),
    Set(),
    Set(),
    Some(new Card(5)),
    new Card(0),
    (7 to 39).map(new Card(_)))

  describe("The next status") {

    describe("for player first hand playing 2nd choice") {

      it("should have the played card in the table") {
        status1.nextStatus(1).played should contain(status1.playerCards(1))
      }

      it("should have remaining 2 cards to oppsite") {
        status1.nextStatus(1).oppositeCards should be(Seq(0, 2).map(status1.playerCards(_)))
      }

      it("should have player cards = previous opposite cards") {
        status1.nextStatus(1).playerCards should be(status1.oppositeCards)
      }

      it("should have deck cards = previous deck cards") {
        status1.nextStatus(1).deck should be(status1.deck)
      }
    }

    describe("for opposite first hand playing a win") {

      it("should have no played card") {
        status2.nextStatus(1).played should be(None)
      }

      it("should have 3 cards to opposite") {
        status2.nextStatus(1).oppositeCards should be(Seq(1, 3, 8).map(new Card(_)))
      }

      it("should have 3 player cards") {
        status2.nextStatus(1).playerCards should be(Seq(4, 6, 7).map(new Card(_)))
      }

      it("should have deck cards = previous deck cards except the first 2 cards") {
        status2.nextStatus(1).deck should be(status2.deck.drop(2))
      }

      it("should have no lost cards") {
        status2.nextStatus(1).lostCards should be(Set())
      }

      it("should have 2 won cards") {
        status2.nextStatus(1).wonCards should be(Set(new Card(2), new Card(5)))
      }
    }

    describe("for opposite first hand playing a loss") {

      it("should have no played card") {
        status2.nextStatus(1).played should be(None)
      }

      it("should have 3 player cards") {
        status2.nextStatus(1).playerCards should be(Seq(4, 6, 7).map(new Card(_)))
      }

      it("should have 3 cards to opposite") {
        status2.nextStatus(1).oppositeCards should be(Seq(1, 3, 8).map(new Card(_)))
      }

      it("should have deck cards = previous deck cards except the first 2 cards") {
        status2.nextStatus(1).deck should be(status2.deck.drop(2))
      }

      it("should have no lost cards") {
        status2.nextStatus(1).lostCards should be(Set())
      }

      it("should have 2 won cards") {
        status2.nextStatus(1).wonCards should be(Set(new Card(2), new Card(5)))
      }
    }
  }

}