package org.mmarini.briscala

import org.scalatest._

class StatusTest extends FlatSpec with Matchers {
  "A Status" should "get player cards" in {
    val status = Status(Set(), Set(), Set(), None, Card(Figure.Three, Seed.Trump))
  }
}