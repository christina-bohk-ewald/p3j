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

import p3j.misc.gui.GUI;
import p3j.pppm.SubPopulationModel;

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
    // TODO Auto-generated method stub
  }

  @Override
  protected void addOKButtonAction() {
    this.confirmed = true;
  }

  public SubPopulationModel getSubPopulationModel() {
    return subPopulationModel;
  }

  public boolean isConfirmed() {
    return confirmed;
  }

}
