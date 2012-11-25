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
 * <p/>
 * The must be at least one sub-population specified, and their order matters:
 * it is used to define the parameter order in the tree. Additionally, the names
 * of the sub-populations have to be unique.
 * 
 * <p/>
 * This dialogs also supports a 'non-editable' mode (via {@link #editingAllowed}
 * ) that can be used to only show the current setup. In this mode, buttons to
 * change the state are either disabled or left out entirely.
 * 
 * <p/>
 * Each defined sub-population is represented by a {@link JPanel} that is added
 * to the {@link #subPopListPanel}. The methods
 * {@link #addSubPopulation(SubPopulation, Component, int)},
 * {@link #removeSubPopulation(SubPopulation)},
 * {@link #moveSubPopulationUp(SubPopulation)}, and
 * {@link #moveSubPopulationDown(SubPopulation)} are used to edit the list.
 * 
 * @see SubPopulationModel
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

  /** Flag to define whether editing is allowed. */
  private final boolean editingAllowed;

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
   * @param editingAllowed
   *          the flag to allow editing
   */
  public SubPopulationModelEditDialog(SubPopulationModel subPopModel,
      boolean editingAllowed) {
    this.editingAllowed = editingAllowed;
    subPopulationModel = new SubPopulationModel(subPopModel.getSubPopulations());

    setModal(true);
    setTitle("Edit Sub-Populations");
    setSize(DIALOG_WIDTH, 2 * DIALOG_HEIGHT);
    GUI.centerOnScreen(this);
    initialize();
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

  private void addSubPopulation(SubPopulation subPopulation,
      Component subPopPanel, int index) {
    subPopulationModel.getSubPopulations().add(index, subPopulation);
    subPopListPanel.add(subPopPanel, index);
  }

  private Component removeSubPopulation(int indexToDelete) {
    Component deleted = subPopListPanel.getComponent(indexToDelete);
    subPopListPanel.remove(indexToDelete);
    subPopulationModel.getSubPopulations().remove(indexToDelete);
    return deleted;
  }

  private void refreshSubPopList() {
    BasicUtilities.invalidateOnEDT(subPopListPanel);
    listScroll.validate();
  }

  /**
   * Adds a sub-population (to the end of the list).
   * 
   * <p/>
   * This and the following methods alter the state of the sub-population list
   * and are thus synchronized, to grant them exclusive access to both
   * {@link #subPopulationModel} and {@link #subPopListPanel}.
   * 
   * @param subPopulation
   *          the sub-population
   */
  private synchronized void addSubPopulation(SubPopulation subPopulation) {
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
    addSubPopulation(subPopulation,
        createSubPopulationDisplayPanel(subPopulation, index), index);
    refreshSubPopList();
  }

  private synchronized void removeSubPopulation(SubPopulation subPopulation) {
    if (subPopulationModel.getSubPopulations().size() == 1) {
      GUI.printMessage(this, "Deletion failed",
          "At least one sub-population has to be defined.");
      return;
    }
    int indexToDelete = subPopulationModel.getSubPopulations().indexOf(
        subPopulation);
    if (indexToDelete >= 0) {
      removeSubPopulation(indexToDelete);

    }
    refreshSubPopList();
  }

  private synchronized void moveSubPopulationUp(SubPopulation subPop) {
    int currentIndex = subPopulationModel.getSubPopulations().indexOf(subPop);
    if (currentIndex <= 0)
      return;
    Component componentToMove = removeSubPopulation(currentIndex);
    addSubPopulation(subPop, componentToMove, currentIndex - 1);
    refreshSubPopList();
  }

  private synchronized void moveSubPopulationDown(SubPopulation subPop) {
    int currentIndex = subPopulationModel.getSubPopulations().indexOf(subPop);
    if (currentIndex == subPopulationModel.getSubPopulations().size() - 1
        || currentIndex < 0)
      return;
    Component componentToMove = removeSubPopulation(currentIndex);
    addSubPopulation(subPop, componentToMove, currentIndex + 1);
    refreshSubPopList();
  }

  /**
   * Initializes the UI components.
   */
  private void initialize() {

    JPanel overall = new JPanel(GUI.getStdBorderLayout());
    getContentPane().add(overall);

    JPanel content = new JPanel(GUI.getStdBorderLayout());
    content.add(initializeSubPopListPanel(), BorderLayout.CENTER);
    content.add(editingAllowed ? createAddSubPopPanel() : new JPanel(),
        BorderLayout.SOUTH);

    overall.add(new JPanel(), BorderLayout.NORTH);
    overall.add(new JPanel(), BorderLayout.WEST);
    overall.add(new JPanel(), BorderLayout.EAST);
    overall.add(content, BorderLayout.CENTER);
    overall.add(createButtonPanel(), BorderLayout.SOUTH);
  }

  private JPanel initializeSubPopListPanel() {
    List<SubPopulation> subPops = subPopulationModel.getSubPopulations();
    for (int i = 0; i < subPops.size(); i++)
      subPopListPanel.add(createSubPopulationDisplayPanel(subPops.get(i), i));
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
    if (editingAllowed) {
      bbBuilder.addButton(getCancelButton());
      bbBuilder.addUnrelatedGap();
    }
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

    return createAddSubPopPanelLayout(radioGroupPanel, subPopName,
        hasDescendantGenerations, addButton);
  }

  private JPanel createAddSubPopPanelLayout(JPanel radioGroupPanel,
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

  private JPanel createSubPopulationDisplayPanel(SubPopulation subPop, int index) {
    JPanel subPopDisplayPanel = new JPanel(GUI.getStdBorderLayout());
    subPopDisplayPanel.add(new JPanel(), BorderLayout.NORTH);

    subPopDisplayPanel.add(createAdditiveButton(subPop, index),
        BorderLayout.WEST);
    subPopDisplayPanel.add(createSubPopulationDescription(subPop),
        BorderLayout.CENTER);
    subPopDisplayPanel
        .add(editingAllowed ? createControlButtonPanel(subPop, index)
            : new JPanel(), BorderLayout.EAST);
    subPopDisplayPanel.add(new JPanel(), BorderLayout.SOUTH);
    subPopDisplayPanel.setMaximumSize(new Dimension(
        (int) (0.8 * SubPopulationModelEditDialog.DIALOG_WIDTH), 60));
    return subPopDisplayPanel;
  }

  private JLabel createSubPopulationDescription(SubPopulation subPop) {
    JLabel subPopDesc = new JLabel(subPop.getName()
        + (subPop.isConsistingOfDescendantGenerations() ? " (& descendants)"
            : ""));
    subPopDesc.setFont(GUI.getDefaultFontBold());
    return subPopDesc;
  }

  private JPanel createControlButtonPanel(final SubPopulation subPop,
      final int index) {
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    JButton deleteButton = GUI.createIconButton("delete_obj.gif", "Delete");
    deleteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        removeSubPopulation(subPop);
      }
    });
    JButton upButton = GUI.createIconButton("step_current_up.gif", "up");
    upButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        moveSubPopulationUp(subPop);
      }
    });
    JButton downButton = GUI.createIconButton("step_current_down.gif", "down");
    downButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        moveSubPopulationDown(subPop);
      }
    });

    buttonPanel.add(upButton);
    buttonPanel.add(new JPanel());
    buttonPanel.add(downButton);
    buttonPanel.add(new JPanel());
    buttonPanel.add(deleteButton);
    return buttonPanel;
  }

  private JButton createAdditiveButton(final SubPopulation subPopulation,
      final int subPopIndex) {
    final JButton additivityButton = new JButton();
    additivityButton.setEnabled(editingAllowed);
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

  private static void setButtonIcon(JButton additivityButton, boolean isAdditive) {
    GUI.decorateButtonWithIconOrText(additivityButton, GUI
        .retrieveIcon(isAdditive ? "add_correction.png"
            : "remove_correction.png"), isAdditive ? "+" : "-");
    additivityButton.setToolTipText(isAdditive ? "Adds to overall population"
        : "Subtracts from overall population");
  }
}