package org.mmarini

package object briscala {

  object Seed extends Enumeration {
    val Trump = Value("Trump")
    val Seed1 = Value("Seed1")
    val Seed2 = Value("Seed2")
    val Seed3 = Value("Seed3")
  }

  object Figure extends Enumeration {
    val Two = Value("2")
    val Four = Value("4")
    val Five = Value("5")
    val Six = Value("6")
    val Seven = Value("7")
    val Jack = Value("Jack")
    val Knight = Value("Knight")
    val King = Value("King")
    val Three = Value("3")
    val Ace = Value("Ace")
  }

  type StateValue = Map[IndexedSeq[Int], (Int, Int)]

  type StateActionValue = Map[(Int, IndexedSeq[Int]), (Int, Int)]
}