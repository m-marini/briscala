package org.mmarini.briscala

import org.scalatest._

class CardTest extends FunSpec with Matchers {
  describe("A Three") {
    it("should have score 10") { Card(Figure.Three, Seed.Seed1).score should be(10) }
  }
  describe("An Ace") {
    it("should have score 11") { Card(Figure.Ace, Seed.Seed1).score should be(11) }
  }
  describe("A trump") {
    it("should win against a seed ace") { Card(Figure.Two, Seed.Trump).versus(Card(Figure.Ace, Seed.Seed1)) should be(true) }
    it("should win against a seed three") { Card(Figure.Two, Seed.Trump).versus(Card(Figure.Three, Seed.Seed1)) should be(true) }
    it("should win against a seed king") { Card(Figure.Two, Seed.Trump).versus(Card(Figure.King, Seed.Seed1)) should be(true) }
    it("should win against a seed knight") { Card(Figure.Two, Seed.Trump).versus(Card(Figure.Knight, Seed.Seed1)) should be(true) }
    it("should win against a seed jack") { Card(Figure.Two, Seed.Trump).versus(Card(Figure.Jack, Seed.Seed1)) should be(true) }
    it("should win against a seed seven") { Card(Figure.Two, Seed.Trump).versus(Card(Figure.Seven, Seed.Seed1)) should be(true) }
    it("should win against a seed six") { Card(Figure.Two, Seed.Trump).versus(Card(Figure.Six, Seed.Seed1)) should be(true) }
    it("should win against a seed five") { Card(Figure.Two, Seed.Trump).versus(Card(Figure.Five, Seed.Seed1)) should be(true) }
    it("should win against a seed four") { Card(Figure.Two, Seed.Trump).versus(Card(Figure.Four, Seed.Seed1)) should be(true) }
    it("should win against a seed two") { Card(Figure.Two, Seed.Trump).versus(Card(Figure.Two, Seed.Seed1)) should be(true) }
  }
  describe("ace") {
    it("should win a lower trump") { Card(Figure.Ace, Seed.Trump).versus(Card(Figure.Three, Seed.Trump)) should be(true) }
  }
  describe("three") {
    it("should win a lower trump") { Card(Figure.Three, Seed.Trump).versus(Card(Figure.King, Seed.Trump)) should be(true) }
  }
  describe("king") {
    it("should win a lower trump") { Card(Figure.King, Seed.Trump).versus(Card(Figure.Knight, Seed.Trump)) should be(true) }
  }
  describe("knight") {
    it("should win a lower trump") { Card(Figure.Knight, Seed.Trump).versus(Card(Figure.Jack, Seed.Trump)) should be(true) }
  }
  describe("jack") {
    it("should win a lower trump") { Card(Figure.Jack, Seed.Trump).versus(Card(Figure.Seven, Seed.Trump)) should be(true) }
  }
  describe("seven") {
    it("should win a lower trump") { Card(Figure.Seven, Seed.Trump).versus(Card(Figure.Six, Seed.Trump)) should be(true) }
  }
  describe("six") {
    it("should win a lower trump") { Card(Figure.Six, Seed.Trump).versus(Card(Figure.Five, Seed.Trump)) should be(true) }
  }
  describe("five") {
    it("should win a lower trump") { Card(Figure.Five, Seed.Trump).versus(Card(Figure.Four, Seed.Trump)) should be(true) }
  }
  describe("four") {
    it("should win a lower trump") { Card(Figure.Four, Seed.Trump).versus(Card(Figure.Two, Seed.Trump)) should be(true) }
  }
  describe("A seed") {
    it("should win against different seed ace") { Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Ace, Seed.Seed1)) should be(true) }
    it("should win against different seed Three") { Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Three, Seed.Seed1)) should be(true) }
    it("should win against different seed king") { Card(Figure.Two, Seed.Seed2).versus(Card(Figure.King, Seed.Seed1)) should be(true) }
    it("should win against different seed kinght") { Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Knight, Seed.Seed1)) should be(true) }
    it("should win against different seed jack") { Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Jack, Seed.Seed1)) should be(true) }
    it("should win against different seed seven") { Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Seven, Seed.Seed1)) should be(true) }
    it("should win against different seed six") { Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Six, Seed.Seed1)) should be(true) }
    it("should win against different seed five") { Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Five, Seed.Seed1)) should be(true) }
    it("should win against different seed four") { Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Four, Seed.Seed1)) should be(true) }
    it("should win against different seed two") { Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Two, Seed.Seed1)) should be(true) }
    describe("ace") {
      it("should win against same seed three") { Card(Figure.Ace, Seed.Seed1).versus(Card(Figure.Three, Seed.Seed1)) should be(true) }
    }
    describe("three") {
      it("should win against same seed king") { Card(Figure.Three, Seed.Seed1).versus(Card(Figure.King, Seed.Seed1)) should be(true) }
    }
    describe("king") {
      it("should win against same seed knight") { Card(Figure.King, Seed.Seed1).versus(Card(Figure.Knight, Seed.Seed1)) should be(true) }
    }
    describe("knight") {
      it("should win against same seed jack") { Card(Figure.Knight, Seed.Seed1).versus(Card(Figure.Jack, Seed.Seed1)) should be(true) }
    }
    describe("jack") {
      it("should win against same seed seven") { Card(Figure.Jack, Seed.Seed1).versus(Card(Figure.Seven, Seed.Seed1)) should be(true) }
    }
    describe("seven") {
      it("should win against same seed six") { Card(Figure.Seven, Seed.Seed1).versus(Card(Figure.Six, Seed.Seed1)) should be(true) }
    }
    describe("six") {
      it("should win against same seed five") { Card(Figure.Six, Seed.Seed1).versus(Card(Figure.Five, Seed.Seed1)) should be(true) }
    }
    describe("five") {
      it("should win against same seed four") { Card(Figure.Five, Seed.Seed1).versus(Card(Figure.Four, Seed.Seed1)) should be(true) }
    }
    describe("four") {
      it("should win against same seed two") { Card(Figure.Four, Seed.Seed1).versus(Card(Figure.Two, Seed.Seed1)) should be(true) }
    }
  }
}