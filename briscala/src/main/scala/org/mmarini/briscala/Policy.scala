package org.mmarini.briscala

/**
 *
 */
trait Policy {
  /**
   *
   */
  def selectAction(state: HiddenStatus): Int
}