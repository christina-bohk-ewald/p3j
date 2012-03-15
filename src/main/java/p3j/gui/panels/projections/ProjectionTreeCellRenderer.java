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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Cell renderer for an {@link IProjectionTree} instance.
 * 
 * Created: September 7, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ProjectionTreeCellRenderer extends DefaultTreeCellRenderer {

	/** Serialization ID. */
	private static final long serialVersionUID = 7794868165808019534L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
	    boolean selected, boolean expanded, boolean leaf, int row,
	    boolean hasFocus) {

		JLabel result = (JLabel) super.getTreeCellRendererComponent(tree, value,
		    selected, expanded, leaf, row, hasFocus);

		return result;
	}

}
