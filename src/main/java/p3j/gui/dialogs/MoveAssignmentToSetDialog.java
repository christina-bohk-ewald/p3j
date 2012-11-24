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
package p3j.gui.dialogs;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import p3j.database.DatabaseFactory;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.gui.panels.projections.IProjectionTree;
import p3j.gui.panels.projections.ProjectionTreeNode;
import p3j.misc.gui.GUI;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * Dialog to choose a new set for a parameter assignment.
 * 
 * @see Set
 * @see ParameterAssignment
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class MoveAssignmentToSetDialog extends ProjectionDialog {

  /** Serialization ID. */
  private static final long serialVersionUID = -1158858943985692727L;

  /** The parameter assignment to be moved. */
  private final ParameterAssignment assignment;

  /** The set the parameter assignment is currently associated with. */
  private final Set oldSet;

  /** The current Settype. */
  private final SetType setType;

  /** The projection tree. */
  private final IProjectionTree projectionTree;

  /** The select box to choose the destination set. */
  private final JComboBox<Set> desitnationSetSelector = new JComboBox<Set>();

  /**
   * Instantiates a new move assignment to set dialog.
   * 
   * @param parameterAssignment
   *          the parameter assignment
   * @param projTree
   *          the projection tree
   * @param assignmentSet
   *          the set the assignment is currently stored in
   * @param currentSetType
   *          the current Settype
   * @param node
   *          the node of the current set
   */
  public MoveAssignmentToSetDialog(ParameterAssignment parameterAssignment,
      IProjectionTree projTree, Set assignmentSet, SetType currentSetType) {
    assignment = parameterAssignment;
    oldSet = assignmentSet;
    projectionTree = projTree;
    setType = currentSetType;
    initUI();
  }

  @Override
  protected void okAction() {
    Object item = desitnationSetSelector.getSelectedItem();
    if (!(item instanceof Set)) {
      return;
    }
    Set destSet = (Set) item;
    oldSet.removeParameterAssignment(assignment);
    destSet.addParameterAssignment(assignment);
    DatabaseFactory.getDatabaseSingleton().saveSet(oldSet);
    DatabaseFactory.getDatabaseSingleton().saveSet(destSet);
    projectionTree.totalRefresh();
    ProjectionTreeNode<?> selectNode = ((ProjectionTreeNode<?>) projectionTree
        .getTreeModel().getRoot()).getChildWithEntity(assignment
        .getParamInstance());
    if (selectNode.getChildCount() > 0) {
      selectNode = (ProjectionTreeNode<?>) selectNode.getChildAt(0);
    }
    projectionTree.selectNode(selectNode);
    setVisible(false);
  }

  /**
   * Initialize the user interface.
   */
  private void initUI() {
    setTitle("Moving a parameter assignment");
    setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    setModal(true);
    GUI.centerOnScreen(this);

    List<Set> sets = new ArrayList<Set>(setType.getSets());
    sets.remove(oldSet);

    for (Set s : sets) {
      desitnationSetSelector.addItem(s);
    }

    PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(
        getButtons(), 1);
    pspf.sep("Assignment '" + assignment.getName() + "'");
    pspf.app("Move to set:", desitnationSetSelector);
    getContentPane().add(pspf.constructPanel());
  }

  /**
   * Checks if showing the dialog is meaningful.
   * 
   * @return true, if showing is meaningful because there is at least one other
   *         set
   */
  public boolean isMeaningful() {
    return desitnationSetSelector.getItemCount() > 0;
  }
}
