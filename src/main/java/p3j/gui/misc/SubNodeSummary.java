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
package p3j.gui.misc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import p3j.gui.panels.projections.IProjectionTree;
import p3j.gui.panels.projections.ProjectionTreeNode;
import p3j.misc.Misc;
import p3j.misc.gui.GUI;

/**
 * Preview of all entities below a certain node.
 * 
 * Created: August 26, 2008
 * 
 * @param <E>
 *          type of the target entity
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class SubNodeSummary<E> extends JPanel {

  /** Serialization ID. */
  private static final long serialVersionUID = 6979550281628665829L;

  /** Label that to signal that the displayed number of nodes has been cut off. */
  private static final String SUBNODE_CUTOFF_LABEL = "...";

  /**
   * Default constructor.
   * 
   * @param node
   *          the current node
   * @param projTree
   *          the overall projection tree
   * @param desiredClass
   *          the class of the target entities
   */
  public SubNodeSummary(ProjectionTreeNode<?> node, IProjectionTree projTree,
      Class<E> desiredClass) {
    setLayout(GUI.getStdBorderLayout());

    JPanel panel = new JPanel();
    BoxLayout bl = new BoxLayout(panel, BoxLayout.Y_AXIS);
    panel.setLayout(bl);

    List<ProjectionTreeNode<E>> children = node.getChildsByType(desiredClass);

    int childCounter = 0;
    for (ProjectionTreeNode<E> child : children) {
      String str = child.getEntityLabel();
      JLabel label = new JLabel("<html><u>" + str + "</u></html>");
      label.setForeground(Color.BLUE);
      label.addMouseListener(new ClickAssignmentListener<E>(child.getEntity(),
          node, projTree));
      panel.add(label);
      childCounter++;
      if (childCounter >= Misc.MAX_SUBNODE_SUMMARY_ELEMENTS) {
        panel.add(new JLabel(SUBNODE_CUTOFF_LABEL));
        break;
      }
    }

    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    add(scrollPane, BorderLayout.CENTER);
  }

}

/**
 * Action listener that navigates to the {@link ProjectionTreeNode} holding the
 * given target entity after clicking.
 * 
 * Created: August 26, 2008
 * 
 * @param <E>
 *          type of the target entity
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
class ClickAssignmentListener<E> extends MouseAdapter {

  /** The target entity. */
  private final E entity;

  /** The current node. */
  private final ProjectionTreeNode<?> node;

  /** Reference to the projection tree. */
  private final IProjectionTree projectionTree;

  ClickAssignmentListener(E targetEntity, ProjectionTreeNode<?> piNode,
      IProjectionTree projTree) {
    entity = targetEntity;
    node = piNode;
    projectionTree = projTree;
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    projectionTree.selectNode(node.getChildWithEntity(entity));
  }
}
