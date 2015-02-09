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

/**
 * @author us00852
 *
 */

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