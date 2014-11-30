
import org.mmarini.briscala._
import scala.util.Random

object Test {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(86); val res$0 = 
  Figure.values;System.out.println("""res0: org.mmarini.briscala.Figure.ValueSet = """ + $show(res$0));$skip(22); 
  val v = Seed.values;System.out.println("""v  : org.mmarini.briscala.Seed.ValueSet = """ + $show(v ));$skip(81); 
  val d = for {
    i <- Figure.values
    j <- Seed.values
  } yield Card(i, j);System.out.println("""d  : scala.collection.immutable.Set[org.mmarini.briscala.Card] = """ + $show(d ));$skip(9); val res$1 = 
  d.size;System.out.println("""res1: Int = """ + $show(res$1))}
}
