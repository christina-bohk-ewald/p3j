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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;

import p3j.database.DatabaseFactory;
import p3j.gui.P3J;
import p3j.gui.misc.SubNodeSummary;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.misc.gui.GUI;
import p3j.misc.math.Matrix2D;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterAssignmentSet;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.Set;

/**
 * Node that represents a {@link ParameterInstance}.
 * 
 * Created: August 24, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ParameterInstanceNode extends
    ProjectionTreeNode<ParameterInstance> {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 1069251581222368972L;

  /**
   * Name for new assignment and title for the button to create one.
   */
  public static final String NEW_ASSIGNMENT = "New Assignment";

  /**
   * Default constructor.
   * 
   * @param instance
   *          the instance that is represented by this node
   */
  public ParameterInstanceNode(ParameterInstance instance) {
    super(instance, getDisplayName(instance));
  }

  @Override
  public JPanel selected(final TreePath selectionPath,
      final IProjectionTree projTree) {

    final ParameterInstanceNode node = this;
    final Set set = getProjectionEntity(Set.class);

    JButton adjustProbability = new JButton("Adjust Probability");

    // Create new assignment
    JButton newAssignment = new JButton(NEW_ASSIGNMENT);
    newAssignment.addActionListener(new ParamAssignmentCreator(node, projTree));

    List<JButton> buttons = new ArrayList<JButton>();
    buttons.add(adjustProbability);
    buttons.add(newAssignment);
    PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(buttons, 1);
    pspf.sep("General Information");
    pspf.app("Population:", getEntity().getParameter().getPopulation());
    pspf.app("Parameter:", getDisplayName(getEntity()));
    pspf.app("Descendant Generation:", getEntity().getGeneration() <= 0 ? "-"
        : getEntity().getGeneration());
    pspf.app("Matrix width:", getEntity().getValueWidth().getDimension());
    pspf.app("Matrix height:", getEntity().getValueHeight().getDimension());

    ParameterAssignmentSet assignmentSet = set
        .getParameterAssignments(getEntity());
    java.util.Set<ParameterAssignment> assignments = assignmentSet
        .getAssignments();
    int size = assignments.size();
    pspf.app("Assignments:", size);
    final JLabel probSum = new JLabel(assignmentSet.getProbabilitySum() + "");
    pspf.app("Probability sum:", probSum);
    pspf.appPreview(new SubNodeSummary<ParameterAssignment>(this, projTree,
        ParameterAssignment.class));

    // Adjust probability
    adjustProbability.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ParameterAssignmentSet paSet = set.getParameterAssignments(getEntity());
        paSet.adjustProbability();
        DatabaseFactory.getDatabaseSingleton().saveSet(set);
        probSum.setText(paSet.getProbabilitySum() + "");
        getContentPanel().repaint();
        projTree.recursiveRefresh(node);
      }
    });

    return pspf.constructPanel();
  }

  /**
   * Gets the display name. In case this refers to direct
   * emigration/immigration, the name to displayed is prefixed with 'Direct '.
   * 
   * @param instance
   *          the instance
   * 
   * @return the display name
   */
  public static String getDisplayName(ParameterInstance instance) {
    return instance.getParameter().getName();
  }

}

/**
 * 
 * Creates a new parameter assignment.
 * 
 * Created: August 26, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
class ParamAssignmentCreator implements ActionListener {

  /** Tree projection tree. */
  private final IProjectionTree projectionTree;

  /** Node holding parameter instance. */
  private final ParameterInstanceNode node;

  /**
   * Default constructor.
   * 
   * @param piNode
   *          current node
   * @param projTree
   *          the projection tree (for changing the selection, etc.)
   */
  ParamAssignmentCreator(ParameterInstanceNode piNode, IProjectionTree projTree) {
    this.projectionTree = projTree;
    this.node = piNode;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Set set = node.getProjectionEntity(Set.class);
    if (set == null) {
      return;
    }
    try {
      ParameterAssignment newAssignment = DatabaseFactory
          .getDatabaseSingleton().newParameterAssignment(
              node.getEntity(),
              ParameterInstanceNode.NEW_ASSIGNMENT,
              "",
              0.0,
              0.0,
              // TODO: Matrices should be stored just the other way round, this
              // would be more 'natural' (take care: this requires to remove
              // automatic transposition in the UI)
              new Matrix2D(new double[node.getEntity().getValueWidth()
                  .getDimension()][node.getEntity().getValueHeight()
                  .getDimension()],
                  node.getEntity().getValueWidth().getLabel(), node.getEntity()
                      .getValueHeight().getLabel()));
      set.addParameterAssignment(newAssignment);
      DatabaseFactory.getDatabaseSingleton().saveSet(set);
      ParameterAssignmentNode child = new ParameterAssignmentNode(newAssignment);
      node.add(child);
      projectionTree.nodeAdded(node,
          set.getParameterAssignments(node.getEntity()).size() - 1);
      projectionTree.selectNode(child);
    } catch (Exception ex) {
      GUI.printErrorMessage(P3J.getInstance(), "Error: Matrix creation failed",
          ex.getMessage(), ex);
    }
  }
}