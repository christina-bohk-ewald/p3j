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
package p3j.gui.panels.results;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.tree.TreePath;

import p3j.database.DatabaseFactory;
import p3j.experiment.results.ResultExport;
import p3j.experiment.results.ResultsOfTrial;
import p3j.gui.P3J;
import p3j.gui.dialogs.ConfigureResultFilterDialog;
import p3j.gui.dialogs.execstatus.SimpleProgressDialog;
import p3j.gui.misc.SubNodeSummary;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.gui.panels.projections.IProjectionTree;
import p3j.gui.panels.projections.ProjectionTreeNode;
import p3j.misc.IProgressObserver;
import p3j.misc.gui.GUI;
import p3j.pppm.ProjectionModel;

/**
 * Represents the root of the results tree.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ResultTreeRoot extends ProjectionTreeNode<ProjectionModel> {

  /** The key width (left column) of the panel. */
  private static final int PANEL_KEY_WIDTH = 50;

  /** Serialization ID. */
  private static final long serialVersionUID = -425870180408032392L;

  /**
   * Instantiates a new result tree root.
   * 
   * @param projectionModel
   *          the projection model
   */
  public ResultTreeRoot(ProjectionModel projectionModel) {
    super(projectionModel, "Overall Results");
  }

  @Override
  public JPanel selected(TreePath selectionPath, final IProjectionTree projTree) {

    JButton generateReport = new JButton("<html><b>Generate Report</b></html>");
    generateReport.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        (new SwingWorker<Void, Void>() {
          @Override
          protected Void doInBackground() throws Exception {
            JFileChooser fileChooser = GUI
                .getDirectoryChooser("Determine in which directory to store the report");
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
              ResultExport resultExport = configureResultExport(getEntity(),
                  fileChooser.getSelectedFile());
              if (resultExport == null) {
                return null;
              }
              try {
                IProgressObserver progress = SimpleProgressDialog.showDialog(
                    P3J.getInstance(), "Generating results report", "", 1, true);
                resultExport.createResultReport(progress);
                progress.taskFinished();
              } catch (Exception ex) {
                GUI.printErrorMessage("Report Generation Failed", ex);
              }
            }
            return null;
          }

        }).execute();
      }
    });

    JButton exportAggregatedData = new JButton("Aggregate");
    exportAggregatedData.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        (new SwingWorker<Void, Void>() {
          @Override
          protected Void doInBackground() throws Exception {
            JFileChooser fileChooser = GUI
                .getDirectoryChooser("Select directory for aggregated data");
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
              ResultExport resultExport = configureResultExport(getEntity(),
                  fileChooser.getSelectedFile());
              // TODO: Add dialog for aggregated data
              if (resultExport == null) {
                return null;
              }
              try {
                IProgressObserver progress = SimpleProgressDialog.showDialog(
                    P3J.getInstance(), "Aggregating results", "", 1, true);
                resultExport.exportAggregatedResults(progress);
                progress.taskFinished();

              } catch (Exception ex) {
                GUI.printErrorMessage("Aggregated Data Export Failed", ex);
              }
            }
            return null;
          }

        }).execute();
      }
    });

    JButton exportData = new JButton("Export");
    exportData.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = GUI
            .getDirectoryChooser("Select directory for export");
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
          ResultExport resultExport = configureResultExport(getEntity(),
              fileChooser.getSelectedFile());
          if (resultExport == null) {
            return;
          }
          try {
            resultExport.exportAllResults();
          } catch (Exception ex) {
            GUI.printErrorMessage("Data Export Failed", ex);
          }
        }
      }
    });

    JButton clearResults = new JButton("Clear Results");
    clearResults.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        (new SwingWorker<Void, Void>() {
          @Override
          protected Void doInBackground() throws Exception {
            if (GUI.printQuestion(P3J.getInstance(),
                "Really delete all results?",
                "Approving this will delete ALL results associated with this projection.")) {
              try {
                IProgressObserver progress = SimpleProgressDialog.showDialog(
                    P3J.getInstance(), "Clearing Results",
                    "Clearing all trial results:", 1, true);
                DatabaseFactory.getDatabaseSingleton().deleteAllResults(
                    getEntity(), progress);
                P3J.getInstance().refreshNavigationTree();
                progress.taskFinished();
              } catch (Exception ex) {
                GUI.printErrorMessage("Result Deletion Failed", ex);
              }
            }
            return null;
          }
        }).execute();
      }
    });

    List<JButton> buttons = new ArrayList<JButton>();
    buttons.add(clearResults);
    buttons.add(exportData);
    buttons.add(exportAggregatedData);
    buttons.add(generateReport);

    PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(
        PANEL_KEY_WIDTH, buttons, 0);
    pspf.sep("General Information");
    pspf.app("Projection:", getEntity().getName());
    pspf.app("#Trials:", getChildCount());
    pspf.appPreview(new SubNodeSummary<ResultsOfTrial>(this, projTree,
        ResultsOfTrial.class));
    return pspf.constructPanel();
  }

  /**
   * Configures result export.
   * 
   * @param projection
   *          the projection
   * @param targetDir
   *          the target directory
   * 
   * @return the result export
   */
  protected ResultExport configureResultExport(ProjectionModel projection,
      File targetDir) {
    ConfigureResultFilterDialog resultFilterDialog = new ConfigureResultFilterDialog(
        null, getEntity());
    resultFilterDialog.setVisible(true);
    return resultFilterDialog.isCancelled() ? null : new ResultExport(
        projection, targetDir, resultFilterDialog.getConfiguredResultFilter());
  }

  @Override
  public void deselected() {
  }

}
