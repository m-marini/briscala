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
import javax.swing.SwingUtilities
import breeze.linalg.DenseVector
import scalax.io.Resource
import scalax.file.Path
import akka.actor.ActorRef
import org.mmarini.briscala.actor.ShutdownMessage

/**
 * @author us00852
 *
 */
object SwingLearn extends SimpleSwingApplication with LazyLogging {
  private var results: IndexedSeq[(Double, Double, Double)] = IndexedSeq()
  private var selectionActor: ActorRef = null

  object trainField extends TextField {
    columns = 10
    text = "100"
    horizontalAlignment = Alignment.Right
  }

  object validationField extends TextField {
    columns = 10
    text = "50"
    horizontalAlignment = Alignment.Right
  }

  object randomField extends TextField {
    columns = 10
    text = "50"
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
    text = "10"
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
    text = "network"
    editable = false
  }
  object fileChooser extends FileChooser {
    title = "Network file"
  }
  object fileButton extends Button(Action("...") {
    if (fileChooser.showSaveDialog(null) == FileChooser.Result.Approve) {
      val path = Path.fromString(fileChooser.selectedFile.toString)
      val parent = path.parent

      val re1 = """(.*)-\d+\..*""".r
      val re2 = """(.*)\..*""".r

      val name = path.name
      val prefix = name match {
        case re1(p) => p
        case re2(p) => p
        case _ => name
      }
      fileField.text = parent match {
        case Some(path) => path./(prefix).path
        case None => prefix
      }
    }
  })

  object outField extends TextField {
    columns = 30
    text = "outx.mat"
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

  object startButton extends Button

  object stopButton extends Button

  private val btns = fileButton :: outButton :: startButton :: Nil

  private val inFields = trainField :: validationField :: randomField :: iterField ::
    cField :: alphaField :: lambdaField :: epsilonField :: hiddensField ::
    populationField :: eliminationField :: mutationField :: Nil

  startButton.action = Action("Start") {
    validationRate.text = ""
    trainRate.text = ""
    randRate.text = ""

    inFields.foreach(_.editable = false)
    btns.foreach(_.enabled = false)
    stopButton.enabled = true
    progressBar.value = 0
    startCompetition
  }

  stopButton.action = Action("Stop") {
    stopCompetition
    inFields.foreach(_.editable = true)
    btns.foreach(_.enabled = true)
    stopButton.enabled = false;
    progressBar.value = 0
  }

  stopButton.enabled = false;
  /**
   * Create top frame
   */
  def top = new MainFrame {
    title = "Learning"
    size = new Dimension(800, 600)

    object buttonPane extends BoxPanel(Orientation.Horizontal) {
      contents += startButton
      contents += stopButton
    }

    object inPane extends ExtGridBagPanel {
      val baseCons = defCons.setInsets(5, 5, 5, 5).right.east
      val fieldCons = baseCons.west

      layout(new Label("# Train Games")) = baseCons
      layout(trainField) = fieldCons.hspan

      layout(new Label("# Validation Games")) = baseCons
      layout(validationField) = fieldCons.hspan

      layout(new Label("# Random Games")) = baseCons
      layout(randomField) = fieldCons.hspan

      layout(new Label("# Update iterations")) = baseCons
      layout(iterField) = fieldCons.hspan

      layout(new Label("c")) = baseCons
      layout(cField) = fieldCons.hspan

      layout(new Label("alpha")) = baseCons
      layout(alphaField) = fieldCons.hspan

      layout(new Label("lambda")) = baseCons
      layout(lambdaField) = fieldCons.hspan

      layout(new Label("epsilon")) = baseCons
      layout(epsilonField) = fieldCons.hspan

      layout(new Label("# Hidden neurons")) = baseCons
      layout(hiddensField) = fieldCons.hspan

      layout(new Label("# Players")) = baseCons
      layout(populationField) = fieldCons.hspan

      layout(new Label("# Eliminating players")) = baseCons
      layout(eliminationField) = fieldCons.hspan

      layout(new Label("Mutation probability")) = baseCons
      layout(mutationField) = fieldCons.hspan

      layout(new Label("Network filename")) = baseCons
      layout(fileField) = fieldCons
      layout(fileButton) = baseCons.hspan

      layout(new Label("Performance filename")) = baseCons
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
  }

  /**
   * Handle save result event
   */
  private def saveResult(trainingRate: Double, valRate: Double, randomRate: Double) = {
    SwingUtilities.invokeLater(new Runnable() {
      def run = {
        trainRate.text = trainingRate.toString
        validationRate.text = valRate.toString
        randRate.text = randomRate.toString
      }
    })
    results = results :+ (trainingRate, valRate, randomRate)
    saveResults(outField.text, results)
  }

  /**
   * Start competition
   */
  private def startCompetition = {
    logger.info("Starting")
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
      randomField.text.toInt,
      populationField.text.toInt,
      eliminationField.text.toInt,
      mutationField.text.toDouble)

    selectionActor = ActorSystem.create.actorOf(SelectionActor.props(parms, new SelectionCallbacks() {

      def startCompetition = () => SwingUtilities.invokeLater(new Runnable() {
        def run = {
          progressBar.max = (parms.trainGameCount + parms.validationGameCount) * parms.populationCount / 2 + parms.randomGameCount
          progressBar.value = 0
          logger.debug(s"Progress bar max=${progressBar.max}")
        }
      })

      def selectedPopulation = (p) => savePopulation(fileField.text, p)

      def selectedResult = saveResult

      def training = (a, b) => SwingUtilities.invokeLater(new Runnable() {
        def run = progressBar.value += 1
      })

      def validating = (a, b) => SwingUtilities.invokeLater(new Runnable() {
        def run = progressBar.value += 1
      })

      def testing = (a) => SwingUtilities.invokeLater(new Runnable() {
        def run = progressBar.value += 1
      })
    }))

    selectionActor ! StartCompetitionMessage(loadPopulation(fileField.text, parms.populationCount, parms.epsilonGreedy))
  }

  /**
   * Stop competition
   */
  private def stopCompetition = {
    logger.info("Stopping")
    selectionActor ! ShutdownMessage
    selectionActor = null
  }

  /**
   * Save results
   */
  private def saveResults(filename: String, results: Seq[(Double, Double, Double)]) = {
    Path(filename).deleteIfExists()
    MathFile.save(Resource.fromFile(filename),
      results.unzip3 match {
        case (t, v, r) => Map(
          "trainRate" -> DenseVector(t.toArray).toDenseMatrix.t,
          "valRate" -> DenseVector(v.toArray).toDenseMatrix.t,
          "randomRate" -> DenseVector(r.toArray).toDenseMatrix.t)
      })
  }

  /**
   * Load population
   */
  private def loadPopulation(filePrefix: String, n: Int, epsilonGreedy: Double): IndexedSeq[TDPolicy] =
    for {
      fn <- (0 until n).map(i => s"$filePrefix-$i.mat")
      if (Path(fn).canRead)
    } yield {
      TDPolicy.load(fn, epsilonGreedy, CommonRandomizers.policyRand)
    }

  /**
   * Save population
   */
  private def savePopulation(filePrefix: String, pop: Seq[TDPolicy]) =
    for (i <- 0 until pop.size)
      pop(i).save(s"$filePrefix-$i.mat")
}
