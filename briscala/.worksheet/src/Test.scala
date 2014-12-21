
import org.mmarini.briscala._
import scala.util.Random
import scala.collection.mutable.Set
import com.sun.org.apache.xml.internal.serializer.ToStream

object Test {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(199); 
  val a = Map("a" -> 1, "b" -> 2);System.out.println("""a  : scala.collection.immutable.Map[String,Int] = """ + $show(a ));$skip(34); 
  val b = Map("b" -> 3, "c" -> 4);System.out.println("""b  : scala.collection.immutable.Map[String,Int] = """ + $show(b ));$skip(64); 
  val c = b.map { case (k, v) => k -> (v + a.getOrElse(k, 0)) };System.out.println("""c  : scala.collection.immutable.Map[String,Int] = """ + $show(c ));$skip(9); val res$0 = 
  a ++ c;System.out.println("""res0: scala.collection.immutable.Map[String,Int] = """ + $show(res$0))}
}
