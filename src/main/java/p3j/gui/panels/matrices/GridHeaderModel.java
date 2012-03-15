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

import net.sf.jeppers.grid.AbstractGridModel;
import net.sf.jeppers.grid.RulerModel;

/**
 * 
 * A model implementation for grid headers. Applies to both row and column
 * headers.
 * 
 * Created on January 14, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class GridHeaderModel extends AbstractGridModel {

	/** Prefix to be displayed in all columns. */
	private String prefix;

	/** Flag that indicates to start enumerating with one. */
	private boolean startsWith1;

	/** Flag that indicates that indices should be suppressed. */
	private boolean surpressIndex;

	/** True if this is a column header. */
	private boolean isColumn = true;

	/** Reference to ruler model. */
	private RulerModel ruler;

	/**
	 * Default constructor.
	 * 
	 * @param columnModel
	 *          column model
	 * @param pref
	 *          prefix
	 * @param sw1
	 *          flag to indicate whether enumeration starts with 1
	 * @param isCol
	 *          true, if this header model is for a column
	 */
	public GridHeaderModel(RulerModel columnModel, String pref, boolean sw1,
	    boolean isCol) {
		ruler = columnModel;
		this.prefix = pref;
		this.surpressIndex = (this.prefix.length() == 0);
		this.startsWith1 = sw1;
		this.isColumn = isCol;
	}

	@Override
	public Object getValueAt(int row, int column) {
		return this.surpressIndex ? "" : prefix
		    + ((isColumn ? column : row) + (startsWith1 ? 1 : 0));
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
	}

	@Override
	public int getRowCount() {
		return ruler.getCount();
	}

	@Override
	public int getColumnCount() {
		return ruler.getCount();
	}
}
