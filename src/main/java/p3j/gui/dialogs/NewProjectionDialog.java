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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import p3j.database.IP3MDatabase;
import p3j.misc.Misc;
import p3j.misc.gui.GUI;
import p3j.pppm.PPPModelFactory;
import p3j.pppm.ProjectionModel;
import p3j.pppm.SubPopulationModel;

import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Creates a new projection.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class NewProjectionDialog extends JDialog {

  /** Serialization ID. */
  private static final long serialVersionUID = -4176931594515368446L;

  /** Width of the dialog. */
  public static final int DIALOG_WIDTH = 600;

  /** Height of the dialog. */
  public static final int DIALOG_HEIGHT = 300;

  /** The number of 'unrelated' gaps between the 'Cancel' button and the others. */
  private static final int GAP_SIZE_CANCEL = 3;

  /** The database in which the new projection shall be stored. */
  private final IP3MDatabase database;

  /** The field to enter a name. */
  private JTextField name = new JTextField("New Projection");

  /** The description of the projection. */
  private JTextArea description = new JTextArea("Description.");

  /** The field to enter the projection horizon. */
  private JTextField horizon = new JTextField(
      Integer.toString(PPPModelFactory.DEFAULT_YEARS));

  /** The field to enter the number of generations. */
  private JTextField generations = new JTextField(
      Integer.toString(PPPModelFactory.DEFAULT_GENERATIONS));

  /** The maximum age to be considered. */
  private JTextField numAgeClasses = new JTextField(
      Integer.toString(PPPModelFactory.DEFAULT_MAX_AGE));

  /** The field to enter the jump-off year. */
  private JTextField jumpOffYear = new JTextField(
      Integer.toString(PPPModelFactory.DEFAULT_JUMP_OFF_YEAR));

  /** The newly created projection. */
  private ProjectionModel newProjection;

  /** The sub-population model to be used. */
  private SubPopulationModel subPopulationModel = PPPModelFactory.DEFAULT_SUBPOPULATION_MODEL;

  /** The button to create a new projection. */
  private JButton newProjectionButton = new JButton("Add Projection");
  {
    newProjectionButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        createNewProjection();
      }
    });
  }

  /** The button to cancel projection creation. */
  private JButton cancelButton = new JButton("Cancel");
  {
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
  }

  /** The button to edit the sub-population model. */
  private JButton editSubPopsButton = new JButton("Edit Sub-Populations");
  {
    editSubPopsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SubPopulationModelEditDialog dialog = new SubPopulationModelEditDialog(
            subPopulationModel, true);
        dialog.setVisible(true);
        if (dialog.isConfirmed())
          subPopulationModel = dialog.getSubPopulationModel();
      }
    });
  }

  /**
   * Instantiates a new new projection dialog.
   * 
   * @param owner
   *          the owner frame
   * @param p3mDatabase
   *          the p3m database
   */
  public NewProjectionDialog(Frame owner, IP3MDatabase p3mDatabase) {
    super(owner, true);
    setTitle("Define New Projection");
    setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    database = p3mDatabase;
    GUI.centerOnScreen(this);
    initialize();
  }

  /**
   * Initializes content of the panel.
   */
  private void initialize() {
    FormLayout layout = new FormLayout("10dlu,d:grow,10dlu,d:grow,10dlu",
        "10dlu,d,10dlu,fill:d:grow,10dlu,d,10dlu,d,10dlu,d,10dlu,d,10dlu,d,10dlu");
    JPanel contentPanel = new JPanel(layout);

    int currRow = GUI.ROW_SKIP_LAYOUT;
    currRow = GUI.addRowToPanel(contentPanel, Misc.GUI_LABEL_NAME, name,
        currRow);
    currRow = GUI.addRowToPanel(contentPanel, Misc.GUI_LABEL_DESCRIPTION,
        description, currRow);
    currRow = GUI.addRowToPanel(contentPanel,
        Misc.GUI_LABEL_PROJECTION_HORIZON, horizon, currRow);
    currRow = GUI.addRowToPanel(contentPanel,
        Misc.GUI_LABEL_DESCENDANT_GENERATIONS, generations, currRow);
    currRow = GUI.addRowToPanel(contentPanel, Misc.GUI_LABEL_NUM_AGE_CLASSES,
        numAgeClasses, currRow);
    currRow = GUI.addRowToPanel(contentPanel, Misc.GUI_LABEL_JUMP_OFF_YEAR,
        jumpOffYear, currRow);

    contentPanel.add(getButtonPanel(),
        new CellConstraints().xy(GUI.INPUT_COLUMN_INDEX, currRow));
    setContentPane(contentPanel);
  }

  /**
   * Creates a panel containing the buttons.
   * 
   * @return a panel containing the buttons
   */
  private JPanel getButtonPanel() {
    ButtonBarBuilder2 bbBuilder = new ButtonBarBuilder2();
    bbBuilder.addButton(cancelButton);
    for (int i = 0; i < GAP_SIZE_CANCEL; i++)
      bbBuilder.addUnrelatedGap();
    bbBuilder.addButton(editSubPopsButton);
    bbBuilder.addRelatedGap();
    bbBuilder.addButton(newProjectionButton);
    return bbBuilder.getPanel();
  }

  /**
   * Gets the new projection.
   * 
   * @return the new projection
   */
  public ProjectionModel getNewProjection() {
    return newProjection;
  }

  /**
   * Checks for created new projection.
   * 
   * @return true, if successful
   */
  public boolean hasCreatedNewProjection() {
    return newProjection != null;
  }

  /**
   * Creates a new projection.
   */
  private void createNewProjection() {
    int gens = -1;
    int years = -1;
    int maxAge = -1;
    int jOffYear = -1;
    try {
      gens = Integer.parseInt(generations.getText()) + 1;
      years = Integer.parseInt(horizon.getText());
      maxAge = Integer.parseInt(numAgeClasses.getText()) - 1;
      jOffYear = Integer.parseInt(jumpOffYear.getText());
    } catch (NumberFormatException ex) {
      GUI.printErrorMessage(
          NewProjectionDialog.this,
          "Erroneous input.",
          "The numeric fields may only contain positive integers:"
              + ex.getMessage(), ex);
    }

    try {
      newProjection = PPPModelFactory.createModel(name.getText(),
          description.getText(), gens, years, maxAge, jOffYear,
          subPopulationModel);
      database.newProjection(newProjection);
      setVisible(false);
    } catch (Exception ex) {
      GUI.printErrorMessage(NewProjectionDialog.this,
          "Error while creating new projection",
          "Creation of new projection failed:" + ex.getMessage(), ex);
    }
  }

}
