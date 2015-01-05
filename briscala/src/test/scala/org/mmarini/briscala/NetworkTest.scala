package org.mmarini.briscala

import org.scalatest._
import scala.util.Random
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import scala.math._

class NetworkTest extends FunSpec with Matchers {
  describe("The testing network") {
    //
    //       w110     w210     w310
    //   x11 w111 x21 w211 x31 w311 x41
    //       w112     w212     w312
    //    
    //       w120     w220     w320
    //   x12 w121 x22 w221 x32 w321 x42
    //       w122     w222     w322
    //
    //  W1 = 0 10  0
    //       0  0 10
    //
    //  W2 = -5  10 -10
    //       -5 -10  10
    //
    //  W3 = -5 10  10
    //        0 -10 10
    //       
    val net = new Network(DenseMatrix((0.0, 10.0, 0.0), (0.0, 0.0, 10.0)),
      DenseMatrix((-5.0, 10.0, -10.0), (-5.0, -10.0, 10.0)),
      DenseMatrix((-5.0, 10.0, 10.0), (-5.0, -10.0, 10.0)))

    it("should result (0,0) at (-1,-1)") {
      val out = net(DenseVector(-1.0, -1.0))

      out(0) should be < 0.5
      out(1) should be < 0.5
    }

    it("should result (1,1) at (-1,1)") {
      val out = net(DenseVector(-1.0, 1.0))

      out(0) should be > 0.5
      out(1) should be > 0.5
    }
    it("should result (1,0) at (1,-1)") {
      val out = net(DenseVector(1.0, -1.0))

      out(0) should be > 0.5
      out(1) should be < 0.5
    }
    it("should result (0,0) at (1,1)") {
      val out = net(DenseVector(1.0, 1.0))

      out(0) should be < 0.5
      out(1) should be < 0.5
    }
  }
}