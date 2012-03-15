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
package p3j.gui.panels.results;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import p3j.database.DatabaseFactory;
import p3j.experiment.results.ResultsOfTrial;
import p3j.gui.panels.projections.ProjectionTreeCellRenderer;
import p3j.gui.panels.projections.ProjectionTreePanel;
import p3j.gui.panels.projections.ProjectionTreeSelectionListener;
import p3j.pppm.ProjectionModel;

/**
 * Panel that displays and controls the result tree.
 * 
 * Created: August 23, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class ResultTreePanel extends ProjectionTreePanel {

	/** Serialization ID. */
	private static final long serialVersionUID = 3722491328298367310L;

	/** The root of the results tree. */
	private ResultTreeRoot root;

	/**
	 * Default constructor.
	 * 
	 * @param proj
	 *          the projection which has results to be displayed
	 * @param content
	 *          the content
	 */
	public ResultTreePanel(ProjectionModel proj, JPanel content) {
		super(proj, content);
	}

	@Override
	protected void initTree() {
		root = new ResultTreeRoot(getProjectionModel());
		setTreeModel(new DefaultTreeModel(root));
		setTree(new JTree(getTreeModel()));
		getTree().setCellRenderer(new ProjectionTreeCellRenderer());
		setScrollPane(new JScrollPane(getTree()));

		totalRefresh();

		getTree().addTreeSelectionListener(
		    new ProjectionTreeSelectionListener(getContentPanel(), this));
	}

	/**
	 * Refreshes results tree.
	 */
	@Override
	public void totalRefresh() {

		// Remove old children from root
		for (int i = root.getChildCount(); i > 0; i--) {
			getTreeModel().removeNodeFromParent(
			    (MutableTreeNode) root.getChildAt(i - 1));
		}

		// Add new children
		List<ResultsOfTrial> results = DatabaseFactory.getDatabaseSingleton()
		    .getAllResults(getProjectionModel());
		for (int i = 0; i < results.size(); i++) {
			root.add(new ResultTreeNode(results.get(i), i + 1));
		}

		getTreeModel().nodeStructureChanged(root);
		getTree().repaint();
	}

}
