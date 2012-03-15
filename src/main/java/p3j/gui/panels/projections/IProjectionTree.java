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

import java.util.Collection;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import p3j.pppm.sets.Set;

/**
 * 
 * Interface for all {@link ProjectionTreeNode} entities to manipulate and
 * notify their projection tree.
 * 
 * Created: August 27, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public interface IProjectionTree {

	/**
	 * Get the model of the tree. Useful to propagate updates etc.
	 * 
	 * @return model of the projection tree
	 */
	DefaultTreeModel getTreeModel();

	/**
	 * Get the actual tree component. Useful for selection updates etc.
	 * 
	 * @return actual tree component
	 */
	JTree getTree();

	/**
	 * Refresh a single node in the tree.
	 * 
	 * @param node
	 *          the node to be refreshed
	 */
	void refreshNode(ProjectionTreeNode<?> node);

	/**
	 * Refreshed the sub-structures of the node in the tree.
	 * 
	 * @param node
	 *          the node of which the sub-structures shall be refreshed
	 */
	void refreshNodeSubStructure(ProjectionTreeNode<?> node);

	/**
	 * Notifies tree that the given node as a new child at the given index.
	 * 
	 * @param node
	 *          node with new child
	 * @param childIndex
	 *          index of the new child
	 */
	void nodeAdded(ParameterInstanceNode node, int childIndex);

	/**
	 * Selects a given node.
	 * 
	 * @param node
	 *          the node to be selected
	 */
	void selectNode(ProjectionTreeNode<?> node);

	/**
	 * Recursively refreshes the node names of node in the sub-tree below the
	 * given node.
	 * 
	 * @param node
	 *          the node of which the subtree shall be refreshed
	 */
	void recursiveRefresh(ProjectionTreeNode<?> node);

	/**
	 * Removes given node.
	 * 
	 * @param node
	 *          the node to be removed
	 */
	void removeNode(ProjectionTreeNode<?> node);

	/**
	 * Removes all nodes that represent the objects given in the collection.
	 * 
	 * @param node
	 *          the node from which to start looking
	 * @param repRemovObjects
	 *          collection of the represented object of the nodes to be removed
	 */
	void removeNodes(ProjectionTreeNode<?> node,
	    Collection<? extends Object> repRemovObjects);

	/**
	 * Cleans the tree from unnecessary leaves. This could be an empty generation
	 * etc.
	 */
	void cleanTree();

	/**
	 * Creates all substructures for displaying a set.
	 * 
	 * @param stNode
	 *          the Settype node the set belongs to
	 * @param set
	 *          the set for which the structure shall be created
	 * @return the set node at the top of the subtree
	 */
	SetNode createNewSetStructure(SetTypeNode stNode, Set set);

	/**
	 * Totally refreshes the projection tree. All nodes beneath the
	 * {@link ProjectionNode} are re-created and re-initialized.
	 */
	void totalRefresh();

}
