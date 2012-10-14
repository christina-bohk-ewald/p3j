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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import p3j.misc.gui.GUI;

/**
 * Simple dialog to display warnings that occurred during the loading of a
 * projection.
 * 
 * @see p3j.misc.Serializer
 * 
 *      Created on 14.10.2012
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class ShowWarningAfterProjectionLoadingDialog extends JDialog {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3246519924977747183L;

  /** Width of the dialog. */
  public static final int DIALOG_WIDTH = 850;

  /** Height of the dialog. */
  public static final int DIALOG_HEIGHT = 300;

  /** The content panel. */
  private final JPanel contentPanel;

  /** The OK button that closes the dialog. */
  private final JButton okButton = new JButton("OK");
  {
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
  }

  /**
   * Instantiates a new dialog.
   * 
   * @param owner
   *          the owner
   * @param p3jConfiguration
   *          the p3j configuration
   */
  public ShowWarningAfterProjectionLoadingDialog(Frame owner,
      List<String> warnings) {
    super(owner, "Warnings During Projection Import", true);
    setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    GUI.centerOnScreen(this);
    contentPanel = new JPanel();
    contentPanel.setLayout(new BorderLayout());
    JTextArea explanation = new JTextArea(
        "Due to parameter name differences between versions, some parameter instances have been matched via name similarity to resolve ambiguity.\n"
            + "Please check if the matching is correct:");
    explanation.setEditable(false);
    contentPanel.add(explanation, BorderLayout.NORTH);
    JList<String> warningsList = new JList<>(
        warnings.toArray(new String[warnings.size()]));
    contentPanel.add(new JScrollPane(warningsList), BorderLayout.CENTER);
    contentPanel.add(okButton, BorderLayout.SOUTH);
    getContentPane().add(contentPanel);
  }
}