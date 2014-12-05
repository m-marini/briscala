package org.mmarini.briscala

import org.scalatest._
import sun.security.jgss.krb5.InitSecContextToken

class StatusTest extends FunSpec with Matchers {

  describe("A Status") {
    describe("with an ace and a three won") {
      it("should have player score 11 + 10") {
        val status = Deck.createStatus(
          true,
          IndexedSeq(),
          IndexedSeq(),
          Set(Card(Figure.Ace, Seed.Seed1), Card(Figure.Three, Seed.Seed1)),
          Set(),
          None,
          Card(Figure.Three, Seed.Trump),
          IndexedSeq())
        status.player0Score should be(21)
      }
    }

    describe("with an ace and a king lost") {
      it("should have opposite score 11 + 4") {
        val status = Deck.createStatus(
          true,
          IndexedSeq(),
          IndexedSeq(),
          Set(),
          Set(Card(Figure.Ace, Seed.Seed1), Card(Figure.King, Seed.Seed1)),
          None,
          Card(Figure.Three, Seed.Trump),
          IndexedSeq())
        status.player1Score should be(15)
      }
    }
  }

  describe("The next status") {
    describe("when player0 playing 2nd choice") {
      val status = Status(
        true,
        (1 to 3).map(new Card(_)),
        (4 to 6).map(new Card(_)),
        Set(),
        Set(),
        None,
        new Card(0),
        (7 to 39).map(new Card(_)))

      it("should have player1 turn") { status.nextStatus(1).player0Turn should be(false) }
      it("should have player0 remaining with 2 cards") { status.nextStatus(1).player0Cards should be(Seq(1, 3).map(new Card(_))) }
      it("should have player1 with previous cards") { status.nextStatus(1).player1Cards should be(status.player1Cards) }
      it("should have player0 with previous won cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with previous won cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have the played card in the table") { status.nextStatus(1).played should contain(new Card(2)) }
      it("should have deck cards with previous cards") { status.nextStatus(1).deck should be(status.deck) }
    }

    describe("when player1 playing 2nd choice") {
      val status = Status(
        false,
        (1 to 3).map(new Card(_)),
        (4 to 6).map(new Card(_)),
        Set(),
        Set(),
        None,
        new Card(0),
        (7 to 39).map(new Card(_)))

      it("should have player0 turn") { status.nextStatus(1).player0Turn should be(true) }
      it("should have player0 with previous cards") { status.nextStatus(1).player0Cards should be(status.player0Cards) }
      it("should have player1 reimaning with 2 cards") { status.nextStatus(1).player1Cards should be(Seq(4, 6).map(new Card(_))) }
      it("should have player0 with previous won cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with previous won cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have the played card in the table") { status.nextStatus(1).played should contain(new Card(5)) }
      it("should have deck cards with previous cards") { status.nextStatus(1).deck should be(status.deck) }
    }

    describe("when player0 replying a win") {
      val status = Status(
        true,
        (4 to 6).map(new Card(_)),
        Vector(1, 3).map(new Card(_)),
        Set(),
        Set(),
        Some(new Card(2)),
        new Card(0),
        (7 to 39).map(new Card(_)))

      it("should have player0 turn") { status.nextStatus(1).player0Turn should be(true) }
      it("should have player0 with 3 cards") { status.nextStatus(1).player0Cards should be(Seq(4, 6, 7).map(new Card(_))) }
      it("should have player1 with 3 cards") { status.nextStatus(1).player1Cards should be(Seq(1, 3, 8).map(new Card(_))) }
      it("should have player0 with played won cards") { status.nextStatus(1).won0Cards should be(status.won0Cards ++ Set(new Card(2), new Card(5))) }
      it("should have player1 with previous won cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have no played card") { status.nextStatus(1).played should be(None) }
      it("should have deck with previous cards without the first 2 ones") { status.nextStatus(1).deck should be(status.deck.drop(2)) }
    }

    describe("when player0 replying a lost") {
      val status = Status(
        true,
        (1 to 3).map(new Card(_)),
        Vector(4, 6).map(new Card(_)),
        Set(),
        Set(),
        Some(new Card(2)),
        new Card(5),
        (7 to 39).map(new Card(_)))

      it("should have player1 turn") { status.nextStatus(1).player0Turn should be(false) }
      it("should have player0 with 3 cards") { status.nextStatus(1).player0Cards should be(Seq(1, 3, 8).map(new Card(_))) }
      it("should have player1 with 3 cards") { status.nextStatus(1).player1Cards should be(Seq(4, 6, 7).map(new Card(_))) }
      it("should have player0 with previous won cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with played won cards") { status.nextStatus(1).won1Cards should be(status.won1Cards ++ Set(new Card(2), new Card(5))) }
      it("should have no played card") { status.nextStatus(1).played should be(None) }
      it("should have deck with previous cards without the first 2 ones") { status.nextStatus(1).deck should be(status.deck.drop(2)) }
    }

    describe("when player1 replying a win") {
      val status = Status(
        false,
        Vector(1, 3).map(new Card(_)),
        (4 to 6).map(new Card(_)),
        Set(),
        Set(),
        Some(new Card(2)),
        new Card(0),
        (7 to 39).map(new Card(_)))

      it("should have player1 turn") { status.nextStatus(1).player0Turn should be(false) }
      it("should have player0 with 3 cards") { status.nextStatus(1).player0Cards should be(Seq(1, 3, 8).map(new Card(_))) }
      it("should have player1 with 3 cards") { status.nextStatus(1).player1Cards should be(Seq(4, 6, 7).map(new Card(_))) }
      it("should have player0 with previous won cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with played won cards") { status.nextStatus(1).won1Cards should be(status.won1Cards ++ Set(new Card(2), new Card(5))) }
      it("should have no played card") { status.nextStatus(1).played should be(None) }
      it("should have deck with previous cards without the first 2 ones") { status.nextStatus(1).deck should be(status.deck.drop(2)) }
    }

    describe("when player1 replying a lost") {
      val status = Status(
        false,
        Vector(4, 6).map(new Card(_)),
        (1 to 3).map(new Card(_)),
        Set(),
        Set(),
        Some(new Card(2)),
        new Card(5),
        (7 to 39).map(new Card(_)))

      it("should have player0 turn") { status.nextStatus(1).player0Turn should be(true) }
      it("should have player0 with 3 cards") { status.nextStatus(1).player0Cards should be(Seq(4, 6, 7).map(new Card(_))) }
      it("should have player1 with 3 cards") { status.nextStatus(1).player1Cards should be(Seq(1, 3, 8).map(new Card(_))) }
      it("should have player0 with played won cards") { status.nextStatus(1).won0Cards should be(status.won0Cards ++ Set(new Card(2), new Card(5))) }
      it("should have player1 with previous won cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have no played card") { status.nextStatus(1).played should be(None) }
      it("should have deck with previous cards without the first 2 ones") { status.nextStatus(1).deck should be(status.deck.drop(2)) }
    }

    describe("when last in-game and player0 playing 2nd choice") {
      val status = Status(
        true,
        (2 to 4).map(new Card(_)),
        (5 to 7).map(new Card(_)),
        (8 to 23).map(new Card(_)).toSet,
        (24 to 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector(new Card(1)))

      it("should have player1 turn") { status.nextStatus(1).player0Turn should be(false) }
      it("should have player0 with 2 cards") { status.nextStatus(1).player0Cards should be(Seq(2, 4).map(new Card(_))) }
      it("should have player1 with previous cards") { status.nextStatus(1).player1Cards should be(status.player1Cards) }
      it("should have player0 with previuos owned cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have played card") { status.nextStatus(1).played should be(Some(new Card(3))) }
      it("should have deck with previous cards") { status.nextStatus(1).deck should be(status.deck) }
    }

    describe("when last in-game and player1 playing 2nd choice") {
      val status = Status(
        false,
        (2 to 4).map(new Card(_)),
        (5 to 7).map(new Card(_)),
        (8 to 23).map(new Card(_)).toSet,
        (24 to 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector(new Card(1)))

      it("should have player0 turn") { status.nextStatus(1).player0Turn should be(true) }
      it("should have player0 with previous cards") { status.nextStatus(1).player0Cards should be(status.player0Cards) }
      it("should have player1 with 2 cards") { status.nextStatus(1).player1Cards should be(Seq(5, 7).map(new Card(_))) }
      it("should have player0 with previuos owned cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have played card") { status.nextStatus(1).played should be(Some(new Card(6))) }
      it("should have deck with previous cards") { status.nextStatus(1).deck should be(status.deck) }
    }

    describe("when last in-game and player0 replying a win") {
      val status = Status(
        true,
        (5 to 7).map(new Card(_)),
        Vector(2, 4).map(new Card(_)),
        (8 to 23).map(new Card(_)).toSet,
        (24 to 39).map(new Card(_)).toSet,
        Some(new Card(3)),
        new Card(0),
        Vector(new Card(1)))

      it("should have player0 turn") { status.nextStatus(1).player0Turn should be(true) }
      it("should have player0 with 3 cards") { status.nextStatus(1).player0Cards should be(Seq(5, 7, 1).map(new Card(_))) }
      it("should have player1 with 3 cards") { status.nextStatus(1).player1Cards should be(Seq(2, 4, 0).map(new Card(_))) }
      it("should have player0 with previuos owned cards plus played cards") { status.nextStatus(1).won0Cards should be(status.won0Cards ++ Set(3, 6).map(new Card(_))) }
      it("should have player1 with previous owned cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have no played card") { status.nextStatus(1).played should be(None) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when last in-game and player0 replying a loss") {
      val status = Status(
        true,
        (2 to 4).map(new Card(_)),
        Vector(5, 7).map(new Card(_)),
        (8 to 23).map(new Card(_)).toSet,
        (24 to 39).map(new Card(_)).toSet,
        Some(new Card(6)),
        new Card(0),
        Vector(new Card(1)))

      it("should have player1 turn") { status.nextStatus(1).player0Turn should be(false) }
      it("should have player0 with 3 cards") { status.nextStatus(1).player0Cards should be(Seq(2, 4, 0).map(new Card(_))) }
      it("should have player1 with 3 cards") { status.nextStatus(1).player1Cards should be(Seq(5, 7, 1).map(new Card(_))) }
      it("should have player0 with previuos owned cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards plus played cards") { status.nextStatus(1).won1Cards should be(status.won1Cards ++ Set(3, 6).map(new Card(_))) }
      it("should have no played card") { status.nextStatus(1).played should be(None) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when last in-game and player1 replying a win") {
      val status = Status(
        false,
        Vector(2, 4).map(new Card(_)),
        (5 to 7).map(new Card(_)),
        (8 to 23).map(new Card(_)).toSet,
        (24 to 39).map(new Card(_)).toSet,
        Some(new Card(3)),
        new Card(0),
        Vector(new Card(1)))

      it("should have player1 turn") { status.nextStatus(1).player0Turn should be(false) }
      it("should have player0 with 3 cards") { status.nextStatus(1).player0Cards should be(Seq(2, 4, 0).map(new Card(_))) }
      it("should have player1 with 3 cards") { status.nextStatus(1).player1Cards should be(Seq(5, 7, 1).map(new Card(_))) }
      it("should have player0 with previuos owned cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards plus played cards") { status.nextStatus(1).won1Cards should be(status.won1Cards ++ Set(3, 6).map(new Card(_))) }
      it("should have no played card") { status.nextStatus(1).played should be(None) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when last in-game and player1 replying a loss") {
      val status = Status(
        false,
        Vector(5, 7).map(new Card(_)),
        (2 to 4).map(new Card(_)),
        (8 to 23).map(new Card(_)).toSet,
        (24 to 39).map(new Card(_)).toSet,
        Some(new Card(6)),
        new Card(0),
        Vector(new Card(1)))

      it("should have player0 turn") { status.nextStatus(1).player0Turn should be(true) }
      it("should have player0 with 3 cards") { status.nextStatus(1).player0Cards should be(Seq(5, 7, 1).map(new Card(_))) }
      it("should have player1 with 3 cards") { status.nextStatus(1).player1Cards should be(Seq(2, 4, 0).map(new Card(_))) }
      it("should have player0 with previuos owned cards plus played cards") { status.nextStatus(1).won0Cards should be(status.won0Cards ++ Set(3, 6).map(new Card(_))) }
      it("should have player1 with previous owned cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have no played card") { status.nextStatus(1).played should be(None) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when 3 cards final and player0 playing 2nd choice") {
      val status = Status(
        true,
        Vector(0, 1, 2).map(new Card(_)),
        Vector(3, 4, 5).map(new Card(_)),
        (6 to 22).map(new Card(_)).toSet,
        (23 to 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector())

      it("should have player1 turn") { status.nextStatus(1).player0Turn should be(false) }
      it("should have player0 with 2 cards") { status.nextStatus(1).player0Cards should be(Seq(0, 2).map(new Card(_))) }
      it("should have player1 with previous cards") { status.nextStatus(1).player1Cards should be(status.player1Cards) }
      it("should have player0 with previuos owned cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have played card") { status.nextStatus(1).played should be(Some(new Card(1))) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when 3 cards final and player1 playing 2nd choice") {
      val status = Status(
        false,
        Vector(0, 1, 2).map(new Card(_)),
        Vector(3, 4, 5).map(new Card(_)),
        (6 to 22).map(new Card(_)).toSet,
        (23 to 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector())

      it("should have player0 turn") { status.nextStatus(1).player0Turn should be(true) }
      it("should have player1 with previous cards") { status.nextStatus(1).player0Cards should be(status.player0Cards) }
      it("should have player0 with 2 cards") { status.nextStatus(1).player1Cards should be(Seq(3, 5).map(new Card(_))) }
      it("should have player0 with previuos owned cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have played card") { status.nextStatus(1).played should be(Some(new Card(1))) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when 3 cards final and player0 replying a win") {
      val status = Status(
        true,
        Vector(3, 4, 5).map(new Card(_)),
        Vector(0, 2).map(new Card(_)),
        (6 to 22).map(new Card(_)).toSet,
        (23 to 39).map(new Card(_)).toSet,
        Some(new Card(1)),
        new Card(0),
        Vector())

      it("should have player0 turn") { status.nextStatus(1).player0Turn should be(true) }
      it("should have player1 with 2 cards") { status.nextStatus(1).player0Cards should be(Seq(3, 5).map(new Card(_))) }
      it("should have player0 with 2 cards") { status.nextStatus(1).player1Cards should be(Seq(0, 2).map(new Card(_))) }
      it("should have player0 with previuos owned cards plus played cards") { status.nextStatus(1).won0Cards should be(status.won0Cards ++ Set(1, 6).map(new Card(_))) }
      it("should have player1 with previous owned cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have no played") { status.nextStatus(1).played should be(None) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when 3 cards final and player0 replying a loss") {
      val status = Status(
        true,
        Vector(0, 1, 2).map(new Card(_)),
        Vector(3, 5).map(new Card(_)),
        (6 to 22).map(new Card(_)).toSet,
        (23 to 39).map(new Card(_)).toSet,
        Some(new Card(4)),
        new Card(0),
        Vector())

      it("should have player1 turn") { status.nextStatus(1).player0Turn should be(false) }
      it("should have player1 with 2 cards") { status.nextStatus(1).player0Cards should be(Seq(0, 2).map(new Card(_))) }
      it("should have player0 with 2 cards") { status.nextStatus(1).player1Cards should be(Seq(3, 5).map(new Card(_))) }
      it("should have player0 with previuos owned cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards plus played cards") { status.nextStatus(1).won1Cards should be(status.won1Cards ++ Set(1, 6).map(new Card(_))) }
      it("should have no played") { status.nextStatus(1).played should be(None) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when 3 cards final and player1 replying a win") {
      val status = Status(
        false,
        Vector(0, 2).map(new Card(_)),
        Vector(3, 4, 5).map(new Card(_)),
        (6 to 22).map(new Card(_)).toSet,
        (23 to 39).map(new Card(_)).toSet,
        Some(new Card(1)),
        new Card(0),
        Vector())

      it("should have player1 turn") { status.nextStatus(1).player0Turn should be(false) }
      it("should have player1 with 2 cards") { status.nextStatus(1).player0Cards should be(Seq(0, 2).map(new Card(_))) }
      it("should have player0 with 2 cards") { status.nextStatus(1).player1Cards should be(Seq(3, 5).map(new Card(_))) }
      it("should have player0 with previuos owned cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards plus played cards") { status.nextStatus(1).won1Cards should be(status.won1Cards ++ Set(1, 6).map(new Card(_))) }
      it("should have no played") { status.nextStatus(1).played should be(None) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when 3 cards final and player1 replying a loss") {
      val status = Status(
        false,
        Vector(3, 5).map(new Card(_)),
        Vector(0, 1, 2).map(new Card(_)),
        (6 to 22).map(new Card(_)).toSet,
        (23 to 39).map(new Card(_)).toSet,
        Some(new Card(4)),
        new Card(0),
        Vector())

      it("should have player1 turn") { status.nextStatus(1).player0Turn should be(false) }
      it("should have player1 with 2 cards") { status.nextStatus(1).player0Cards should be(Seq(3, 5).map(new Card(_))) }
      it("should have player0 with 2 cards") { status.nextStatus(1).player1Cards should be(Seq(0, 2).map(new Card(_))) }
      it("should have player0 with previuos owned cards plus played cards") { status.nextStatus(1).won0Cards should be(status.won0Cards ++ Set(1, 6).map(new Card(_))) }
      it("should have player1 with previous owned cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have no played") { status.nextStatus(1).played should be(None) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when final turn and player0 playing the choice") {
      val status = Status(
        true,
        Vector(0).map(new Card(_)),
        Vector(1).map(new Card(_)),
        (2 to 20).map(new Card(_)).toSet,
        (21 to 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector())

      it("should have player1 turn") { status.nextStatus(1).player0Turn should be(false) }
      it("should have player0 with no cards") { status.nextStatus(1).player0Cards should be(Vector()) }
      it("should have player1 with previous card") { status.nextStatus(1).player1Cards should be(status.player1Cards) }
      it("should have player0 with previuos owned cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have played card") { status.nextStatus(1).played should be(Some(new Card(0))) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when final turn and player1 playing the choice") {
      val status = Status(
        false,
        Vector(0).map(new Card(_)),
        Vector(1).map(new Card(_)),
        (2 to 20).map(new Card(_)).toSet,
        (21 to 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector())

      it("should have player0 turn") { status.nextStatus(1).player0Turn should be(false) }
      it("should have player0 with previous card") { status.nextStatus(1).player0Cards should be(status.player0Cards) }
      it("should have player1 with no cards") { status.nextStatus(1).player1Cards should be(Vector()) }
      it("should have player0 with previuos owned cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with previous owned cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have played card") { status.nextStatus(1).played should be(Some(new Card(1))) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when final turn and player0 replying the win choice") {
      val status = Status(
        true,
        Vector(new Card(1)),
        Vector(),
        (2 to 20).map(new Card(_)).toSet,
        (21 to 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector())

      it("should have player0 turn") { status.nextStatus(1).player0Turn should be(true) }
      it("should have player0 with no card") { status.nextStatus(1).player0Cards should be(Vector()) }
      it("should have player1 with no cards") { status.nextStatus(1).player1Cards should be(Vector()) }
      it("should have player0 with previuos owned cards plus played cards") { status.nextStatus(1).won0Cards should be(status.won0Cards ++ Set(0, 1).map(new Card(_))) }
      it("should have player1 with previous owned cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have no played") { status.nextStatus(1).played should be(None) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when final turn and player0 replying the loss choice") {
      val status = Status(
        true,
        Vector(new Card(0)),
        Vector(),
        (2 to 20).map(new Card(_)).toSet,
        (21 to 39).map(new Card(_)).toSet,
        None,
        new Card(1),
        Vector())

      it("should have player1 turn") { status.nextStatus(1).player0Turn should be(false) }
      it("should have player0 with no card") { status.nextStatus(1).player0Cards should be(Vector()) }
      it("should have player1 with no cards") { status.nextStatus(1).player1Cards should be(Vector()) }
      it("should have player0 with previous owned cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with previuos owned cards plus played cards") { status.nextStatus(1).won1Cards should be(status.won1Cards ++ Set(0, 1).map(new Card(_))) }
      it("should have no played") { status.nextStatus(1).played should be(None) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when final turn and player1 replying the win choice") {
      val status = Status(
        false,
        Vector(),
        Vector(new Card(0)),
        (2 to 20).map(new Card(_)).toSet,
        (21 to 39).map(new Card(_)).toSet,
        None,
        new Card(1),
        Vector())

      it("should have player1 turn") { status.nextStatus(1).player0Turn should be(false) }
      it("should have player0 with no card") { status.nextStatus(1).player0Cards should be(Vector()) }
      it("should have player1 with no cards") { status.nextStatus(1).player1Cards should be(Vector()) }
      it("should have player0 with previous owned cards") { status.nextStatus(1).won0Cards should be(status.won0Cards) }
      it("should have player1 with previuos owned cards plus played cards") { status.nextStatus(1).won1Cards should be(status.won1Cards ++ Set(0, 1).map(new Card(_))) }
      it("should have no played") { status.nextStatus(1).played should be(None) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }

    describe("when final turn and player1 replying the loss choice") {
      val status = Status(
        false,
        Vector(),
        Vector(new Card(1)),
        (2 to 20).map(new Card(_)).toSet,
        (21 to 39).map(new Card(_)).toSet,
        None,
        new Card(0),
        Vector())

      it("should have player0 turn") { status.nextStatus(1).player0Turn should be(true) }
      it("should have player0 with no card") { status.nextStatus(1).player0Cards should be(Vector()) }
      it("should have player1 with no cards") { status.nextStatus(1).player1Cards should be(Vector()) }
      it("should have player0 with previuos owned cards plus played cards") { status.nextStatus(1).won0Cards should be(status.won0Cards ++ Set(0, 1).map(new Card(_))) }
      it("should have player1 with previous owned cards") { status.nextStatus(1).won1Cards should be(status.won1Cards) }
      it("should have no played") { status.nextStatus(1).played should be(None) }
      it("should have empty deck") { status.nextStatus(1).deck should be(Vector()) }
    }
  }
}