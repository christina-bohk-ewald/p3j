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

import james.gui.utils.BasicUtilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import p3j.misc.gui.GUI;
import p3j.pppm.SubPopulation;
import p3j.pppm.SubPopulationModel;

import com.jgoodies.forms.builder.ButtonBarBuilder2;

/**
 * A dialog to edit the {@link SubPopulationModel} setup for a given
 * {@link p3j.pppm.ProjectionModel}.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class SubPopulationModelEditDialog extends ProjectionDialog {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3979733245241552349L;

  /** The sub-population model to be edited. */
  private final SubPopulationModel subPopulationModel;

  /** The panel containing the panel describing the existing sub-populations. */
  private final JPanel subPopListPanel = new JPanel();
  {
    subPopListPanel.setLayout(new BoxLayout(subPopListPanel, BoxLayout.Y_AXIS));
  }

  /** The scroll pane for the list of sub-populations. */
  private final JScrollPane listScroll = new JScrollPane(subPopListPanel);

  /** The factory to create panels for editing individual sub-populations. */
  private final SubPopPanelFactory panelFactory;

  /**
   * The flag that shows whether the edited sub-population model was confirmed
   * by the user.
   */
  private boolean confirmed = false;

  /**
   * Default constructor.
   * 
   * @param subPopModel
   *          the sub-population model
   */
  public SubPopulationModelEditDialog(SubPopulationModel subPopModel) {
    setModal(true);
    setTitle("Edit Sub-Populations");
    setSize(DIALOG_WIDTH, 2 * DIALOG_HEIGHT);
    subPopulationModel = new SubPopulationModel(subPopModel.getSubPopulations());
    panelFactory = new SubPopPanelFactory(this);
    GUI.centerOnScreen(this);
    initializeSubPopListPanel();
    initialize();
  }

  void initializeSubPopListPanel() {
    subPopListPanel.removeAll();
    List<SubPopulation> subPops = subPopulationModel.getSubPopulations();
    for (int i = 0; i < subPops.size(); i++)
      subPopListPanel.add(panelFactory.getPanel(subPops.get(i), i));
  }

  private void initialize() {
    JPanel overall = new JPanel(GUI.getStdBorderLayout());
    getContentPane().add(overall);

    JPanel content = new JPanel(GUI.getStdBorderLayout());
    content.add(createSubPopListPanel(), BorderLayout.CENTER);
    content.add(createAddSubPopPanel(), BorderLayout.SOUTH);

    overall.add(new JPanel(), BorderLayout.NORTH);
    overall.add(new JPanel(), BorderLayout.WEST);
    overall.add(new JPanel(), BorderLayout.EAST);
    overall.add(content, BorderLayout.CENTER);
    overall.add(createButtonPanel(), BorderLayout.SOUTH);
  }

  private Component createSubPopListPanel() {
    JPanel surroundingPanel = new JPanel(GUI.getStdBorderLayout());
    surroundingPanel.add(listScroll, BorderLayout.CENTER);
    return surroundingPanel;
  }

  private JPanel createButtonPanel() {
    JPanel buttonPanel = new JPanel(GUI.getStdBorderLayout());
    buttonPanel.add(createButtonSubPanel(), BorderLayout.EAST);
    return buttonPanel;
  }

  private JPanel createButtonSubPanel() {
    ButtonBarBuilder2 bbBuilder = new ButtonBarBuilder2();
    bbBuilder.addButton(getCancelButton());
    bbBuilder.addUnrelatedGap();
    bbBuilder.addButton(getOkButton());
    JPanel subPanel = new JPanel();
    subPanel.add(bbBuilder.getPanel());
    return subPanel;
  }

  private JPanel createAddSubPopPanel() {
    final JRadioButton additiveButton = new JRadioButton("+");
    additiveButton.setFont(GUI.getDefaultFontBold());
    additiveButton.setSelected(true);
    final JRadioButton subtractiveButton = new JRadioButton("-");
    subtractiveButton.setFont(GUI.getDefaultFontBold());
    final ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(additiveButton);
    buttonGroup.add(subtractiveButton);
    JPanel radioGroupPanel = new JPanel(GUI.getStdBorderLayout());
    radioGroupPanel.add(additiveButton, BorderLayout.CENTER);
    radioGroupPanel.add(subtractiveButton, BorderLayout.EAST);

    final JTextField subPopName = new JTextField("Subpopulation Name");
    final JCheckBox hasDescendantGenerations = new JCheckBox(
        "Distinct Descendant Generations");
    final JButton addButton = new JButton("Add");

    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addSubPopulation(new SubPopulation(subPopName.getText(), additiveButton
            .isSelected(), hasDescendantGenerations.isSelected()));
      }
    });

    return constructAddSubPopPanelLayout(radioGroupPanel, subPopName,
        hasDescendantGenerations, addButton);
  }

  private JPanel constructAddSubPopPanelLayout(JPanel radioGroupPanel,
      final JTextField subPopName, final JCheckBox hasDescendantGenerations,
      final JButton addButton) {
    JPanel generalPanel = new JPanel(GUI.getStdBorderLayout());
    JPanel addButtonPanel = new JPanel(GUI.getStdBorderLayout());

    addButtonPanel.add(radioGroupPanel, BorderLayout.WEST);
    addButtonPanel.add(subPopName, BorderLayout.CENTER);
    addButtonPanel.add(hasDescendantGenerations, BorderLayout.EAST);

    generalPanel.add(addButtonPanel, BorderLayout.CENTER);
    generalPanel.add(addButton, BorderLayout.EAST);
    return generalPanel;
  }

  synchronized void addSubPopulation(SubPopulation subPopulation) {
    for (SubPopulation subPop : subPopulationModel.getSubPopulations())
      if (subPop.getName().equals(subPopulation.getName())) {
        GUI.printMessage(
            this,
            "Adding sub-population failed",
            "Sub-population names must be unique, and there already is a sub-population called '"
                + subPopulation.getName() + "'.");
        return;
      }
    int index = subPopulationModel.getSubPopulations().size();
    subPopulationModel.getSubPopulations().add(subPopulation);
    subPopListPanel.add(panelFactory.getPanel(subPopulation, index), index);
    listScroll.validate();
  }

  synchronized void removeSubPopulation(SubPopulation subPopulation) {
    if (subPopulationModel.getSubPopulations().size() == 1) {
      GUI.printMessage(this, "Deletion failed",
          "At least one sub-population has to be defined.");
      return;
    }
    int indexToDelete = subPopulationModel.getSubPopulations().indexOf(
        subPopulation);
    if (indexToDelete >= 0) {
      subPopListPanel.remove(indexToDelete);
      subPopulationModel.getSubPopulations().remove(indexToDelete);
      BasicUtilities.invalidateOnEDT(subPopListPanel);
    }
    listScroll.validate();
  }

  @Override
  protected void okAction() {
    this.confirmed = true;
    setVisible(false);
  }

  public SubPopulationModel getSubPopulationModel() {
    return subPopulationModel;
  }

  public boolean isConfirmed() {
    return confirmed;
  }

}

