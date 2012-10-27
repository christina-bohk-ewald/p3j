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

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import p3j.gui.P3J;
import p3j.gui.misc.P3JConfigFile;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.misc.Misc;
import p3j.misc.gui.GUI;
import p3j.simulation.ExecutionMode;

/**
 * Simple dialog to show the execution preferences.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class ExecutionPreferencesDialog extends JDialog {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3246519924977747183L;

  /** Width of the dialog. */
  public static final int DIALOG_WIDTH = 600;

  /** Height of the dialog. */
  public static final int DIALOG_HEIGHT = 200;

  /** The width of the key column in the form. */
  private static final int FORM_KEY_WIDTH = 200;

  /** The width of the value column in the form. */
  private static final int FORM_VALUE_WIDTH = 100;

  /** The content panel. */
  private final JPanel contentPanel;

  /** The field for the number of trials. */
  private final JTextField numOfTrials = new JTextField();

  /** The field for the number of parallel threads. */
  private final JTextField numOfParallelThreads = new JTextField();

  /** The p3j configuration file. */
  private final P3JConfigFile p3jConfiguration;

  /** The execution mode button group. */
  private final ButtonGroup execModeButtonGroup = new ButtonGroup();

  /** The apply button. */
  private final JButton apply = new JButton("Apply");
  {
    apply.addActionListener(new ApplyAction(this));
  }

  /** The cancel button. */
  private final JButton cancel = new JButton("Cancel");
  {
    cancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
  }

  /** The 'reset to defaults'-button. */
  private final JButton resetToDefaults = new JButton("Reset Defaults");
  {
    resetToDefaults.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        numOfTrials.setText("" + Misc.DEFAULT_NUM_TRIALS);
        numOfParallelThreads.setText("" + Misc.DEFAULT_NUM_PARALLEL_THREADS);

        ExecutionMode defaultMode = Misc.DEFAULT_EXEC_MODE;
        Enumeration<AbstractButton> execModeButtons = execModeButtonGroup
            .getElements();
        while (execModeButtons.hasMoreElements()) {
          AbstractButton execModeButton = execModeButtons.nextElement();
          if (execModeButton.getText().equals(defaultMode.toString())) {
            execModeButton.setSelected(true);
            break;
          }
        }
        contentPanel.repaint();
      }
    });
  }

  /** The buttons. */
  private final List<JButton> buttons = new ArrayList<JButton>();
  {
    buttons.add(resetToDefaults);
    buttons.add(cancel);
    buttons.add(apply);
  }

  /**
   * Instantiates a new execution preferences dialog.
   * 
   * @param owner
   *          the owner
   */
  public ExecutionPreferencesDialog(Frame owner) {
    super(owner, "Edit Execution Preferences", true);
    p3jConfiguration = P3J.getInstance().getConfigFile();
    setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    GUI.centerOnScreen(this);

    PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(
        FORM_KEY_WIDTH, FORM_VALUE_WIDTH, buttons, 2);
    numOfTrials.setText("" + p3jConfiguration.get(Misc.PREF_NUM_TRIALS));
    pspf.app(Misc.PREF_NUM_TRIALS + ":", numOfTrials);

    numOfParallelThreads.setText(""
        + p3jConfiguration.get(Misc.PREF_NUM_PARALLEL_THREADS));
    pspf.app(Misc.PREF_NUM_PARALLEL_THREADS + ":", numOfParallelThreads);

    pspf.app(Misc.PREF_EXECUTION_MODE + ":", createExecutionModePanel());
    contentPanel = pspf.constructPanel();
    this.getContentPane().add(contentPanel);
  }

  /**
   * Create the panel to select an execution mode.
   * 
   * @return the execution mode panel
   */
  private JPanel createExecutionModePanel() {
    JPanel execModePanel = new JPanel();
    ExecutionMode currentMode = (ExecutionMode) p3jConfiguration
        .get(Misc.PREF_EXECUTION_MODE);

    for (ExecutionMode mode : ExecutionMode.values()) {
      JRadioButton button = new JRadioButton(mode.toString());
      execModePanel.add(button);
      execModeButtonGroup.add(button);
      if (mode == currentMode) {
        button.setSelected(true);
      }
    }
    return execModePanel;
  }

  /**
   * The application of the new preferences.
   */
  class ApplyAction implements ActionListener {

    /** The owner. */
    private Component owner;

    /**
     * Instantiates a new apply action executioner.
     * 
     * @param own
     *          the owner
     */
    ApplyAction(Component own) {
      owner = own;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        Integer numTrials = Integer.parseInt(numOfTrials.getText());
        Integer numParallelThreads = Integer.parseInt(numOfParallelThreads
            .getText());

        Enumeration<AbstractButton> execModeButtons = execModeButtonGroup
            .getElements();
        ExecutionMode execMode = Misc.DEFAULT_EXEC_MODE;
        while (execModeButtons.hasMoreElements()) {
          AbstractButton execModeButton = execModeButtons.nextElement();
          if (execModeButton.isSelected()) {
            execMode = ExecutionMode.forString(execModeButton.getText());
            break;
          }
        }

        // Increase number of trials in case they are not a multiple of the
        // number of threads
        if (numTrials % numParallelThreads != 0)
          numTrials += (numParallelThreads - numTrials % numParallelThreads);

        p3jConfiguration.put(Misc.PREF_NUM_TRIALS, numTrials);
        p3jConfiguration
            .put(Misc.PREF_NUM_PARALLEL_THREADS, numParallelThreads);
        p3jConfiguration.put(Misc.PREF_EXECUTION_MODE, execMode);
        setVisible(false);
      } catch (Exception ex) {
        GUI.printErrorMessage(owner, "Error applying new settings.",
            "The new settings cannot be applied:" + ex.getMessage(), ex);
      }
    }
  }
}
