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

import java.util.ArrayList;
import java.util.List;

import net.sf.jeppers.grid.GridModel;
import net.sf.jeppers.grid.GridModelListener;
import p3j.misc.Misc;
import p3j.misc.math.Matrix2D;

/**
 * Model of {@link net.sf.jeppers.grid.JGrid} to integrate {@link Matrix2D}
 * objects with {@link net.sf.jeppers.grid.JGrid}. The original matrix is
 * transposed, i.e. columns are regarded as rows and vice versa.
 * 
 * Created on January 14, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class GridMatrixModel implements GridModel {

	/** List of model listeners. */
	private List<GridModelListener> gridModelListener = new ArrayList<GridModelListener>();

	/** Reference to matrix. */
	private Matrix2D matrix;

	/**
	 * Default constructor.
	 * 
	 * @param mat
	 *          the matrix to be edited
	 */
	public GridMatrixModel(Matrix2D mat) {
		matrix = mat;
	}

	@Override
	public void addGridModelListener(GridModelListener listener) {
		gridModelListener.add(listener);
	}

	@Override
	public int getColumnCount() {
		return matrix.rows();
	}

	@Override
	public int getRowCount() {
		return matrix.columns();
	}

	@Override
	public Object getValueAt(int row, int column) {
		return Misc.NUMBER_FORMAT.format(matrix.get(column, row));
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}

	@Override
	public void removeGridModelListener(GridModelListener listener) {
		gridModelListener.remove(listener);
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		if (!value.equals("")) {
			matrix.set(column, row, Misc.parseToDouble(value));
		} else {
			matrix.set(column, row, 0);
		}
	}

}