package org.mmarini.briscala

import org.scalatest._
import scala.util.Random

class ValueFunctionTest extends FunSpec with Matchers {
  describe("A ValueFunctionTest") {
    val a = Game.createValues(new Random(1))

    it("should have all state value totals = 1") {
      val ValueFunctions(v, _) = a
      v.foreach { case (_, (w, t)) => t should be(1) }
    }

    it("should have all action value totals = 1") {
      val ValueFunctions(_, q) = a
      q.foreach { case (_, (w, t)) => t should be(1) }
    }

    it("should have all non zero state values = 1") {
      val ValueFunctions(v, _) = a
      v.filter {
        case (_, (w, t)) => w > 0
      }.foreach {
        case (_, (w, t)) => w should be(1)
      }
    }

    it("should have all non zero action values = 1") {
      val ValueFunctions(_, q) = a
      q.filter {
        case (_, (w, t)) => w > 0
      }.foreach {
        case (_, (w, t)) => w should be(1)
      }
    }

    describe("when merged with same ValueFunctionTest") {
      val b = a + a;

      it("should have all state value totals twiced") {
        val ValueFunctions(v, _) = b
        v.foreach { case (_, (w, t)) => t should be(2) }
      }

      it("should have all action value totals twiced") {
        val ValueFunctions(_, q) = b
        q.foreach { case (_, (w, t)) => t should be(2) }
      }

      it("should have all non zero state values twiced") {
        val ValueFunctions(v, _) = b
        v.filter {
          case (_, (w, t)) => w > 0
        }.foreach {
          case (_, (w, t)) => w should be(2)
        }
      }

      it("should have all non zero action values twiced") {
        val ValueFunctions(_, q) = b
        q.filter {
          case (_, (w, t)) => w > 0
        }.foreach {
          case (_, (w, t)) => w should be(2)
        }
      }

    }
  }

}