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

import james.core.base.IEntity;
import james.core.experiments.tasks.IComputationTask;
import james.core.observe.IObserver;
import james.gui.utils.BasicUtilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import p3j.gui.P3J;
import p3j.gui.misc.NavigationTreeTab;
import p3j.misc.gui.GUI;

/**
 * 
 * Dialog to show during execution of trials.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ExecutionProgressDialog extends JDialog implements IObserver {

	/** Serialization ID. */
	private static final long serialVersionUID = 6990212654769873627L;

	/** Width of the dialog. */
	private static final int DIALOG_WIDTH = 400;

	/** Height of the dialog. */
	private static final int DIALOG_HEIGHT = 130;

	/** The initial name of the button to pause execution. */
	private static final String PAUSE_COMMAND = "Pause";

	/** The font size of the status message. */
	private static final int FONT_SIZE_STATUS_MSG = 20;

	/** The preffered height of the progress bar. */
	private static final int PREFERRED_HEIGHT_PROGBAR = 40;

	/** The progress bar. */
	private final JProgressBar progressBar;

	/** Overall number of trials. */
	private final int numberOfTrials;

	/** Set of the computation tasks that are observed. */
	private Set<IComputationTask> computationTasks = new HashSet<IComputationTask>();

	/** The trial counter. */
	private int trialCounter;

	/** Button to cancel execution. */
	private JButton cancelButton = new JButton("Cancel");
	{
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public synchronized void actionPerformed(ActionEvent e) {
				if (GUI.printQuestion(P3J.getInstance(), "Really cancel execution?",
				    "Do you really want to cancel the execution?")) {
					for (IComputationTask simulationRun : computationTasks) {
						simulationRun.stopProcessor();
					}
					executionDone();
				}
			}
		});
	}

	/** Button to pause/resume the execution. */
	private JButton pauseButton = new JButton(PAUSE_COMMAND);
	{
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public synchronized void actionPerformed(ActionEvent e) {
				boolean pausing = false;
				for (IComputationTask simulationRun : computationTasks) {
					simulationRun.pauseProcessor();
					pausing = simulationRun.isPausing();
				}
				if (pausing) {
					pauseButton.setText("Resume");
				} else {
					pauseButton.setText(PAUSE_COMMAND);
				}
			}
		});
	}

	/** Button to close the dialog. */
	private JButton okButton = new JButton("OK");
	{
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				executionDone();
			}
		});
	}

	/** Button panel. */
	private JPanel buttonPanel = new JPanel();
	{
		buttonPanel.add(okButton);
		buttonPanel.add(pauseButton);
		buttonPanel.add(cancelButton);
	}

	/**
	 * Instantiates a new execution progress dialog.
	 * 
	 * @param numOfTrials
	 *          the number of trials
	 */
	public ExecutionProgressDialog(int numOfTrials) {
		setModal(false);
		numberOfTrials = numOfTrials;
		progressBar = new JProgressBar(0, numberOfTrials);
		initUI();
	}

	/**
	 * Initializes the user interface.
	 */
	private void initUI() {
		setTitle("Execution Status");
		setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		GUI.centerOnScreen(this);
		okButton.setEnabled(false);

		JPanel content = new JPanel(new BorderLayout(GUI.STD_LAYOUT_GAP,
		    GUI.STD_LAYOUT_GAP));
		JLabel executionSummary = new JLabel("Number of Trials to execute: "
		    + numberOfTrials, SwingConstants.CENTER);
		executionSummary.setFont(new Font("Sans", Font.BOLD, FONT_SIZE_STATUS_MSG));
		content.add(executionSummary, BorderLayout.NORTH);
		content.add(progressBar, BorderLayout.CENTER);
		content.add(buttonPanel, BorderLayout.SOUTH);

		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(0, PREFERRED_HEIGHT_PROGBAR));
		getContentPane().add(content);

		setVisible(true);
		BasicUtilities.repaintOnEDT(this);
	}

	@Override
	public void update(IEntity entity) {
		// Don't do anything here...
	}

	@Override
	public void update(IEntity entity, Object hint) {
		trialCounter++;
		if (trialCounter == numberOfTrials) {
			okButton.setEnabled(true);
			pauseButton.setEnabled(false);
			cancelButton.setEnabled(false);
		}
		progressBar.setValue(trialCounter);
		progressBar.setString("Trial #" + trialCounter);
	}

	/**
	 * Add computation task to the set of observed ones.
	 * 
	 * @param computationTask
	 *          the computation task to be observed
	 */
	public void addSimulationRun(IComputationTask computationTask) {
		computationTasks.add(computationTask);
	}

	/**
	 * Event handler called when execution is done
	 */
	private void executionDone() {
		P3J.getInstance().switchNavigationTreeTab(
		    NavigationTreeTab.RESULTS_OVERVIEW);
		P3J.getInstance().refreshNavigationTree();
		setVisible(false);
	}

}
