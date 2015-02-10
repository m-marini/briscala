/**
 *
 */
package org.mmarini.briscala.actor

import org.mmarini.briscala.Network
import org.mmarini.briscala.TDPolicy

/**
 * @author us00852
 *
 */
case class TrainingGameMessage(n: Int, player0: (Int, TDPolicy), player1: (Int, TDPolicy)) {
}

/**
 *
 */
case class RandomPolicyGameMessage(n: Int, player0: (Int, TDPolicy)) {
}

/**
 *
 */
case class ValidationGameMessage(n: Int, player0: (Int, Int, TDPolicy), player1: (Int, Int, TDPolicy)) {

}

/**
 *
 */
case class EndCompetitionMessage(player0: (Int, Int, TDPolicy), player1: (Int, Int, TDPolicy)) {

}

/**
 *
 */
case object ShutdownMessage {

}

/**
 *
 */
case class StartCompetitionMessage(players: IndexedSeq[TDPolicy]) {

}