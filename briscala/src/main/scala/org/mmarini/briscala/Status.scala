/**
 *
 */
package org.mmarini.briscala

import scala.collection.immutable.Map

/**
 * @author us00852
 *
 */
case class Status(
  player0Turn: Boolean,
  player0Cards: IndexedSeq[Card],
  player1Cards: IndexedSeq[Card],
  won0Cards: Set[Card],
  won1Cards: Set[Card],
  played: Option[Card],
  trump: Card,
  deck: IndexedSeq[Card]) {

  /**
   * Return the player cards
   */
  lazy val playerCards = if (player0Turn) player0Cards else player1Cards

  /**
   * Return the player cards
   */
  lazy val oppositeCards = if (player0Turn) player1Cards else player0Cards

  /**
   * Return the player cards
   */
  lazy val wonCards = if (player0Turn) won0Cards else won1Cards

  /**
   * Return the player cards
   */
  lazy val lostCards = if (player0Turn) won1Cards else won0Cards

  /**
   * Return player score
   */
  lazy val playerScore = if (player0Turn) player0Score else player1Score

  /**
   * Return player score
   */
  lazy val oppositeScore = if (player0Turn) player1Score else player0Score

  /**
   * Return the number of possible choices
   */
  lazy val numOfChoice = playerCards.size

  /**
   * Compute the score of player
   */
  lazy val player0Score: Int = score(won0Cards)

  /**
   * Compute the score of opposite
   */
  lazy val player1Score: Int = score(won1Cards)

  /**
   * Compute the score of a cards set
   */
  def score(cards: Set[Card]): Int = cards.toList.map(_.score).sum

  /**
   * Return if the player wins the game
   */
  lazy val isWinner0: Boolean = player0Score > 60

  /**
   * Return if the player wins the game
   */
  lazy val isWinner1: Boolean = player1Score > 60

  /**
   *
   */
  lazy val isDraw: Boolean = player0Score == 60 && player1Score == 60

  /**
   * Return if the game is completed
   */
  lazy val isCompleted: Boolean = numOfChoice == 0

  /**
   * Return if the result game is determined by now
   */
  lazy val isDetermined: Boolean = isWinner0 || isWinner1 || isDraw

  /**
   * Generate the status when played a card
   */
  def nextStatus(choice: Int): Status =
    if (player0Turn)
      if (player0Cards.isEmpty)
        this
      else {
        val card = player0Cards(choice)
        if (played.isEmpty)
          Status(
            false,
            player0Cards.filterNot(_ == card),
            player1Cards,
            won0Cards,
            won1Cards,
            Some(card),
            trump,
            deck)
        else
          (played.get.versus(card), deck.size) match {
            // player 0 perde in finale
            case (true, 0) => Status(
              false,
              player0Cards.filterNot(_ == card),
              player1Cards,
              won0Cards,
              won1Cards + card + played.get,
              None,
              trump,
              deck)

            // player 0 perde last in-game
            case (true, 1) => Status(
              false,
              player0Cards.filterNot(_ == card) :+ trump,
              player1Cards :+ deck.head,
              won0Cards,
              won1Cards + card + played.get,
              None,
              trump,
              Vector())

            // player 0 perde in-game
            case (true, _) => Status(
              false,
              player0Cards.filterNot(_ == card) :+ deck(1),
              player1Cards :+ deck(0),
              won0Cards,
              won1Cards + card + played.get,
              None,
              trump,
              deck.drop(2))

            // player 0 vince in finale
            case (false, 0) => Status(
              true,
              player0Cards.filterNot(_ == card),
              player1Cards,
              won0Cards + card + played.get,
              won1Cards,
              None,
              trump,
              deck)

            // player 0 vince last in-game
            case (false, 1) => Status(
              true,
              player0Cards.filterNot(_ == card) :+ deck.head,
              player1Cards :+ trump,
              won0Cards + card + played.get,
              won1Cards,
              None,
              trump,
              Vector())

            // player 0 vince in-game
            case (false, _) => Status(
              true,
              player0Cards.filterNot(_ == card) :+ deck(0),
              player1Cards :+ deck(1),
              won0Cards + card + played.get,
              won1Cards,
              None,
              trump,
              deck.drop(2))
          }
      }
    else if (player1Cards.isEmpty)
      this
    else {
      val card = player1Cards(choice)
      if (played.isEmpty)
        Status(
          true,
          player0Cards,
          player1Cards.filterNot(_ == card),
          won0Cards,
          won1Cards,
          Some(card),
          trump,
          deck)
      else
        (played.get.versus(card), deck.size) match {
          // player 1 perde in finale
          case (true, 0) => Status(
            true,
            player0Cards,
            player1Cards.filterNot(_ == card),
            won0Cards + card + played.get,
            won1Cards,
            None,
            trump,
            deck)

          // player 1 perde last in-game
          case (true, 1) => Status(
            true,
            player0Cards :+ deck.head,
            player1Cards.filterNot(_ == card) :+ trump,
            won0Cards + card + played.get,
            won1Cards,
            None,
            trump,
            Vector())

          // player 1 perde in-game
          case (true, _) => Status(
            true,
            player0Cards :+ deck(0),
            player1Cards.filterNot(_ == card) :+ deck(1),
            won0Cards + card + played.get,
            won1Cards,
            None,
            trump,
            deck.drop(2))

          // player 1 vince in finale
          case (false, 0) => Status(
            false,
            player0Cards,
            player1Cards.filterNot(_ == card),
            won0Cards,
            won1Cards + card + played.get,
            None,
            trump,
            deck)

          // player 1 vince last in-game
          case (false, 1) => Status(
            false,
            player0Cards :+ trump,
            player1Cards.filterNot(_ == card) :+ deck.head,
            won0Cards,
            won1Cards + card + played.get,
            None,
            trump,
            Vector())

          // player 1 vince in-game
          case (false, _) => Status(
            false,
            player0Cards :+ deck(1),
            player1Cards.filterNot(_ == card) :+ deck(0),
            won0Cards,
            won1Cards + card + played.get,
            None,
            trump,
            deck.drop(2))
        }
    }

  /**
   * Return the optimizer seed map
   */
  private def seedMap(cards: Map[Card, CardState.Value]): Map[Seed.Value, Seed.Value] = {
    // Filter seed only cards
    val seedsOnly = cards.filterKeys(!_.isTrump);

    // Group them by seed then state and then count
    val bySeedState = seedsOnly.groupBy {
      case (card, state) => card.seed
    }.map {
      case (seed, cards) => seed ->
        cards.groupBy {
          case (_, state) => state
        }.map {
          case (state, map) => state -> map.size
        }
    }

    /**
     *
     */
    def compare(
      a: (Seed.Value, Map[CardState.Value, Int]),
      b: (Seed.Value, Map[CardState.Value, Int])): Boolean = (a, b) match {
      case ((seedA, stateMapA), (seedB, stateMapB)) => {
        val maxA = stateMapA.maxBy { case (state, count) => count }
        val maxB = stateMapB.maxBy { case (state, count) => count }
        val diffCount = maxB._2 - maxA._2
        if (diffCount < 0)
          true
        else if (diffCount > 0)
          false
        else if (maxA._1 < maxB._1)
          true
        else if (maxA._1 > maxB._1)
          false
        else
          seedA < seedB
      }
    }

    // Sort the seed 
    val sorted = bySeedState.toList.sortWith(compare)

    val sortedSeed = sorted.map(_._1)

    val map = sortedSeed.zipWithIndex.map { case (seed, id) => Seed(id + 1) -> seed }.toMap

    map + (Seed.Trump -> Seed.Trump)
  }

  /**
   *
   */
  private def mapCard(seedMap: (Seed.Value) => Seed.Value)(card: Card): Card = Card(card.figure, seedMap(card.seed))

  /**
   *
   */
  private def optimize(seedMap: (Seed.Value) => Seed.Value)(cardMap: Map[Card, CardState.Value]): Map[Card, CardState.Value] = {
    cardMap.map {
      case (card, state) => mapCard(seedMap)(card) -> state
    }
  }

  /**
   * Transform the status in int value masked to match the player perspective
   *
   */
  def toRow(player0: Boolean)(): (IndexedSeq[CardState.Value]) = {
    val seq = cardSeq(player0)
    Deck.sorted.map(optimize(seedMap(seq))(seq))
  }

  /**
   * Transform the status in int value masked to match the player perspective
   *
   */
  def toRow(player0: Boolean, choice: Int): (IndexedSeq[CardState.Value], Card) = {
    val seq = cardSeq(player0)
    val toSeed = seedMap(seq)
    (Deck.sorted.map(optimize(toSeed)(seq)),
      mapCard(toSeed)(playerCards(choice)))

  }

  /**
   * Return the list of card and state
   */
  private def cardSeq(player0: Boolean): Map[Card, CardState.Value] = {
    val withDeck = deck.map((_ -> CardState.Unknown)).toMap;
    val withTrump = if (deck.isEmpty)
      withDeck
    else
      withDeck + (trump -> CardState.Trump)
    val withPlayed = if (played.isEmpty)
      withTrump
    else
      withTrump + (played.get -> CardState.Played)

    if (player0)
      withPlayed ++
        player0Cards.map(_ -> CardState.Player) ++
        player1Cards.map(_ -> CardState.Unknown) ++
        won0Cards.map(_ -> CardState.Won) ++
        won1Cards.map(_ -> CardState.Lost)
    else
      withPlayed ++
        player1Cards.map(_ -> CardState.Player) ++
        player0Cards.map(_ -> CardState.Unknown) ++
        won1Cards.map(_ -> CardState.Won) ++
        won0Cards.map(_ -> CardState.Lost)
  }
}