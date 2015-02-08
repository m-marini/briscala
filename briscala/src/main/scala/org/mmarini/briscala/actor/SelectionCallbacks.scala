package org.mmarini.briscala.actor

import org.mmarini.briscala.TDPolicy

/**
 *
 */
trait SelectionCallbacks {
  /**
   *
   */
  def selectedPopulation: Option[(Seq[TDPolicy]) => Unit]

  /**
   *
   */
  def selectedResult: Option[(Int, Int) => Unit]
}