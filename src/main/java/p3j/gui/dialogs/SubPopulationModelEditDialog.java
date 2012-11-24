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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import p3j.misc.gui.GUI;
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

  /**
   * The flag that shows whether the edited sub-population model was confirmed
   * by the user.
   */
  private boolean confirmed = false;

  JButton createDelButton() {
    return GUI.createIconButton("remove_correction.png", "-");
  }

  /**
   * Default constructor.
   * 
   * @param subPopModel
   *          the sub-population model
   */
  public SubPopulationModelEditDialog(SubPopulationModel subPopModel) {
    setModal(true);
    setTitle("Edit Sub-Populations");
    setSize(DIALOG_WIDTH, DIALOG_HEIGHT * 3);
    subPopulationModel = new SubPopulationModel(subPopModel.getSubPopulations());
    GUI.centerOnScreen(this);
    initialize();
  }

  private void initialize() {
    JPanel overall = new JPanel(GUI.getStdBorderLayout());
    getContentPane().add(overall);

    JPanel content = new JPanel(GUI.getStdBorderLayout());
    content.add(createAddButtonPanel(), BorderLayout.SOUTH);

    overall.add(new JPanel(), BorderLayout.WEST);
    overall.add(new JPanel(), BorderLayout.EAST);
    overall.add(content, BorderLayout.CENTER);
    overall.add(createButtonPanel(), BorderLayout.SOUTH);
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

  private JPanel createAddButtonPanel() {

    JPanel generalPanel = new JPanel(GUI.getStdBorderLayout());
    JPanel addButtonPanel = new JPanel(GUI.getStdBorderLayout());

    final JTextField subPopName = new JTextField("Subpopulation Name");
    final JCheckBox hasDescendantGenerations = new JCheckBox(
        "Distinct Descendant Generations");
    final JButton addButton = GUI.createIconButton("add_correction.png", "+");
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        addSubPopulation(subPopName.getText(),
            hasDescendantGenerations.isSelected());
      }
    });

    addButtonPanel.add(subPopName, BorderLayout.CENTER);
    addButtonPanel.add(hasDescendantGenerations, BorderLayout.EAST);

    generalPanel.add(addButtonPanel, BorderLayout.CENTER);
    generalPanel.add(addButton, BorderLayout.EAST);
    return generalPanel;
  }

  protected void addSubPopulation(String text, boolean selected) {
    // TODO Auto-generated method stub
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
