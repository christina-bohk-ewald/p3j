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

import james.SimSystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import p3j.database.DatabaseFactory;
import p3j.database.IP3MDatabase;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.misc.gui.GUI;
import p3j.pppm.PPPModelFactory;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterAssignmentSet;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * Duplicates a projection.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class DuplicateProjectionDialog extends ProcessProjectionDialog {

	/** Serialization ID. */
	private static final long serialVersionUID = 8436821751889161548L;

	/** The text field to set the name of the new projection. */
	private final JTextField newProjectionName = new JTextField("");

	/**
	 * Instantiates a new duplicate projection dialog.
	 * 
	 * @param projection
	 *          the projection
	 */
	public DuplicateProjectionDialog(ProjectionModel projection) {
		super(projection);
		initUI();
	}

	/**
	 * Initialize the user interface.
	 */
	private void initUI() {
		setTitle("Name of the duplicate projection.");
		setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		setModal(true);
		GUI.centerOnScreen(this);
		newProjectionName.setText(getProjectionModel().getName());
		PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(
		    getButtons(), 1);
		pspf.sep("Duplicate Projection");
		pspf.app("Name of duplicated projection:", newProjectionName);
		getContentPane().add(pspf.constructPanel());
	}

	/**
	 * Duplicates projection.
	 * 
	 * @param newProjName
	 *          the new projection name
	 */
	protected void duplicateProjection(final String newProjName) {
		final JLabel status = reinitializeUIForAdjustment();

		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {
				try {
					DuplicateProjectionDialog.duplicate(
					    DatabaseFactory.getDatabaseSingleton(), getProjectionModel(),
					    newProjName, status);
				} catch (RuntimeException ex) {
					GUI.printErrorMessage("Error while duplicating projection!", ex);
				}
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
	 * Duplicate.
	 * 
	 * @param db
	 *          the database to use
	 * @param originalProjection
	 *          the projection to be duplicated
	 * @param newProjectionName
	 *          the new projection name
	 * @param status
	 *          a label to print the status (may be null)
	 */
	public static void duplicate(IP3MDatabase db,
	    ProjectionModel originalProjection, String newProjectionName,
	    JLabel status) {

		ProjectionModel duplicateProjection = (new PPPModelFactory())
		    .createModel(newProjectionName, originalProjection.getDescription(),
		        originalProjection.getGenerations(), originalProjection.getYears(),
		        originalProjection.getMaximumAge(),
		        originalProjection.getJumpOffYear());

		db.newProjection(duplicateProjection);

		// Copy Settypes
		Map<SetType, SetType> setTypeMap = new HashMap<SetType, SetType>();
		for (SetType originalSetType : originalProjection.getUserDefinedTypes()) {
			SetType duplicateSetType = duplicateProjection.createSetType(
			    originalSetType.getName(), originalSetType.getDescription());
			setTypeMap.put(originalSetType, duplicateSetType);
			for (ParameterInstance parameterInstance : originalSetType
			    .getDefinedParameters()) {
				duplicateProjection.assignParameterInstance(
				    getInstance(duplicateProjection, parameterInstance),
				    duplicateSetType, false);
			}
		}

		// Copy sets
		for (Entry<SetType, SetType> setTypes : setTypeMap.entrySet()) {
			copySets(db, status, setTypes.getKey(), duplicateProjection,
			    setTypes.getValue(), false);
		}
		copySets(db, status, originalProjection.getDefaultSetType(),
		    duplicateProjection, duplicateProjection.getDefaultSetType(), true);

		db.saveProjection(duplicateProjection);
	}

	/**
	 * Copy sets.
	 * 
	 * @param db
	 *          the database
	 * @param status
	 *          the status
	 * @param originalSetType
	 *          the original Settype
	 * @param duplicateProjection
	 *          the duplicate projection
	 * @param duplicateSetType
	 *          the duplicate Settype
	 * @param isDefaultSetType
	 *          the flag for being the default Settype
	 */
	private static void copySets(IP3MDatabase db, JLabel status,
	    SetType originalSetType, ProjectionModel duplicateProjection,
	    SetType duplicateSetType, boolean isDefaultSetType) {
		for (Set set : originalSetType.getSets()) {
			Set duplicateSet = isDefaultSetType ? duplicateProjection.getDefaultSet()
			    : duplicateSetType.createSet(set.getName(), set.getDescription(),
			        set.getProbability());
			copySet(db, status, set, duplicateProjection, duplicateSet,
			    originalSetType.getDefinedParameters());
		}
	}

	/**
	 * Copy a set.
	 * 
	 * @param db
	 *          the database
	 * @param status
	 *          the status label
	 * @param originalSet
	 *          the original set
	 * @param duplicateProjection
	 *          the duplicate projection
	 * @param duplicateSet
	 *          the duplicate set
	 * @param definedParameters
	 *          the defined parameters
	 */
	private static void copySet(IP3MDatabase db, JLabel status, Set originalSet,
	    ProjectionModel duplicateProjection, Set duplicateSet,
	    List<ParameterInstance> definedParameters) {
		for (ParameterInstance paramInst : definedParameters) {
			ParameterAssignmentSet paramAssignments = originalSet
			    .getParameterAssignments(paramInst);
			ParameterInstance duplInst = getInstance(duplicateProjection, paramInst);
			for (ParameterAssignment origPA : paramAssignments.getAssignments()) {
				status.setText("Copying parameter assignment '" + origPA.getName()
				    + "' for " + paramInst);
				SimSystem.report(Level.INFO,
				    "Copying parameter assignment '" + origPA.getName() + "' for "
				        + paramInst);
				ParameterAssignment duplPA = db.newParameterAssignment(duplInst,
				    origPA.getName(), origPA.getDescription(), origPA.getProbability(),
				    origPA.getDeviation(), origPA.getMatrixValue());
				duplPA.setParamInstance(duplInst);
				duplicateSet.addParameterAssignment(duplPA);
			}
		}
	}

	/**
	 * Gets a parameter instance equal to the parameter from the projection model.
	 * 
	 * @param newProj
	 *          the new projection model
	 * @param instance
	 *          the instance
	 * 
	 * @return single instance of DuplicateProjectionDialog
	 */
	private static ParameterInstance getInstance(ProjectionModel newProj,
	    ParameterInstance instance) {
		for (ParameterInstance inst : newProj.getAllParameterInstances()) {
			// TODO: Move this to equals(...) method in parameter instance
			boolean valDimentionMatches = (inst.getValueHeight() == instance
			    .getValueHeight() && inst.getValueWidth() == instance.getValueWidth());
			boolean subPopMatches = (inst.getGeneration() == instance.getGeneration() && inst
			    .getParameter().getPopulation() == instance.getParameter()
			    .getPopulation());
			if (inst.getParameter().getName()
			    .equals(instance.getParameter().getName())
			    && subPopMatches && valDimentionMatches) {
				return inst;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see p3j.gui.dialogs.ProjectionDialog#addOKButtonAction()
	 */
	@Override
	protected void addOKButtonAction() {
		getOkButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				duplicateProjection(newProjectionName.getText());
			}
		});
	}

}
