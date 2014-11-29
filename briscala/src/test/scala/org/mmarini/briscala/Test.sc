
import org.mmarini.briscala._

import java.util.Random

object Test {
  val s = new GameGen(new Random).create          //> s  : org.mmarini.briscala.Status = Status(Vector(Deck, Deck, Deck, Deck, Dec
                                                  //| k, Deck, Deck, Deck, Deck, Trump, Deck, Deck, Deck, Deck, Player, Deck, Deck
                                                  //| , Deck, Deck, Deck, Deck, Deck, Deck, Deck, Deck, Deck, Deck, Deck, Deck, De
                                                  //| ck, Player, Player, Deck, Deck, Deck, Deck, Deck, Deck, Deck, Deck))
	s.playerScore                             //> res0: Int = 0
	
}