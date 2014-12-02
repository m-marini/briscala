
import org.mmarini.briscala._
import scala.util.Random
import scala.collection.mutable.Set

object Test {
  val deck = Deck.shuffle(new Random)

  deck.take(3)
  deck.drop(3).take(3)
  deck.drop(6).find(_.isTrump)
  val s = new Status(
    deck.take(3),
    deck.drop(3).take(3),
    Set(),
    Set(),
    None,
    deck.drop(6).find(_.isTrump),
    deck)
}