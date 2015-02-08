package org.mmarini.briscala

import akka.actor.Actor
import akka.actor.Props

/**
 *
 */
class LearnActor extends Actor {
  def receive = ???
}

/**
 *
 */
object LearnActor {
  def props = Props(new LearnActor)
}