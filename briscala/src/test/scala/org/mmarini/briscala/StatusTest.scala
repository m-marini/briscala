package org.mmarini.briscala

import org.scalatest._
import sun.security.jgss.krb5.InitSecContextToken
import scala.collection.immutable.Vector
import scala.collection.immutable.Map

class StatusTest extends FunSpec with Matchers {

  describe("A Status") {

    describe("when have an ace and a king lost and an ace and a three won") {
      val status = Status(
        true,
        IndexedSeq(),
        IndexedSeq(),
        Set(Card(Figure.Ace, Seed.Seed2), Card(Figure.Three, Seed.Seed2)),
        Set(Card(Figure.Ace, Seed.Seed1), Card(Figure.King, Seed.Seed1)),
        None,
        new Card(0),
        Vector())

      it("should have player score 11 + 10") { status.player0Score should be(21) }
      it("should have opposite score 11 + 4") { status.player1Score should be(15) }
    }

    describe("when have unsorted deck list") {
      val status = Status(
        true,
        (1 to 3).map(new Card(_)),
        (4 to 6).map(new Card(_)),
        List(29, 38, 39).map(new Card(_)).toSet,
        Set(),
        None,
        new Card(0),
        ((7 to 28) ++ (30 to 37)).map(new Card(_)).toIndexedSeq)
      it("should sort the outage row") {
        status.toRow.drop(4) should be(
          5 :: 0 :: 0 :: 0 :: 1 :: 1 :: 1 :: 6 :: 6 :: 6 ::
            6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 2 :: 2 ::
            6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 2 ::
            6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 6 ::
            List())
      }
    }

    describe("when have unsorted won list") {
      val status = Status(
        true,
        (1 to 3).map(new Card(_)),
        (4 to 6).map(new Card(_)),
        List(17, 18, 19, 27, 28, 37).map(new Card(_)).toSet,
        List(29, 38, 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        ((7 to 16) ++ (20 to 26) ++ (30 to 36)).map(new Card(_)).toIndexedSeq)
      it("should sort the outage row") {
        status.toRow.drop(4) should be(
          5 :: 0 :: 0 :: 0 :: 1 :: 1 :: 1 :: 6 :: 6 :: 6 ::
            6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 2 :: 3 :: 3 ::
            6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 2 :: 2 :: 3 ::
            6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 2 :: 2 :: 2 ::
            List())
      }
    }

    describe("when have unsorted lost list") {
      val status = Status(
        true,
        (1 to 3).map(new Card(_)),
        List(28, 38, 39).map(new Card(_)).toIndexedSeq,
        List(16, 17, 26, 27, 36, 37).map(new Card(_)).toSet,
        List(18, 19, 29).map(new Card(_)).toSet,
        None,
        new Card(0),
        ((4 to 15) ++ (20 to 25) ++ (30 to 35)).map(new Card(_)).toIndexedSeq)
      it("should sort the outage row") {
        status.toRow.drop(4) should be(
          5 :: 0 :: 0 :: 0 :: 6 :: 6 :: 6 :: 6 :: 6 :: 6 ::
            6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 2 :: 2 :: 1 :: 1 ::
            6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 2 :: 2 :: 1 :: 3 ::
            6 :: 6 :: 6 :: 6 :: 6 :: 6 :: 2 :: 2 :: 3 :: 3 ::
            List())
      }
    }
  }

  describe("The next status") {
    describe("when player0 plays 2nd choice") {
      val status = Status(
        true,
        (1 to 3).map(new Card(_)),
        (4 to 6).map(new Card(_)),
        Set(),
        Set(),
        None,
        new Card(0),
        (7 to 39).map(new Card(_)))
      val next = status.nextStatus(1)

      it("should have player1 turn") { next.player0Turn should be(false) }
      it("should have player0 remaining with 2 cards") { next.player0Cards should be(Seq(1, 3).map(new Card(_))) }
      it("should have player1 with previous cards") { next.player1Cards should be(status.player1Cards) }
      it("should have player0 with previous won cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with previous won cards") { next.won1Cards should be(status.won1Cards) }
      it("should have the played card in the table") { next.played should contain(new Card(2)) }
      it("should have deck cards with previous cards") { next.deck should be(status.deck) }
    }

    describe("when player1 plays 2nd choice") {
      val status = Status(
        false,
        (1 to 3).map(new Card(_)),
        (4 to 6).map(new Card(_)),
        Set(),
        Set(),
        None,
        new Card(0),
        (7 to 39).map(new Card(_)))
      val next = status.nextStatus(1)

      it("should have player0 turn") { next.player0Turn should be(true) }
      it("should have player0 with previous cards") { next.player0Cards should be(status.player0Cards) }
      it("should have player1 reimaning with 2 cards") { next.player1Cards should be(Seq(4, 6).map(new Card(_))) }
      it("should have player0 with previous won cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with previous won cards") { next.won1Cards should be(status.won1Cards) }
      it("should have the played card in the table") { next.played should contain(new Card(5)) }
      it("should have deck cards with previous cards") { next.deck should be(status.deck) }
    }

    describe("when player0 replies and wins") {
      val status = Status(
        true,
        (4 to 6).map(new Card(_)),
        Vector(1, 3).map(new Card(_)),
        Set(),
        Set(),
        Some(new Card(2)),
        new Card(0),
        (7 to 39).map(new Card(_)))
      val next = status.nextStatus(1)

      it("should have player0 turn") { next.player0Turn should be(true) }
      it("should have player0 with 3 cards") { next.player0Cards should be(Seq(4, 6, 7).map(new Card(_))) }
      it("should have player1 with 3 cards") { next.player1Cards should be(Seq(1, 3, 8).map(new Card(_))) }
      it("should have player0 with played won cards") { next.won0Cards should be(status.won0Cards ++ Set(new Card(2), new Card(5))) }
      it("should have player1 with previous won cards") { next.won1Cards should be(status.won1Cards) }
      it("should have no played card") { next.played should be(None) }
      it("should have deck with previous cards without the first 2 ones") { next.deck should be(status.deck.drop(2)) }
    }

    describe("when player0 replies and looses") {
      val status = Status(
        true,
        (1 to 3).map(new Card(_)),
        Vector(4, 6).map(new Card(_)),
        Set(),
        Set(),
        Some(new Card(5)),
        new Card(5),
        (7 to 39).map(new Card(_)))
      val next = status.nextStatus(1)

      it("should have player1 turn") { next.player0Turn should be(false) }
      it("should have player0 with 3 cards") { next.player0Cards should be(Seq(1, 3, 8).map(new Card(_))) }
      it("should have player1 with 3 cards") { next.player1Cards should be(Seq(4, 6, 7).map(new Card(_))) }
      it("should have player0 with previous won cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with played won cards") { next.won1Cards should be(status.won1Cards ++ Set(2, 5).map(new Card(_))) }
      it("should have no played card") { next.played should be(None) }
      it("should have deck with previous cards without the first 2 ones") { next.deck should be(status.deck.drop(2)) }
    }

    describe("when player1 replies and wins") {
      val status = Status(
        false,
        Vector(1, 3).map(new Card(_)),
        (4 to 6).map(new Card(_)),
        Set(),
        Set(),
        Some(new Card(2)),
        new Card(0),
        (7 to 39).map(new Card(_)))
      val next = status.nextStatus(1)

      it("should have player1 turn") { next.player0Turn should be(false) }
      it("should have player0 with 3 cards") { next.player0Cards should be(Seq(1, 3, 8).map(new Card(_))) }
      it("should have player1 with 3 cards") { next.player1Cards should be(Seq(4, 6, 7).map(new Card(_))) }
      it("should have player0 with previous won cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with played won cards") { next.won1Cards should be(status.won1Cards ++ Set(new Card(2), new Card(5))) }
      it("should have no played card") { next.played should be(None) }
      it("should have deck with previous cards without the first 2 ones") { next.deck should be(status.deck.drop(2)) }
    }

    describe("when player1 replies and looses") {
      val status = Status(
        false,
        Vector(4, 6).map(new Card(_)),
        (1 to 3).map(new Card(_)),
        Set(),
        Set(),
        Some(new Card(5)),
        new Card(0),
        (7 to 39).map(new Card(_)))
      val next = status.nextStatus(1)

      it("should have player0 turn") { next.player0Turn should be(true) }
      it("should have player0 with 3 cards") { next.player0Cards should be(Seq(4, 6, 7).map(new Card(_))) }
      it("should have player1 with 3 cards") { next.player1Cards should be(Seq(1, 3, 8).map(new Card(_))) }
      it("should have player0 with played won cards") { next.won0Cards should be(status.won0Cards ++ Set(new Card(2), new Card(5))) }
      it("should have player1 with previous won cards") { next.won1Cards should be(status.won1Cards) }
      it("should have no played card") { next.played should be(None) }
      it("should have deck with previous cards without the first 2 ones") { next.deck should be(status.deck.drop(2)) }
    }

    describe("when last in-game and player0 plays 2nd choice") {
      val status = Status(
        true,
        (2 to 4).map(new Card(_)),
        (5 to 7).map(new Card(_)),
        (8 to 23).map(new Card(_)).toSet,
        (24 to 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector(new Card(1)))
      val next = status.nextStatus(1)

      it("should have player1 turn") { next.player0Turn should be(false) }
      it("should have player0 with 2 cards") { next.player0Cards should be(Seq(2, 4).map(new Card(_))) }
      it("should have player1 with previous cards") { next.player1Cards should be(status.player1Cards) }
      it("should have player0 with previuos owned cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards") { next.won1Cards should be(status.won1Cards) }
      it("should have played card") { next.played should be(Some(new Card(3))) }
      it("should have deck with previous cards") { next.deck should be(status.deck) }
    }

    describe("when last in-game and player1 plays 2nd choice") {
      val status = Status(
        false,
        (2 to 4).map(new Card(_)),
        (5 to 7).map(new Card(_)),
        (8 to 23).map(new Card(_)).toSet,
        (24 to 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector(new Card(1)))
      val next = status.nextStatus(1)

      it("should have player0 turn") { next.player0Turn should be(true) }
      it("should have player0 with previous cards") { next.player0Cards should be(status.player0Cards) }
      it("should have player1 with 2 cards") { next.player1Cards should be(Seq(5, 7).map(new Card(_))) }
      it("should have player0 with previuos owned cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards") { next.won1Cards should be(status.won1Cards) }
      it("should have played card") { next.played should be(Some(new Card(6))) }
      it("should have deck with previous cards") { next.deck should be(status.deck) }
    }

    describe("when last in-game and player0 replies and wins") {
      val status = Status(
        true,
        (5 to 7).map(new Card(_)),
        Vector(2, 4).map(new Card(_)),
        (8 to 23).map(new Card(_)).toSet,
        (24 to 39).map(new Card(_)).toSet,
        Some(new Card(3)),
        new Card(0),
        Vector(new Card(1)))
      val next = status.nextStatus(1)

      it("should have player0 turn") { next.player0Turn should be(true) }
      it("should have player0 with 3 cards") { next.player0Cards should be(Seq(5, 7, 1).map(new Card(_))) }
      it("should have player1 with 3 cards") { next.player1Cards should be(Seq(2, 4, 0).map(new Card(_))) }
      it("should have player0 with previuos owned cards plus played cards") { next.won0Cards should be(status.won0Cards ++ Set(3, 6).map(new Card(_))) }
      it("should have player1 with previous owned cards") { next.won1Cards should be(status.won1Cards) }
      it("should have no played card") { next.played should be(None) }
      it("should have empty deck") { next.deck should be(Vector()) }
    }

    describe("when last in-game and player0 replies a loss") {
      val status = Status(
        true,
        (2 to 4).map(new Card(_)),
        Vector(5, 7).map(new Card(_)),
        (8 to 23).map(new Card(_)).toSet,
        (24 to 39).map(new Card(_)).toSet,
        Some(new Card(6)),
        new Card(0),
        Vector(new Card(1)))
      val next = status.nextStatus(1)

      it("should have player1 turn") { next.player0Turn should be(false) }
      it("should have player0 with 3 cards") { next.player0Cards should be(Seq(2, 4, 0).map(new Card(_))) }
      it("should have player1 with 3 cards") { next.player1Cards should be(Seq(5, 7, 1).map(new Card(_))) }
      it("should have player0 with previuos owned cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards plus played cards") { next.won1Cards should be(status.won1Cards ++ Set(3, 6).map(new Card(_))) }
      it("should have no played card") { next.played should be(None) }
      it("should have empty deck") { next.deck should be(Vector()) }
    }

    describe("when last in-game and player1 replies and wins") {
      val status = Status(
        false,
        Vector(2, 4).map(new Card(_)),
        (5 to 7).map(new Card(_)),
        (8 to 23).map(new Card(_)).toSet,
        (24 to 39).map(new Card(_)).toSet,
        Some(new Card(3)),
        new Card(0),
        Vector(new Card(1)))
      val next = status.nextStatus(1)

      it("should have player1 turn") { next.player0Turn should be(false) }
      it("should have player0 with 3 cards") { next.player0Cards should be(Seq(2, 4, 0).map(new Card(_))) }
      it("should have player1 with 3 cards") { next.player1Cards should be(Seq(5, 7, 1).map(new Card(_))) }
      it("should have player0 with previuos owned cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards plus played cards") { next.won1Cards should be(status.won1Cards ++ Set(3, 6).map(new Card(_))) }
      it("should have no played card") { next.played should be(None) }
      it("should have empty deck") { next.deck should be(Vector()) }
    }

    describe("when last in-game and player1 replies a loss") {
      val status = Status(
        false,
        Vector(5, 7).map(new Card(_)),
        (2 to 4).map(new Card(_)),
        (8 to 23).map(new Card(_)).toSet,
        (24 to 39).map(new Card(_)).toSet,
        Some(new Card(6)),
        new Card(0),
        Vector(new Card(1)))
      val next = status.nextStatus(1)

      it("should have player0 turn") { next.player0Turn should be(true) }
      it("should have player0 with 3 cards") { next.player0Cards should be(Seq(5, 7, 1).map(new Card(_))) }
      it("should have player1 with 3 cards") { next.player1Cards should be(Seq(2, 4, 0).map(new Card(_))) }
      it("should have player0 with previuos owned cards plus played cards") { next.won0Cards should be(status.won0Cards ++ Set(3, 6).map(new Card(_))) }
      it("should have player1 with previous owned cards") { next.won1Cards should be(status.won1Cards) }
      it("should have no played card") { next.played should be(None) }
      it("should have empty deck") { next.deck should be(Vector()) }
    }

    describe("when 3 cards final and player0 plays 2nd choice") {
      val status = Status(
        true,
        Vector(0, 1, 2).map(new Card(_)),
        Vector(3, 4, 5).map(new Card(_)),
        (6 to 22).map(new Card(_)).toSet,
        (23 to 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector())
      val next = status.nextStatus(1)

      it("should have player1 turn") { next.player0Turn should be(false) }
      it("should have player0 with 2 cards") { next.player0Cards should be(Seq(0, 2).map(new Card(_))) }
      it("should have player1 with previous cards") { next.player1Cards should be(status.player1Cards) }
      it("should have player0 with previuos owned cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards") { next.won1Cards should be(status.won1Cards) }
      it("should have played card") { next.played should be(Some(new Card(1))) }
      it("should have empty deck") { next.deck should be(Vector()) }
    }

    describe("when 3 cards final and player1 plays 2nd choice") {
      val status = Status(
        false,
        Vector(0, 1, 2).map(new Card(_)),
        Vector(3, 4, 5).map(new Card(_)),
        (6 to 22).map(new Card(_)).toSet,
        (23 to 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector())
      val next = status.nextStatus(1)

      it("should have player0 turn") { next.player0Turn should be(true) }
      it("should have player1 with previous cards") { next.player0Cards should be(status.player0Cards) }
      it("should have player0 with 2 cards") { next.player1Cards should be(Seq(3, 5).map(new Card(_))) }
      it("should have player0 with previuos owned cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards") { next.won1Cards should be(status.won1Cards) }
      it("should have played card") { next.played should be(Some(new Card(4))) }
      it("should have empty deck") { next.deck should be(Vector()) }
    }

    describe("when 3 cards final and player0 replies and wins") {
      val status = Status(
        true,
        Vector(3, 4, 5).map(new Card(_)),
        Vector(0, 2).map(new Card(_)),
        (6 to 22).map(new Card(_)).toSet,
        (23 to 39).map(new Card(_)).toSet,
        Some(new Card(1)),
        new Card(0),
        Vector())
      val next = status.nextStatus(1)

      it("should have player0 turn") { next.player0Turn should be(true) }
      it("should have player1 with 2 cards") { next.player0Cards should be(Seq(3, 5).map(new Card(_))) }
      it("should have player0 with 2 cards") { next.player1Cards should be(Seq(0, 2).map(new Card(_))) }
      it("should have player0 with previuos owned cards plus played cards") { next.won0Cards should be(status.won0Cards ++ Set(1, 4).map(new Card(_))) }
      it("should have player1 with previous owned cards") { next.won1Cards should be(status.won1Cards) }
      it("should have no played") { next.played should be(None) }
      it("should have empty deck") { next.deck should be(Vector()) }
    }

    describe("when 3 cards final and player0 replies a loss") {
      val status = Status(
        true,
        Vector(0, 1, 2).map(new Card(_)),
        Vector(3, 5).map(new Card(_)),
        (6 to 22).map(new Card(_)).toSet,
        (23 to 39).map(new Card(_)).toSet,
        Some(new Card(4)),
        new Card(0),
        Vector())
      val next = status.nextStatus(1)

      it("should have player1 turn") { next.player0Turn should be(false) }
      it("should have player1 with 2 cards") { next.player0Cards should be(Seq(0, 2).map(new Card(_))) }
      it("should have player0 with 2 cards") { next.player1Cards should be(Seq(3, 5).map(new Card(_))) }
      it("should have player0 with previuos owned cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards plus played cards") { next.won1Cards should be(status.won1Cards ++ Set(1, 4).map(new Card(_))) }
      it("should have no played") { next.played should be(None) }
      it("should have empty deck") { next.deck should be(Vector()) }
    }

    describe("when 3 cards final and player1 replies and wins") {
      val status = Status(
        false,
        Vector(0, 2).map(new Card(_)),
        Vector(3, 4, 5).map(new Card(_)),
        (6 to 22).map(new Card(_)).toSet,
        (23 to 39).map(new Card(_)).toSet,
        Some(new Card(1)),
        new Card(0),
        Vector())
      val next = status.nextStatus(1)

      it("should have player1 turn") { next.player0Turn should be(false) }
      it("should have player1 with 2 cards") { next.player0Cards should be(Seq(0, 2).map(new Card(_))) }
      it("should have player0 with 2 cards") { next.player1Cards should be(Seq(3, 5).map(new Card(_))) }
      it("should have player0 with previuos owned cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards plus played cards") { next.won1Cards should be(status.won1Cards ++ Set(1, 4).map(new Card(_))) }
      it("should have no played") { next.played should be(None) }
      it("should have empty deck") { next.deck should be(Vector()) }
    }

    describe("when 3 cards final and player1 replies a loss") {
      val status = Status(
        false,
        Vector(3, 5).map(new Card(_)),
        Vector(0, 1, 2).map(new Card(_)),
        (6 to 22).map(new Card(_)).toSet,
        (23 to 39).map(new Card(_)).toSet,
        Some(new Card(4)),
        new Card(0),
        Vector())
      val next = status.nextStatus(1)

      it("should have player1 turn") { next.player0Turn should be(true) }
      it("should have player0 with 2 cards") { next.player0Cards should be(Seq(3, 5).map(new Card(_))) }
      it("should have player1 with 2 cards") { next.player1Cards should be(Seq(0, 2).map(new Card(_))) }
      it("should have player0 with previuos owned cards plus played cards") { next.won0Cards should be(status.won0Cards ++ Set(1, 4).map(new Card(_))) }
      it("should have player1 with previous owned cards") { next.won1Cards should be(status.won1Cards) }
      it("should have no played") { next.played should be(None) }
      it("should have empty deck") { next.deck should be(Vector()) }
    }

    describe("when final turn and player0 plays the choice") {
      val status = Status(
        true,
        Vector(0).map(new Card(_)),
        Vector(1).map(new Card(_)),
        (2 to 20).map(new Card(_)).toSet,
        (21 to 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector())
      val next = status.nextStatus(0)

      it("should have player1 turn") { status.nextStatus(0).player0Turn should be(false) }
      it("should have player0 with no cards") { status.nextStatus(0).player0Cards should be(Vector()) }
      it("should have player1 with previous card") { status.nextStatus(0).player1Cards should be(status.player1Cards) }
      it("should have player0 with previuos owned cards") { status.nextStatus(0).won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards") { status.nextStatus(0).won1Cards should be(status.won1Cards) }
      it("should have played card") { status.nextStatus(0).played should be(Some(new Card(0))) }
      it("should have empty deck") { status.nextStatus(0).deck should be(Vector()) }
    }

    describe("when final turn and player1 plays the choice") {
      val status = Status(
        false,
        Vector(0).map(new Card(_)),
        Vector(1).map(new Card(_)),
        (2 to 20).map(new Card(_)).toSet,
        (21 to 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector())
      val next = status.nextStatus(0)

      it("should have player0 turn") { next.player0Turn should be(true) }
      it("should have player0 with previous card") { next.player0Cards should be(status.player0Cards) }
      it("should have player1 with no cards") { next.player1Cards should be(Vector()) }
      it("should have player0 with previuos owned cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards") { next.won1Cards should be(status.won1Cards) }
      it("should have played card") { next.played should be(Some(new Card(1))) }
      it("should have empty deck") { next.deck should be(Vector()) }
    }

    describe("when final turn, player0 replies and wins") {
      val status = Status(
        true,
        Vector(new Card(1)),
        Vector(),
        (2 to 20).map(new Card(_)).toSet,
        (21 to 39).map(new Card(_)).toSet,
        Some(new Card(0)),
        new Card(0),
        Vector())
      val next = status.nextStatus(0)

      it("should have player0 turn") { next.player0Turn should be(true) }
      it("should have player0 with no card") { next.player0Cards should be(Vector()) }
      it("should have player1 with no cards") { next.player1Cards should be(Vector()) }
      it("should have player0 with previuos owned cards plus played cards") { next.won0Cards should be(status.won0Cards ++ Set(0, 1).map(new Card(_))) }
      it("should have player1 with previous owned cards") { next.won1Cards should be(status.won1Cards) }
      it("should have no played") { next.played should be(None) }
      it("should have empty deck") { next.deck should be(Vector()) }
      it("should have completed") { next.isCompleted should be(true) }
      it("should have been completed") { next.isCompleted should be(true) }
      it("should have 120 total score ") { next.player0Score + next.player1Score should be(120) }
      it("should have been determined") { next.isDetermined should be(true) }
    }

    describe("when final turn, player0 replies and looses") {
      val status = Status(
        true,
        Vector(new Card(0)),
        Vector(),
        (2 to 20).map(new Card(_)).toSet,
        (21 to 39).map(new Card(_)).toSet,
        Some(new Card(1)),
        new Card(1),
        Vector())
      val next = status.nextStatus(0)

      it("should have player1 turn") { next.player0Turn should be(false) }
      it("should have player0 with no card") { next.player0Cards should be(Vector()) }
      it("should have player1 with no cards") { next.player1Cards should be(Vector()) }
      it("should have player0 with previous owned cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with previuos owned cards plus played cards") { next.won1Cards should be(status.won1Cards ++ Set(0, 1).map(new Card(_))) }
      it("should have no played") { next.played should be(None) }
      it("should have empty deck") { next.deck should be(Vector()) }
      it("should have been completed") { next.isCompleted should be(true) }
      it("should have 120 total score ") { next.player0Score + next.player1Score should be(120) }
      it("should have been determined") { next.isDetermined should be(true) }
    }

    describe("when final turn, player1 replies and wins") {
      val status = Status(
        false,
        Vector(),
        Vector(new Card(1)),
        (2 to 20).map(new Card(_)).toSet,
        (21 to 39).map(new Card(_)).toSet,
        Some(new Card(0)),
        new Card(1),
        Vector())
      val next = status.nextStatus(0)

      it("should have player1 turn") { next.player0Turn should be(false) }
      it("should have player0 with no card") { next.player0Cards should be(Vector()) }
      it("should have player1 with no cards") { next.player1Cards should be(Vector()) }
      it("should have player0 with previous owned cards") { next.won0Cards should be(status.won0Cards) }
      it("should have player1 with previuos owned cards plus played cards") { next.won1Cards should be(status.won1Cards ++ Set(0, 1).map(new Card(_))) }
      it("should have no played") { next.played should be(None) }
      it("should have empty deck") { next.deck should be(Vector()) }
      it("should have been completed") { next.isCompleted should be(true) }
      it("should have 120 total score ") { next.player0Score + next.player1Score should be(120) }
      it("should have been determined") { next.isDetermined should be(true) }
    }

    describe("when final turn, player1 replies and looses") {
      val status = Status(
        false,
        Vector(),
        Vector(new Card(0)),
        (2 to 20).map(new Card(_)).toSet,
        (21 to 39).map(new Card(_)).toSet,
        Some(new Card(1)),
        new Card(0),
        Vector())
      val next = status.nextStatus(0)

      it("should have player0 turn") { next.player0Turn should be(true) }
      it("should have player0 with no card") { next.player0Cards should be(Vector()) }
      it("should have player1 with no cards") { next.player1Cards should be(Vector()) }
      it("should have player0 with previuos owned cards plus played cards") { next.won0Cards should be(status.won0Cards ++ Set(0, 1).map(new Card(_))) }
      it("should have player1 with previous owned cards") { next.won1Cards should be(status.won1Cards) }
      it("should have no played") { next.played should be(None) }
      it("should have empty deck") { next.deck should be(Vector()) }
      it("should have been completed") { next.isCompleted should be(true) }
      it("should have 120 total score ") { next.player0Score + next.player1Score should be(120) }
      it("should have been determined") { next.isDetermined should be(true) }
    }
  }
}