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
package p3j.gui.panels.matrices;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import net.sf.jeppers.grid.JGrid;
import net.sf.jeppers.grid.JScrollGrid;

import org.math.plot.Plot2DPanel;
import org.math.plot.Plot3DPanel;

import p3j.misc.gui.GUI;
import p3j.misc.math.Matrix2D;
import p3j.pppm.parameters.ParameterAssignment;

/**
 * Panel that contains a matrix to be edited.
 * 
 * Created: August 26, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class EditMatrixPanel extends JTabbedPane {

	/** Serialization ID. */
	private static final long serialVersionUID = -1060345291806349111L;

	/** The panel to hold the matrix editing components. */
	private JPanel panel;

	/** The panel to contain the plot results. */
	private JPanel plotPanel = new JPanel(new BorderLayout());

	/** Table to display the matrix. */
	private JGrid matrixGrid;

	/** Reference to matrix that should be edited. */
	private Matrix2D matrix;

	/** Parameter assignment to be edited. */
	private ParameterAssignment paramAssignment;

	// Definition of pop-up menu

	/** Cut pop-up menu. */
	private JMenuItem cutMenu = new JMenuItem("Cut");
	{
		cutMenu.setIcon(new ImageIcon(this.getClass().getResource(
		    "/p3j/icons/cut.gif")));
		cutMenu.setAccelerator(GridBehaviourAdapter.getCutKeyStroke());
	}

	/** Copy pop-up menu. */
	private JMenuItem copyMenu = new JMenuItem("Copy");
	{
		copyMenu.setIcon(new ImageIcon(this.getClass().getResource(
		    "/p3j/icons/copy.gif")));
		copyMenu.setAccelerator(GridBehaviourAdapter.getCopyKeyStroke());
	}

	/** Paste pop-up menu. */
	private JMenuItem pasteMenu = new JMenuItem("Paste");
	{
		pasteMenu.setIcon(new ImageIcon(this.getClass().getResource(
		    "/p3j/icons/paste.gif")));
		pasteMenu.setAccelerator(GridBehaviourAdapter.getPasteKeyStroke());
	}

	/**
	 * Pop up - Menu for cut/copy/paste.
	 */
	private final JPopupMenu popupMenu = new JPopupMenu();
	{
		popupMenu.add(copyMenu);
		popupMenu.add(pasteMenu);
		popupMenu.add(cutMenu);
	}

	/**
	 * Default constructor.
	 * 
	 * @param pAssignment
	 *          the parameter assignment to be edited
	 */
	public EditMatrixPanel(ParameterAssignment pAssignment) {
		panel = new JPanel(GUI.getStdBorderLayout());
		matrix = pAssignment.getMatrixValue();
		paramAssignment = pAssignment;
		initialize();
	}

	/**
	 * This method initializes the panel.
	 */
	private void initialize() {

		// Careful: the matrix grid is initialized to view a *transposed* matrix!
		// TODO: Remove double-transposition of matrices.
		matrixGrid = new JGrid(matrix.columns(), matrix.rows());
		matrixGrid.setGridModel(new GridMatrixModel(matrix));

		matrixGrid.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					Point p = new Point(e.getX(), e.getY());
					int startRow = matrixGrid.rowAtPoint(p);
					int startCol = matrixGrid.columnAtPoint(p);
					matrixGrid.getSelectionModel().setSelectionRange(startRow, startCol,
					    startRow, startCol);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		GridBehaviourAdapter behaviourAdapter = new GridBehaviourAdapter(matrixGrid);
		cutMenu.addActionListener(behaviourAdapter);
		cutMenu.setActionCommand("Cut");
		copyMenu.addActionListener(behaviourAdapter);
		copyMenu.setActionCommand("Copy");
		pasteMenu.addActionListener(behaviourAdapter);
		pasteMenu.setActionCommand("Paste");

		JScrollGrid scrollGrid = new JScrollGrid(matrixGrid);

		scrollGrid.setColumnHeader(new GridHeader(matrixGrid, matrix.getRowLabel(),
		    true, true));
		scrollGrid.setRowHeader(new GridHeader(matrixGrid, matrix.getColumnLabel(),
		    false, false));

		panel.add(scrollGrid, BorderLayout.CENTER);
		plotPanel.add(getPlotPanel(), BorderLayout.CENTER);

		add("Data", panel);
		add("Plot", plotPanel);
	}

	@Override
	public void setSelectedIndex(int index) {
		// Creates data plot if necessary.
		if (index == 1) {
			plotPanel.removeAll();
			plotPanel.add(getPlotPanel(), BorderLayout.CENTER);
		}
		super.setSelectedIndex(index);
	}

	/**
	 * Creates either a 2D- or a 3D diagram.
	 * 
	 * @return the plot component
	 */
	private Component getPlotPanel() {
		if (matrix.columns() == 1) {
			return create2DChartFromColumn();
		}
		if (matrix.rows() == 1) {
			return create2DChartFromRow();
		}
		return create3DDiagram();
	}

	private Component create3DDiagram() {
		double[] x = new double[matrix.rows()];
		double[] y = new double[matrix.columns()];
		double[][] matVals = new double[matrix.rows()][matrix.columns()];
		for (int i = 0; i < matrix.rows(); i++) {
			x[i] = i;
		}
		for (int i = 0; i < matrix.columns(); i++) {
			y[i] = i;
		}
		for (int i = 0; i < matrix.rows(); i++) {
			for (int j = 0; j < matrix.columns(); j++) {
				matVals[i][j] = matrix.getQuick(i, j);
			}
		}
		Plot3DPanel plot3DPanel = new Plot3DPanel();
		plot3DPanel.setAxeLabel(0, matrix.getColumnLabel());
		plot3DPanel.setAxeLabel(1, matrix.getRowLabel());
		plot3DPanel.addGridPlot("twast", y, x, matVals);
		return plot3DPanel;
	}

	private Component create2DChartFromRow() {
		Plot2DPanel plot2DPanel = new Plot2DPanel();
		double[][] plotData = new double[1][matrix.columns()];
		for (int i = 0; i < matrix.columns(); i++) {
			plotData[0][i] = matrix.getQuick(0, i);
		}
		plot2DPanel.addLinePlot(paramAssignment.getName(), plotData);
		plot2DPanel.setAxeLabel(0, matrix.getColumnLabel());
		return plot2DPanel;
	}

	private Component create2DChartFromColumn() {
		Plot2DPanel plot2DPanel = new Plot2DPanel();
		double[][] plotData = new double[1][matrix.rows()];
		for (int i = 0; i < matrix.rows(); i++) {
			plotData[0][i] = matrix.getQuick(i, 0);
		}
		plot2DPanel.addLinePlot(paramAssignment.getName(), plotData);
		plot2DPanel.setAxeLabel(0, matrix.getRowLabel());
		return plot2DPanel;
	}
}
