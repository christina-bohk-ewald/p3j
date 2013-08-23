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
package p3j.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.ComputationInstrumenterFactory;
import org.jamesii.core.experiments.taskrunner.parallel.ParallelComputationTaskRunnerFactory;
import org.jamesii.core.experiments.taskrunner.plugintype.TaskRunnerFactory;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStopFactory;
import org.jamesii.gui.application.SplashScreen;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.experiment.ExperimentExecutorThreadPool;
import org.jamesii.gui.experiment.execution.ExperimentThread;
import org.jamesii.gui.utils.BasicUtilities;

import p3j.database.DatabaseFactory;
import p3j.database.DatabaseType;
import p3j.gui.dialogs.CopyGenerationsDialog;
import p3j.gui.dialogs.DatabaseTypeSelectionDialog;
import p3j.gui.dialogs.ExecutionPreferencesDialog;
import p3j.gui.dialogs.NewProjectionDialog;
import p3j.gui.dialogs.PreferencesDialog;
import p3j.gui.dialogs.execstatus.ExecProgressInstrFactory;
import p3j.gui.misc.NavigationTreeTab;
import p3j.gui.misc.P3JConfigFile;
import p3j.gui.panels.WelcomePanel;
import p3j.gui.panels.dboverview.DatabaseOverviewPanel;
import p3j.gui.panels.projections.ProjectionTreePanel;
import p3j.gui.panels.results.ResultTreePanel;
import p3j.misc.LoadedProjectionFormatException;
import p3j.misc.Misc;
import p3j.misc.Serializer;
import p3j.misc.gui.GUI;
import p3j.pppm.ProjectionModel;
import p3j.pppm.readerwriter.database.PPPModelDatabaseReaderFactory;
import p3j.simulation.ExecutionMode;
import p3j.simulation.PPPMProcessorFactory;
import p3j.simulation.assignments.plugintype.ParamAssignmentGenFactory;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;

