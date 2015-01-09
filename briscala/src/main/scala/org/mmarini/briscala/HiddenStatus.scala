/**
 *
 */
package org.mmarini.briscala

import breeze.linalg.DenseVector
import breeze.linalg.operators.DenseVector_GenericOps

/**
 * @author us00852
 *
 */
case class HiddenStatus(status: Status, player0: Boolean) {

  /**
   *
   */
  def afterState(choice: Int): HiddenStatus =
    HiddenStatus(next(choice), player0)

  /**
   *
   */
  def numOfChoice: Int = status.numOfChoice

  /**
   *
   */
  def isCompleted: Boolean = status.isCompleted

  /**
   * Return the optimizer seed map
   */
  private lazy val seedMap: Map[Seed.Value, Seed.Value] = {
    // Filter seed only cards
    val seedsOnly = cardSeq.filterKeys(!_.isTrump);

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
  private def mapCard(card: Card): Card = Card(card.figure, seedMap(card.seed))

  /**
   *
   */
  private def optimizedMap: Map[Card, CardState.Value] = {
    cardSeq.map {
      case (card, state) => mapCard(card) -> state
    }
  }

  /**
   * Transform the status in int value masked to match the player perspective
   *
   */
  lazy val toRow: (IndexedSeq[CardState.Value]) = {
    Deck.sorted.map(optimizedMap)
  }

  /**
   *
   */
  private lazy val reverseSeedMap = seedMap.map { case (k, v) => (v -> k) }

  /**
   * Transform the status in int value masked to match the player perspective
   *
   */
  private def reverseMapCard(card: Card): Card = {
    Card(card.figure, reverseSeedMap(card.seed))
  }

  /**
   * Return the list of card and state
   */
  private lazy val cardSeq: Map[Card, CardState.Value] = {
    val withDeck = status.deck.map((_ -> CardState.Unknown)).toMap;
    val withTrump = if (status.deck.isEmpty)
      withDeck
    else
      withDeck + (status.trump -> CardState.Trump)
    val withPlayed = if (!status.isFirstHand)
      withTrump
    else
      withTrump + (status.played.get -> CardState.Played)

    if (player0)
      withPlayed ++
        status.player0Cards.map(_ -> CardState.Player) ++
        status.player1Cards.map(_ -> CardState.Unknown) ++
        status.won0Cards.map(_ -> CardState.Won) ++
        status.won1Cards.map(_ -> CardState.Lost)
    else
      withPlayed ++
        status.player1Cards.map(_ -> CardState.Player) ++
        status.player0Cards.map(_ -> CardState.Unknown) ++
        status.won1Cards.map(_ -> CardState.Won) ++
        status.won0Cards.map(_ -> CardState.Lost)
  }

  /**
   *
   */
  lazy val statusFeatures: DenseVector[Double] = {
    val row = toRow
    val z = DenseVector.tabulate[Double](HiddenStatus.statusFeatureSize)(i => {
      if (i < 60)
        if (row(i / 6).id == (i % 6)) 1.0 else 0.0
      else if (row((i - 60) / 5 + 10).id == (i - 60) % 5)
        1.0
      else
        0.0
    })
    z
  }

  /**
   *
   */
  def actionFeatures(action: Int): DenseVector[Double] =
    DenseVector.vertcat(statusFeatures, DenseVector.tabulate(3)((i) => if (i == action) 1.0 else 0.0))

  /**
   * Compute the next visible state for current hidden status give a choice in the hidden status
   * The choice of hidden status is the ordinal number of player cards
   * (first player card, second player card or third player card).
   *
   */
  def next(choice: Int): Status = status.nextStatus(reverseMapChoice(choice))

  /**
   * Map choice of hidden status to the choice of visible status
   */
  def reverseMapChoice(choice: Int): Int =
    status.playerCards.indexOf(reverseMapCard(playerCard(choice)))

  /**
   * Get the card in hidden status given a choice
   */
  private def playerCard(choice: Int): Card =
    toRow.zipWithIndex.filter { case (s, _) => s == CardState.Player }(choice) match {
      case (_, i) => new Card(i)
    }

}

/**
 *
 */
object HiddenStatus {

  /**
   *
   */
  val statusFeatureSize = 10 * 6 + 30 * 5;

  /**
   *
   */
  val actionFeatureSize = 3 + statusFeatureSize;

}