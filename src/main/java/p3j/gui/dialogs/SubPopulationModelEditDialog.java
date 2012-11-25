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
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

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
  private final SubPopListModel subPopulationModel;

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
    subPopulationModel = new SubPopListModel(new SubPopulationModel(
        subPopModel.getSubPopulations()));
    GUI.centerOnScreen(this);
    initialize();
  }

  private void initialize() {
    JPanel overall = new JPanel(GUI.getStdBorderLayout());
    getContentPane().add(overall);

    JPanel content = new JPanel(GUI.getStdBorderLayout());
    content.add(createAddSubPopListPanel(), BorderLayout.CENTER);
    content.add(createAddSubPopPanel(), BorderLayout.SOUTH);

    overall.add(new JPanel(), BorderLayout.NORTH);
    overall.add(new JPanel(), BorderLayout.WEST);
    overall.add(new JPanel(), BorderLayout.EAST);
    overall.add(content, BorderLayout.CENTER);
    overall.add(createButtonPanel(), BorderLayout.SOUTH);
  }

  private Component createAddSubPopListPanel() {
    JList<SubPopulation> subPopList = new JList<>(subPopulationModel);
    subPopList.setCellRenderer(new SubPopCellRenderer());
    JPanel subPopListPanel = new JPanel(GUI.getStdBorderLayout());
    subPopListPanel.add(new JScrollPane(subPopList), BorderLayout.CENTER);
    return subPopListPanel;
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
        addSubPopulation(subPopName.getText(), additiveButton.isSelected(),
            hasDescendantGenerations.isSelected());
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

  protected void addSubPopulation(String subPopName, boolean additive,
      boolean hasDescendantGenerations) {
    subPopulationModel.addElement(new SubPopulation(subPopName, additive,
        hasDescendantGenerations));
  }

  @Override
  protected void okAction() {
    this.confirmed = true;
    setVisible(false);
  }

  public SubPopulationModel getSubPopulationModel() {
    SubPopulation[] subPops = (SubPopulation[]) subPopulationModel.toArray();
    return new SubPopulationModel(Arrays.asList(subPops));
  }

  public boolean isConfirmed() {
    return confirmed;
  }

}

/**
 * Manages the list of sub-populations to be edited.
 */
class SubPopListModel extends DefaultListModel<SubPopulation> {

  final SubPopulationModel subPopModel;

  SubPopListModel(SubPopulationModel subPopulationModel) {
    subPopModel = subPopulationModel;
    for (SubPopulation subPop : subPopulationModel.getSubPopulations())
      addElement(subPop);
  }

  private static final long serialVersionUID = -4527611539757241306L;

}

/**
 * Renders the relevant data fields of a sub-population to a panel and provides
 * buttons to move it up/down.
 */
class SubPopCellRenderer implements ListCellRenderer<SubPopulation> {

  @Override
  public Component getListCellRendererComponent(
      JList<? extends SubPopulation> list, SubPopulation subPop, int index,
      boolean isSelected, boolean cellHasFocus) {
    JPanel subPopDisplayPanel = new JPanel(GUI.getStdBorderLayout());

    subPopDisplayPanel.add(new JPanel(), BorderLayout.NORTH);
    subPopDisplayPanel.add(new JLabel(subPop.getName()), BorderLayout.CENTER);
    subPopDisplayPanel.add(new JPanel(), BorderLayout.SOUTH);

    subPopDisplayPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    return subPopDisplayPanel;
  }
}