package org.mmarini.briscala

import scala.util.Random

import scalax.file.Path
import scalax.io.JavaConverters.asOutputUnmanagedConverter
import scalax.io.Output
import scalax.io.StandardOpenOption.WriteAppend

object Generate extends App {

  val n = argForInt("--n")
  val seed = argForInt("--seed")
  val filename = argFor("--file");

  val out: Output = if (filename.isEmpty)
    System.out.asUnmanagedOutput
  else
    Path(filename.get).delete().outputStream(WriteAppend: _*)

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

  def save(l: List[(Status, Option[Int])]) =
    l.reverse.foreach {
      case (status, choice) => {
        out.write(if (choice.isEmpty) "-1" else status.optimizedCard(status.playerCards(choice.get)).id.toString)
        out.write(" ")
        out.writeStrings(status.toRow.map(_.toString), " ")
        out.write("\n")
      }
    }
}