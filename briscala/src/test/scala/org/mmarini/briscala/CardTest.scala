package org.mmarini.briscala

import org.scalatest._

class CardTest extends FlatSpec with Matchers {
  "A Three" should "have 10 score" in {
    Card(Figure.Three, Seed.Seed1).score should be (10)
  }
}