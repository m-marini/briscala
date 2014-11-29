
import org.mmarini.briscala._

import java.util.Random

object Test {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(111); 
  val s = new GameGen(new Random).create;System.out.println("""s  : org.mmarini.briscala.Status = """ + $show(s ));$skip(15); val res$0 = 
	s.playerScore;System.out.println("""res0: Int = """ + $show(res$0))}
	
}
