/**
 *
 */
package org.mmarini.briscala

import scala.Vector

/**
 * @author us00852
 *
 */
case class Status(
  isPlayer0: Boolean,
  playerCards: IndexedSeq[Card],
  oppositeCards: IndexedSeq[Card],
  wonCards: Set[Card],
  lostCards: Set[Card],
  played: Option[Card],
  trump: Card,
  deck: IndexedSeq[Card]) {

  /**
   *
   */
  lazy val isFirstHand = played.isEmpty

  /**
   *
   */
  lazy val playerHidden = HiddenStatus(this, true)

  /**
   *
   */
  lazy val oppositeHidden = HiddenStatus(this, false)

  /**
   * Return the number of possible choices
   */
  lazy val numOfChoice = playerCards.size

  /**
   * Compute the score of player
   */
  lazy val playerScore: Int = score(wonCards)

  /**
   * Compute the score of opposite
   */
  lazy val oppositeScore: Int = score(lostCards)

  /**
   * Compute the score of a cards set
   */
  def score(cards: Set[Card]): Int = cards.toList.map(_.score).sum

  /**
   * Return if the player wins the game
   */
  lazy val isWinner: Boolean = playerScore > 60

  /**
   * Return if the player wins the game
   */
  lazy val isLooser: Boolean = oppositeScore > 60

  /**
   *
   */
  lazy val isDraw: Boolean = playerScore == 60 && oppositeScore == 60

  /**
   * Return if the game is completed
   */
  lazy val isCompleted: Boolean = numOfChoice == 0

  /**
   * Return if the result game is determined by now
   */
  lazy val isDetermined: Boolean = isWinner || isLooser || isDraw

  /**
   * Generate the status when played a card
   */
  def nextStatus(choice: Int): Status =
    if (playerCards.isEmpty)
      this
    else {
      val card = playerCards(choice)
      if (played.isEmpty)
        Status(
          !isPlayer0,
          oppositeCards,
          playerCards.filterNot(_ == card),
          lostCards,
          wonCards,
          Some(card),
          trump,
          deck)
      else
        (played.get.versus(card), deck.size) match {
          // player perde in finale
          case (true, 0) => Status(
            !isPlayer0,
            oppositeCards,
            playerCards.filterNot(_ == card),
            lostCards + card + played.get,
            wonCards,
            None,
            trump,
            deck)

          // player 0 perde last in-game
          case (true, 1) => Status(
            !isPlayer0,
            oppositeCards :+ deck.head,
            playerCards.filterNot(_ == card) :+ trump,
            lostCards + card + played.get,
            wonCards,
            None,
            trump,
            Vector())

          // player 0 perde in-game
          case (true, _) => Status(
            !isPlayer0,
            oppositeCards :+ deck(0),
            playerCards.filterNot(_ == card) :+ deck(1),
            lostCards + card + played.get,
            wonCards,
            None,
            trump,
            deck.drop(2))

          // player 0 vince in finale
          case (false, 0) => Status(
            isPlayer0,
            playerCards.filterNot(_ == card),
            oppositeCards,
            wonCards + card + played.get,
            lostCards,
            None,
            trump,
            deck)

          // player 0 vince last in-game
          case (false, 1) => Status(
            isPlayer0,
            playerCards.filterNot(_ == card) :+ deck.head,
            oppositeCards :+ trump,
            wonCards + card + played.get,
            lostCards,
            None,
            trump,
            Vector())

          // player 0 vince in-game
          case (false, _) => Status(
            isPlayer0,
            playerCards.filterNot(_ == card) :+ deck(0),
            oppositeCards :+ deck(1),
            wonCards + card + played.get,
            lostCards,
            None,
            trump,
            deck.drop(2))
        }
    }
}