/**
 * Main user interface to P3J (singleton).
 * 
 * Created on July 16, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public final class P3J extends JFrame {

  /** Serialization ID. */
  private static final long serialVersionUID = -142244898041811667L;

  /** Initial window width. */
  static final int INITIAL_WIDTH = 1024;

  /** Initial window height. */
  static final int INITIAL_HEIGHT = 768;

  /** Initial width of the projection/result tree tab-pane. */
  static final int INITIAL_TREE_WIDTH = 300;

  /** Reference to singleton. */
  private static P3J instance;

  /** The splash screen to be shown at startup. */
  private static SplashScreen splashScreen;

  /** Current projection to be edited. */
  private static ProjectionModel currentProjection;

  /** Serialization tool. */
  private final Serializer serializer = new Serializer();

  // GUI elements

  /** The button to run a calculation. */
  private final JButton runButton = GUI.createIconButton("run.gif", "Run");
  {
    runButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        startExperiment();
      }
    });
  }

  /** The button in the toolbar shown the replication configurations. */
  private JButton execPrefsButton = new JButton();
  {
    execPrefsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        editExecutionPreferences();
      }
    });
  }

  /** Main toolbar. */
  private final JToolBar toolBar = new JToolBar();
  {
    toolBar.add(runButton);
    toolBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
    toolBar.putClientProperty(PlasticLookAndFeel.IS_3D_KEY, Boolean.FALSE);
    toolBar.add(execPrefsButton);
  }

  /** The desktop. */
  private final JDesktopPane desktop = new JDesktopPane();

  /** File chooser to save scenario files. */
  private final JFileChooser scenarioFileChooser = new JFileChooser();

  // File menu

  /** Menu to create a new scenario. */
  private final JMenuItem newProjectionMenu = new JMenuItem("New projection",
      KeyEvent.VK_N);
  {
    newProjectionMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
        ActionEvent.CTRL_MASK));
    newProjectionMenu.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        newProjection();
      }
    });
  }

  /** Menu to load a scenario. */
  private final JMenuItem openProjectionMenu = new JMenuItem(
      "Open projection...", KeyEvent.VK_O);
  {
    openProjectionMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
        ActionEvent.CTRL_MASK));
    openProjectionMenu.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        loadProjection();
      }
    });
  }

  /** Menu to quick-save. */
  private final JMenuItem quickSaveProjectionMenu = new JMenuItem(
      "Save projection", KeyEvent.VK_S);
  {
    quickSaveProjectionMenu.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    quickSaveProjectionMenu.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (currentProjectionFile == null) {
          saveProjection();
        } else {
          quickSaveProjection(currentProjectionFile);
        }
      }
    });
  }

  /** Menu to save scenario. */
  private final JMenuItem saveProjectionMenu = new JMenuItem(
      "Save projection as...");
  {
    saveProjectionMenu.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveProjection();
      }
    });
  }

  /** Menu to load results. */
  private final JMenuItem loadResultsMenu = new JMenuItem("Load results...");
  {
    loadResultsMenu.setVisible(false); // TODO
  }

  /** Menu to save results. */
  private final JMenuItem saveResultsMenu = new JMenuItem("Save results...");
  {
    saveResultsMenu.setVisible(false); // TODO
  }

  /** Menu to quick-save results. */
  private final JMenuItem quickSaveResultsMenu = new JMenuItem("Save results");
  {
    quickSaveResultsMenu.setVisible(false); // TODO
  }

  /** Menu to open preferences dialog. */
  private final JMenuItem preferencesMenu = new JMenuItem("Preferences...");
  {
    preferencesMenu.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        editPreferences();
      }
    });
  }

  /** Menu to open execution preferences dialog. */
  private final JMenuItem execPreferencesMenu = new JMenuItem(
      "Execution preferences...");
  {
    execPreferencesMenu.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        execPrefsButton.doClick();
      }
    });
  }

  /** Menu to quit. */
  private final JMenuItem quitMenu = new JMenuItem("Quit", KeyEvent.VK_Q);
  {
    quitMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
        ActionEvent.CTRL_MASK));
    quitMenu.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        quitApplication();
      }
    });
  }

  /** File menu. */
  private final JMenu fileMenu = new JMenu("File");
  {
    fileMenu.add(newProjectionMenu);
    fileMenu.add(openProjectionMenu);
    fileMenu.add(quickSaveProjectionMenu);
    fileMenu.add(saveProjectionMenu);
    // TODO: Implement binary result storage.
    // fileMenu.add(new JSeparator());
    fileMenu.add(loadResultsMenu);
    fileMenu.add(quickSaveResultsMenu);
    fileMenu.add(saveResultsMenu);
    fileMenu.add(new JSeparator());
    fileMenu.add(preferencesMenu);
    fileMenu.add(execPreferencesMenu);
    fileMenu.add(new JSeparator());
    fileMenu.add(quitMenu);
    fileMenu.setMnemonic(KeyEvent.VK_F);
  }

  // Parameter menu

  /** Menu to edit sets. */
  private final JMenuItem editSetsMenu = new JMenuItem("Edit Sets...");
  {
    editSetsMenu.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        editSets();
      }
    });
  }

  /** Menu to edit Settypes. */
  private final JMenuItem editSetTypesMenu = new JMenuItem("Edit Settypes...");
  {
    editSetTypesMenu.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        editSetTypes();
      }
    });

  }

  /** Menu to input a matrix quickly. */
  private final JMenuItem quickMatrixInput = new JMenuItem(
      "Quick Matrix Input...");
  {
    quickMatrixInput.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        inputMatrixQuickly();
      }
    });
  }

  /** Menu to input a matrix quickly. */
  private final JMenuItem copyGenerations = new JMenuItem("Copy Generations...");
  {
    copyGenerations.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        copyGenerations();
      }
    });
  }

  /** Parameter menu. */
  private final JMenu parameterMenu = new JMenu("Parameters");
  {
    parameterMenu.add(new JSeparator());
    parameterMenu.add(editSetTypesMenu);
    parameterMenu.add(editSetsMenu);
    parameterMenu.add(new JSeparator());
    parameterMenu.add(quickMatrixInput);
    parameterMenu.add(copyGenerations);
  }

  /** Menu to open info panel. */
  private final JMenuItem infoMenu = new JMenuItem("Info (Welcome Screen)");
  {
    infoMenu.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        contentPanel.removeAll();
        contentPanel.add(new WelcomePanel(), BorderLayout.CENTER);
        pack();
        repaint();
      }
    });
  }

  /** Help menu. */
  private final JMenu helpMenu = new JMenu("Help");
  {
    helpMenu.add(infoMenu);
  }

  /** Main menu bar. */
  private final JMenuBar menuBar = new JMenuBar();
  {
    menuBar.add(fileMenu);
    // TODO: Redesign and refactor old parameter dialogs
    // menuBar.add(parameterMenu);
    menuBar.add(helpMenu);
    menuBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
    menuBar.putClientProperty(PlasticLookAndFeel.IS_3D_KEY, Boolean.FALSE);
  }

  // Dialogs to be used

  /** Stores name of the last saved projection. */
  private String currentProjectionFile;

  /** Large panel for displaying the content of a selected element. */
  private JPanel contentPanel = new JPanel(GUI.getStdBorderLayout());

  /**
   * The tabbed pane for viewing the different levels (database overview,
   * structure of a single projection, etc).
   */
  private JTabbedPane tabbedPane = new JTabbedPane();

  /** Panel that allows navigation through the current projection. */
  private ProjectionTreePanel projTreePanel = new ProjectionTreePanel(
      getCurrentProjection(), contentPanel);

  /**
   * Panel that allows navigation through the results of the current projection.
   */
  private ResultTreePanel resultsPanel = new ResultTreePanel(
      getCurrentProjection(), contentPanel);

  /**
   * Panel that allows to navigate through the overall database and load
   * projection models etc.
   */
  private DatabaseOverviewPanel dbOverviewPanel;

  /** The configuration file. */
  private final P3JConfigFile configFile = new P3JConfigFile();

  /**
   * Default constructor.
   */
  private P3J() {

    try {
      getConfigFile().readFile("./" + Misc.CONFIG_FILE);
    } catch (Exception ex) {
      getConfigFile().setFileName("./" + Misc.CONFIG_FILE);
      getConfigFile().setDefaults();
      try {
        getConfigFile().writeFile();
      } catch (Exception e) {
        GUI.printErrorMessage(this,
            "Error while writing default configuration file.",
            "An error occurred while attempting to write the file '"
                + Misc.CONFIG_FILE + "':" + ex, ex);
      }
      GUI.printErrorMessage(this, "Error while loading configuration file.",
          "An error occurred while attempting to read the file '"
              + Misc.CONFIG_FILE + "' from the working directory:" + ex, ex);
    }

    updateFromExecConfig();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  /**
   * Edits the preferences.
   */
  protected void editPreferences() {

    DatabaseTypeSelectionDialog dbTypeDialog = new DatabaseTypeSelectionDialog(
        this, (DatabaseType) getConfigFile().get(Misc.PREF_DB_TYPE));
    dbTypeDialog.setVisible(true);

    // Check whether user cancelled
    if (dbTypeDialog.getDBType() == null)
      return;

    PreferencesDialog prefsDialog = new PreferencesDialog(this,
        getConfigFile(), dbTypeDialog.getDBType());
    prefsDialog.setVisible(true);
    try {
      getConfigFile().writeFile();
    } catch (Exception ex) {
      SimSystem.report(ex);
      GUI.printErrorMessage(this, "Error writing configuration file.",
          "An error occurred while attempting to write to file '"
              + getConfigFile().getFileName() + "' in the working directory:"
              + ex);
    }
    updateFromConfig();
  }

  /**
   * Edits the execution preferences.
   */
  protected void editExecutionPreferences() {
    ExecutionPreferencesDialog execPrefsDialog = new ExecutionPreferencesDialog(
        this);
    execPrefsDialog.setVisible(true);
    try {
      getConfigFile().writeFile();
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE, "Error writing configuration file.", ex);
      GUI.printErrorMessage(this, "Error writing configuration file.",
          "An error occurred while attempting to write to file '"
              + getConfigFile().getFileName() + "' in the working directory:"
              + ex);
    }
    updateFromExecConfig();
  }

  /**
   * Displays a dialog to edit sets.
   */
  protected void editSets() {
    // (new EditSetsDialog(this,
    // P3J.getCurrentProjection())).setVisible(true);
  }

  /**
   * Displays a dialog to edit Settypes.
   */
  protected void editSetTypes() {
    // (new EditSetTypesDialog(this,
    // getCurrentProjection())).setVisible(true);
  }

  /**
   * Opens dialog to copy generations.
   */
  protected void copyGenerations() {
    CopyGenerationsDialog cgd = new CopyGenerationsDialog(
        getCurrentProjection(), this);
    cgd.setVisible(true);
  }

  /**
   * Create a new scenario in database.
   */
  protected void newProjection() {
    NewProjectionDialog newProjectionDialog = new NewProjectionDialog(this,
        DatabaseFactory.getDatabaseSingleton());
    newProjectionDialog.setVisible(true);
    if (newProjectionDialog.hasCreatedNewProjection()) {
      currentProjection = newProjectionDialog.getNewProjection();
      projTreePanel.setProjection(currentProjection);
      switchNavigationTreeTab(NavigationTreeTab.PROJECTION_OVERVIEW);
      dbOverviewPanel.totalRefresh();
    }
  }

  /**
   * Retrieves user interface singleton.
   * 
   * @return instance of P3J
   */
  public static P3J getInstance() {
    if (instance == null) {
      instance = new P3J();
      instance.initUI();
    }
    return instance;
  }

  /**
   * Initializes user interface.
   */
  private void initUI() {

    this.setTitle("P3J : Probabilistic Population Projection");
    this.setPreferredSize(new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT));

    this.setJMenuBar(menuBar);
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(toolBar, BorderLayout.NORTH);
    this.getContentPane().add(desktop, BorderLayout.CENTER);

    DatabaseFactory.setDbConnData(getConfigFile().getDBConnectionData());
    currentProjection = null;
    dbOverviewPanel = new DatabaseOverviewPanel(contentPanel);
    projTreePanel = new ProjectionTreePanel(getCurrentProjection(),
        contentPanel);

    tabbedPane.removeAll();
    tabbedPane.addTab("Database", dbOverviewPanel);
    tabbedPane.addTab("Projection", projTreePanel);
    tabbedPane.addTab("Results", resultsPanel);
    contentPanel.removeAll();
    contentPanel.add(new WelcomePanel(), BorderLayout.CENTER);

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.add(tabbedPane);
    splitPane.setDividerLocation(INITIAL_TREE_WIDTH);
    splitPane.add(contentPanel);

    this.getContentPane().add(splitPane);

    // Action handler: close window
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent l) {
        P3J.this.quitApplication();
      }
    });

    this.pack();
    this.repaint();
  }

  /**
   * Retrieves current PPP model.
   * 
   * @return the PPP model that is currently edited
   */
  public static ProjectionModel getCurrentProjection() {
    return currentProjection;
  }

  /**
   * Save current projection.
   */
  void saveProjection() {

    if (currentProjection == null) {
      GUI.printMessage(this, "No projection to save.",
          "In order to save a projection, you first have to load it from the database.");
      return;
    }

    int userReaction = this.scenarioFileChooser.showDialog(this, "Save");

    if (userReaction != JFileChooser.ERROR_OPTION
        && userReaction != JFileChooser.CANCEL_OPTION) {
      File projectionFile = this.scenarioFileChooser.getSelectedFile();

      String ending = Misc.getFileEnding(projectionFile);

      if (ending.compareToIgnoreCase("p3j") != 0) {
        projectionFile = new File(projectionFile.getAbsolutePath() + ".p3j");
      }

      if (projectionFile != null
          && (!projectionFile.exists() || projectionFile.canWrite())) {
        quickSaveProjection(projectionFile.getAbsolutePath());
      }

    }
  }

  /**
   * Quick-save current scenario.
   * 
   * @param scenarioFile
   *          the file to which the scenario shall be saved
   */
  void quickSaveProjection(final String scenarioFile) {
    (new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        try {
          serializer.save(getCurrentProjection(), scenarioFile);
          currentProjectionFile = scenarioFile;
        } catch (Exception ex) {
          SimSystem.report(Level.SEVERE, "Error while saving scenario file.",
              ex);
          GUI.printErrorMessage(P3J.getInstance(),
              "Error while saving scenario file.",
              "An error occurred while attempting to save file to '"
                  + scenarioFile + "': " + ex);
        }
        return null;
      }
    }).execute();

  }

  /**
   * Load a scenario.
   */
  void loadProjection() {

    int userReaction = this.scenarioFileChooser.showDialog(this, "Open");

    if (userReaction != JFileChooser.ERROR_OPTION
        && userReaction != JFileChooser.CANCEL_OPTION) {
      final File scenarioFile = this.scenarioFileChooser.getSelectedFile();
      final P3J owner = this;
      if (scenarioFile != null && scenarioFile.exists()) {
        (new SwingWorker<Void, Void>() {
          @Override
          protected Void doInBackground() throws Exception {
            try {
              ProjectionModel loadedModel = serializer.loadProjection(
                  scenarioFile.getAbsolutePath(),
                  DatabaseFactory.getDatabaseSingleton());
              currentProjectionFile = scenarioFile.getAbsolutePath();
              setCurrentProjection(loadedModel);
            } catch (IOException | ClassNotFoundException ex) {
              GUI.printErrorMessage(
                  owner,
                  "Error while opening scenario file.",
                  "An error occurred while attempting to load file from '"
                      + scenarioFile.getAbsolutePath() + "': "
                      + ex.getMessage(), ex);
            } catch (LoadedProjectionFormatException ex) {
              GUI.printErrorMessage(owner,
                  "Error while loading projection into database.",
                  "An error occurred while attempting to load the projection:"
                      + ex.getMessage(), ex);
            }
            return null;
          }
        }).execute();
      }
    }
  }

  // Event Handler

  /**
   * Quit application.
   */
  public void quitApplication() {
    if (GUI.printQuestion(this, "Quit?", "Do you really want to quit?")) {
      System.exit(0);
    }
  }

  /**
   * Switches to a new projection model for editing.
   * 
   * @param newProjModel
   *          the new projection model to be edited
   */
  public void setCurrentProjection(ProjectionModel newProjModel) {

    if (currentProjection != null) {
      DatabaseFactory.getDatabaseSingleton().saveProjection(currentProjection);
    }

    currentProjection = newProjModel;
    dbOverviewPanel.totalRefresh();

    if (currentProjection != null) {
      projTreePanel.setProjection(currentProjection);
      resultsPanel.setProjection(currentProjection);
      switchNavigationTreeTab(NavigationTreeTab.PROJECTION_OVERVIEW);
    }
  }

  /**
   * Input a matrix quickly.
   */
  protected void inputMatrixQuickly() {
    // (new QuickInputDialog(this,
    // getCurrentProjection())).setVisible(true);
  }

  /**
   * Executed to run a calculation.
   */
  protected void startExperiment() {
    BaseExperiment baseExperiment = new BaseExperiment();
    configureModelLocation(baseExperiment);
    configureSimulator(baseExperiment);
    configureMultiThreading(baseExperiment);
    ExperimentExecutorThreadPool.getInstance().getExecutor()
        .execute(new ExperimentThread(baseExperiment) {
          @Override
          protected List<List<RunInformation>> doInBackground() {
            List<List<RunInformation>> results = null;
            try {
              results = super.doInBackground();
            } catch (Throwable t) {
              GUI.printErrorMessage("Error executing model", t);
            }
            return results;
          }
        });
  }

  /**
   * Configures the given experiment for multi-threading.
   * 
   * @param baseExperiment
   *          the experiment to be configured
   */
  private void configureMultiThreading(BaseExperiment baseExperiment) {
    int numOfTrials = (Integer) getConfigFile().get(Misc.PREF_NUM_TRIALS);
    int numOfThreads = (Integer) getConfigFile().get(
        Misc.PREF_NUM_PARALLEL_THREADS);
    double stopTime = Math.ceil((double) numOfTrials / numOfThreads);

    baseExperiment.setRepeatRuns(numOfThreads);
    baseExperiment
        .setComputationTaskStopPolicyFactory(new ParameterizedFactory<ComputationTaskStopPolicyFactory>(
            new SimTimeStopFactory(), new ParameterBlock(stopTime,
                SimTimeStopFactory.SIMEND)));
    baseExperiment
        .setComputationInstrumenterFactory(new ParameterizedFactory<ComputationInstrumenterFactory>(
            new ExecProgressInstrFactory(), new ParameterBlock(numOfTrials,
                ExecProgressInstrFactory.NUM_OF_TRIALS)));

    if (numOfThreads > 1) {
      baseExperiment
          .setTaskRunnerFactory(new ParameterizedFactory<TaskRunnerFactory>(
              new ParallelComputationTaskRunnerFactory(), new ParameterBlock(
                  numOfThreads, ParallelComputationTaskRunnerFactory.NUM_CORES)));
    }
  }

  /**
   * Configures experiment to use PPPM simulator.
   * 
   * @param baseExperiment
   *          the experiment to be configured
   */
  private void configureSimulator(BaseExperiment baseExperiment) {
    ParameterBlock processorParameters = baseExperiment
        .getParameters()
        .getParameterBlock()
        .addSubBlock(ProcessorFactory.class.getName(),
            PPPMProcessorFactory.class.getName());
    processorParameters.addSubBl(ParamAssignmentGenFactory.class.getName(),
        ((ExecutionMode) getConfigFile().get(Misc.PREF_EXECUTION_MODE))
            .getFactoryName());
  }

  /**
   * Configures experiment regarding model location.
   * 
   * @param baseExperiment
   *          the experiment to be configured
   */
  private void configureModelLocation(BaseExperiment baseExperiment) {
    try {
      baseExperiment.setModelLocation(new URI(
          PPPModelDatabaseReaderFactory.DEFAULT_URI));
      baseExperiment.setModelRWParameters(PPPModelDatabaseReaderFactory
          .createReaderParams(DatabaseFactory.getDbConnData(),
              currentProjection.getID()));
    } catch (Exception ex) {
      GUI.printErrorMessage(this, "Could not configure model location",
          "Configuration of model reader failed.", ex);
    }
  }

  /**
   * Main function.
   * 
   * @param argv
   *          command line arguments
   */
  public static void main(String[] argv) {

    showSplashScreen();
    configureEnglishAsUILanguage();

    processCmdLineArgs(argv);

    try {
      String lf = LookUtils.IS_OS_WINDOWS ? Options
          .getCrossPlatformLookAndFeelClassName() : Options
          .getSystemLookAndFeelClassName();
      UIManager.setLookAndFeel(lf);
    } catch (Exception e) {
      SimSystem.report(e);
    }

    P3J p3j = P3J.getInstance();
    splashScreen.setVisible(false);
    GUI.centerOnScreen(p3j);
    p3j.setVisible(true);
  }

  /**
   * Configures English as UI language. Settings for localized names of default
   * buttons are overridden.
   */
  private static void configureEnglishAsUILanguage() {
    Locale.setDefault(Locale.US);
    UIManager.put("OptionPane.yesButtonText", "Yes");
    UIManager.put("OptionPane.noButtonText", "No");
    UIManager.put("OptionPane.okButtonText", "OK");
    UIManager.put("OptionPane.cancelButtonText", "Cancel");
  }

  /**
   * Process cmd line arguments.
   * 
   * @param argv
   *          the argv
   */
  private static void processCmdLineArgs(String[] argv) {
    if (argv.length > 0) {
      SimSystem
          .report(Level.WARNING,
              "No command line arguments are currently supported. Arguments will be ignored.");
    }
  }

  /**
   * Shows splash screen.
   */
  private static void showSplashScreen() {
    try {
      BasicUtilities.invokeAndWaitOnEDT(new Runnable() {
        @Override
        public void run() {
          Image image = null;
          try {
            image = IconManager.getImage("image:/p3j/splashscreen.png");
          } catch (Exception ex) {
            SimSystem.report(ex);
          }
          splashScreen = new SplashScreen(image, "", "Version 0.9.9", "", true);
          splashScreen.setVisible(true);
          splashScreen.pack();
        }
      });
    } catch (InterruptedException e1) {
      SimSystem.report(e1);
    } catch (InvocationTargetException e1) {
      SimSystem.report(e1);
    }
  }

  /**
   * Has to be called to refresh GUI after a {@link ProjectionModel} has been
   * deleted.
   * 
   * @param deletedModel
   *          the model that has been deleted from the database
   */
  public void projectionDeleted(ProjectionModel deletedModel) {
    dbOverviewPanel.totalRefresh();
    if (currentProjection != deletedModel) {
      setCurrentProjection(null);
      switchNavigationTreeTab(NavigationTreeTab.DB_OVERVIEW);
    }
  }

  /**
   * Switch the navigation tree tab.
   * 
   * @param targetTab
   *          the desired tab of the navigation tree
   */
  public void switchNavigationTreeTab(NavigationTreeTab targetTab) {
    tabbedPane.setSelectedIndex(targetTab.getTabIndex());
  }

  /**
   * Update UI in case configuration changed.
   */
  private void updateFromConfig() {
    this.getContentPane().removeAll();
    initUI();
  }

  /**
   * Updates the summary of the execution preferences displayed on the buttons
   * that opens them.
   */
  private void updateFromExecConfig() {
    execPrefsButton.setText("#Trials:"
        + getConfigFile().get(Misc.PREF_NUM_TRIALS) + ", #Threads:"
        + getConfigFile().get(Misc.PREF_NUM_PARALLEL_THREADS) + ", Mode:"
        + getConfigFile().get(Misc.PREF_EXECUTION_MODE));
  }

  /**
   * Get the configuration file.
   * 
   * @return the configFile
   */
  public P3JConfigFile getConfigFile() {
    return configFile;
  }

  /**
   * Refreshes the navigation tree and selects the root node in the currently
   * selected tab, so that all relevant GUI elements are refreshed.
   */
  public void refreshNavigationTree() {
    NavigationTreeTab currentTab = NavigationTreeTab.values()[tabbedPane
        .getSelectedIndex()];
    setCurrentProjection(currentProjection);
    switchNavigationTreeTab(currentTab);
    switch (currentTab) {
    case PROJECTION_OVERVIEW:
      projTreePanel.selectRoot();
      break;
    case RESULTS_OVERVIEW:
      resultsPanel.selectRoot();
      break;
    case DB_OVERVIEW:
      dbOverviewPanel.selectRoot();
      break;
    default:
      break;
    }

  }
}
