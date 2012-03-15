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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import p3j.gui.P3J;
import p3j.gui.misc.ParameterListCellRenderer;
import p3j.misc.Misc;
import p3j.misc.gui.GUI;
import p3j.misc.math.Matrix;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * Dialog to edit {@link Set} objects.
 * 
 * TODO: Needs major refactoring. Display error message and close, if number of
 * Settypes is 0.
 * 
 * Created on February 4, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
@Deprecated
public class EditSetsDialog extends JDialog {

	/** Serialization ID. */
	private static final long serialVersionUID = -5975274418446080442L;

	/** The height of the panel showing the available sets. */
	private static final int AVAILABLE_SET_PANEL_HEIGHT = 350;

	/** The width of the panel showing the available sets. */
	private static final int AVAILABLE_SETS_PANEL_WIDTH = 200;

	/** The height of the panel showing existing matrices. */
	private static final int EXISTING_MATRIX_PANEL_HEIGHT = 350;

	/** The width of the panel showing existing matrices. */
	private static final int EXISTING_MATRIX_PANEL_WIDTH = 200;

	/** The height of the panel showing available parameters. */
	private static final int AVAILABLE_PARAMS_PANEL_HEIGHT = 350;

	/** The widht of the panel showing available parameters. */
	private static final int AVAILABLE_PARAMS_PANEL_WIDTH = 400;

	/** Width of the dialog. */
	public static final int DIALOG_WIDTH = 1200;

	/** Height of the dialog. */
	public static final int DIALOG_HEIGHT = 640;

	/** Reference to current scenario. */
	private final ProjectionModel projection;

	/** Reference to current Settype. */
	private SetType currentSetType;

	/** Reference to current set. */
	private Set currentSet;

	/** Reference to current parameter instance. */
	private ParameterInstance currentParameter;

	/** Reference to current parameter assignment. */
	private ParameterAssignment currentParameterAssignment;

	// Dialog structure

	/** Panel to store all UI elements. */
	private JPanel contentPanel;

	/** Panel for set editing. */
	private JPanel setEditingPanel;

	/** Panel for editing the current set's attributes. */
	private JPanel editSetPanel;

	/** Panel to hold elements for choosing another Settype and to create sets. */
	private JPanel chooseTypeCreateSetPanel;

	/** Panel to edit the contents of sets. */
	private JPanel setContentEditingPanel;

	/** Panel to edit current matrix. */
	private JPanel editMatrixPanel;

	/** Panel to show existing matrices. */
	private JPanel showExistingMatricesPanel;

	/** Panel to show available parameters (in this Settype). */
	private JPanel showAvailableParametersPanel;

	/** Panel to show available sets. */
	private JPanel showAvailableSetsPanel;

	// Lists

