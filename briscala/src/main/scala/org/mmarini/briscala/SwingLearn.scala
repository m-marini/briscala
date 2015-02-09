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
import akka.actor.ActorSystem
import org.mmarini.briscala.actor.SelectionActor
import org.mmarini.briscala.actor.SelectionCallbacks
import com.typesafe.scalalogging.LazyLogging
import org.mmarini.briscala.actor.StartCompetitionMessage

/**
 * @author us00852
 *
 */
object SwingLearn extends SimpleSwingApplication with LazyLogging {
  /**
   *
   */
  def top = new MainFrame {
    title = "Learning"
    size = new Dimension(800, 600)

    object trainField extends TextField {
      columns = 10
      text = "1000"
      horizontalAlignment = Alignment.Right
    }

    object validationField extends TextField {
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

    object populationField extends TextField {
      columns = 10
      text = "20"
      horizontalAlignment = Alignment.Right
    }

    object eliminationField extends TextField {
      columns = 10
      text = "1"
      horizontalAlignment = Alignment.Right
    }

    object mutationField extends TextField {
      columns = 10
      text = "0.01"
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

    object trainRate extends TextField {
      columns = 10
      horizontalAlignment = Alignment.Right
      editable = false
    }

    object validationRate extends TextField {
      columns = 10
      horizontalAlignment = Alignment.Right
      editable = false
    }

    object randRate extends TextField {
      columns = 10
      horizontalAlignment = Alignment.Right
      editable = false
    }

    val inFields = trainField :: validationField :: iterField ::
      cField :: alphaField :: lambdaField :: epsilonField :: hiddensField ::
      populationField :: eliminationField :: mutationField :: Nil
    val btns = fileButton :: outButton :: startButton :: Nil

    object startButton extends Button(Action("Start") {
      validationRate.text = 1.1.toString
      trainRate.text = 1.1.toString
      randRate.text = 1.1.toString

      inFields.foreach(_.editable = false)
      btns.foreach(_.enabled = false)
      stopButton.enabled = true
      progressBar.value = 0
      startCompetition
    })

    val sb = stopButton
    object stopButton extends Button(Action("Stop") {
      inFields.foreach(_.editable = true)
      btns.foreach(_.enabled = true)
      sb.enabled = false;
      progressBar.value = 0
      stopCompetition
    })

    stopButton.enabled = false

    object buttonPane extends BoxPanel(Orientation.Horizontal) {
      contents += startButton
      contents += stopButton
    }

    object inPane extends ExtGridBagPanel {
      val baseCons = defCons.setInsets(5, 5, 5, 5).right.east
      val fieldCons = baseCons.west

      layout(new Label("# Train")) = baseCons
      layout(trainField) = fieldCons.hspan

      layout(new Label("# Validation")) = baseCons
      layout(validationField) = fieldCons.hspan

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

      layout(new Label("# Population")) = baseCons
      layout(populationField) = fieldCons.hspan

      layout(new Label("# Elimination")) = baseCons
      layout(eliminationField) = fieldCons.hspan

      layout(new Label("Mutation probability")) = baseCons
      layout(mutationField) = fieldCons.hspan

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

      layout(new Label("Train performance")) = baseCons
      layout(trainRate) = fieldCons.hspan

      layout(new Label("Validation performance")) = baseCons
      layout(validationRate) = fieldCons.hspan

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

    val parms = SelectionParameters(
      hiddensField.text.toInt,
      epsilonField.text.toDouble,
      LearningParameters(
        cField.text.toDouble,
        alphaField.text.toDouble,
        lambdaField.text.toDouble),
      iterField.text.toInt,
      trainField.text.toInt,
      validationField.text.toInt,
      populationField.text.toInt,
      eliminationField.text.toInt,
      mutationField.text.toDouble)
    val selectionActor = ActorSystem.create.actorOf(SelectionActor.props(parms, new SelectionCallbacks() {
      def selectedPopulation = None

      def selectedResult = Some((trainWon: Int, validationWon: Int) => {
        trainRate.text = trainWon.toString
        validationRate.text = validationWon.toString
      })
    }))

    def startCompetition = {
      logger.info("Starting")
      selectionActor ! StartCompetitionMessage(IndexedSeq())
    }

    def stopCompetition = {
      logger.info("Stopping")
    }
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