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

  describe("The next status") {
    describe("for player first hand playing 2nd choice") {
      val status = Status(
        (1 to 3).map(new Card(_)),
        (4 to 6).map(new Card(_)),
        Set(),
        Set(),
        None,
        new Card(0),
        (7 to 39).map(new Card(_)))

      it("should have player cards = previous opposite cards") { status.nextStatus(1).playerCards should be(status.oppositeCards) }
      it("should have remaining 2 cards to oppsite") { status.nextStatus(1).oppositeCards should be(Seq(0, 2).map(status.playerCards(_))) }
      it("should have the played card in the table") { status.nextStatus(1).played should contain(status.playerCards(1)) }
      it("should have deck cards = previous deck cards") { status.nextStatus(1).deck should be(status.deck) }
    }

    describe("for opposite first hand playing a win") {
      val status = Status(
        (4 to 6).map(new Card(_)),
        Vector(1, 3).map(new Card(_)),
        Set(),
        Set(),
        Some(new Card(2)),
        new Card(0),
        (7 to 39).map(new Card(_)))

      it("should have 3 players' cards") { status.nextStatus(1).playerCards should be(Seq(4, 6, 7).map(new Card(_))) }
      it("should have 3 opposites' cards") { status.nextStatus(1).oppositeCards should be(Seq(1, 3, 8).map(new Card(_))) }
      it("should win the played cards") { status.nextStatus(1).wonCards should be(Set(new Card(2), new Card(5))) }
      it("should have no lost cards") { status.nextStatus(1).lostCards should be(Set()) }
      it("should have no played card") { status.nextStatus(1).played should be(None) }
      it("should have deck cards = previous deck cards without the first 2 cards") { status.nextStatus(1).deck should be(status.deck.drop(2)) }
    }

    describe("for opposite first hand playing a loss") {
      val status = Status(
        (1 to 3).map(new Card(_)),
        Vector(4, 6).map(new Card(_)),
        Set(),
        Set(),
        Some(new Card(5)),
        new Card(0),
        (7 to 39).map(new Card(_)))

      it("should have 3 player cards") { status.nextStatus(1).playerCards should be(Seq(4, 6, 8).map(new Card(_))) }
      it("should have 3 opposite cards") { status.nextStatus(1).oppositeCards should be(Seq(1, 3, 7).map(new Card(_))) }
      it("should win the played cards") { status.nextStatus(1).wonCards should be(Set(new Card(2), new Card(5))) }
      it("should have no lost cards") { status.nextStatus(1).lostCards should be(Set()) }
      it("should have no played card") { status.nextStatus(1).played should be(None) }
      it("should have deck cards = previous deck cards without the first 2 cards") { status.nextStatus(1).deck should be(status.deck.drop(2)) }
    }

    describe("for player last in-game hand playing 2nd choice") {
      val status = Status(
        (2 to 4).map(new Card(_)),
        (5 to 7).map(new Card(_)),
        ((10 to 19) ++ Seq(9, 30, 32, 35, 36, 37)).map(new Card(_))toSet,
        ((20 to 29) ++ Seq(8, 31, 33, 34, 38, 39)).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector(new Card(1)))

      it("should have player cards = previous opposite cards") { status.nextStatus(1).playerCards should be(status.oppositeCards) }
      it("should have remaining 2 cards to opposite") { status.nextStatus(1).oppositeCards should be(Seq(2, 4).map(new Card(_))) }
      it("should have the played card in the table") { status.nextStatus(1).played should contain(status.playerCards(1)) }
      it("should have deck cards = previous deck cards") { status.nextStatus(1).deck should be(status.deck) }
    }

    describe("for opposite last in-game hand playing a win") {
      val status = Status(
        (5 to 7).map(new Card(_)),
        Vector(2, 4).map(new Card(_)),
        ((10 to 19) ++ Seq(9, 30, 32, 35, 36, 37)).map(new Card(_))toSet,
        ((20 to 29) ++ Seq(8, 31, 33, 34, 38, 39)).map(new Card(_)).toSet,
        Some(new Card(3)),
        new Card(0),
        Vector(new Card(1)))

      it("should have 3 players' cards") { status.nextStatus(1).playerCards should be(Seq(5, 7, 1).map(new Card(_))) }
      it("should have 3 opposites' cards") { status.nextStatus(1).oppositeCards should be(Seq(2, 4, 0).map(new Card(_))) }
      it("should win the played cards") { status.nextStatus(1).wonCards should be(status.wonCards ++ Set(new Card(3), new Card(6))) }
      it("should have lost cards = previous lost card") { status.nextStatus(1).lostCards should be(status.lostCards) }
      it("should have no played card") { status.nextStatus(1).played should be(None) }
      it("should have trump card") { status.nextStatus(1).trump should be(new Card(0)) }
      it("should have no deck cards") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("for opposite last in-game hand playing a lost") {
      val status = Status(
        (2 to 4).map(new Card(_)),
        Vector(5, 7).map(new Card(_)),
        ((10 to 19) ++ Seq(9, 30, 32, 35, 36, 37)).map(new Card(_))toSet,
        ((20 to 29) ++ Seq(8, 31, 33, 34, 38, 39)).map(new Card(_)).toSet,
        Some(new Card(6)),
        new Card(0),
        Vector(new Card(1)))

      it("should have 3 players' cards") { status.nextStatus(1).playerCards should be(Seq(5, 7, 1).map(new Card(_))) }
      it("should have 3 opposites' cards") { status.nextStatus(1).oppositeCards should be(Seq(2, 4, 0).map(new Card(_))) }
      it("should win the played cards") { status.nextStatus(1).wonCards should be(status.lostCards ++ Set(new Card(3), new Card(6))) }
      it("should have lost cards == previous won card") { status.nextStatus(1).lostCards should be(status.wonCards) }
      it("should have no played card") { status.nextStatus(1).played should be(None) }
      it("should have trump card") { status.nextStatus(1).trump should be(new Card(0)) }
      it("should have no deck cards") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("for 3 cards final, player hand playing 2nd choice") {
      val status = Status(
        Vector(0, 1, 2).map(new Card(_)),
        Vector(3, 4, 5).map(new Card(_)),
        ((10 to 19) ++ Seq(6, 9, 30, 32, 35, 36, 37)).map(new Card(_)).toSet,
        ((20 to 29) ++ Seq(7, 8, 31, 33, 34, 38, 39)).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector())

      it("should have player cards = previous opposite cards") { status.nextStatus(1).playerCards should be(status.oppositeCards) }
      it("should have 2 opposite cards") { status.nextStatus(1).oppositeCards should be(Seq(0, 2).map(new Card(_))) }
      it("should have the played card in the table") { status.nextStatus(1).played should contain(new Card(1)) }
      it("should have no deck cards") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("for last card final, player hand playing only choice") {
      val status = Status(
        Vector(0).map(new Card(_)),
        Vector(1).map(new Card(_)),
        ((10 to 19) ++ Seq(2, 5, 6, 9, 30, 32, 35, 36, 37)).map(new Card(_)).toSet,
        ((20 to 29) ++ Seq(3, 4, 7, 8, 31, 33, 34, 38, 39)).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector())

      it("should have player cards = previous opposite cards") { status.nextStatus(0).playerCards should be(status.oppositeCards) }
      it("should have no opposite cards") { status.nextStatus(0).oppositeCards should be(Vector()) }
      it("should have the played card in the table") { status.nextStatus(0).played should contain(new Card(0)) }
      it("should have no deck cards") { status.nextStatus(0).deck should be(Vector()) }
    }

    describe("for last card final, opposite hand playing only win choice") {
      val status = Status(
        Vector(1).map(new Card(_)),
        Vector(),
        ((10 to 19) ++ Seq(2, 5, 6, 9, 30, 32, 35, 36, 37)).map(new Card(_)).toSet,
        ((20 to 29) ++ Seq(3, 4, 7, 8, 31, 33, 34, 38, 39)).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector())

      it("should have player cards = previous opposite cards") { status.nextStatus(0).playerCards should be(status.oppositeCards) }
      it("should have no opposite cards") { status.nextStatus(0).oppositeCards should be(Vector()) }
      it("should have the played card in the table") { status.nextStatus(0).played should contain(new Card(0)) }
      it("should have no deck cards") { status.nextStatus(0).deck should be(Vector()) }
    }
  }
}