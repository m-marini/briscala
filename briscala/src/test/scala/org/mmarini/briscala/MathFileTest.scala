package org.mmarini.briscala

import org.scalatest._
import scalax.io.Resource
import breeze.linalg.DenseMatrix
import java.io.File
import scalax.file.Path

class MathFileTest extends FunSpec with Matchers {

  describe("MathFile.load") {

    val map = MathFile.load(Resource.fromFile("test.mat").lines())

    it("should read variabled") {
      map should have size (6)
    }
  }
  describe("MathFile.save") {
    Path("test1.mat").deleteIfExists()
    MathFile.save(Resource.fromFile("test1.mat"),
      Map(("matrix" -> DenseMatrix((1.0, 2.0), (3.0, 4.0))), ("scalar" -> 1.3)))

    it("should create a file") {
      new File("test1.mat") should exist
    }
  }
}