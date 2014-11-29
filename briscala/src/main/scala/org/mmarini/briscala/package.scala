package org.mmarini

package object briscala {

  object CardStatus extends Enumeration {
    val Player = Value("Player")
    val Won = Value("Won")
    val Lost = Value("Lost")
    val Trump = Value("Trump")
    val Deck = Value("Deck")
    val Played = Value("Played")
  
  }
}