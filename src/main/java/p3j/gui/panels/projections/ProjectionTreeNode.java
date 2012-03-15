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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import p3j.misc.Misc;

/**
 * Super class of all nodes representing PPPM entities in the projection tree.
 * 
 * Created: August 24, 2008
 * 
 * @param <E>
 *          type of the represented entity
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ProjectionTreeNode<E> extends DefaultMutableTreeNode {

	/** Serialization ID. */
	private static final long serialVersionUID = 1987698685076459120L;

	/** Reference to the represented entity. */
	private final E entity;

	/** Class of the entity (necessary for lookup functions). */
	private final Class<?> entityClass;

	/** Panel holding the content overview for this node. */
	private JPanel contentPanel;

	/**
	 * Default constructor.
	 * 
	 * @param pppmEntity
	 *          the PPPM entity to be represented
	 * @param name
	 *          the name to be displayed
	 */
	public ProjectionTreeNode(E pppmEntity, String name) {
		super(name);
		entity = pppmEntity;
		entityClass = pppmEntity.getClass();
	}

	public E getEntity() {
		return entity;
	}

	/**
	 * Method that is called by the
	 * {@link javax.swing.event.TreeSelectionListener} when this node gets
	 * selected.
	 * 
	 * @param selectionPath
	 *          the path containing all ancestors
	 * @param generalPanel
	 *          the general panel to put the content on
	 * @param projTree
	 *          the projection tree
	 */
	public void selected(TreePath selectionPath, JPanel generalPanel,
	    IProjectionTree projTree) {
		contentPanel = selected(selectionPath, projTree);
		if (contentPanel != null) {
			generalPanel.add(contentPanel, BorderLayout.CENTER);
		}
	}

	/**
	 * Method that is called by
	 * {@link ProjectionTreeNode#selected(TreePath, JPanel, DefaultTreeModel, javax.swing.JTree)}
	 * originally. Implementations of {@link ProjectionTreeNode} might override
	 * either of the 'selected' methods, this one is more convenient as it stores
	 * the generated panel in an auxiliary variable and does not require to add
	 * the panel manually, it just needs to be returned.
	 * 
	 * @param selectionPath
	 *          the path containing all ancestors
	 * @param projTree
	 *          the projection tree
	 * @return panel with a node summary
	 */
	public JPanel selected(TreePath selectionPath, IProjectionTree projTree) {
		return null;
	}

	/**
	 * This is called when the node's representation might have changed. A reason
	 * for this could be changes from the outside. Default implementation is
	 * empty. This should simple re-invoke
	 * {@link DefaultMutableTreeNode#setUserObject(Object)}.
	 */
	protected void refreshRepresentation() {
	}

	/**
	 * Method that is called by the
	 * {@link javax.swing.event.TreeSelectionListener} when this node gets
	 * de-selected.
	 */
	public void deselected() {
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}

	/**
	 * Traverses current {@link javax.swing.JTree} towards its root. See
	 * {@link ProjectionTreeNode#getProjectionEntity(Class, ProjectionTreeNode)},
	 * which is called with this.
	 * 
	 * @param <T>
	 *          required type
	 * @param targetClass
	 *          class of required type
	 * @return found entity, or null
	 */
	public <T> T getProjectionEntity(Class<T> targetClass) {
		return getProjectionEntity(targetClass, this);
	}

	/**
	 * Traverses a {@link javax.swing.JTree} towards its root. Returns first
	 * entity of a given type that is found. Requires that all nodes are of type
	 * {@link ProjectionTreeNode}, returns null when encountering another class.
	 * 
	 * @param <T>
	 *          the type of the required entity
	 * @param targetClass
	 *          the class of the required entity
	 * @param sourceNode
	 *          the node from where to start searching upwards
	 * @return first entity of requested type on path towards root, or null if not
	 *         found
	 */
	@SuppressWarnings("unchecked")
	public <T> T getProjectionEntity(Class<T> targetClass,
	    ProjectionTreeNode<?> sourceNode) {
		if (Misc.checkClassEquality(sourceNode.getEntityClass(), targetClass)) {
			return (T) sourceNode.getEntity();
		}
		TreeNode parent = sourceNode.getParent();
		if (!(parent instanceof ProjectionTreeNode<?>)) {
			return null;
		}

		return getProjectionEntity(targetClass, (ProjectionTreeNode<?>) parent);
	}

	/**
	 * Gets a projection tree node of a specific type, which is on the path.
	 * 
	 * @param targetClass
	 *          the target class
	 * 
	 * @param <T>
	 *          the type of the target
	 * 
	 * @return the projection tree node on path
	 */
	@SuppressWarnings("unchecked")
	public <T> T getProjectionTreeNodeOnPath(Class<T> targetClass) {
		TreeNode treeNode = getParent();
		while (treeNode != null) {
			if (treeNode.getClass().equals(targetClass)) {
				return (T) treeNode;
			}
			treeNode = treeNode.getParent();
		}
		return null;
	}

	/**
	 * Looks for a certain object associated with a child node of type
	 * {@link ProjectionTreeNode}.
	 * 
	 * @param childEntity
	 *          the target entity
	 * @return the child node associated with this entity
	 */
	public ProjectionTreeNode<?> getChildWithEntity(Object childEntity) {
		if (getEntity() == childEntity) {
			return this;
		}
		for (int i = 0; i < this.getChildCount(); i++) {
			ProjectionTreeNode<?> child = (ProjectionTreeNode<?>) this.getChildAt(i);
			ProjectionTreeNode<?> result = child.getChildWithEntity(childEntity);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Retrieves all children of a given type. Traverses the tree in preorder.
	 * 
	 * @param <X>
	 *          the type of the entity
	 * @param entityType
	 *          the type of the entity
	 * @return list of child nodes representing such entities (which have to be
	 *         non-null)
	 */
	public <X> List<ProjectionTreeNode<X>> getChildsByType(Class<X> entityType) {
		return getChildsByType(new ArrayList<ProjectionTreeNode<X>>(), entityType);
	}

	@SuppressWarnings("unchecked")
	protected <X> List<ProjectionTreeNode<X>> getChildsByType(
	    List<ProjectionTreeNode<X>> list, Class<X> entityType) {
		if (getEntity() != null
		    && Misc.checkClassEquality(this.getEntityClass(), entityType)) {
			list.add((ProjectionTreeNode<X>) this);
		}
		for (int i = 0; i < this.getChildCount(); i++) {
			ProjectionTreeNode<?> node = (ProjectionTreeNode<?>) getChildAt(i);
			node.getChildsByType(list, entityType);
		}
		return list;
	}

	/**
	 * Refreshes nodes and all of its children recursively, by invoking
	 * {@link ProjectionTreeNode#refreshRepresentation()}.
	 * 
	 * @param treeModel
	 *          the tree model to be notified
	 */
	protected void refreshRecursively(DefaultTreeModel treeModel) {
		refreshRepresentation();
		treeModel.nodeChanged(this);
		for (int i = 0; i < getChildCount(); i++) {
			ProjectionTreeNode<?> node = (ProjectionTreeNode<?>) getChildAt(i);
			node.refreshRecursively(treeModel);
		}
	}

	/**
	 * Retrieves list of child nodes.
	 * 
	 * @return list of child nodes
	 */
	public List<ProjectionTreeNode<?>> getChilds() {
		List<ProjectionTreeNode<?>> result = new ArrayList<ProjectionTreeNode<?>>();
		for (int i = 0; i < getChildCount(); i++) {
			result.add((ProjectionTreeNode<?>) getChildAt(i));
		}
		return result;
	}

	/**
	 * Gets the user object string.
	 * 
	 * @return the object string
	 */
	public String getEntityLabel() {
		return this.getEntity().toString();
	}

}
