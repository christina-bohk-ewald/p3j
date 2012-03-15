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

import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * Default selection listener for projection trees.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ProjectionTreeSelectionListener implements TreeSelectionListener {

	/** The content panel. */
	private final JPanel contentPanel;

	/** The projection tree. */
	private final IProjectionTree projectionTree;

	/**
	 * Instantiates a new projection tree selection listener.
	 * 
	 * @param content
	 *          the content
	 * @param tree
	 *          the tree
	 */
	public ProjectionTreeSelectionListener(JPanel content, IProjectionTree tree) {
		contentPanel = content;
		projectionTree = tree;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath oldPath = e.getOldLeadSelectionPath();
		if (oldPath != null) {
			((ProjectionTreeNode<?>) oldPath.getLastPathComponent()).deselected();
		}
		contentPanel.removeAll();
		TreePath newPath = e.getNewLeadSelectionPath();
		if (newPath != null) {
			((ProjectionTreeNode<?>) newPath.getLastPathComponent()).selected(
			    newPath, contentPanel, projectionTree);
		}
		contentPanel.repaint();
		contentPanel.updateUI();
	}
}