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
package p3j.gui.panels.results;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;

import p3j.database.DatabaseFactory;
import p3j.experiment.results.ResultsOfTrial;
import p3j.gui.P3J;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.gui.panels.projections.IProjectionTree;
import p3j.gui.panels.projections.ProjectionTreeNode;
import p3j.misc.gui.GUI;

/**
 * Represents a node for a single predicted trajectory.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ResultTreeNode extends ProjectionTreeNode<ResultsOfTrial> {

	/**
	 * Instantiates a new result tree node.
	 * 
	 * @param trialResult
	 *          the trial result
	 * @param number
	 *          the number
	 */
	public ResultTreeNode(ResultsOfTrial trialResult, int number) {
		super(trialResult, "Trial #" + number);
	}

	/** Serialization ID. */
	private static final long serialVersionUID = 6093408747499597813L;

	@Override
	public JPanel selected(TreePath selectionPath, final IProjectionTree projTree) {
		JButton generateReport = new JButton("<html><b>Generate Report</b></html>");
		generateReport.setEnabled(false);
		generateReport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUI.printMessage(P3J.getInstance(), "Feature not implemented yet",
				    "A trial-specific report generation is not implemented yet.");
			}
		});

		JButton exportData = new JButton("Export Data");
		exportData.setEnabled(false);
		exportData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: Add trial-specific data-export.
				GUI.printMessage(P3J.getInstance(), "Feature not implemented yet",
				    "A trial-specific data export is not implemented yet.");
			}
		});

		JButton clearData = new JButton("Clear Result");
		clearData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GUI.printQuestion(P3J.getInstance(), "Really delete this result?",
				    "Approving this will delete the results of this trial.")) {
					try {
						DatabaseFactory.getDatabaseSingleton().deleteResult(getEntity());
					} catch (Exception ex) {
						GUI.printErrorMessage("Result Deletion Failed", ex);
					}
				}
				P3J.getInstance().refreshNavigationTree();
			}
		});

		List<JButton> buttons = new ArrayList<JButton>();
		buttons.add(clearData);
		buttons.add(exportData);
		buttons.add(generateReport);

		PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(buttons, 1);
		pspf.sep("General Information");
		pspf.app("Name:", getEntityLabel());
		pspf.app("Assignment Probability:", this.getEntity()
		    .getAssignmentProbability());
		pspf.app("Set Combination Probability:", this.getEntity()
		    .getSetCombinationProbability());
		return pspf.constructPanel();
	}

	@Override
	public void deselected() {
	}

	@Override
	public String getEntityLabel() {
		return userObject.toString();
	}

}
