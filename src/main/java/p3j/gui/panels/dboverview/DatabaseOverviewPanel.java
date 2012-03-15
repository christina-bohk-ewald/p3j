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
package p3j.gui.panels.dboverview;

import james.SimSystem;

import java.awt.BorderLayout;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import p3j.database.DatabaseFactory;
import p3j.database.IP3MDatabase;
import p3j.gui.panels.AbstractNavigationPanel;
import p3j.gui.panels.projections.ProjectionTreeNode;
import p3j.pppm.ProjectionModel;

/**
 * Panel to display an overview of the database, e.g. to facilitate loading.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class DatabaseOverviewPanel extends AbstractNavigationPanel {

	/** Serialization ID. */
	private static final long serialVersionUID = 7431882091363418297L;

	/** Root of the database overview tree. */
	private DatabaseNode root;

	/** Reference to the database. */
	private IP3MDatabase db;

	/**
	 * Instantiates a new database overview panel.
	 * 
	 * @param contentP
	 *          the content panel
	 */
	public DatabaseOverviewPanel(JPanel contentP) {
		super(null, contentP);
		db = DatabaseFactory.getDatabaseSingleton();
		initTree();
		setLayout(new BorderLayout());
		add(getScrollPane(), BorderLayout.CENTER);
	}

	@Override
	protected final void initTree() {

		// We do not need to be re-initialized
		if (root != null) {
			return;
		}

		root = new DatabaseNode(DatabaseFactory.getDbConnData(), DatabaseFactory
		    .getDbConnData().getUrl());
		setTreeModel(new DefaultTreeModel(root));
		setTree(new JTree(getTreeModel()));
		getTree().setRootVisible(true);
		setScrollPane(new JScrollPane(getTree()));

		totalRefresh();

		getTree().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath oldPath = e.getOldLeadSelectionPath();
				if (oldPath != null) {
					((ProjectionTreeNode<?>) oldPath.getLastPathComponent()).deselected();
				}
				getContentPanel().removeAll();
				TreePath newPath = e.getNewLeadSelectionPath();
				if (newPath != null) {
					((ProjectionTreeNode<?>) newPath.getLastPathComponent()).selected(
					    newPath, getContentPanel(), null);
				}
				getContentPanel().repaint();
				getContentPanel().updateUI();
			}
		});
	}

	/**
	 * Refreshes database information.
	 */
	public final void totalRefresh() {
		for (ProjectionTreeNode<?> child : root.getChilds()) {
			getTreeModel().removeNodeFromParent(child);
		}

		List<ProjectionModel> projections = null;

		try {
			projections = db.getAllProjections();
		} catch (Exception ex) {
			SimSystem.report(Level.SEVERE,
			    "Could not read projections list from data base.", ex);
			projections = null;
		}

		if (projections == null) {
			return;
		}

		for (ProjectionModel projection : projections) {
			root.add(new OverviewProjectionNode(projection, projection.getName()));
		}

		getTreeModel().nodeStructureChanged(root);
		getTree().repaint();
	}

}
