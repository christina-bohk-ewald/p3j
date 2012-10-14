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
package p3j.gui.dialogs;

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

import p3j.gui.dialogs.execstatus.ExecutionProgressDialog;
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
public class SimpleProgressDialog extends JDialog {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6832108832051608722L;

  /** Width of the dialog. */
  private static final int DIALOG_WIDTH = 400;

  /** Height of the dialog. */
  private static final int DIALOG_HEIGHT = 130;

  final int waypoints;

  final JProgressBar progressBar;

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

  public SimpleProgressDialog(Frame owner, String processName,
      String detailedDescription, int numOfWaypoints) {
    super(owner, processName, false);
    setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    GUI.centerOnScreen(this);

    waypoints = numOfWaypoints;
    progressBar = new JProgressBar(0, numOfWaypoints);
    okButton.setEnabled(false);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton);

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

  public void taskFinished() {
    updateProgress(waypoints, "Done");
    okButton.setEnabled(true);
  }

  public void updateProgress(int waypoint, String status) {
    progressBar.setValue(waypoint);
    progressBar.setString(status);
  }

  public void incrementProgress(String status) {
    updateProgress(progressBar.getValue() + 1, status);
  }

}
