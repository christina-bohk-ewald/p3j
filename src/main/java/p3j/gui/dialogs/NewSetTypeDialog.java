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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import p3j.database.DatabaseFactory;
import p3j.database.IP3MDatabase;
import p3j.gui.misc.ParameterListModel;
import p3j.gui.panels.projections.IProjectionTree;
import p3j.gui.panels.projections.ProjectionNode;
import p3j.gui.panels.projections.SetTypeNode;
import p3j.misc.gui.GUI;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.SetType;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Dialog to create a new {@link SetType}. The {@link ParameterInstance}
 * entities from the default {@link SetType} can be assigned to the new type via
 * two lists.
 * 
 * Created: August 27, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class NewSetTypeDialog extends
    AbstractProjectionTreeDialog<ProjectionModel, ProjectionNode> {

  /** Serialization ID. */
  private static final long serialVersionUID = 3065923250959082561L;

  /** Width of the dialog. */
  public static final int DIALOG_WIDTH = 800;

  /** Height of the dialog. */
  public static final int DIALOG_HEIGHT = 600;

  /** The column grouping used in the layout. */
  private static final int[] COLUMN_GROUPING = { 2, 4 };

  /** List model for all available parameters. */
  private final ParameterListModel defTypeParamsModel = new ParameterListModel();

  /** Settype that shall be created. */
  private final SetType newSetType;

  /**
   * Row index at which the panel holding the add/remove buttons shall be
   * positioned.
   */
  private static final int ADD_REMOVE_PANEL_ROW_INDEX = 5;

  /**
   * Column index at which the panel holding the add/remove buttons shall be
   * positioned.
   */
  private static final int ADD_REMOVE_PANEL_COL_INDEX = 3;

  /**
   * Row index at which the panel with the parameter lists shall be positioned.
   */
  private static final int SELECTION_PANEL_ROW_INDEX = 4;

  /**
   * Height of parameter selection panels (in rows).
   */
  private static final int SELECTION_PANEL_ROW_HEIGHT = 3;

  /**
   * Width of parameter selection panels (in columns).
   */
  private static final int SELECTION_PANEL_COL_WIDTH = 1;

  /**
   * The column index at which the right-hand side parameter list panel is
   * positioned.
   */
  private static final int COLUMN_INDEX_RHS = 4;

  /**
   * The column index at which the left-hand side parameter list panel is
   * positioned.
   */
  private static final int COLUMN_INDEX_LHS = 2;

  /** The row index at which the button panel is positioned. */
  private static final int BUTTON_PANEL_ROW_INDEX = 8;

  /** The column index at which the button panel is positioned. */
  private static final int BUTTON_PANEL_COL_INDEX = 4;

  // GUI elements

  /** Field to enter name of the new Settype. */
  private JTextField stName = new JTextField("New Settype");

  /** Button to add an instance to the new type. */
  private JButton addParamToNewType = GUI.createIconButton("arrow_r2l.gif",
      "<=");
  {
    addParamToNewType.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        transferInstances(defTypeParamsList, defTypeParamsModel,
            newTypeParamsModel, newSetType);
      }
    });
  }

  /** Button to remove a instance from the new type. */
  private JButton remParamFromNewType = GUI.createIconButton("arrow_l2r.gif",
      "=>");
  {
    remParamFromNewType.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        transferInstances(newTypeParamsList, newTypeParamsModel,
            defTypeParamsModel, getEntity().getDefaultSetType());
      }
    });
  }

  /**
   * List of all available parameters. All parameters from the default type are
   * available.
   */
  private final JList<ParameterInstance> defTypeParamsList = new JList<>(
      defTypeParamsModel);

  /** List model of all parameters of new Settype. */
  private final ParameterListModel newTypeParamsModel = new ParameterListModel();

  /** List of parameters of new Settype. */
  private final JList<ParameterInstance> newTypeParamsList = new JList<>(
      newTypeParamsModel);

  /**
   * Default constructor.
   * 
   * @param projNode
   *          the node representing the {@link ProjectionModel} for which a new
   *          Settype shall be created
   * @param projTree
   *          the projection tree
   */
  public NewSetTypeDialog(ProjectionNode projNode, IProjectionTree projTree) {
    super(projNode, projTree, DIALOG_WIDTH, DIALOG_HEIGHT, "Add Settype");
    setModal(true);
    newSetType = getEntity().createSetType("", "");
    initUI();
  }

  /**
   * Initializes user interface.
   */
  private void initUI() {
    setTitle("Create New Settype");

    JPanel namePanel = new JPanel(GUI.getStdBorderLayout());
    namePanel.add(new JLabel("Name:"), BorderLayout.WEST);
    namePanel.add(stName, BorderLayout.CENTER);

    // Set up parameter lists
    defTypeParamsModel.updateSetType(getEntity().getDefaultSetType());
    newTypeParamsModel.updateSetType(newSetType);

    JPanel addRemovePanel = new JPanel();
    addRemovePanel.setLayout(new BoxLayout(addRemovePanel, BoxLayout.Y_AXIS));
    addRemovePanel.add(addParamToNewType);
    addRemovePanel.add(remParamFromNewType);

    // Create overall layout
    FormLayout layout = new FormLayout(
        "10dlu,d:grow,center:50dlu,right:d:grow,10dlu",
        "10dlu,20dlu,10dlu,d:grow,50dlu,d:grow,10dlu,20dlu,10dlu");
    JPanel contentPanel = new JPanel(layout);
    layout.setColumnGroups(new int[][] { COLUMN_GROUPING });

    CellConstraints cc = new CellConstraints();
    contentPanel.add(namePanel, cc.xy(COLUMN_INDEX_LHS, 2));

    contentPanel.add(new JScrollPane(newTypeParamsList), cc.xywh(
        COLUMN_INDEX_LHS, SELECTION_PANEL_ROW_INDEX, SELECTION_PANEL_COL_WIDTH,
        SELECTION_PANEL_ROW_HEIGHT));

    contentPanel.add(new JScrollPane(defTypeParamsList), cc.xywh(
        COLUMN_INDEX_RHS, SELECTION_PANEL_ROW_INDEX, SELECTION_PANEL_COL_WIDTH,
        SELECTION_PANEL_ROW_HEIGHT));

    contentPanel.add(addRemovePanel,
        cc.xy(ADD_REMOVE_PANEL_COL_INDEX, ADD_REMOVE_PANEL_ROW_INDEX));

    contentPanel.add(getButtonPanel(),
        cc.xy(BUTTON_PANEL_COL_INDEX, BUTTON_PANEL_ROW_INDEX));
    setContentPane(contentPanel);
  }

  /**
   * Transfers instances from one Settype to the other.
   * 
   * @param fromList
   *          the list containing the parameter instances of the source type
   * @param from
   *          the model of the source type's parameter instance list
   * @param to
   *          the model of the destination type's parameter instance list
   * @param targetSetType
   *          the target Settype
   */
  public void transferInstances(JList<ParameterInstance> fromList,
      ParameterListModel from, ParameterListModel to, SetType targetSetType) {

    int firstIndex = fromList.getSelectedIndex();
    List<ParameterInstance> instances = fromList.getSelectedValuesList();

    for (Object instance : instances) {
      ParameterInstance instanceToBeAdded = (ParameterInstance) instance;
      getEntity().assignParameterInstance(instanceToBeAdded, targetSetType,
          true);
    }

    to.refresh();
    from.refresh();

    if (instances.size() > 0 && from.getSize() > 0) {
      fromList
          .setSelectedIndex(from.getSize() == firstIndex ? from.getSize() - 1
              : firstIndex);
    }
  }

  /**
   * Create Settype.
   */
  void createSetType() {
    if (newTypeParamsModel.getSize() == 0) {
      GUI.printErrorMessage(this, "Settype is empty",
          "Settypes should at least cover one parameter.");
      return;
    }

    IP3MDatabase database = DatabaseFactory.getDatabaseSingleton();
    newSetType.setName(stName.getText());
    database.saveProjection(getEntity());
    getProjectionTree().removeNodes(getEntityNode(),
        newSetType.getDefinedParameters());
    getProjectionTree().cleanTree();
    SetTypeNode newNode = new SetTypeNode(newSetType);
    getEntityNode().add(newNode);
    getProjectionTree().refreshNodeSubStructure(getEntityNode());
    getProjectionTree().selectNode(newNode);
    setVisible(false);
  }

  @Override
  protected void cancel() {
    getEntity().removeSetType(newSetType);
    setVisible(false);
  }

  /**
   * Gets the new Settype.
   * 
   * @return the new Settype
   */
  public SetType getNewSetType() {
    return newSetType;
  }

  @Override
  protected void ok() {
    createSetType();
  }
}
