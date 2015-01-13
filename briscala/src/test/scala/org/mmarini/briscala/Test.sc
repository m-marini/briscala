
import org.mmarini.briscala._
import scalax.io._
import scala.util.matching.Regex
import scala.util.matching.Regex.MatchIterator
import breeze.linalg.DenseVector
import breeze.linalg.DenseMatrix

object Test {
  val map: (Int => Double) = Map((0 -> 1.0), (1 -> 4.2))
                                                  //> map  : Int => Double = Map(0 -> 1.0, 1 -> 4.2)

  map(1)                                          //> res0: Double = 4.2
  map(0)                                          //> res1: Double = 1.0
  map(3)                                          //> java.util.NoSuchElementException: key not found: 3
                                                  //| 	at scala.collection.MapLike$class.default(MapLike.scala:228)
                                                  //| 	at scala.collection.AbstractMap.default(Map.scala:59)
                                                  //| 	at scala.collection.MapLike$class.apply(MapLike.scala:141)
                                                  //| 	at scala.collection.AbstractMap.apply(Map.scala:59)
                                                  //| 	at scala.Function1$class.apply$mcDI$sp(Function1.scala:39)
                                                  //| 	at scala.collection.AbstractMap.apply$mcDI$sp(Map.scala:59)
                                                  //| 	at Test$$anonfun$main$1.apply$mcV$sp(Test.scala:14)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$$anonfun$$exe
                                                  //| cute$1.apply$mcV$sp(WorksheetSupport.scala:76)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.redirected(W
                                                  //| orksheetSupport.scala:65)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.$execute(Wor
                                                  //| ksheetSupport.scala:75)
                                                  //| 	at Test$.main(Test.scala:9)
                                                  //| 	at Test.main(Test.scala)
}