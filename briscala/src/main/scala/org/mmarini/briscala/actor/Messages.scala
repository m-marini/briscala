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
case class TrainingGameMessage(n: Int, player0: (TDPolicy, Int), player1: (TDPolicy, Int)) {
}

/**
 *
 */
case class ValidationGameMessage(n: Int, player0: (TDPolicy, Int, Int), player1: (TDPolicy, Int, Int)) {

}

/**
 *
 */
case class EndCompetitionMessage(player0: (TDPolicy, Int, Int), player1: (TDPolicy, Int, Int)) {

}

/**
 *
 */
object ShutdownMessage {

}

/**
 *
 */
case class StartCompetitionMessage(players: IndexedSeq[TDPolicy]) {

}