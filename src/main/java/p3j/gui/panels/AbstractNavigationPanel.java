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
package p3j.gui.panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import p3j.pppm.ProjectionModel;

/**
 * Super class for navigation panels.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public abstract class AbstractNavigationPanel extends JPanel {

	/** Serialization ID. */
	private static final long serialVersionUID = -8802994657383849297L;

	/** Tree to display current structure of the projection. */
	private JTree tree;

	/** The model of the projection tree. */
	private DefaultTreeModel treeModel;

	/** Scroll pane for the tree. */
	private JScrollPane scrollPane;

	/** The projection to be edited. */
	private ProjectionModel projection;

	/** Panel from {@link import p3j.gui.P3J} that holds the content. */
	private final JPanel contentPanel;

	/**
	 * Instantiates a new abstract navigation panel.
	 * 
	 * @param projectionModel
	 *          the projectionModel
	 * @param content
	 *          the content panel
	 */
	public AbstractNavigationPanel(ProjectionModel projectionModel, JPanel content) {
		projection = projectionModel;
		contentPanel = content;
		if (projection == null) {
			return;
		}
		setProjection(projection);
	}

	/**
	 * Sets the new projection model to be displayed.
	 * 
	 * @param projMod
	 *          the new projection model
	 */
	public final void setProjection(ProjectionModel projMod) {
		projection = projMod;
		removeAll();
		initTree();
		setLayout(new BorderLayout());
		if (getScrollPane() != null) {
			add(getScrollPane(), BorderLayout.CENTER);
		}
	}

	/**
	 * Initializes tree (after projection changed, for example).
	 */
	protected abstract void initTree();

	public final JTree getTree() {
		return tree;
	}

	public void setTree(JTree tree) {
		this.tree = tree;
	}

	public final DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(DefaultTreeModel treeModel) {
		this.treeModel = treeModel;
	}

	public final JScrollPane getScrollPane() {
		return scrollPane;
	}

	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	public ProjectionModel getProjectionModel() {
		return projection;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}
}
