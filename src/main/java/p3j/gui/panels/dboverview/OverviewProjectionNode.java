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
package p3j.gui.panels.dboverview;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.tree.TreePath;

import p3j.database.DatabaseFactory;
import p3j.gui.P3J;
import p3j.gui.dialogs.execstatus.SimpleProgressDialog;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.gui.panels.projections.IProjectionTree;
import p3j.gui.panels.projections.ProjectionTreeNode;
import p3j.misc.IProgressObserver;
import p3j.misc.Misc;
import p3j.misc.gui.GUI;
import p3j.pppm.ProjectionModel;

/**
 * This node delivers an overview of a projections and options to remove or load
 * it.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class OverviewProjectionNode extends ProjectionTreeNode<ProjectionModel> {

  /** Serialization ID. */
  private static final long serialVersionUID = -7668884101075657298L;

  /**
   * Instantiates a new overview projection node.
   * 
   * @param projectionModel
   *          the projection model
   * @param name
   *          the name of the node
   */
  public OverviewProjectionNode(ProjectionModel projectionModel, String name) {
    super(projectionModel, name);
  }

  @Override
  public JPanel selected(TreePath selectionPath, final IProjectionTree projTree) {

    JButton loadProjection = new JButton("<html><b>Load</b></html>");
    loadProjection.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        P3J.getInstance().setCurrentProjection(getEntity());
      }
    });

    JButton removeProjection = new JButton("Remove");
    removeProjection.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        (new SwingWorker<Void, Void>() {
          @Override
          protected Void doInBackground() {
            try {
              if (GUI.printQuestion(P3J.getInstance(), "Are you sure?",
                  "Do you really want to delete the projection'"
                      + getEntity().getName() + "'? This cannot be undone!")) {
                IProgressObserver progress = SimpleProgressDialog.showDialog(
                    P3J.getInstance(), "Deleting projection and its results",
                    "", 2, true);
                DatabaseFactory.getDatabaseSingleton().deleteProjection(
                    getEntity(), progress);
                progress.incrementProgress("Updating user interface...");
                P3J.getInstance().projectionDeleted(getEntity());
                progress.taskFinished();
              }
            } catch (Exception ex) {
              GUI.printErrorMessage("Projection Deletion Failed", ex);
            }
            return null;
          }
        }).execute();
      }
    });

    List<JButton> buttons = new ArrayList<JButton>();
    buttons.add(removeProjection);
    buttons.add(loadProjection);

    PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(buttons, 1);
    pspf.sep("General Information");
    pspf.app(Misc.GUI_LABEL_NAME, getEntity().getName());
    pspf.app(Misc.GUI_LABEL_DESCRIPTION, getEntity().getDescription());
    pspf.app(Misc.GUI_LABEL_JUMP_OFF_YEAR,
        Integer.toString(getEntity().getJumpOffYear()));
    pspf.app("Settypes:", getEntity().getAllSetTypes().size());
    pspf.app("Descendant Generations:", getEntity().getGenerations() - 1);
    pspf.app("Years to be predicted:", getEntity().getYears());
    pspf.app("Number of age classes:", getEntity().getNumberOfAgeClasses());
    return pspf.constructPanel();
  }

}
