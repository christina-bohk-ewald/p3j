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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import p3j.experiment.results.filters.ConfigurableResultFilter;
import p3j.experiment.results.filters.IResultFilter;
import p3j.experiment.results.filters.IncludeAllResultFilter;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.misc.gui.GUI;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.SetType;

/**
 * Dialog to instantiate a result filter interactively. Makes use of
 * {@link ConfigurableResultFilter}.
 * 
 * @see ConfigurableResultFilter
 * @see IResultFilter
 * @see p3j.experiment.results.ResultExport
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class ConfigureResultFilterDialog extends JDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2646722465211989759L;

	/** Width of the dialog. */
	public static final int DIALOG_WIDTH = 640;

	/** Height of the dialog. */
	public static final int DIALOG_HEIGHT = 200;

	/** The width of the key column in the form. */
	private static final int KEY_COLUMN_WIDTH = 70;

	/** The current projection. */
	private final ProjectionModel projection;

	/** Flag to determine if the user cancelled the dialog. */
	private boolean cancelled;

	/** The result filter to be returned. */
	private transient IResultFilter resultFilter = new IncludeAllResultFilter();

	/** For editing the string to be matched. */
	private final JTextField matchString = new JTextField();

	/** The 'no filter' option. */
	private final JRadioButton noFilter = new JRadioButton("No Filter");
	{
		noFilter.setSelected(true);
	}

	/** The 'match set name' option. */
	private final JRadioButton matchSetName = new JRadioButton("Match Set Name");

	/** The 'match assignment name' option. */
	private final JRadioButton matchAssignName = new JRadioButton(
	    "Match Assignment Name");

	/** The button group for the filter options. */
	private final ButtonGroup buttonGroup = new ButtonGroup();
	{
		buttonGroup.add(noFilter);
		buttonGroup.add(matchSetName);
		buttonGroup.add(matchAssignName);
	}

	/**
	 * Instantiates a new configure result filter dialog.
	 * 
	 * @param owner
	 *          the owner
	 * @param projectionModel
	 *          the projection model
	 */
	public ConfigureResultFilterDialog(Frame owner,
	    final ProjectionModel projectionModel) {
		super(owner, "Configure Result Filtering", true);
		setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		GUI.centerOnScreen(this);
		projection = projectionModel;

		// Create buttons
		JButton finish = new JButton("Finish");
		JButton cancel = new JButton("Cancel");

		List<JButton> buttons = new ArrayList<JButton>();
		buttons.add(cancel);
		buttons.add(finish);

		PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(
		    KEY_COLUMN_WIDTH, buttons, 1);

		pspf.sep("Filter Setup");
		pspf.app("Matching String:", matchString);
		final JPanel switchBox = new JPanel();
		switchBox.add(noFilter);
		switchBox.add(matchSetName);
		switchBox.add(matchAssignName);
		pspf.app("Filter Scope:", switchBox);

		finish.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				configureFilter();
				setVisible(false);
			}
		});

		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelled = true;
				setVisible(false);
			}
		});

		getContentPane().add(pspf.constructPanel());
	}

	/**
	 * Configures filter if necessary.
	 */
	protected void configureFilter() {
		if (noFilter.isSelected()) {
			return;
		}
		if (this.matchSetName.isSelected()) {
			configureBySetName(matchString.getText());
		} else {
			configureByAssignmentName(matchString.getText());
		}
	}

	/**
	 * Configure result filter by set name.
	 * 
	 * @param setName
	 *          the part of the set name that shall match
	 */
	private void configureBySetName(String setName) {
		int matchingSetCounter = 0;
		Set<Integer> permissibleIDs = new HashSet<Integer>();
		for (SetType setType : getProjection().getAllSetTypes()) {
			for (p3j.pppm.sets.Set set : setType.getSets()) {
				if (set.getName().contains(setName)) {
					matchingSetCounter++;
					Set<ParameterAssignment> assignmentSet = set.getParameterAssignments(
					    setType.getDefinedParameters().get(0)).getAssignments();
					for (ParameterAssignment assignment : assignmentSet) {
						permissibleIDs.add(assignment.getID());
					}
				}
			}
		}
		resultFilter = new ConfigurableResultFilter(permissibleIDs);
		GUI.printMessage(this, "Matching sets", matchingSetCounter
		    + " set(s) could be matched.");
	}

	/**
	 * Configures filter by assignment name.
	 * 
	 * @param assignmentName
	 *          the assignment name
	 */
	private void configureByAssignmentName(String assignmentName) {
		Set<Integer> permissibleIDs = new HashSet<Integer>();
		for (SetType setType : getProjection().getAllSetTypes()) {
			for (ParameterInstance paramInstance : setType.getDefinedParameters()) {
				for (p3j.pppm.sets.Set set : setType.getSets()) {
					for (ParameterAssignment paramAssignment : set
					    .getParameterAssignments(paramInstance).getAssignments()) {
						if (paramAssignment.getName().contains(assignmentName)) {
							permissibleIDs.add(paramAssignment.getID());
						}
					}
				}
			}
		}
		resultFilter = new ConfigurableResultFilter(permissibleIDs);
		GUI.printMessage(this, "Matching assignments", permissibleIDs.size()
		    + " assignment(s) could be matched.");
	}

	/**
	 * Checks if the dialog is cancelled.
	 * 
	 * @return true, if is cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Create a result filter as defined by the user.
	 * 
	 * @return result filter
	 */
	public IResultFilter getConfiguredResultFilter() {
		return resultFilter;
	}

	public ProjectionModel getProjection() {
		return projection;
	}

}
