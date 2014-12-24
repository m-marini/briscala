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

  object CardState extends Enumeration {
    val Unknown = Value
    val Player = Value
    val Won = Value
    val Lost = Value
    val Played = Value
    val Trump = Value
  }

  type StateValue = Map[IndexedSeq[CardState.Value], (Int, Int)]

  type ActionValue = Map[(IndexedSeq[CardState.Value], Card), (Int, Int)]
}