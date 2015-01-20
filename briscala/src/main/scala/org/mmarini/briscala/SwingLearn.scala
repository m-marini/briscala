/**
 *
 */
package org.mmarini.briscala

import scala.swing.SimpleSwingApplication
import scala.swing.MainFrame
import java.awt.Dimension
import scala.swing.MenuBar
import scala.swing.Menu
import scala.swing.GridBagPanel
import scala.swing.TextField
import scala.swing.Button
import scala.swing.Action
import java.awt.GridBagConstraints
import scala.swing.Label
import scala.swing.Insets
import scala.swing.Alignment
import java.awt.GridBagLayout
import scala.swing.BoxPanel
import scala.swing.Orientation
import scala.swing.ProgressBar
import javax.swing.BorderFactory
import scala.swing.FileChooser

/**
 * @author us00852
 *
 */
object SwingLearn extends SimpleSwingApplication {
  /**
   *
   */
  def top = new MainFrame {
    title = "Learning"
    size = new Dimension(800, 600)

    object nField extends TextField {
      columns = 10
      text = "1000000"
      horizontalAlignment = Alignment.Right
    }

    object trainField extends TextField {
      columns = 10
      text = "1000"
      horizontalAlignment = Alignment.Right
    }

    object testField extends TextField {
      columns = 10
      text = "500"
      horizontalAlignment = Alignment.Right
    }

    object iterField extends TextField {
      columns = 10
      text = "10"
      horizontalAlignment = Alignment.Right
    }

    object cField extends TextField {
      columns = 10
      text = "3000"
      horizontalAlignment = Alignment.Right
    }

    object alphaField extends TextField {
      columns = 10
      text = "300E-9"
      horizontalAlignment = Alignment.Right
    }

    object lambdaField extends TextField {
      columns = 10
      text = "0.8"
      horizontalAlignment = Alignment.Right
    }

    object epsilonField extends TextField {
      columns = 10
      text = "0.1"
      horizontalAlignment = Alignment.Right
    }

    object hiddensField extends TextField {
      columns = 10
      text = "32"
      horizontalAlignment = Alignment.Right
    }

    object fileField extends TextField {
      columns = 30
      text = "network.mat"
      editable = false
    }
    object fileButton extends Button(Action("...") {
      if (fileChooser.showSaveDialog(null) == FileChooser.Result.Approve)
        fileField.text = fileChooser.selectedFile.toString
    })
    object fileChooser extends FileChooser {
      title = "Network file"
    }

    object outField extends TextField {
      columns = 30
      text = "out.mat"
      editable = false
    }
    object outButton extends Button(Action("...") {
      if (outChooser.showSaveDialog(null) == FileChooser.Result.Approve)
        outField.text = outChooser.selectedFile.toString
    })
    object outChooser extends FileChooser {
      title = "Output file"
    }

    object progressBar extends ProgressBar {
      labelPainted = true
    }

    object costField extends TextField {
      columns = 10
      editable = false
      horizontalAlignment = Alignment.Right
    }

    object trainRate extends TextField {
      columns = 10
      horizontalAlignment = Alignment.Right
      editable = false
    }

    object testRate extends TextField {
      columns = 10
      horizontalAlignment = Alignment.Right
      editable = false
    }

    object randRate extends TextField {
      columns = 10
      horizontalAlignment = Alignment.Right
      editable = false
    }

    val inFields = nField :: trainField :: testField :: iterField :: cField :: alphaField :: lambdaField :: epsilonField :: hiddensField :: List()
    val btns = fileButton :: outButton :: startButton :: Nil

    object startButton extends Button(Action("Start") {
      costField.text = 1.1.toString
      testRate.text = 1.1.toString
      trainRate.text = 1.1.toString
      randRate.text = 1.1.toString

      inFields.foreach(_.editable = false)
      btns.foreach(_.enabled = false)
      progressBar.value = 50
    })

    object buttonPane extends BoxPanel(Orientation.Horizontal) {
      contents += startButton
    }

    object inPane extends ExtGridBagPanel {
      val baseCons = defCons.setInsets(5, 5, 5, 5).right.east
      val fieldCons = baseCons.west

      layout(new Label("n")) = baseCons
      layout(nField) = fieldCons.hspan

      layout(new Label("# Train")) = baseCons
      layout(trainField) = fieldCons.hspan

      layout(new Label("# Test")) = baseCons
      layout(testField) = fieldCons.hspan

      layout(new Label("# Iterations")) = baseCons
      layout(iterField) = fieldCons.hspan

      layout(new Label("c")) = baseCons
      layout(cField) = fieldCons.hspan

      layout(new Label("alpha")) = baseCons
      layout(alphaField) = fieldCons.hspan

      layout(new Label("lambda")) = baseCons
      layout(lambdaField) = fieldCons.hspan

      layout(new Label("epsilon")) = baseCons
      layout(epsilonField) = fieldCons.hspan

      layout(new Label("# Hiddens")) = baseCons
      layout(hiddensField) = fieldCons.hspan

      layout(new Label("Network")) = baseCons
      layout(fileField) = fieldCons
      layout(fileButton) = baseCons.hspan

      layout(new Label("Output")) = baseCons
      layout(outField) = fieldCons
      layout(outButton) = baseCons.hspan

      border = BorderFactory.createTitledBorder("Inputs")
    }

    object outPane extends ExtGridBagPanel {
      val baseCons = defCons.setInsets(5, 5, 5, 5).right.east
      val fieldCons = baseCons.west

      layout(new Label("Cost")) = baseCons
      layout(costField) = fieldCons.hspan

      layout(new Label("Train performance")) = baseCons
      layout(trainRate) = fieldCons.hspan

      layout(new Label("Test performance")) = baseCons
      layout(testRate) = fieldCons.hspan

      layout(new Label("Random performance")) = baseCons
      layout(randRate) = fieldCons.hspan

      layout(progressBar) = baseCons.hweight(1).hfill.hspan

      border = BorderFactory.createTitledBorder("Outputs")
    }

    object vPane extends BoxPanel(Orientation.Vertical) {
      contents += inPane
      contents += outPane
      contents += buttonPane
    }
    contents = vPane
  }
}

