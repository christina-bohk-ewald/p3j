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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import p3j.gui.P3J;
import p3j.misc.Misc;
import p3j.misc.gui.GUI;
import p3j.misc.math.Matrix2D;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * Dialog that allows the quick input of matrices.
 * 
 * TODO: This needs to be refactored.
 * 
 * Created on March 6, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
@Deprecated
public class QuickInputDialog extends JDialog {

	/** Serialization ID. */
	private static final long serialVersionUID = 971779309134143674L;

	/** The height of the dialog. */
	private static final int DIALOG_HEIGHT = 480;

	/** The width of the dialog. */
	private static final int DIALOG_WIDTH = 640;

	/** The column size of the matrix description text area. */
	private static final int MATRIX_DESC_COL_SIZE = 15;

	/** The row size of the matrix description text area. */
	private static final int MATRIX_DESC_ROW_SIZE = 7;

	/**
	 * The size of the text area to enter a probability for the matrix
	 * (assignment).
	 */
	private static final int MATRIX_PROB_FIELD_SIZE = 3;

	/** The size of the text area to enter the matrix name. */
	private static final int MATRIX_NAME_FIELD_SIZE = 15;

	/** Reference to current scenario. */
	private final ProjectionModel currentProjection;

	/** Reference to current Settype. */
	private SetType currentSetType;

	/** Parameter assignment to be copied. */
	private ParameterAssignment currentParameterAssignment = new ParameterAssignment();

	// GUI elements

