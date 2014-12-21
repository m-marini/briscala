
import org.mmarini.briscala._
import scala.util.Random
import scala.collection.mutable.Set
import com.sun.org.apache.xml.internal.serializer.ToStream

object Test {
  val a = Map("a" -> 1, "b" -> 2)                 //> a  : scala.collection.immutable.Map[String,Int] = Map(a -> 1, b -> 2)
  val b = Map("b" -> 3, "c" -> 4)                 //> b  : scala.collection.immutable.Map[String,Int] = Map(b -> 3, c -> 4)
  val c = b.map { case (k, v) => k -> (v + a.getOrElse(k, 0)) }
                                                  //> c  : scala.collection.immutable.Map[String,Int] = Map(b -> 5, c -> 4)
  a ++ c                                          //> res0: scala.collection.immutable.Map[String,Int] = Map(a -> 1, b -> 5, c -> 
                                                  //| 4)
}