/**
 * Provides data editing capabilities for the table.
 */
class SubPopPanelFactory {

  final SubPopulationModelEditDialog editDialog;

  SubPopPanelFactory(SubPopulationModelEditDialog editDialog) {
    this.editDialog = editDialog;
  }

  JPanel getPanel(SubPopulation subPop, int index) {
    JPanel subPopDisplayPanel = new JPanel(GUI.getStdBorderLayout());
    JLabel subPopDesc = new JLabel(
        subPop.getName()
            + (subPop.isConsistingOfDescendantGenerations() ? "(and descendant generations)"
                : ""));

    subPopDesc.setFont(GUI.getDefaultFontBold());

    subPopDisplayPanel.add(new JPanel(), BorderLayout.NORTH);
    subPopDisplayPanel.add(createAdditiveButton(subPop, index),
        BorderLayout.WEST);
    subPopDisplayPanel.add(subPopDesc, BorderLayout.CENTER);
    subPopDisplayPanel.add(createControlButtonPanel(subPop, index),
        BorderLayout.EAST);
    subPopDisplayPanel.add(new JPanel(), BorderLayout.SOUTH);
    subPopDisplayPanel.setMaximumSize(new Dimension(
        (int) (0.8 * SubPopulationModelEditDialog.DIALOG_WIDTH), 60));

    return subPopDisplayPanel;
  }

  private JPanel createControlButtonPanel(final SubPopulation subPop,
      final int index) {
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    JButton deleteButton = GUI.createIconButton("delete_obj.gif", "Delete");
    buttonPanel.add(deleteButton);
    deleteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        editDialog.removeSubPopulation(subPop);
      }
    });
    return buttonPanel;
  }

  JButton createAdditiveButton(final SubPopulation subPopulation,
      final int subPopIndex) {
    final JButton additivityButton = new JButton();
    setButtonIcon(additivityButton, subPopulation.isAdditive());
    additivityButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        subPopulation.setAdditive(!subPopulation.isAdditive());
        setButtonIcon(additivityButton, subPopulation.isAdditive());
      }
    });
    return additivityButton;
  }

  static void setButtonIcon(JButton additivityButton, boolean isAdditive) {
    GUI.decorateButtonWithIconOrText(additivityButton, GUI
        .retrieveIcon(isAdditive ? "add_correction.png"
            : "remove_correction.png"), isAdditive ? "+" : "-");
    additivityButton.setToolTipText(isAdditive ? "Adds to overall population"
        : "Subtracts from overall population");
  }
}