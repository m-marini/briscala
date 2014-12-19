package org.mmarini.briscala

import scala.util.Random
import scalax.file.Path
import scalax.io.JavaConverters.asOutputUnmanagedConverter
import scalax.io.Output
import scalax.io.StandardOpenOption.WriteAppend

/**
 *
 */
object Generate extends App {

  val n = argForInt("--n")
  val seed = argForInt("--seed")
  val filename = argFor("--file");

  val out: Output = if (filename.isEmpty)
    System.out.asUnmanagedOutput
  else
    Path(filename.get).delete().outputStream(WriteAppend: _*)

  val random = if (seed == None) new Random else new Random(seed.get)

  save(n.getOrElse(1))

  /**
   *
   */
  private def argFor(name: String): Option[String] = {
    val i = args.indexOf(name)
    if (i >= 0 && i + 1 < args.length)
      Some(args(i + 1))
    else
      None
  }

  /**
   *
   */
  private def argForBoolean(name: String): Option[Boolean] = argFor(name) match {
    case None => None
    case Some(value) => Some(value.toBoolean)
  }

  /**
   *
   */
  private def argForInt(name: String): Option[Int] = argFor(name) match {
    case None => None
    case Some(value) => Some(value.toInt)
  }

  /**
   *
   */
  private def merge(a: (Int, Int), b: (Int, Int)): (Int, Int) = (a._1 + b._1, a._2 + b._2)

  /**
   *
   */
  private def merge(a: (StateValue, StateActionValue), b: (StateValue, StateActionValue)): (StateValue, StateActionValue) = {
    val (sva, sava) = a;
    val (svb, savb) = b;
    val sv = sva ++ svb.map {
      case (k, v) => k -> merge(v, sva.getOrElse(k, (0, 0)))
    }
    val sav = sava ++ savb.map {
      case (k, v) => k -> merge(v, sava.getOrElse(k, (0, 0)))
    }
    (sv, sav)
  }

  /**
   *
   */
  private def save(n: Int): Unit = {

    def saveLoop(acc: (StateValue, StateActionValue), n: Int): (StateValue, StateActionValue) =
      if (n == 0)
        acc
      else
        saveLoop(merge(acc, Game.createValues(random)), n - 1)

    val (stateValue, actionStateValue) = saveLoop((Map(), Map()), n)
    stateValue.foreach {
      case (state, (win, tot)) => {
        out.write(win.toDouble / tot.toDouble)
        out.write(" ")
        out.write(state.mkString(" "))
        out.write("\n")
      }
    }
    out.write("\n")
    actionStateValue.foreach {
      case ((action, state), (win, tot)) => {
        out.write(win.toDouble / tot.toDouble)
        out.write(" ")
        out.write(action)
        out.write(" ")
        out.write(state.mkString(" "))
        out.write("\n")
      }
    }
  }
}