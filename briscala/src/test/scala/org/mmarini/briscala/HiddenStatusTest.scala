package org.mmarini.briscala

import org.scalatest._
import sun.security.jgss.krb5.InitSecContextToken
import scala.collection.immutable.Vector
import scala.collection.immutable.Map
import breeze.linalg.DenseVector

class HiddenStatusTest extends FunSpec with Matchers {

  describe("A HiddenStatus for unsorted deck list") {

    val visibleStatus = Status(
      true,
      Vector(2, 1, 3).map(new Card(_)),
      (4 to 6).map(new Card(_)),
      List(29, 18, 19).map(new Card(_)).toSet,
      Set(),
      None,
      new Card(0),
      ((7 to 17) ++ (20 to 28) ++ (30 to 39)).map(new Card(_)).toIndexedSeq)

    describe("when perspected by player 0") {

      val status = HiddenStatus(visibleStatus, true)

      it("should sort the outage row") {
        status.toRow should be(
          CardState.Trump :: CardState.Player :: CardState.Player :: CardState.Player :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown ::
            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown ::
            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Won ::
            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Won :: CardState.Won ::
            List())
      }

      it("should map choices") {
        status.reverseMapChoice(0) should be(1)
        status.reverseMapChoice(1) should be(0)
        status.reverseMapChoice(2) should be(2)
      }
    }

    describe("when perspected by player 1") {

      val status = HiddenStatus(visibleStatus, false)

      it("should sort the outage row") {
        status.toRow should be(
          CardState.Trump :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Player :: CardState.Player :: CardState.Player :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown ::
            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown ::
            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Lost ::
            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Lost :: CardState.Lost ::
            List())
      }
    }
  }

  describe("A HiddenStatus for a test all card status") {

    val visibleStatus = Status(
      true,
      (1 to 3).map(new Card(_)),
      (5 to 6).map(new Card(_)),
      List(29, 18, 19).map(new Card(_)).toSet,
      Set(),
      Some(new Card(4)),
      new Card(0),
      ((7 to 17) ++ (20 to 28) ++ (30 to 39)).map(new Card(_)).toIndexedSeq)

    describe("when perspected by player 0") {
      val status = HiddenStatus(visibleStatus, true)

      it("should generate a DenseVector which maps the outage row status") {
        val x = status.statusFeatures
        val expected = DenseVector(
          //
          0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
          //
          0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,

          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,

          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 0.0, 1.0, 0.0, 0.0,

          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 0.0, 1.0, 0.0, 0.0,
          //
          0.0, 0.0, 1.0, 0.0, 0.0)
        //          CardState.Trump 0 :: CardState.Player :: CardState.Player :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown ::
        //            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown ::
        //            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Won ::
        //            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Won :: CardState.Won ::

        for { i <- 0 until x.size } withClue(s"index=${i}: ") {
          x(i) should be(expected(i))
        }
      }

      it("should generate a DenseVector which maps the outage row action") {
        val x = status.actionFeatures(2)
        val expected = DenseVector(
          //
          0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
          //
          0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,

          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,

          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 0.0, 1.0, 0.0, 0.0,

          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 0.0, 1.0, 0.0, 0.0,
          //
          0.0, 0.0, 1.0, 0.0, 0.0,
          //
          0.0, 0.0, 1.0)
        //          CardState.Trump 0 :: CardState.Player :: CardState.Player :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown ::
        //            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown ::
        //            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Won ::
        //            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Won :: CardState.Won ::

        for { i <- 0 until x.size } withClue(s"index=${i}: ") {
          x(i) should be(expected(i))
        }
      }
    }

    describe("when perspected by player 1") {
      val status = HiddenStatus(visibleStatus, false)

      it("should generate a DenseVector which maps the outage row status") {
        val x = status.statusFeatures
        val expected = DenseVector(
          //
          0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
          //
          0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,

          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,

          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 0.0, 0.0, 1.0, 0.0,

          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 0.0, 0.0, 1.0, 0.0,
          //
          0.0, 0.0, 0.0, 1.0, 0.0)
        //          CardState.Trump 0 :: CardState.Player :: CardState.Player :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown ::
        //            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown ::
        //            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Won ::
        //            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Won :: CardState.Won ::

        for { i <- 0 until x.size } withClue(s"index=${i}: ") {
          x(i) should be(expected(i))
        }
      }

      it("should generate a DenseVector which maps the outage row action1") {
        val x = status.actionFeatures(1)
        val expected = DenseVector(
          //
          0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
          //
          0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0, 0.0,

          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,

          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 0.0, 0.0, 1.0, 0.0,

          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          1.0, 0.0, 0.0, 0.0, 0.0,
          //
          0.0, 0.0, 0.0, 1.0, 0.0,
          //
          0.0, 0.0, 0.0, 1.0, 0.0,
          //
          0.0, 1.0, 0.0)
        //          CardState.Trump 0 :: CardState.Player :: CardState.Player :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown ::
        //            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown ::
        //            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Won ::
        //            CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Won :: CardState.Won ::

        for { i <- 0 until x.size } withClue(s"index=${i}: ") {
          x(i) should be(expected(i))
        }
      }
    }
  }

  describe("A HiddenStatus for unsorted won list") {
    val vs = Status(
      true,
      (1 to 3).map(new Card(_)),
      (4 to 6).map(new Card(_)),
      List(17, 18, 19, 27, 28, 37).map(new Card(_)).toSet,
      List(29, 38, 39).map(new Card(_)).toSet,
      None,
      new Card(0),
      ((7 to 16) ++ (20 to 26) ++ (30 to 36)).map(new Card(_)).toIndexedSeq)

    it("should sort the outage row for player 0") {
      HiddenStatus(vs, true).toRow should be(
        CardState.Trump :: CardState.Player :: CardState.Player :: CardState.Player :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown ::
          CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Won :: CardState.Won :: CardState.Won ::
          CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Won :: CardState.Won :: CardState.Lost ::
          CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Won :: CardState.Lost :: CardState.Lost ::
          List())
    }
    it("should sort the outage row for player 1") {
      HiddenStatus(vs, false).toRow should be(
        CardState.Trump :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Player :: CardState.Player :: CardState.Player :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown ::
          CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Lost :: CardState.Lost :: CardState.Lost ::
          CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Lost :: CardState.Lost :: CardState.Won ::
          CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Unknown :: CardState.Lost :: CardState.Won :: CardState.Won ::
          List())
    }
  }
}