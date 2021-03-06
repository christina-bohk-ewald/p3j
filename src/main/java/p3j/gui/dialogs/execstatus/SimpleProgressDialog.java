/*
 * Copyright 2006 - 2012 Christina Bohk and Roland Ewald
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package p3j.gui.dialogs.execstatus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.jamesii.gui.utils.BasicUtilities;

import p3j.misc.IProgressObserver;
import p3j.misc.gui.GUI;

/**
 * Simple progress bar dialog to show progress of generic tasks that may take
 * longer. Typical examples would be loading and storing projections, as this
 * depends on the size of the projections (and also on the type of data storage
 * and the hardware).
 * 
 * @see p3j.gui.dialogs.execstatus.ExecutionProgressDialog for a more
 *      sophisticated, task-specific implementation
 * 
 *      Created on 14.10.2012
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class SimpleProgressDialog extends JDialog implements IProgressObserver {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6832108832051608722L;

  /** Width of the dialog. */
  private static final int DIALOG_WIDTH = 400;

  /** Height of the dialog. */
  private static final int DIALOG_HEIGHT = 130;

  /** The actual progress bar. */
  final JProgressBar progressBar;

  /** Flag to determine whether this process can be cancelled. */
  final boolean cancellationAllowed;

  /** Flag to signal cancellation. */
  private boolean cancelled = false;

  /** Button to close the dialog. */
  private JButton okButton = new JButton("OK");
  {
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
  }

  /** Button to cancel the task. */
  private JButton cancelButton = new JButton("Cancel");
  {
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelled = true;
      }
    });
  }

  /**
   * Instantiates a new simple progress dialog.
   * 
   * @param owner
   *          the owner of the dialog
   * @param processName
   *          the process name
   * @param detailedDescription
   *          the detailed description
   * @param numOfWaypoints
   *          the number of waypoints
   * @param cancellationAllowed
   *          flag to allow cancellation
   */
  public SimpleProgressDialog(Frame owner, String processName,
      String detailedDescription, int numOfWaypoints,
      boolean cancellationAllowed) {
    super(owner, processName, false);
    this.cancellationAllowed = cancellationAllowed;
    setSize(DIALOG_WIDTH, DIALOG_HEIGHT
        - (detailedDescription.isEmpty() ? 20 : 0));
    GUI.centerOnScreen(this);

    progressBar = new JProgressBar(0, numOfWaypoints);
    okButton.setEnabled(false);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton);
    if (cancellationAllowed)
      buttonPanel.add(cancelButton);

    JPanel content = new JPanel(new BorderLayout(GUI.STD_LAYOUT_GAP,
        GUI.STD_LAYOUT_GAP));
    JLabel executionSummary = new JLabel(detailedDescription,
        SwingConstants.CENTER);
    content.add(executionSummary, BorderLayout.NORTH);
    content.add(progressBar, BorderLayout.CENTER);
    content.add(buttonPanel, BorderLayout.SOUTH);

    progressBar.setStringPainted(true);
    progressBar.setPreferredSize(new Dimension(0,
        ExecutionProgressDialog.PREFERRED_HEIGHT_PROGBAR));
    getContentPane().add(content);
  }

  @Override
  public void taskFinished() {
    updateProgress(progressBar.getMaximum(), "Done");
    okButton.setEnabled(true);
    cancelButton.setEnabled(false);
  }

  @Override
  public void taskCanceled() {
    setVisible(false);
  }

  @Override
  public void updateProgress(final int waypoint, final String status) {
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        progressBar.setValue(waypoint);
        progressBar.setString(status);
      }
    });
  }

  @Override
  public int getCurrentWaypoint() {
    return progressBar.getValue();
  }

  @Override
  public void addWaypoints(int additionalWayPoints) {
    progressBar.setMaximum(progressBar.getMaximum() + additionalWayPoints);
  }

  @Override
  public void incrementProgress(String status) {
    updateProgress(progressBar.getValue() + 1, status);
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  /**
   * Shows the progress dialog.
   * 
   * @param owner
   *          the owner of the dialog
   * @param processName
   *          the process name
   * @param detailedDescription
   *          the detailed description
   * @param numOfWaypoints
   *          the number of waypoints to be shown
   * @param cancellationAllowed
   *          the flag to allow cancellation
   * @return the simple progress dialog
   */
  public static IProgressObserver showDialog(Frame owner, String processName,
      String detailedDescription, int numOfWaypoints,
      boolean cancellationAllowed) {
    final SimpleProgressDialog theDialog = new SimpleProgressDialog(owner,
        processName, detailedDescription, numOfWaypoints, cancellationAllowed);
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        theDialog.setVisible(true);
      }
    });
    return theDialog;
  }

}