	/** Combo-box of available Settypes. */
	private JComboBox availableSetTypesCombo = new JComboBox();
	{
		availableSetTypesCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentSetType = (SetType) availableSetTypesCombo.getSelectedItem();
				refreshLists();
			}
		});
	}

	/** List of parameters for given set. */
	private JList parametersList = new JList(new DefaultListModel());

	/** List of sets for given Settype. */
	private JList setsList = new JList(new DefaultListModel());

	/** Field to edit the name of the current matrix. */
	private JTextField currentMatrixName = new JTextField(MATRIX_NAME_FIELD_SIZE);

	/** Field to edit the probability of the current matrix. */
	private JTextField currentMatrixProb = new JTextField(MATRIX_PROB_FIELD_SIZE);

	/** Field to edit the description of the current matrix. */
	private JTextArea currentMatrixDesc = new JTextArea(MATRIX_DESC_ROW_SIZE,
	    MATRIX_DESC_COL_SIZE);

	/** Button to edit the values of the matrix. */
	private JButton editCurrentMatrixButton = new JButton("Edit values");
	{
		editCurrentMatrixButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editCurrentMatrix();
			}
		});
	}

	/** Close button. */
	private JButton closeButton = new JButton("Close");
	{
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
	}

	/** Button to start copy process. */
	private JButton copyButton = new JButton("Copy");
	{
		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyParameterAssignment();
			}
		});
	}

	/**
	 * Default constructor.
	 * 
	 * @param owner
	 *          the owning window
	 * @param projection
	 *          the projection to be edited
	 */
	public QuickInputDialog(Frame owner, ProjectionModel projection) {
		super(owner);
		this.currentProjection = projection;
		init();
		resetUI();
	}

	/**
	 * UI initialization.
	 */
	private void init() {
		this.setTitle("Input matrix for multiple parameters");
		this.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		this.setModal(true);
		GUI.centerOnScreen(this);
		this.setContentPane(getContentPanel());
	}

	/**
	 * Gets the content panel.
	 * 
	 * @return panel that contains all content
	 */
	private JPanel getContentPanel() {
		JPanel contentPanel = new JPanel(GUI.getStdBorderLayout());
		contentPanel.add(getSelectSetTypePanel(), BorderLayout.NORTH);
		contentPanel.add(getEditMatrixPanel(), BorderLayout.WEST);
		contentPanel.add(getChooseTargetPanel(), BorderLayout.CENTER);
		contentPanel.add(getControlPanel(), BorderLayout.SOUTH);
		return contentPanel;
	}

	/**
	 * Gets the control panel.
	 * 
	 * @return panel with control buttons
	 */
	private JPanel getControlPanel() {
		JPanel returnPanel = new JPanel();
		returnPanel.add(copyButton);
		return returnPanel;
	}

	/**
	 * Gets the choose target panel.
	 * 
	 * @return panel to select targets
	 */
	private JPanel getChooseTargetPanel() {
		JPanel returnPanel = new JPanel();

		JPanel setListPanel = new JPanel();
		setListPanel.add(new JLabel("Sets:"));
		setListPanel.add(new JScrollPane(setsList));

		JPanel parameterListPanel = new JPanel();
		parameterListPanel.add(new JLabel("Parameters:"));
		parameterListPanel.add(new JScrollPane(parametersList));

		returnPanel.add(setListPanel);
		returnPanel.add(parameterListPanel);

		return returnPanel;
	}

	/**
	 * Gets the edit matrix panel.
	 * 
	 * @return panel to edit matrix
	 */
	private JPanel getEditMatrixPanel() {
		JPanel returnPanel = new JPanel();
		returnPanel.setLayout(GUI.getStdBorderLayout());

		JPanel editNamePanel = new JPanel();
		editNamePanel.add(new JLabel(Misc.GUI_LABEL_NAME));
		editNamePanel.add(this.currentMatrixName);
		editNamePanel.add(this.editCurrentMatrixButton);

		JPanel editProbPanel = new JPanel();
		editProbPanel.add(new JLabel(Misc.GUI_LABEL_PROBABILITY));
		editProbPanel.add(this.currentMatrixProb);

		JPanel editDescPanel = new JPanel();
		editDescPanel.add(new JLabel(Misc.GUI_LABEL_DESCRIPTION));
		editDescPanel.add(this.currentMatrixDesc);

		returnPanel.add(editNamePanel, BorderLayout.NORTH);
		returnPanel.add(editDescPanel, BorderLayout.CENTER);
		returnPanel.add(editProbPanel, BorderLayout.SOUTH);

		return returnPanel;
	}

	/**
	 * Gets the select Settype panel.
	 * 
	 * @return the select Settype panel
	 */
	private JPanel getSelectSetTypePanel() {
		JPanel returnPanel = new JPanel();
		returnPanel.add(new JLabel("Settype:"));
		returnPanel.add(availableSetTypesCombo);
		return returnPanel;
	}

	@Override
	public void setVisible(boolean visible) {
		resetUI();
		super.setVisible(visible);
	}

	/**
	 * Refreshes parameter and set lists on selection of another Settype.
	 */
	private void refreshLists() {

		if (currentSetType == null) {
			return;
		}

		GUI.replaceListContents((DefaultListModel) parametersList.getModel(),
		    currentSetType.getDefinedParameters());
		GUI.replaceListContents((DefaultListModel) setsList.getModel(),
		    currentSetType.getSets());
	}

	/**
	 * Refresh interface.
	 */
	private void resetUI() {

		List<SetType> setTypes = currentProjection.getUserDefinedTypes();
		availableSetTypesCombo
		    .setModel(new DefaultComboBoxModel(setTypes.toArray()));
		currentSetType = setTypes.size() > 0 ? setTypes.get(0) : null;
		refreshLists();

		// This is the maximum
		if (currentParameterAssignment.getMatrix() == null) {
			currentParameterAssignment.setMatrixValue(new Matrix2D(currentProjection
			    .getMaximumAge() + 1, currentProjection.getYears()));
		}

	}

	/**
	 * Edit the current matrix.
	 */
	protected void editCurrentMatrix() {
		EditMatrixDialog editMatrixDialog = new EditMatrixDialog(P3J.getInstance(),
		    currentParameterAssignment);
		editMatrixDialog.setVisible(true);
	}

	/**
	 * Copy parameter assignment.
	 */
	protected void copyParameterAssignment() {

		Object[] selectedSets = setsList.getSelectedValues();
		Object[] selectedParameters = parametersList.getSelectedValues();

		if (selectedSets.length == 0 || selectedParameters.length == 0) {
			return;
		}

		// Prepare assignment variables
		String name = this.currentMatrixName.getText();
		String desc = this.currentMatrixDesc.getText();
		double prob = Misc.parseToDoubleProb(currentMatrixProb.getText());

		for (int i = 0; i < selectedSets.length; i++) {
			Set currSet = (Set) selectedSets[i];
			for (int j = 0; j < selectedParameters.length; j++) {
				ParameterInstance currParam = (ParameterInstance) selectedParameters[j];
				ParameterAssignment newAssignment = new ParameterAssignment(currParam);
				newAssignment.setName(name);
				newAssignment.setDescription(desc);
				newAssignment.setProbability(prob);
				Matrix2D newValue = currParam.createEmptyValue();
				Matrix2D.subMatrix(currentParameterAssignment.getMatrixValue(),
				    newValue);
				newAssignment.setMatrixValue(newValue);
				currSet.addParameterAssignment(newAssignment);
			}
		}

	}
}
