package org.mmarini.briscala

import akka.actor.Actor
import akka.event.Logging
import akka.actor.Props
import com.typesafe.scalalogging.LazyLogging
import akka.actor.ActorSystem
import akka.actor.PoisonPill

class TestActor extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case "test" => log.info("received test")
    case _ => log.info("received unknown message")
  }
}

object TestActor {
  def props = Props(new TestActor())
}

object ActorTest extends App with LazyLogging {
  logger.info("Ciao")

  val system = ActorSystem("mySystem")
  val myActor = system.actorOf(TestActor.props, "myactor2")

  myActor ! "test"
  myActor ! PoisonPill

  system.shutdown
  system.awaitTermination
}