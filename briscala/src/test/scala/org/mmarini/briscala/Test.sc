
import org.mmarini.briscala._
import scala.util.Random

object Test {
  Figure.values                                   //> res0: org.mmarini.briscala.Figure.ValueSet = Figure.ValueSet(Ace, Two, Three,
                                                  //|  Four, Five, Six, Seven, Jack, Knight, King)
  val v = Seed.values                             //> v  : org.mmarini.briscala.Seed.ValueSet = Seed.ValueSet(Trump, Seed1, Seed2,
                                                  //|  Seed3)
  val d = for {
    i <- Figure.values
    j <- Seed.values
  } yield Card(i, j)                              //> d  : scala.collection.immutable.Set[org.mmarini.briscala.Card] = Set(Card(Se
                                                  //| ven,Seed1), Card(King,Seed3), Card(Five,Trump), Card(Knight,Trump), Card(Ace
                                                  //| ,Seed2), Card(Ace,Trump), Card(Five,Seed2), Card(Five,Seed1), Card(Four,Trum
                                                  //| p), Card(Three,Seed1), Card(Knight,Seed1), Card(Six,Seed1), Card(Four,Seed1)
                                                  //| , Card(Six,Seed2), Card(Jack,Seed1), Card(Two,Trump), Card(Ace,Seed3), Card(
                                                  //| Three,Trump), Card(Jack,Trump), Card(King,Seed1), Card(Six,Seed3), Card(Seve
                                                  //| n,Seed3), Card(Jack,Seed3), Card(King,Seed2), Card(Jack,Seed2), Card(Three,S
                                                  //| eed2), Card(Two,Seed2), Card(Four,Seed2), Card(Ace,Seed1), Card(Five,Seed3),
                                                  //|  Card(Three,Seed3), Card(Two,Seed3), Card(King,Trump), Card(Two,Seed1), Card
                                                  //| (Four,Seed3), Card(Six,Trump), Card(Knight,Seed3), Card(Seven,Seed2), Card(K
                                                  //| night,Seed2), Card(Seven,Trump))
  d.size                                          //> res1: Int = 40
}