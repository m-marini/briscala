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
  private def save(n: Int): Unit = {

    def saveLoop(acc: ValueFunctions, n: Int): ValueFunctions =
      if (n == 0)
        acc
      else {
        if (n % 1000 == 0)
          println(s"Remaining iteration: $n\r")
        saveLoop(acc + Game.createValues(random), n - 1)
      }

    val ValueFunctions(v, q) = saveLoop(ValueFunctions(), n)

    //    val v = vt.filter { case (_, (_, t)) => t > 2 }
    //    val q = qt.filter { case (_, (_, t)) => t > 2 }

    out.write("# name: V\n");
    out.write("# type: matrix\n")
    out.write(s"# rows: ${v.size}\n")
    out.write("# columns: 42\n");

    v.foreach {
      case (state, (win, tot)) =>
        out.write(s"${state.map(_.id).mkString(" ")} $win $tot\n")
    }
    out.write("\n")
    out.write("# name: Q\n");
    out.write("# type: matrix\n")
    out.write(s"# rows: ${q.size}\n")
    out.write("# columns: 43\n");

    q.foreach {
      case ((state, card), (win, tot)) =>
        out.write(s"${state.map(_.id).mkString(" ")} ${card.id} $win $tot\n")
    }
  }
}