class ExtGridBagPanel extends GridBagPanel {

  class ExtConstraints(c: GridBagConstraints) extends Constraints(c) {
    /**
     * <
     *
     */
    def this(c: ExtConstraints) = this(new GridBagConstraints(c.self.gridx, c.self.gridy,
      c.self.gridwidth, c.self.gridheight,
      c.self.weightx, c.self.weighty,
      c.self.anchor, c.self.fill, c.self.insets,
      c.self.ipadx, c.self.ipady))

    /**
     *
     */
    def at(c: (Int, Int)) = {
      val c1 = new ExtConstraints(this)
      c1.grid = c
      c1
    }

    /**
     *
     */
    def setInsets(c: (Int, Int, Int, Int)) = {
      val c1 = new ExtConstraints(this)
      c1.insets = new Insets(c._1, c._2, c._3, c._4)
      c1
    }

    /**
     *
     */
    def hspan = {
      val c1 = new ExtConstraints(this)
      c1.gridwidth = GridBagConstraints.REMAINDER
      c1
    }

    /**
     *
     */
    def hfill = {
      val c1 = new ExtConstraints(this)
      c1.fill = GridBagPanel.Fill.Horizontal
      c1
    }

    /**
     *
     */
    def right = {
      val c1 = new ExtConstraints(this)
      c1.gridx = GridBagConstraints.RELATIVE
      c1
    }

    /**
     *
     */
    def east = {
      val c1 = new ExtConstraints(this)
      c1.anchor = GridBagPanel.Anchor.East
      c1
    }

    /**
     *
     */
    def west = {
      val c1 = new ExtConstraints(this)
      c1.anchor = GridBagPanel.Anchor.West
      c1
    }

    /**
     *
     */
    def hweight(w: Double) = {
      val c1 = new ExtConstraints(this)
      c1.weightx = w
      c1
    }
  }

  object defCons extends ExtConstraints(new GridBagConstraints)
}