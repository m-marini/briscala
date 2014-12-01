package org.mmarini.briscala

import org.scalatest._

class CardTest extends FlatSpec with Matchers {
  "A Three" should "have score 10" in {
    Card(Figure.Three, Seed.Seed1).score should be(10)
  }

  "An Ace" should "have score 11" in {
    Card(Figure.Ace, Seed.Seed1).score should be(11)
  }

  "A trump " should "win a lower trump" in {
    Card(Figure.Ace, Seed.Trump).versus(Card(Figure.Three, Seed.Trump)) should be(true)
    Card(Figure.Three, Seed.Trump).versus(Card(Figure.King , Seed.Trump)) should be(true)
    Card(Figure.King, Seed.Trump).versus(Card(Figure.Knight , Seed.Trump)) should be(true)
    Card(Figure.Knight , Seed.Trump).versus(Card(Figure.Jack , Seed.Trump)) should be(true)
    Card(Figure.Jack , Seed.Trump).versus(Card(Figure.Seven  , Seed.Trump)) should be(true)
    Card(Figure.Seven , Seed.Trump).versus(Card(Figure.Six , Seed.Trump)) should be(true)
    Card(Figure.Six, Seed.Trump).versus(Card(Figure.Five  , Seed.Trump)) should be(true)
    Card(Figure.Five , Seed.Trump).versus(Card(Figure.Four  , Seed.Trump)) should be(true)
    Card(Figure.Four, Seed.Trump).versus(Card(Figure.Two , Seed.Trump)) should be(true)
  }

  "A trump " should "win any other seed" in {
    Card(Figure.Two, Seed.Trump).versus(Card(Figure.Ace, Seed.Seed1)) should be(true)
    Card(Figure.Two, Seed.Trump).versus(Card(Figure.Three, Seed.Seed1)) should be(true)
    Card(Figure.Two, Seed.Trump).versus(Card(Figure.King , Seed.Seed1)) should be(true)
    Card(Figure.Two, Seed.Trump).versus(Card(Figure.Knight , Seed.Seed1)) should be(true)
    Card(Figure.Two , Seed.Trump).versus(Card(Figure.Jack , Seed.Seed1)) should be(true)
    Card(Figure.Two , Seed.Trump).versus(Card(Figure.Seven  , Seed.Seed1)) should be(true)
    Card(Figure.Two , Seed.Trump).versus(Card(Figure.Six , Seed.Seed1)) should be(true)
    Card(Figure.Two, Seed.Trump).versus(Card(Figure.Five  , Seed.Seed1)) should be(true)
    Card(Figure.Two , Seed.Trump).versus(Card(Figure.Four  , Seed.Seed1)) should be(true)
    Card(Figure.Two, Seed.Trump).versus(Card(Figure.Two , Seed.Seed1)) should be(true)
    Card(Figure.Two, Seed.Trump).versus(Card(Figure.Two , Seed.Seed1)) should be(true)
  }

  "A seed" should "win any other seed" in {
    Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Ace , Seed.Seed1)) should be(true)
    Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Three, Seed.Seed1)) should be(true)
    Card(Figure.Two, Seed.Seed2).versus(Card(Figure.King , Seed.Seed1)) should be(true)
    Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Knight , Seed.Seed1)) should be(true)
    Card(Figure.Two , Seed.Seed2).versus(Card(Figure.Jack , Seed.Seed1)) should be(true)
    Card(Figure.Two , Seed.Seed2).versus(Card(Figure.Seven  , Seed.Seed1)) should be(true)
    Card(Figure.Two , Seed.Seed2).versus(Card(Figure.Six , Seed.Seed1)) should be(true)
    Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Five  , Seed.Seed1)) should be(true)
    Card(Figure.Two , Seed.Seed2).versus(Card(Figure.Four  , Seed.Seed1)) should be(true)
    Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Two , Seed.Seed1)) should be(true)
    Card(Figure.Two, Seed.Seed2).versus(Card(Figure.Two , Seed.Seed1)) should be(true)
  }

  "A seed" should "win a lower same seed" in {
    Card(Figure.Ace, Seed.Seed1).versus(Card(Figure.Three, Seed.Seed1)) should be(true)
    Card(Figure.Three, Seed.Seed1).versus(Card(Figure.King , Seed.Seed1)) should be(true)
    Card(Figure.King, Seed.Seed1).versus(Card(Figure.Knight , Seed.Seed1)) should be(true)
    Card(Figure.Knight  , Seed.Seed1).versus(Card(Figure.Jack , Seed.Seed1)) should be(true)
    Card(Figure.Jack , Seed.Seed1).versus(Card(Figure.Seven  , Seed.Seed1)) should be(true)
    Card(Figure.Seven , Seed.Seed1).versus(Card(Figure.Six , Seed.Seed1)) should be(true)
    Card(Figure.Six, Seed.Seed1).versus(Card(Figure.Five  , Seed.Seed1)) should be(true)
    Card(Figure.Five , Seed.Seed1).versus(Card(Figure.Four  , Seed.Seed1)) should be(true)
    Card(Figure.Four, Seed.Seed1).versus(Card(Figure.Two , Seed.Seed1)) should be(true)
  }

}