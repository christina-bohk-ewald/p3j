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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import p3j.database.DatabaseType;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.misc.gui.GUI;

/**
 * Allows user to select a database type to use.
 * 
 * Created on 27.10.2012
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class DatabaseTypeSelectionDialog extends JDialog {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6739792487369234085L;

  private static final int DIALOG_WIDTH = 500;

  private static final int DIALOG_HEIGHT = 200;

  private final JPanel contentPanel = new JPanel(GUI.getStdBorderLayout());

  private DatabaseType dbType = DatabaseType.HSQLDB;

  public DatabaseTypeSelectionDialog(Frame owner, DatabaseType currentDBType) {
    super(owner, "Select Database Type", true);
    setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    GUI.centerOnScreen(this);

    dbType = currentDBType;

    JButton next = new JButton("Next");
    next.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });

    JButton cancel = new JButton("Cancel");
    cancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dbType = null;
        setVisible(false);
      }
    });

    List<JButton> buttons = new ArrayList<JButton>();
    buttons.add(cancel);
    buttons.add(next);

    PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(0,
        buttons, 1);

    JPanel radioButtonPanel = createRadioButtons();

    contentPanel.add(radioButtonPanel, BorderLayout.CENTER);
    contentPanel.add(pspf.constructPanel(), BorderLayout.SOUTH);
    this.getContentPane().add(contentPanel);
  }

  /**
   * Creates the radio buttons.
   * 
   * @return the panel containing the radio buttons
   */
  private JPanel createRadioButtons() {
    JPanel radioButtonPanel = new JPanel();
    BoxLayout layout = new BoxLayout(radioButtonPanel, BoxLayout.Y_AXIS);
    radioButtonPanel.setLayout(layout);
    ButtonGroup dbTypesRadioButtons = new ButtonGroup();
    for (final DatabaseType type : DatabaseType.values()) {
      JRadioButton radioButton = new JRadioButton(type.toString(),
          type == dbType);
      radioButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          dbType = type;
        }
      });
      dbTypesRadioButtons.add(radioButton);
      radioButtonPanel.add(radioButton);
    }

    JPanel resultPanel = new JPanel(GUI.getStdBorderLayout());
    resultPanel.add(radioButtonPanel, BorderLayout.CENTER);
    resultPanel.add(new JPanel(), BorderLayout.NORTH);
    resultPanel.add(new JPanel(), BorderLayout.WEST);
    return resultPanel;
  }

  public DatabaseType getDBType() {
    return dbType;
  }

}
