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
package p3j.gui.misc;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import p3j.gui.dialogs.EditSetsDialog;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.Set;

/**
 * Renderer of cells for the parameter list.
 * 
 * Created on March 12, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
@Deprecated
public class ParameterListCellRenderer implements ListCellRenderer {

	/** Reference to edit sets dialog. */
	private EditSetsDialog dialog;

	/** Label to display. */
	private JLabel label = new JLabel();
	{
		label.setOpaque(true);
	}

	/**
	 * Default constructor.
	 * 
	 * @param dlg
	 *          reference to dialog that edits the sets
	 */
	public ParameterListCellRenderer(EditSetsDialog dlg) {
		this.dialog = dlg;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
	    int index, boolean isSelected, boolean cellHasFocus) {

		ParameterInstance param = (ParameterInstance) value;

		label.setText(param.toString());

		label.setForeground(isSelected ? list.getSelectionForeground() : list
		    .getForeground());
		label.setBackground(isSelected ? list.getSelectionBackground() : list
		    .getBackground());

		Set currentSet = dialog.getCurrentSet();

		if (currentSet.getNumberOfAssignments(param) == 0) {
			label.setForeground(Color.GRAY);
		}

		return label;
	}
}