	/** List of matrices for given parameter and set. */
	private JList matricesList = new JList(new DefaultListModel());
	{
		matricesList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				currentParameterAssignment = (ParameterAssignment) matricesList
				    .getSelectedValue();
				updateMatrixFields();
			}
		});
	}

	/** Renderer for parameter list. */
	private final ParameterListCellRenderer paramListRenderer = new ParameterListCellRenderer(
	    this);

	/** List of parameters for given set. */
	private final JList parametersList = new JList(new DefaultListModel());
	{
		parametersList.setCellRenderer(paramListRenderer);
		parametersList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				currentParameter = (ParameterInstance) parametersList
				    .getSelectedValue();
				refreshAssignments(0);
			}
		});
	}

	/** List of sets for given Settype. */
	private final JList setsList = new JList(new DefaultListModel());
	{
		setsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				currentSet = (Set) setsList.getSelectedValue();
				updateSetFields();
				refreshAssignments(0);
			}
		});
	}

	// Set attributes

	/** Field to edit the current set's name. */
	private final JTextField currentSetName = new JTextField(15);

	/** Field to edit the current set's probability. */
	private final JTextField currentSetProb = new JTextField(3);

	/** Field to edit the name of a set to be created. */
	private final JTextField newSetName = new JTextField(15);

	/** Field to edit the current set's description. */
	private final JTextArea currentSetDesc = new JTextArea(5, 30);

	/** Box with all available Settypes. */
	private final JComboBox availableSetTypes = new JComboBox();
	{
		availableSetTypes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentSetType = (SetType) availableSetTypes.getSelectedItem();
				refreshSetType(0, 0);
			}
		});
	}

	/** Field to edit the name of the current matrix. */
	private final JTextField currentMatrixName = new JTextField(15);

	/** Field to edit the probability of the current matrix. */
	private final JTextField currentMatrixProb = new JTextField(3);

	/** Field to edit the description of the current matrix. */
	private final JTextArea currentMatrixDesc = new JTextArea(7, 15);

	/**
	 * Combo box to select a type of the current Settype, to which the current
	 * matrix will be copied.
	 */
	private final JComboBox availableSetsCombo = new JComboBox();

	/** Box with all available parameters. */
	private final JComboBox availableParametersCombo = new JComboBox();

	// Control elements

	/** Button to change the attributes of the current set. */
	private final JButton changeCurrentSetButton = new JButton("Change");
	{
		changeCurrentSetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				storeSetFields();
			}
		});
	}

	/** Button to delete the attributes of the current set. */
	private final JButton deleteCurrentSetButton = new JButton("Delete");
	{
		deleteCurrentSetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteCurrentSet();
			}
		});
	}

	/** Button to create a new set. */
	private final JButton createNewSetButton = new JButton("Add");
	{
		createNewSetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentSetType.createSet(newSetName.getText(),
				    "Edit set description here.", 0);
				refreshSetType(currentSetType.getNumOfSets() - 1, -1);
			}
		});
	}

	/** Button to edit the values of the matrix. */
	private final JButton editCurrentMatrixButton = new JButton("Edit values");
	{
		editCurrentMatrixButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editCurrentMatrix();
			}
		});
	}

	/** Button to delete the current matrix. */
	private final JButton deleteCurrentMatrixButton = new JButton("Delete");
	{
		deleteCurrentMatrixButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteCurrentMatrix();
			}
		});
	}

	/**
	 * Button to apply the changes to the matrix (except for value changes, these
	 * will always be stored).
	 */
	private final JButton changeCurrentMatrixButton = new JButton("Save");
	{
		changeCurrentMatrixButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				storeMatrixFields();
			}
		});
	}

	/** Button to create a new matrix. */
	private final JButton newMatrixButton = new JButton("New");
	{
		newMatrixButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewMatrix();
			}
		});
	}

	/** Button to copy a matrix. */
	private final JButton copyMatrixButton = new JButton("Copy");
	{
		copyMatrixButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyMatrix();
			}
		});
	}

	/**
	 * Default constructor.
	 * 
	 * @param owner
	 *          the owner of this dialog
	 * @param proj
	 *          the projection to be edited
	 */
	public EditSetsDialog(Frame owner, ProjectionModel proj) {
		super(owner);

		this.projection = proj;

		availableSetTypes.setModel(new DefaultComboBoxModel(projection
		    .getUserDefinedTypes().toArray()));

		initialize();
		resetUI();
	}

	/**
	 * Copy a matrix.
	 */
	protected void copyMatrix() {

		Set targetSet = (Set) availableSetsCombo.getSelectedItem();
		int setIndex = availableSetsCombo.getSelectedIndex();
		ParameterInstance targetParameter = (ParameterInstance) availableParametersCombo
		    .getSelectedItem();
		int parameterIndex = availableParametersCombo.getSelectedIndex();

		if (targetSet == null || targetParameter == null) {
			return;
		}

		ParameterAssignment paramAssign = new ParameterAssignment(targetParameter);

		paramAssign.setName(currentMatrixName.getText());
		paramAssign.setDescription(currentMatrixDesc.getText());
		paramAssign.setProbability(Misc.parseToDoubleProb(currentMatrixProb
		    .getText()));
		paramAssign
		    .setMatrix(currentParameterAssignment != null ? currentParameterAssignment
		        .getMatrix().copy() : new Matrix(currentParameter
		        .createEmptyValue()));

		targetSet.addParameterAssignment(paramAssign);

		this.currentSet = targetSet;
		this.currentParameter = targetParameter;

		refreshSets(false, setIndex);
		refreshParameters(true, parameterIndex);
	}

	/**
	 * Resets user interface.
	 */
	public final void resetUI() {
		if (projection.getNumOfSetTypes() > 0) {
			availableSetTypes.setSelectedIndex(0);
			currentSetType = (SetType) availableSetTypes.getSelectedItem();
			refreshSetType(0, 0);
		}
	}

	/**
	 * Initializes user interface.
	 */
	private void initialize() {
		this.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		this.setContentPane(getContentPanel());
		this.setTitle("Edit Sets");
	}

	// Refreshing

	@Override
	public void setVisible(boolean visible) {
		availableSetTypes.setModel(new DefaultComboBoxModel(projection
		    .getUserDefinedTypes().toArray()));
		super.setVisible(visible);
	}

	/**
	 * Refreshes after a Settype change.
	 * 
	 * @param setIndex
	 *          currently selected set
	 * @param matrixIndex
	 *          currently selected assumption matrix
	 */
	final void refreshSetType(int setIndex, int matrixIndex) {
		if (currentSetType == null) {
			return;
		}
		refreshSets(false, setIndex);
		refreshParameters(false, -1);
		refreshAssignments(matrixIndex);
	}

	/**
	 * Refreshes a set change.
	 * 
	 * @param refreshAll
	 *          if true, the refresh is propagated further (to dependent lists)
	 * @param setIndex
	 *          the index of the set to be selected
	 */
	final void refreshSets(boolean refreshAll, int setIndex) {

		List<Set> sets = currentSetType.getSets();
		availableSetsCombo.setModel(new DefaultComboBoxModel(sets.toArray()));

		GUI.replaceListContents((DefaultListModel) setsList.getModel(), sets);

		Set selectedSet = sets.size() > setIndex ? sets.get(setIndex) : null;
		currentSet = selectedSet;

		if (selectedSet != null) {
			setsList.setSelectedIndex(setIndex);
		} else if (sets.size() > 0) {
			setsList.setSelectedIndex(0);
			currentSet = sets.get(0);
		}

		updateSetFields();

		if (refreshAll) {
			refreshAssignments(0);
		}
	}

	/**
	 * Refreshes a parameters change.
	 * 
	 * @param refreshAll
	 *          flag that determines if selected assignment shall be re-set too
	 * @param selIndex
	 *          index of the selected parameter
	 */
	final void refreshParameters(boolean refreshAll, int selIndex) {

		int selectedIndex = selIndex < 0 ? parametersList.getSelectedIndex()
		    : selIndex;

		List<ParameterInstance> parameters = currentSetType.getDefinedParameters();
		availableParametersCombo.setModel(new DefaultComboBoxModel(parameters
		    .toArray()));

		GUI.replaceListContents((DefaultListModel) parametersList.getModel(),
		    parameters);

		ParameterInstance selectedParameter = parameters.size() > selectedIndex
		    && selectedIndex >= 0 ? parameters.get(selectedIndex) : null;

		currentParameter = selectedParameter;

		if (selectedParameter != null) {
			parametersList.setSelectedIndex(selectedIndex);
		} else if (parameters.size() > 0) {
			parametersList.setSelectedIndex(0);
			currentParameter = parameters.get(0);
		}

		if (refreshAll) {
			refreshAssignments(0);
		}
	}

	/**
	 * Refreshes list of parameter assignments.
	 * 
	 * @param matIndex
	 *          the index of the selected matrix
	 */
	final void refreshAssignments(int matIndex) {

		int matrixIndex = Math.max(0, matIndex);

		List<ParameterAssignment> matrices = null;

		if (currentSet != null && currentParameter != null) {
			matrices = new ArrayList<ParameterAssignment>(currentSet
			    .getParameterAssignments(currentParameter).getAssignments());
		}

		GUI.replaceListContents((DefaultListModel) matricesList.getModel(),
		    matrices);

		if (matrices != null) {
			ParameterAssignment selectedMatrix = matrices.size() > matrixIndex ? matrices
			    .get(matrixIndex) : null;

			if (selectedMatrix != null) {
				matricesList.setSelectedIndex(matrixIndex);
				currentParameterAssignment = matrices.get(matrixIndex);
			} else if (matrices.size() > 0) {
				matricesList.setSelectedIndex(0);
				currentParameterAssignment = matrices.get(0);
			}
		}

		updateMatrixFields();

	}

	// Function to implement button actions

	/**
	 * Updates all fields that show the properties of the current set.
	 */
	final void updateSetFields() {
		currentSetName.setText(currentSet == null ? "" : currentSet.getName());
		currentSetProb.setText(currentSet == null ? "" : Double.toString(currentSet
		    .getProbability()));
		currentSetDesc.setText(currentSet == null ? "" : currentSet
		    .getDescription());
	}

	/**
	 * Updates all fields that display the current parameter assignment's
	 * properties.
	 */
	final void updateMatrixFields() {
		currentMatrixName.setText(currentParameterAssignment == null ? ""
		    : currentParameterAssignment.getName());
		currentMatrixProb.setText(currentParameterAssignment == null ? "" : Double
		    .toString(currentParameterAssignment.getProbability()));
		currentMatrixDesc.setText(currentParameterAssignment == null ? ""
		    : currentParameterAssignment.getDescription());
	}

	/**
	 * Stores set fields to current set.
	 */
	void storeSetFields() {

		if (currentSet == null) {
			return;
		}

		currentSet.setName(currentSetName.getText());
		currentSet.setDescription(currentSetDesc.getText());
		currentSet.setProbability(Double.parseDouble(currentSetProb.getText()));

		setsList.repaint();
	}

	/**
	 * Stores matrix fields to the current matrix.
	 */
	void storeMatrixFields() {
		if (currentParameterAssignment == null) {
			return;
		}
		currentParameterAssignment.setName(currentMatrixName.getText());
		currentParameterAssignment.setDescription(currentMatrixDesc.getText());
		currentParameterAssignment.setProbability(Misc
		    .parseToDoubleProb(currentMatrixProb.getText()));

		matricesList.repaint();
	}

	/**
	 * Deletes current set.
	 */
	void deleteCurrentSet() {

		if (currentSet == null) {
			return;
		}

		currentSetType.removeSet(currentSet);
		refreshSetType(setsList.getSelectedIndex() - 1,
		    matricesList.getSelectedIndex());
	}

	/**
	 * Deletes current matrix.
	 */
	void deleteCurrentMatrix() {

		if (currentSet == null || currentParameterAssignment == null) {
			return;
		}

		int selectedIndex = matricesList.getSelectedIndex();
		currentSet.removeParameterAssignment(currentParameterAssignment);
		refreshAssignments(selectedIndex - 1);

	}

	/**
	 * Create a new parameter assignment (a matrix).
	 */
	void createNewMatrix() {

		if (currentSet == null || currentParameter == null) {
			return;
		}

		ParameterAssignment paramAssign = new ParameterAssignment(currentParameter);

		paramAssign.setName(currentMatrixName.getText());
		paramAssign.setDescription(currentMatrixDesc.getText());
		paramAssign.setProbability(Misc.parseToDoubleProb(currentMatrixProb
		    .getText()));
		paramAssign
		    .setMatrix(currentParameterAssignment != null ? currentParameterAssignment
		        .getMatrix().copy() : new Matrix(currentParameter
		        .createEmptyValue()));

		currentSet.addParameterAssignment(paramAssign);

		refreshAssignments(currentSet.getNumberOfAssignments(currentParameter) - 1);
	}

	/**
	 * Edit the current matrix.
	 */
	void editCurrentMatrix() {
		if (currentParameterAssignment == null) {
			return;
		}
		EditMatrixDialog editMatrixDialog = new EditMatrixDialog(P3J.getInstance(),
		    currentParameterAssignment);
		editMatrixDialog.setVisible(true);
	}

	// User interface

	/**
	 * Initializes content panel.
	 * 
	 * @return initialized content panel
	 */
	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel(GUI.getStdBorderLayout());
			contentPanel.add(getSetEditingPanel(), BorderLayout.NORTH);
			contentPanel.add(getSetContentEditingPanel(), BorderLayout.CENTER);
			contentPanel.add(getCopyMatrixPanel(), BorderLayout.SOUTH);
		}
		return contentPanel;
	}

	/**
	 * Initializes panel for editing sets.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSetEditingPanel() {
		if (setEditingPanel == null) {
			setEditingPanel = new JPanel(GUI.getStdBorderLayout());
			setEditingPanel.add(getChooseTypeAndCreateSetsPanel(), BorderLayout.WEST);
			setEditingPanel.add(getEditSetPanel(), BorderLayout.CENTER);
		}
		return setEditingPanel;
	}

	/**
	 * Gets the set content editing panel.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSetContentEditingPanel() {

		if (setContentEditingPanel == null) {
			setContentEditingPanel = new JPanel();
			setContentEditingPanel.add(getShowAvailableSetsPanel());
			setContentEditingPanel.add(getShowAvailableParametersPanel());
			setContentEditingPanel.add(getShowExistingMatricesPanel());
			setContentEditingPanel.add(getEditMatrixPanel());
		}

		return setContentEditingPanel;
	}

	/**
	 * Gets the edit matrix panel.
	 * 
	 * @return panel to edit current parameter assignment (matrix)
	 */
	private JPanel getEditMatrixPanel() {

		if (editMatrixPanel == null) {
			editMatrixPanel = new JPanel(GUI.getStdBorderLayout());
			editMatrixPanel.add(new JLabel("Edit matrix:"), BorderLayout.NORTH);

			JPanel editMatrixInnerPanel = new JPanel(GUI.getStdBorderLayout());
			editMatrixPanel.add(editMatrixInnerPanel, BorderLayout.CENTER);

			// Name + Probability
			JPanel editNameProbPanel = new JPanel(GUI.getStdBorderLayout());
			editMatrixInnerPanel.add(editNameProbPanel, BorderLayout.NORTH);

			JPanel editNamePanel = new JPanel();
			editNameProbPanel.add(editNamePanel, BorderLayout.NORTH);
			editNamePanel.add(new JLabel(Misc.GUI_LABEL_NAME));
			editNamePanel.add(currentMatrixName);
			editNamePanel.add(editCurrentMatrixButton);

			JPanel editProbPanel = new JPanel();
			editNameProbPanel.add(editProbPanel, BorderLayout.SOUTH);
			editProbPanel.add(new JLabel(Misc.GUI_LABEL_PROBABILITY));
			editProbPanel.add(currentMatrixProb);

			// Description
			JPanel editDescriptionPanel = new JPanel(GUI.getStdBorderLayout());
			editMatrixInnerPanel.add(editDescriptionPanel, BorderLayout.CENTER);
			editDescriptionPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
			editDescriptionPanel.add(currentMatrixDesc, BorderLayout.CENTER);

			JPanel buttonPanel = new JPanel();
			editDescriptionPanel.add(buttonPanel, BorderLayout.SOUTH);
			buttonPanel.add(deleteCurrentMatrixButton);
			buttonPanel.add(changeCurrentMatrixButton);
			buttonPanel.add(newMatrixButton);
		}

		return editMatrixPanel;
	}

	/**
	 * Gets the copy matrix panel.
	 * 
	 * @return panel for fast matrix copy
	 */
	private JPanel getCopyMatrixPanel() {
		JPanel returnPanel = new JPanel(GUI.getStdBorderLayout());
		returnPanel.add(new JPanel(), BorderLayout.CENTER);

		JPanel copyMatrixPanel = new JPanel(GUI.getStdBorderLayout());
		copyMatrixPanel.add(new JLabel("Copy matrix to:"), BorderLayout.NORTH);
		JPanel comboBoxPanel = new JPanel();
		copyMatrixPanel.add(comboBoxPanel, BorderLayout.CENTER);
		comboBoxPanel.setLayout(new BoxLayout(comboBoxPanel, BoxLayout.Y_AXIS));
		comboBoxPanel.add(availableSetsCombo);
		comboBoxPanel.add(availableParametersCombo);
		copyMatrixPanel.add(copyMatrixButton, BorderLayout.EAST);

		returnPanel.add(copyMatrixPanel, BorderLayout.EAST);
		return returnPanel;
	}

	/**
	 * Gets the show existing matrices panel.
	 * 
	 * @return panel to show existing parameter assignments (= matrices)
	 */
	private JPanel getShowExistingMatricesPanel() {

		if (showExistingMatricesPanel == null) {
			showExistingMatricesPanel = new JPanel(GUI.getStdBorderLayout());

			showExistingMatricesPanel.add(new JLabel("Matrices in Set: "),
			    BorderLayout.NORTH);

			JScrollPane matricesScrollPane = new JScrollPane();
			matricesScrollPane.setPreferredSize(new Dimension(
			    EXISTING_MATRIX_PANEL_WIDTH, EXISTING_MATRIX_PANEL_HEIGHT));
			matricesScrollPane.getViewport().add(matricesList);
			showExistingMatricesPanel.add(matricesScrollPane, BorderLayout.CENTER);

		}

		return showExistingMatricesPanel;
	}

	/**
	 * Gets the show available parameters panel.
	 * 
	 * @return panel to show available parameters
	 */
	private JPanel getShowAvailableParametersPanel() {
		if (showAvailableParametersPanel == null) {
			showAvailableParametersPanel = new JPanel(GUI.getStdBorderLayout());

			showAvailableParametersPanel.add(new JLabel("Parameters of Set: "),
			    BorderLayout.NORTH);

			JScrollPane parametersScrollPane = new JScrollPane();
			parametersScrollPane.setPreferredSize(new Dimension(
			    AVAILABLE_PARAMS_PANEL_WIDTH, AVAILABLE_PARAMS_PANEL_HEIGHT));
			parametersScrollPane.getViewport().add(parametersList);
			showAvailableParametersPanel.add(parametersScrollPane,
			    BorderLayout.CENTER);
		}
		return showAvailableParametersPanel;
	}

	/**
	 * Gets the show available sets panel.
	 * 
	 * @return panel to show available sets
	 */
	private JPanel getShowAvailableSetsPanel() {

		if (showAvailableSetsPanel == null) {
			showAvailableSetsPanel = new JPanel(GUI.getStdBorderLayout());

			showAvailableSetsPanel.add(new JLabel("Parameters of Set: "),
			    BorderLayout.NORTH);

			JScrollPane setsScrollPane = new JScrollPane();
			setsScrollPane.setPreferredSize(new Dimension(AVAILABLE_SETS_PANEL_WIDTH,
			    AVAILABLE_SET_PANEL_HEIGHT));
			setsScrollPane.getViewport().add(setsList);
			showAvailableSetsPanel.add(setsScrollPane, BorderLayout.CENTER);
		}

		return showAvailableSetsPanel;
	}

	/**
	 * Gets the choose type and create sets panel.
	 * 
	 * @return javax.swing.JPanel
	 */
	final JPanel getChooseTypeAndCreateSetsPanel() {

		if (chooseTypeCreateSetPanel == null) {

			chooseTypeCreateSetPanel = new JPanel(GUI.getStdBorderLayout());

			JPanel chooseSetType = new JPanel();
			chooseTypeCreateSetPanel.add(chooseSetType, BorderLayout.NORTH);
			chooseSetType.add(new JLabel("Choose Settype:"));
			chooseSetType.add(availableSetTypes);

			JPanel createNewSet = new JPanel();
			chooseTypeCreateSetPanel.add(createNewSet, BorderLayout.SOUTH);
			createNewSet.add(new JLabel("Create new set:"));
			createNewSet.add(newSetName);
			createNewSet.add(createNewSetButton);

		}

		return chooseTypeCreateSetPanel;
	}

	/**
	 * Gets the edit set panel.
	 * 
	 * @return javax.swing.JPanel
	 */
	final JPanel getEditSetPanel() {
		if (editSetPanel == null) {
			editSetPanel = new JPanel(GUI.getStdBorderLayout());

			JPanel nameProbPanel = new JPanel();
			nameProbPanel.setLayout(new BoxLayout(nameProbPanel, BoxLayout.Y_AXIS));
			editSetPanel.add(nameProbPanel, BorderLayout.WEST);
			JPanel editCurrentSetNamePanel = new JPanel();
			editCurrentSetNamePanel.add(new JLabel(Misc.GUI_LABEL_NAME));
			editCurrentSetNamePanel.add(currentSetName);
			JPanel editCurrentSetProbPanel = new JPanel();
			editCurrentSetProbPanel.add(new JLabel(Misc.GUI_LABEL_PROBABILITY));
			editCurrentSetProbPanel.add(currentSetProb);
			nameProbPanel.add(editCurrentSetNamePanel);
			nameProbPanel.add(editCurrentSetProbPanel);

			JPanel descPanel = new JPanel();
			descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS));
			editSetPanel.add(descPanel, BorderLayout.CENTER);
			descPanel.add(new JLabel("Description:"));
			descPanel.add(currentSetDesc);

			JPanel buttonPanel = new JPanel();
			editSetPanel.add(buttonPanel, BorderLayout.SOUTH);
			buttonPanel.add(changeCurrentSetButton);
			buttonPanel.add(deleteCurrentSetButton);
		}
		return editSetPanel;
	}

	/**
	 * Gets the current set.
	 * 
	 * @return Returns the currentSet.
	 */
	public Set getCurrentSet() {
		return currentSet;
	}

}
