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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import p3j.gui.misc.ParameterListModel;
import p3j.gui.misc.SetTypesListModel;
import p3j.misc.gui.GUI;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.SetType;

/**
 * A dialog to edit the set of defined Settypes.
 * 
 * TODO: Needs major refactoring.
 * 
 * Created on August 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
@Deprecated
public class EditSetTypesDialog extends JDialog {

	/** The text field width of the new and current Settypes' names. */
	private static final int SET_TYPE_NAME_TEXTWIDTH = 15;

	/** The height of the dialog. */
	private static final int DIALOG_HEIGHT = 768;

	/** The width of the dialog. */
	private static final int DIALOG_WIDTH = 1200;

	/** Serialization ID. */
	private static final long serialVersionUID = -1223610044645461902L;

	/** Content panel of dialog. */
	private JPanel contentPanel;

	/** Sroll pane for the Settype list. */
	private JScrollPane setTypeListScrollPane;

	/** Panel to edit a Settype. */
	private JPanel editPanel;

	/** Scroll pane for parameter list of current Settype. */
	private JScrollPane parametersOfTypeScrollPane;

	/** Panel to show the current type. */
	private JPanel currentTypePanel;

	/** Panel to edit the current type. */
	private JPanel editTypePanel;

	/** Panel for parameter selection. */
	private JPanel parameterSelectionPanel;

	/** SCroll pane for all available parameters. */
	private JScrollPane overallParametersScroll;

	/** Panel to display the parameters of the current Settype. */
	private JPanel parametersOfTypePanel;

	/** Panel to hold a Settype's description and edit/delete controls. */
	private JPanel descAndButtonPanel;

	/** Panel to hold the list of all existing Settypes. */
	private JPanel setTypeListPanel;

	/** Text area for the current Settype's description. */
	private JTextArea currentSetTypeDescription = new JTextArea();

	/** Field to change the current Settype's name. */
	private JTextField currentSetTypeName = new JTextField(
	    SET_TYPE_NAME_TEXTWIDTH);

	// Basic data structures

	/** Current projection that is edited. */
	private ProjectionModel currentProjection;

	/** Index of current Settype. */
	private int indexOfCurrentSetType = -1;

	// Parameter list

	/** List model for all available parameters. */
	private ParameterListModel availableParameters = new ParameterListModel();

	/** List of all available parameters. */
	private JList availableParametersList = new JList(availableParameters);

	// Settype management

	/** List of all available Settypes. */
	private SetTypesListModel availableSetTypes;

	/** List of existing Settypes. */
	private JList setTypeList;

	/** Field to put in name of new Settype. */
	private JTextField newSetTypeName = new JTextField(SET_TYPE_NAME_TEXTWIDTH);

	/** Button to create a new Settype. */
	private JButton createNewSetTypeButton = new JButton("OK");
	{
		createNewSetTypeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				availableSetTypes.addSetType(newSetTypeName.getText(),
				    "Put in description here.");
				setTypeList.setSelectedIndex(availableSetTypes.getSize() - 1);
			}
		});
	}

	/** Button to change the name/description of a Settype. */
	private JButton changeSetTypeButton = new JButton("Change");
	{
		changeSetTypeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SetType currentSetType = getCurrentSetType();
				if (currentSetType == null) {
					return;
				}
				currentSetType.setName(currentSetTypeName.getText());
				currentSetType.setDescription(currentSetTypeDescription.getText());
				availableSetTypes.setTypeChanged(indexOfCurrentSetType);
			}
		});
	}

	/** Button to delete a Settype. */
	private JButton deleteSetTypeButton = new JButton("Delete");
	{
		deleteSetTypeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SetType currentSetType = getCurrentSetType();
				if (currentSetType == null) {
					return;
				}
				availableSetTypes.removeSetType(indexOfCurrentSetType);
				int numOfSetTypes = availableSetTypes.getSize();
				if (numOfSetTypes > 0) {
					updateCurrentSetType(indexOfCurrentSetType == numOfSetTypes ? numOfSetTypes - 1
					    : indexOfCurrentSetType);
				} else {
					resetCurrentSetType();
				}
			}
		});
	}

	// Parameter instance management

	/** List of all parameters of Settype. */
	private ParameterListModel parametersOfSetType = new ParameterListModel();

	/** List of parameters of current Settype. */
	private JList parametersOfSetTypeList = new JList(parametersOfSetType);

	/** Button to add a parameter instance to the current Settype. */
	private JButton addInstanceToSetTypeButton = new JButton("<=");
	{
		addInstanceToSetTypeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				transferInstances(availableParametersList, availableParameters,
				    parametersOfSetType, getCurrentSetType());
			}
		});
	}

	/** Button to remove a parameter instance from the current Settype. */
	private JButton removeInstanceFromSetTypeButton = new JButton("=>");
	{
		removeInstanceFromSetTypeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				transferInstances(parametersOfSetTypeList, parametersOfSetType,
				    availableParameters, currentProjection.getDefaultSetType());
			}
		});
	}

	/**
	 * Default constructor.
	 * 
	 * @param owner
	 *          the owner
	 * @param scenario
	 *          the scenario
	 */
	public EditSetTypesDialog(Frame owner, ProjectionModel scenario) {
		super(owner);
		initialize();

		currentProjection = scenario;
		availableSetTypes = new SetTypesListModel(currentProjection);
		setTypeList = new JList(availableSetTypes);
		setTypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setTypeList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateCurrentSetType(setTypeList.getSelectedIndex());
			}
		});

		availableParameters.updateSetType(currentProjection.getDefaultSetType());

		availableSetTypes.setProjection(currentProjection);

		if (currentProjection.getNumOfSetTypes() > 0) {
			updateCurrentSetType(0);
		}
	}

	/**
	 * Transfer instances.
	 * 
	 * @param fromList
	 *          the from list
	 * @param from
	 *          the from
	 * @param to
	 *          the to
	 * @param targetSetType
	 *          the target Settype
	 */
	public void transferInstances(JList fromList, ParameterListModel from,
	    ParameterListModel to, SetType targetSetType) {

		if (targetSetType != null) {

			int firstIndex = fromList.getSelectedIndex();

			Object[] instances = fromList.getSelectedValues();

			for (Object instance : instances) {

				ParameterInstance instanceToBeAdded = (ParameterInstance) instance;

				currentProjection.assignParameterInstance(instanceToBeAdded,
				    targetSetType, false);
			}

			to.refresh();
			from.refresh();

			if (instances.length > 0 && from.getSize() > 0) {
				fromList
				    .setSelectedIndex(from.getSize() == firstIndex ? from.getSize() - 1
				        : firstIndex);
			}
		}
	}

	/**
	 * Update current Settype.
	 * 
	 * @param index
	 *          the index
	 */
	final void updateCurrentSetType(int index) {

		this.indexOfCurrentSetType = index;
		this.setTypeList.setSelectedIndex(index);

		SetType currentSetType = getCurrentSetType();

		this.currentSetTypeDescription.setText(currentSetType == null ? ""
		    : currentSetType.getDescription());
		this.currentSetTypeName.setText(currentSetType == null ? ""
		    : currentSetType.getName());
		this.parametersOfSetType.updateSetType(currentSetType);
		this.availableParameters.refresh();

	}

	/**
	 * Resets selection of Settype.
	 */
	protected void resetCurrentSetType() {
		this.indexOfCurrentSetType = -1;
		this.currentSetTypeDescription.setText("");
		this.currentSetTypeName.setText("");
		this.setTypeList.getSelectionModel().clearSelection();
		this.availableParameters.refresh();
		this.parametersOfSetType.refresh();
	}

	/**
	 * Gets the current Settype.
	 * 
	 * @return currently selected Settype (or null)
	 */
	final SetType getCurrentSetType() {
		if (this.indexOfCurrentSetType >= 0) {
			return this.currentProjection.getSetType(this.indexOfCurrentSetType);
		}
		return null;
	}

	/**
	 * This method initializes this dialog.
	 */
	private void initialize() {

		this.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		this.setContentPane(getContentPanel());
		this.setTitle("Edit Settypes");
	}

	/**
	 * This method initializes jContentPane.
	 * 
	 * @return the content panel
	 */
	final JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel(GUI.getStdBorderLayout());
			contentPanel.add(getSetTypeListScroll(), BorderLayout.WEST);
			contentPanel.add(getEditPanel(), BorderLayout.CENTER);
		}
		return contentPanel;
	}

	/**
	 * This method initializes setTypeListScroll.
	 * 
	 * @return scroll pane containing Settype list
	 */
	private JPanel getSetTypeListScroll() {
		if (setTypeListPanel == null) {
			setTypeListPanel = new JPanel(GUI.getStdBorderLayout());
			setTypeListScrollPane = new JScrollPane();
			setTypeListScrollPane.setViewportView(setTypeList);
			setTypeListPanel.add(setTypeListScrollPane, BorderLayout.CENTER);
			setTypeListPanel
			    .add(new JLabel("Existing Settypes:"), BorderLayout.NORTH);
			JPanel addNewSetTypePanel = new JPanel();
			setTypeListPanel.add(addNewSetTypePanel, BorderLayout.SOUTH);

			addNewSetTypePanel.add(new JLabel("Add new Settype:"));
			addNewSetTypePanel.add(newSetTypeName);
			addNewSetTypePanel.add(createNewSetTypeButton);
		}
		return setTypeListPanel;
	}

	/**
	 * This method initializes the editing panel.
	 * 
	 * @return the editing panel
	 */
	private JPanel getEditPanel() {
		if (editPanel == null) {
			editPanel = new JPanel(GUI.getStdBorderLayout());
			editPanel.add(getCurrentTypePanel(), BorderLayout.NORTH);
			editPanel.add(getEditTypePanel(), BorderLayout.CENTER);
			editPanel.add(getDescAndButtonPanel(), BorderLayout.SOUTH);
		}
		return editPanel;
	}

	/**
	 * Gets the description and button panel.
	 * 
	 * @return panel to hold description and buttons
	 */
	final JPanel getDescAndButtonPanel() {
		if (descAndButtonPanel == null) {
			descAndButtonPanel = new JPanel(GUI.getStdBorderLayout());
			JPanel descPanel = new JPanel(GUI.getStdBorderLayout());
			JPanel buttonPanel = new JPanel();
			descAndButtonPanel.add(descPanel, BorderLayout.CENTER);
			descAndButtonPanel.add(buttonPanel, BorderLayout.EAST);
			descPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
			descPanel.add(currentSetTypeDescription, BorderLayout.CENTER);
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
			buttonPanel.add(deleteSetTypeButton);
			buttonPanel.add(changeSetTypeButton);
		}
		return descAndButtonPanel;
	}

	/**
	 * Gets the edit type panel.
	 * 
	 * @return panel to edit current Settype
	 */
	final JPanel getEditTypePanel() {
		if (editTypePanel == null) {
			editTypePanel = new JPanel(GUI.getStdBorderLayout());
			editTypePanel.add(getParametersOfTypeScroll(), BorderLayout.WEST);
			editTypePanel.add(getParameterSelection(), BorderLayout.EAST);
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
			editTypePanel.add(buttonPanel, BorderLayout.CENTER);
			buttonPanel.add(addInstanceToSetTypeButton);
			buttonPanel.add(removeInstanceFromSetTypeButton);
		}
		return editTypePanel;
	}

	/**
	 * Gets the parameter selection.
	 * 
	 * @return panel with elements for parameter selection
	 */
	final JPanel getParameterSelection() {
		if (parameterSelectionPanel == null) {
			parameterSelectionPanel = new JPanel(GUI.getStdBorderLayout());
			overallParametersScroll = new JScrollPane();
			overallParametersScroll.setViewportView(availableParametersList);
			parameterSelectionPanel.add(new JLabel("Available parameters:"),
			    BorderLayout.NORTH);
			parameterSelectionPanel.add(overallParametersScroll, BorderLayout.CENTER);
		}
		return parameterSelectionPanel;
	}

	/**
	 * Initializes of panel that shows the name of the current Settype.
	 * 
	 * @return panel to edit current Settype
	 */
	final JPanel getCurrentTypePanel() {
		if (currentTypePanel == null) {
			currentTypePanel = new JPanel();
			currentTypePanel.add(new JLabel("Name of Settype:"));
			currentTypePanel.add(currentSetTypeName);
		}
		return currentTypePanel;
	}

	/**
	 * This method initializes parametersOfTypeScroll.
	 * 
	 * @return javax.swing.JScrollPane
	 */
	final JPanel getParametersOfTypeScroll() {
		if (parametersOfTypePanel == null) {
			parametersOfTypePanel = new JPanel(GUI.getStdBorderLayout());
			parametersOfTypeScrollPane = new JScrollPane();
			parametersOfTypeScrollPane.setViewportView(parametersOfSetTypeList);
			parametersOfTypePanel.add(new JLabel("Defined parameters in Settype:"),
			    BorderLayout.NORTH);
			parametersOfTypePanel
			    .add(parametersOfTypeScrollPane, BorderLayout.CENTER);
		}
		return parametersOfTypePanel;
	}

}
