package org.mmarini.briscala.actor

import org.mmarini.briscala.TDPolicy

/**
 *
 */
trait SelectionCallbacks {
  /**
   *
   */
  def startCompetition: () => Unit

  /**
   *
   */
  def selectedPopulation: (IndexedSeq[(Double, Double, TDPolicy)]) => Unit

  /**
   * Notify result selected (trainWonRate, validateWonRate, randomWonRate)
   */
  def selectedResult: (Double, Double, Double) => Unit

  /**
   * Notify training (pair id, remainingGames)
   */
  def training: (Int, Int) => Unit

  /**
   * Notify validating (pair id, remainingGames)
   */
  def validating: (Int, Int) => Unit

  /**
   * Notify testiing (remainingGames)
   */
  def testing: (Int) => Unit
}