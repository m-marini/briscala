package org.mmarini.briscala

import org.scalatest._
import sun.security.jgss.krb5.InitSecContextToken
import scala.collection.immutable.Vector
import scala.collection.immutable.Map
import breeze.linalg.DenseVector

class StatusTest extends FunSpec with Matchers {

  describe("A status having an ace and a king lost and an ace and a three won") {
    val status = Status(
      false,
      IndexedSeq(),
      IndexedSeq(),
      Set(Card(Figure.Ace, Seed.Seed2), Card(Figure.Three, Seed.Seed2)),
      Set(Card(Figure.Ace, Seed.Seed1), Card(Figure.King, Seed.Seed1)),
      None,
      new Card(0),
      Vector())

    it("should have player score 11 + 10") { status.playerScore should be(21) }
    it("should have opposite score 11 + 4") { status.oppositeScore should be(15) }
  }

  describe("when player0 plays 2nd choice") {
    val status = Status(
      true,
      (1 to 3).map(new Card(_)),
      (4 to 5).map(new Card(_)),
      Set(),
      Set(),
      None,
      new Card(0),
      (7 to 39).map(new Card(_)))
    val next = status.nextStatus(1)

    it("should have player1 turn") { next.isPlayer0 should be(false) }
    it("should have player with previous opposite cards") { next.playerCards should be(status.oppositeCards) }
    it("should have opposite remaining with 2 cards") { next.oppositeCards should be(Seq(1, 3).map(new Card(_))) }
    it("should have won cards equals to previous lost cards") { next.wonCards should be(status.lostCards) }
    it("should have lost cards equals to previous won cards") { next.lostCards should be(status.wonCards) }
    it("should have the played card in the table") { next.played should contain(new Card(2)) }
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

    it("should have player0 turn") { next.isPlayer0 should be(true) }
    it("should have player with 3 cards") { next.playerCards should be(Seq(4, 6, 7).map(new Card(_))) }
    it("should have opposite with 3 cards") { next.oppositeCards should be(Seq(1, 3, 8).map(new Card(_))) }
    it("should have won cards with played and won cards") { next.wonCards should be(status.wonCards ++ Set(new Card(2), new Card(5))) }
    it("should have lost cards with previous lost cards") { next.lostCards should be(status.lostCards) }
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

    it("should have player1 turn") { next.isPlayer0 should be(false) }
    it("should have player with 3 cards") { next.playerCards should be(Seq(4, 6, 7).map(new Card(_))) }
    it("should have opposite with 3 cards") { next.oppositeCards should be(Seq(1, 3, 8).map(new Card(_))) }
    it("should have won cards equal to played and previous lost cards ") { next.wonCards should be(status.lostCards ++ Set(2, 5).map(new Card(_))) }
    it("should have lost cards equal to previous won cards") { next.lostCards should be(status.wonCards) }
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

    it("should have player1 turn") { next.isPlayer0 should be(false) }
    it("should have player with previous cards") { next.playerCards should be(status.oppositeCards) }
    it("should have opposite with 2 cards") { next.oppositeCards should be(Seq(2, 4).map(new Card(_))) }
    it("should have won with previous lost cards") { next.wonCards should be(status.lostCards) }
    it("should have lost cards with previuos won cards") { next.lostCards should be(status.wonCards) }
    it("should have played card") { next.played should be(Some(new Card(3))) }
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

    it("should have player0 turn") { next.isPlayer0 should be(true) }
    it("should have player with 3 cards") { next.playerCards should be(Seq(5, 7, 1).map(new Card(_))) }
    it("should have opposite with 3 cards") { next.oppositeCards should be(Seq(2, 4, 0).map(new Card(_))) }
    it("should have won cards with previuos owned cards plus played cards") { next.wonCards should be(status.wonCards ++ Set(3, 6).map(new Card(_))) }
    it("should have lost cards with previous owned cards") { next.lostCards should be(status.lostCards) }
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

    it("should have player1 turn") { next.isPlayer0 should be(false) }
    it("should have player with 3 cards") { next.playerCards should be(Seq(5, 7, 1).map(new Card(_))) }
    it("should have opposite with 3 cards") { next.oppositeCards should be(Seq(2, 4, 0).map(new Card(_))) }
    it("should have won with previous lost cards plus played cards") { next.wonCards should be(status.lostCards ++ Set(3, 6).map(new Card(_))) }
    it("should have lost with previuos owned cards") { next.lostCards should be(status.wonCards) }
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

    it("should have player1 turn") { next.isPlayer0 should be(false) }
    it("should have player with previous opposite cards") { next.playerCards should be(status.oppositeCards) }
    it("should have opposite with 2 cards") { next.oppositeCards should be(Seq(0, 2).map(new Card(_))) }
    it("should have won with previous lost cards") { next.wonCards should be(status.lostCards) }
    it("should have lost with previuos won cards") { next.lostCards should be(status.wonCards) }
    it("should have played card") { next.played should be(Some(new Card(1))) }
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

    it("should have player0 turn") { next.isPlayer0 should be(true) }
    it("should have player with 2 cards") { next.playerCards should be(Seq(3, 5).map(new Card(_))) }
    it("should have opposite with 2 cards") { next.oppositeCards should be(Seq(0, 2).map(new Card(_))) }
    it("should have won with previuos won cards plus played cards") { next.wonCards should be(status.wonCards ++ Set(1, 4).map(new Card(_))) }
    it("should have lost with previous lost cards") { next.lostCards should be(status.lostCards) }
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

    it("should have player1 turn") { next.isPlayer0 should be(false) }
    it("should have player with 2 cards") { next.playerCards should be(Seq(3, 5).map(new Card(_))) }
    it("should have opposite with 2 cards") { next.oppositeCards should be(Seq(0, 2).map(new Card(_))) }
    it("should have won with previous lost cards plus played cards") { next.wonCards should be(status.lostCards ++ Set(1, 4).map(new Card(_))) }
    it("should have lost with previuos lost cards") { next.lostCards should be(status.wonCards) }
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

    it("should have player1 turn") { next.isPlayer0 should be(false) }
    it("should have player with previous opposite card") { next.playerCards should be(status.oppositeCards) }
    it("should have opposite with no cards") { next.oppositeCards should be(Vector()) }
    it("should have won with previous lost cards") { next.wonCards should be(status.lostCards) }
    it("should have lost with previuos won cards") { next.lostCards should be(status.wonCards) }
    it("should have played card") { next.played should be(Some(new Card(0))) }
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

    it("should have player0 turn") { next.isPlayer0 should be(true) }
    it("should have player with no card") { next.playerCards should be(Vector()) }
    it("should have opposite with no cards") { next.oppositeCards should be(Vector()) }
    it("should have won cards with previuos won cards plus played cards") { next.wonCards should be(status.wonCards ++ Set(0, 1).map(new Card(_))) }
    it("should have lost with previous lost cards") { next.lostCards should be(status.lostCards) }
    it("should have no played") { next.played should be(None) }
    it("should have empty deck") { next.deck should be(Vector()) }
    it("should have completed") { next.isCompleted should be(true) }
    it("should have been completed") { next.isCompleted should be(true) }
    it("should have 120 total score ") { next.playerScore + next.oppositeScore should be(120) }
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

    it("should have player1 turn") { next.isPlayer0 should be(false) }
    it("should have player with no cards") { next.playerCards should be(Vector()) }
    it("should have opposite with no card") { next.oppositeCards should be(Vector()) }
    it("should have won with previuos owned cards plus played cards") { next.wonCards should be(status.lostCards ++ Set(0, 1).map(new Card(_))) }
    it("should have lost with previous owned cards") { next.lostCards should be(status.wonCards) }
    it("should have no played") { next.played should be(None) }
    it("should have empty deck") { next.deck should be(Vector()) }
    it("should have been completed") { next.isCompleted should be(true) }
    it("should have 120 total score ") { next.playerScore + next.oppositeScore should be(120) }
    it("should have been determined") { next.isDetermined should be(true) }
  }

}