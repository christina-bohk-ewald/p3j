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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import p3j.gui.panels.AbstractNavigationPanel;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.parameters.Population;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * Panel that displays a {@link JTree} in a {@link java.awt.ScrollPane} and
 * initializes them properly.
 * 
 * Created: August 23, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ProjectionTreePanel extends AbstractNavigationPanel implements
    IProjectionTree {

	/** Serialization ID. */
	private static final long serialVersionUID = 1648793294436076701L;

	/** Root of the projection tree. */
	private ProjectionNode pTreeRoot;

	/**
	 * The Constructor.
	 * 
	 * @param proj
	 *          the projection model
	 * @param content
	 *          the content pane (to display editing panels)
	 */
	public ProjectionTreePanel(ProjectionModel proj, JPanel content) {
		super(proj, content);
	}

	/**
	 * Initializes projection tree.
	 */
	@Override
	protected void initTree() {
		pTreeRoot = new ProjectionNode(getProjectionModel());
		setTreeModel(new DefaultTreeModel(pTreeRoot));
		setTree(new JTree(getTreeModel()));
		getTree().setCellRenderer(new ProjectionTreeCellRenderer());
		setScrollPane(new JScrollPane(getTree()));

		totalRefresh();
		getTree().addTreeSelectionListener(
		    new ProjectionTreeSelectionListener(getContentPanel(), this));
		getTree().setSelectionPath(new TreePath(pTreeRoot));
	}

	/**
	 * Adds sub tree for {@link SetType}.
	 * 
	 * @param root
	 *          the root of this sub tree
	 * @param setType
	 *          the Settype to be represented
	 */
	protected void addSetTypeSubTree(DefaultMutableTreeNode root, SetType setType) {
		SetTypeNode stNode = new SetTypeNode(setType);
		root.add(stNode);
		for (p3j.pppm.sets.Set set : setType.getSets()) {
			addSetSubTree(stNode, set, setType);
		}
	}

	/**
	 * Adds sub tree for {@link Set}.
	 * 
	 * @param root
	 *          the root of this sub tree
	 * @param set
	 *          the set to be represented
	 * @param setType
	 *          the Settype of this set
	 * @return the set node at the top of the subtree
	 */
	protected SetNode addSetSubTree(SetTypeNode root, Set set, SetType setType) {
		SetNode sNode = new SetNode(set);
		root.add(sNode);

		Map<Integer, List<ParameterInstance>> generationMap = new HashMap<Integer, List<ParameterInstance>>();

		for (ParameterInstance instance : setType.getDefinedParameters()) {
			int generation = retrieveGeneration(instance);
			List<ParameterInstance> genInstList = generationMap.get(generation);
			if (genInstList == null) {
				genInstList = new ArrayList<ParameterInstance>();
				generationMap.put(generation, genInstList);
			}
			genInstList.add(instance);
		}

		List<Integer> genIndices = new ArrayList<Integer>(generationMap.keySet());
		Collections.sort(genIndices);

		for (Integer genIndex : genIndices) {
			if (genIndex < 0) {
				addPopulationsSubTree(sNode, set, generationMap.get(genIndex));
			} else {
				addGenerationSubTree(sNode, generationMap.get(genIndex));
			}
		}

		return sNode;
	}

	/**
	 * Retrieves generation for given instance. Generation-0 instances of
	 * emigrants and immigrants are added to their base populations (i.e. -1 is
	 * returned).
	 * 
	 * @param instance
	 *          the instance of interest
	 * @return its generation
	 */
	private int retrieveGeneration(ParameterInstance instance) {
		int generation = instance.getGeneration();
		if (instance.getParameter().getPopulation() != Population.NATIVES
		    && instance.getGeneration() == 0) {
			generation = -1;
		}
		return generation;
	}

	/**
	 * Adds sub-tree for a generation. A generation is the set of all instances
	 * that define parameters for a specific generation.
	 * 
	 * @param root
	 *          the root of this sub tree
	 * @param instances
	 *          the list of parameter instances of this generation
	 */
	protected void addGenerationSubTree(SetNode root,
	    List<ParameterInstance> instances) {
		GenerationNode gNode = new GenerationNode(instances);
		root.add(gNode);
		addPopulationsSubTree(gNode, root.getEntity(), instances);
	}

	/**
	 * Adds sub tree for populations.
	 * 
	 * @param root
	 *          the root of this sub tree
	 * @param set
	 *          the set containing the assumptions for the instances
	 * @param instances
	 *          list of instances
	 */
	protected void addPopulationsSubTree(ProjectionTreeNode<?> root, Set set,
	    List<ParameterInstance> instances) {

		Map<Population, List<ParameterInstance>> popMap = new HashMap<Population, List<ParameterInstance>>();
		for (ParameterInstance instance : instances) {
			List<ParameterInstance> popInsts = popMap.get(instance.getParameter()
			    .getPopulation());
			if (popInsts == null) {
				popInsts = new ArrayList<ParameterInstance>();
				popMap.put(instance.getParameter().getPopulation(), popInsts);
			}
			popInsts.add(instance);
		}

		// Use always the same order of sub-populations
		List<Population> populations = new ArrayList<Population>(popMap.keySet());
		Collections.sort(populations, new Comparator<Population>() {
			@Override
			public int compare(Population o1, Population o2) {
				return o1.compareTo(o2);
			}
		});

		for (Population population : populations) {
			List<ParameterInstance> popInst = popMap.get(population);
			PopulationNode popNode = new PopulationNode(population);
			root.add(popNode);
			addInstancesSubTree(popNode, set, popInst);
		}
	}

	/**
	 * Adds sub tree for {@link ParameterInstance} entities.
	 * 
	 * @param root
	 *          the root of the sub tree
	 * @param set
	 *          the set containing the assumptions for the instances
	 * @param instances
	 *          the instances that will be nodes
	 */
	protected void addInstancesSubTree(PopulationNode root, Set set,
	    List<ParameterInstance> instances) {
		for (ParameterInstance instance : instances) {
			ParameterInstanceNode instNode = new ParameterInstanceNode(instance);
			root.add(instNode);
			addAssumptionsSubTree(instNode, set, instance);
		}
	}

	/**
	 * Adds sub tree for {@link ParameterAssignment} entities.
	 * 
	 * @param root
	 *          the root node for this sub tree
	 * @param set
	 *          the set in which the assignments are defined
	 * @param instance
	 *          the instance to which the assignments belong
	 */
	protected void addAssumptionsSubTree(ParameterInstanceNode root, Set set,
	    ParameterInstance instance) {
		List<ParameterAssignment> assignments = new ArrayList<ParameterAssignment>(
		    set.getParameterAssignments(instance).getAssignments());

		// Sort assignments alphabetically
		Collections.sort(assignments, new Comparator<ParameterAssignment>() {
			@Override
			public int compare(ParameterAssignment o1, ParameterAssignment o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		for (ParameterAssignment assignment : assignments) {
			root.add(new ParameterAssignmentNode(assignment));
		}

	}

	@Override
	public void refreshNode(ProjectionTreeNode<?> node) {
		getTreeModel().nodeChanged(node);
	}

	@Override
	public void refreshNodeSubStructure(ProjectionTreeNode<?> node) {
		getTreeModel().nodeStructureChanged(node);

	}

	@Override
	public void nodeAdded(ParameterInstanceNode node, int index) {
		getTreeModel().nodesWereInserted(node, new int[] { index });
	}

	@Override
	public void selectNode(ProjectionTreeNode<?> node) {
		selectProjectionTreePath(new TreePath(getTreeModel().getPathToRoot(node)));
	}

	@Override
	public void recursiveRefresh(ProjectionTreeNode<?> node) {
		node.refreshRecursively(getTreeModel());
		getTree().repaint();
	}

	@Override
	public void removeNode(ProjectionTreeNode<?> node) {
		TreePath path = new TreePath(getTreeModel().getPathToRoot(node.getParent()));
		getTreeModel().removeNodeFromParent(node);
		selectProjectionTreePath(path);
	}

	/**
	 * Selects node at the end of the given path in the projection tree.
	 * 
	 * @param path
	 *          the path to be selected
	 */
	void selectProjectionTreePath(TreePath path) {
		getTree().setSelectionPath(path);
		getTree().scrollPathToVisible(path);
	}

	@Override
	public void removeNodes(ProjectionTreeNode<?> node,
	    Collection<? extends Object> repRemovObjects) {
		if (repRemovObjects.contains(node.getEntity())) {
			getTreeModel().removeNodeFromParent(node);
		} else {
			for (ProjectionTreeNode<?> child : node.getChilds()) {
				removeNodes(child, repRemovObjects);
			}
		}
	}

	@Override
	public void cleanTree() {
		cleanTree((ProjectionTreeNode<?>) getTreeModel().getRoot());
	}

	/**
	 * Recursively cleans up the tree.
	 * 
	 * @param node
	 *          the current node
	 */
	protected void cleanTree(ProjectionTreeNode<?> node) {
		for (ProjectionTreeNode<?> child : node.getChilds()) {
			cleanTree(child);
		}
		if (node.getChildCount() == 0
		    && (node instanceof PopulationNode || node instanceof GenerationNode)) {
			getTreeModel().removeNodeFromParent(node);
		}
	}

	@Override
	public SetNode createNewSetStructure(SetTypeNode stNode, Set set) {
		return addSetSubTree(stNode, set, stNode.getEntity());
	}

	@Override
	public void totalRefresh() {

		// Remove children from root
		for (ProjectionTreeNode<?> child : pTreeRoot.getChilds()) {
			getTreeModel().removeNodeFromParent(child);
		}

		// Add new children
		addSetTypeSubTree(pTreeRoot, getProjectionModel().getDefaultSetType());
		for (SetType type : getProjectionModel().getUserDefinedTypes()) {
			addSetTypeSubTree(pTreeRoot, type);
		}

		getTreeModel().nodeStructureChanged(pTreeRoot);
		getTree().repaint();
	}
}
