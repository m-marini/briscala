/**
 *
 */
package org.mmarini.briscala

/**
 * @author us00852
 *
 */
case class ValueFunctions(v: StateValue = Map(), q: ActionValue = Map()) {

  /**
   *
   */
  def +(o: ValueFunctions): ValueFunctions =
    ValueFunctions(
      v ++ o.v.map {
        case (ok, ov) => ok -> merge(ov, v.getOrElse(ok, (0, 0)))
      },
      q ++ o.q.map {
        case (ok, ov) => ok -> merge(ov, q.getOrElse(ok, (0, 0)))
      })

  /**
   *
   */
  private def merge(a: (Int, Int), b: (Int, Int)): (Int, Int) = (a._1 + b._1, a._2 + b._2)
}