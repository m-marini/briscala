package org.mmarini.briscala

import scala.util.Random

import scalax.io.Output
import scalax.io.Resource

object Generate extends App {

  val n = argForInt("--n")
  val seed = argForInt("--seed")
  val filename = argFor("--file");

  val out: Output = if (filename.isEmpty)
    Resource.fromOutputStream(System.out)
  else
    Resource.fromFile(filename.get)

  val random = if (seed == None) new Random else new Random(seed.get)

  for (i <- 1 to n.getOrElse(1))
    save(Game.create(random));

  def argFor(name: String): Option[String] = {
    val i = args.indexOf(name)
    if (i >= 0 && i + 1 < args.length)
      Some(args(i + 1))
    else
      None
  }

  def argForInt(name: String): Option[Int] = argFor(name) match {
    case None => None
    case Some(value) => Some(value.toInt)
  }

  def save(l: List[Status]) = {
    out.write(l.toString)
    out.write("\n")
  }
}