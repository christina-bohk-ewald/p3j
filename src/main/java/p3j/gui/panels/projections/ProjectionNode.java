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
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.tree.TreePath;

import p3j.database.DatabaseFactory;
import p3j.gui.dialogs.AdjustMaxAgeDialog;
import p3j.gui.dialogs.DuplicateProjectionDialog;
import p3j.gui.dialogs.NewSetTypeDialog;
import p3j.gui.dialogs.ProjectionDialog;
import p3j.gui.dialogs.SubPopulationModelEditDialog;
import p3j.gui.misc.SubNodeSummary;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.misc.Misc;
import p3j.pppm.ProjectionModel;
import p3j.pppm.sets.SetType;

/**
 * Node that represents a projection.
 * 
 * Created: August 24, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ProjectionNode extends ProjectionTreeNode<ProjectionModel> {

  /** Serialization ID. */
  private static final long serialVersionUID = 8332182127722759791L;

  /**
   * Default constructor.
   * 
   * @param projection
   *          the projection to be represented
   */
  public ProjectionNode(ProjectionModel projection) {
    super(projection, projection.getName());
  }

  @Override
  public void deselected() {

  }

  @Override
  public JPanel selected(TreePath selectionPath, final IProjectionTree projTree) {

    // Layout for info sheet on projection.
    final ProjectionNode node = this;
    final JTextField name = new JTextField(getEntity().getName());
    final JTextArea description = new JTextArea(getEntity().getDescription());
    final JTextField jumpOffYear = new JTextField(Integer.toString(getEntity()
        .getJumpOffYear()));

    JButton showSubPopsButton = new JButton("Show Sub-Population Structure");
    showSubPopsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        new SubPopulationModelEditDialog(getEntity().getSubPopulationModel(),
            false).setVisible(true);
      }
    });

    JButton duplicateButton = new JButton("Duplicate");
    duplicateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        DuplicateProjectionDialog dp = new DuplicateProjectionDialog(
            getEntity());
        dp.setVisible(true);
      }
    });

    JButton createSetTypeButton = new JButton("New Settype");
    createSetTypeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        NewSetTypeDialog nstd = new NewSetTypeDialog(node, projTree);
        nstd.setVisible(true);
        SetType newSetType = nstd.getNewSetType();
        DatabaseFactory.getDatabaseSingleton().saveSetType(newSetType);
        projTree.totalRefresh();
        projTree.selectNode(node.getChildWithEntity(newSetType));
      }
    });

    JButton applyButton = new JButton("Apply");
    applyButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ProjectionModel projection = getEntity();
        projection.setName(name.getText());
        projection.setDescription(description.getText());
        projection.setJumpOffYear(Integer.parseInt(jumpOffYear.getText()));
        setUserObject(projection.getName());
        DatabaseFactory.getDatabaseSingleton().saveProjection(projection);
        projTree.refreshNode(node);
      }
    });

    JButton adjustMaxAgeButton = new JButton("Adjust Age Classes");
    adjustMaxAgeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        adjustMaxAge(getEntity());
      }
    });

    List<JButton> buttons = new ArrayList<JButton>();
    buttons.add(showSubPopsButton);
    buttons.add(duplicateButton);
    buttons.add(adjustMaxAgeButton);
    buttons.add(createSetTypeButton);
    buttons.add(applyButton);
    PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(buttons, 1);
    pspf.sep("General Information");
    pspf.app(Misc.GUI_LABEL_NAME, name);
    pspf.app(Misc.GUI_LABEL_DESCRIPTION, description, 2);
    pspf.app(Misc.GUI_LABEL_JUMP_OFF_YEAR, jumpOffYear);
    pspf.app("Settypes:", getEntity().getAllSetTypes().size());
    pspf.app("Descendant Generations:", getEntity().getGenerations() - 1);
    pspf.app("Years to be predicted:", getEntity().getYears());
    pspf.app("Number of age classes:", getEntity().getNumberOfAgeClasses());
    pspf.app("Number of sub-populations:", getEntity().getSubPopulationModel()
        .getSubPopulations().size());
    pspf.appPreview(new SubNodeSummary<SetType>(node, projTree, SetType.class));
    return pspf.constructPanel();
  }

  /**
   * Adjusts maximum age.
   * 
   * @param projectionModel
   *          the projection model for which the maximum age shall be adjusted
   */
  protected void adjustMaxAge(ProjectionModel projectionModel) {
    ProjectionDialog maDialog = new AdjustMaxAgeDialog(projectionModel);
    maDialog.setVisible(true);
  }
}
