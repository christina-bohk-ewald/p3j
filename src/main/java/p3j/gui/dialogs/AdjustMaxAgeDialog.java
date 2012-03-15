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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import p3j.database.DatabaseFactory;
import p3j.gui.P3J;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.misc.MatrixDimension;
import p3j.misc.gui.GUI;
import p3j.misc.math.Matrix2D;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterAssignmentSet;
import p3j.pppm.sets.SetType;

/**
 * Simple dialog to adjust maximum age.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class AdjustMaxAgeDialog extends ProcessProjectionDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3471025733131933944L;

	/** The text field to set the new number of age classes. */
	private final JTextField newNumOfAgeClasses = new JTextField("");

	/**
	 * Instantiates a new adjust max age dialog.
	 * 
	 * @param projModel
	 *          the projection model
	 */
	public AdjustMaxAgeDialog(ProjectionModel projModel) {
		super(projModel);
		initUI();
	}

	/**
	 * Initialize the user interface.
	 */
	private void initUI() {
		setTitle("Number of Age Classes for '" + getProjectionModel().getName()
		    + "'");
		setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		setModal(true);
		GUI.centerOnScreen(this);
		newNumOfAgeClasses.setText(""
		    + getProjectionModel().getNumberOfAgeClasses());
		PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(
		    getButtons(), 1);
		pspf.sep("Adjust Age Classes");
		pspf.app("New Number of Age Classes:", newNumOfAgeClasses);
		getContentPane().add(pspf.constructPanel());
	}

	/**
	 * Adjust the age of a projection model.
	 * 
	 * @param newAge
	 *          the new age to be set
	 */
	protected void adjustAge(final int newAge) {
		if (newAge < 0 || newAge == getProjectionModel().getNumberOfAgeClasses()) {
			return;
		}

		final JLabel status = reinitializeUIForAdjustment();

		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {
				int obsoleteAgeClasses = getProjectionModel().getNumberOfAgeClasses()
				    - newAge;
				for (SetType setType : getProjectionModel().getAllSetTypes()) {
					status.setText("Processing Settype '" + setType.getName() + "'");
					adjustAgeForSetType(setType, obsoleteAgeClasses);
				}
				getProjectionModel().setMaximumAge(newAge - 1);
				DatabaseFactory.getDatabaseSingleton().saveProjection(
				    getProjectionModel());
				return null;
			}

			@Override
			public void done() {
				setVisible(false);
			}
		};

		worker.execute();
	}

	/**
	 * Adjust age for Settype.
	 * 
	 * @param setType
	 *          the Settype to be adjusted
	 * @param obsoleteClasses
	 *          the number obsolete age classes
	 */
	private void adjustAgeForSetType(SetType setType, int obsoleteClasses) {
		for (p3j.pppm.sets.Set set : setType.getSets()) {
			for (ParameterAssignmentSet assignmentSet : set.getSetData().values()) {
				for (ParameterAssignment assignment : assignmentSet.getAssignments()) {
					adjustParameterAssignment(assignment, obsoleteClasses);
				}
			}
		}

	}

	/**
	 * Adjust age for parameter assignment.
	 * 
	 * @param assignment
	 *          the assignment to be adjusted
	 * @param obsoleteClasses
	 *          the number of obsolete classes
	 */
	private void adjustParameterAssignment(ParameterAssignment assignment,
	    int obsoleteClasses) {
		boolean changeHeight = assignment.getParamInstance().getValueHeight() == MatrixDimension.AGES;
		boolean changeWidth = assignment.getParamInstance().getValueWidth() == MatrixDimension.AGES;
		if (!changeHeight && !changeWidth) {
			return;
		}

		Matrix2D value = assignment.getMatrixValue();
		int numOfRows = value.rows();
		int numOfColumns = value.columns();
		// TODO: Remove double-transposition of matrices.
		assignment.setMatrixValue(value.getResizedMatrix((changeWidth ? numOfRows
		    - obsoleteClasses : numOfRows), (changeHeight ? numOfColumns
		    - obsoleteClasses : numOfColumns)));
		DatabaseFactory.getDatabaseSingleton().saveParameterAssignment(assignment);
	}

	@Override
	protected void addOKButtonAction() {
		getOkButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int newNumAgeClasses = -1;
				try {
					newNumAgeClasses = Integer.parseInt(newNumOfAgeClasses.getText());
				} catch (NumberFormatException ex) {
					GUI.printErrorMessage(P3J.getInstance(),
					    "Error reading the new number of age classes", ex.getMessage(),
					    ex);
					return;
				}
				if (GUI.printQuestion(
				    P3J.getInstance(),
				    "Do you really want to set the number of age classes?",
				    "All obsolete data will be REMOVED PERMANENTLY. Unknown values will be filled with zeros.")) {
					adjustAge(newNumAgeClasses);
				}
			}
		});
	}

}
