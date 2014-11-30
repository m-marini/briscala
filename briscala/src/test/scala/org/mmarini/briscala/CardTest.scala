package org.mmarini.briscala

import org.scalatest._

class CardTest extends FlatSpec with Matchers {
  "A Three" should "have score 10" in {
    Card(Figure.Three, Seed.Seed1).score should be(10)
  }

  "An Ace" should "have score 11" in {
    Card(Figure.Ace, Seed.Seed1).score should be(11)
  }
}