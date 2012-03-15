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
package p3j.gui.panels.projections;

import james.core.util.misc.CSVReader;
import james.core.util.misc.Files;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;

import p3j.database.DatabaseFactory;
import p3j.gui.P3J;
import p3j.gui.dialogs.EditMatrixDialog;
import p3j.gui.dialogs.MoveAssignmentToSetDialog;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.gui.panels.matrices.EditMatrixPanel;
import p3j.misc.MatrixDimension;
import p3j.misc.Misc;
import p3j.misc.gui.GUI;
import p3j.misc.math.Matrix2D;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * Node that represents a {@link ParameterAssignment}.
 * 
 * Created: August 24, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ParameterAssignmentNode extends
    ProjectionTreeNode<ParameterAssignment> {

	/** Serialization ID. */
	private static final long serialVersionUID = -2859181977807670287L;

	/** Simple singleton to remember file import directory at runtime. */
	private static File importDirectory;

	/**
	 * Default constructor.
	 * 
	 * @param assignment
	 *          the parameter assignment to be represented
	 */
	public ParameterAssignmentNode(ParameterAssignment assignment) {
		super(assignment, assignment.toString());
	}

	@Override
	protected void refreshRepresentation() {
		setUserObject(getEntity().toString());
	}

	@Override
	public JPanel selected(final TreePath selectionPath,
	    final IProjectionTree projTree) {

		final JTextField name = new JTextField(getEntity().getName());
		final JTextArea description = new JTextArea(getEntity().getDescription());
		final JTextField probability = new JTextField(getEntity().getProbability()
		    + "");
		final JTextField deviation = new JTextField(getEntity().getDeviation() + "");
		final EditMatrixPanel previewPanel = new EditMatrixPanel(getEntity());

		final ParameterAssignmentNode node = this;

		// Apply changes
		JButton applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ParameterAssignment assignment = getEntity();
				assignment.setName(name.getText());
				assignment.setDescription(description.getText());
				assignment.setProbability(Double.parseDouble(probability.getText()));
				assignment.setDeviation(Double.parseDouble(deviation.getText()));
				setUserObject(assignment.toString());
				DatabaseFactory.getDatabaseSingleton().saveParameterAssignment(
				    assignment);
				projTree.refreshNode(node);
			}
		});

		// Edit matrix
		JButton editButton = new JButton("Edit Matrix");
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EditMatrixDialog dialog = new EditMatrixDialog(P3J.getInstance(),
				    getEntity());
				// Avoid displaying plot in preview when new window is opened, as it
				// will not be refreshed
				previewPanel.setSelectedIndex(0);
				dialog.setVisible(true);
				getContentPanel().repaint();
			}
		});

		// Import data from file
		JButton importButton = new JButton("Import from File");
		importButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				importDataFromFile();
			}
		});

		// Remove parameter assignment
		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GUI.printQuestion(P3J.getInstance(), "Remove '"
				    + getEntity().getName() + "'?",
				    "Do you really want to remove assignment '" + getEntity().getName()
				        + "'? This removal cannot be undone!")) {
					return;
				}
				Set mySet = getProjectionEntity(Set.class);
				mySet.removeParameterAssignment(getEntity());
				DatabaseFactory.getDatabaseSingleton().saveSet(mySet);
				projTree.removeNode(node);
			}
		});

		// Remove parameter assignment
		JButton moveButton = new JButton("Move");
		moveButton.setToolTipText("Moves assignment to another set.");
		moveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MoveAssignmentToSetDialog mats = new MoveAssignmentToSetDialog(
				    getEntity(), projTree, getProjectionEntity(Set.class),
				    getProjectionEntity(SetType.class));
				if (mats.isMeaningful()) {
					mats.setVisible(true);
				} else {
					GUI.printMessage(P3J.getInstance(), "No other set available",
					    "There is no set to move the assignment to. You have to create it first.");
				}
			}
		});

		// Layout
		List<JButton> buttons = createButtonList(applyButton, editButton,
		    importButton, removeButton, moveButton);

		PropertiesShowPanelFactory pspf = createPanel(name, description,
		    probability, deviation, previewPanel, buttons);

		return pspf.constructPanel();
	}

	/**
	 * Creates the button list.
	 * 
	 * @param addButtons
	 *          the buttons to be added
	 * 
	 * @return the list of buttons
	 */
	private List<JButton> createButtonList(JButton... addButtons) {
		List<JButton> buttons = new ArrayList<JButton>();
		for (JButton b : addButtons) {
			buttons.add(b);
		}
		return buttons;
	}

	/**
	 * Creates the panel.
	 * 
	 * @param name
	 *          the name field
	 * @param description
	 *          the description field
	 * @param probability
	 *          the probability field
	 * @param deviation
	 *          the deviation field
	 * @param previewPanel
	 *          the preview panel
	 * @param buttons
	 *          the list of buttons
	 * 
	 * @return the properties show panel factory
	 */
	private PropertiesShowPanelFactory createPanel(final JTextField name,
	    final JTextArea description, final JTextField probability,
	    final JTextField deviation, final EditMatrixPanel previewPanel,
	    List<JButton> buttons) {
		PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(buttons, 2);
		pspf.sep("General Information");
		pspf.app(Misc.GUI_LABEL_NAME, name);
		pspf.app(Misc.GUI_LABEL_DESCRIPTION, description, 2);
		pspf.app(Misc.GUI_LABEL_PROBABILITY, probability);
		if (getEntity().getParamInstance().getValueWidth() == MatrixDimension.YEARS) {
			pspf.app(Misc.GUI_LABEL_DEVIATION, deviation);
		}
		pspf.appPreview(previewPanel);
		return pspf;
	}

	/**
	 * Queries user which file to import.
	 */
	protected void importDataFromFile() {
		JFileChooser fileChooser = new JFileChooser(importDirectory);
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String ending = Files.getFileEnding(f).toLowerCase();
				return (ending.equals("txt") || ending.equals("csv") || ending
				    .equals("dat"));
			}

			@Override
			public String getDescription() {
				return "(*.csv,*.txt,*.dat) - Text Files containing numeric data as CSV";
			}
		});

		if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(P3J
		    .getInstance())) {
			File selection = fileChooser.getSelectedFile();
			importDirectory = selection.getParentFile();
			readDataFromFile(selection);
			getContentPanel().repaint();
		}
	}

	/**
	 * Reads data from given file and stores it within the matrix.
	 * 
	 * @param dataFile
	 *          the file containing the data
	 */
	private void readDataFromFile(File dataFile) {

		List<String[]> data = new CSVReader().read(dataFile.getAbsolutePath(),
		    false);
		Matrix2D value = getEntity().getMatrixValue();
		checkForRowMatch(data, value);

		boolean errorWasPrinted = false;

		for (int i = 0; i < Math.min(value.rows(), data.size()); i++) {
			String[] row = data.get(i);
			if (!errorWasPrinted) {
				errorWasPrinted = checkForColumnMatch(value, i, row);
			}
			double[] rowData = convertFromString(row);
			for (int j = 0; j < Math.min(value.columns(), rowData.length); j++) {
				value.setQuick(i, j, rowData[j]);
			}
		}
	}

	private double[] convertFromString(String[] row) {
		double[] result = new double[row.length];
		for (int i = 0; i < row.length; i++) {
			try {
				result[i] = Double.parseDouble(row[i]);
			} catch (NumberFormatException ex) {
				GUI.printErrorMessage(P3J.getInstance(), "Number format unknown",
				    "The file contains the value '" + row[i]
				        + "', which cannot be converted into a number.", ex);
			}
		}
		return result;
	}

	private void checkForRowMatch(List<String[]> data, Matrix2D value) {
		if (value.rows() != data.size()) {
			GUI.printErrorMessage(P3J.getInstance(), "Row number does not match",
			    "File has " + data.size() + " rows but matrix has " + value.rows()
			        + " - importing as much as possible.");
		}
	}

	private boolean checkForColumnMatch(Matrix2D value, int i, String[] row) {
		if (row.length == value.columns()) {
			return false;
		}
		GUI.printErrorMessage(
		    P3J.getInstance(),
		    "Column number does not match",
		    "In line"
		        + (i + 1)
		        + " the file contains "
		        + row.length
		        + " columns, but there are "
		        + value.columns()
		        + " matrix columns. Importing as much as possible and ignoring subsequent dimension mismatch.");
		return true;
	}

	@Override
	public void deselected() {
		super.deselected();
		// Save assignment (in case values have been changed)
		DatabaseFactory.getDatabaseSingleton().saveParameterAssignment(getEntity());
	}

}
