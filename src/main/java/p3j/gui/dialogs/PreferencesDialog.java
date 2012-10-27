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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import p3j.database.DatabaseType;
import p3j.database.hibernate.P3MDatabase;
import p3j.gui.misc.P3JConfigFile;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.misc.Misc;
import p3j.misc.gui.GUI;

/**
 * Dialog to edit preferences.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class PreferencesDialog extends JDialog {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3246519924977747183L;

  /** Width of the dialog. */
  public static final int DIALOG_WIDTH = 850;

  /** Height of the dialog. */
  public static final int DIALOG_HEIGHT = 220;

  /** The width of the key column in the form. */
  private static final int FORM_KEY_WIDTH = 200;

  /** The content panel. */
  private final JPanel contentPanel;

  /**
   * Instantiates a new preferences dialog.
   * 
   * @param owner
   *          the owner
   * @param p3jConfiguration
   *          the p3j configuration
   */
  public PreferencesDialog(Frame owner, final P3JConfigFile p3jConfiguration) {
    super(owner, "Edit Preferences", true);
    setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    GUI.centerOnScreen(this);

    // Create new assignment
    JButton apply = new JButton("Apply");
    JButton cancel = new JButton("Cancel");
    JButton resetToDefaults = new JButton("Reset Defaults");
    JButton testDBConnection = new JButton("Test DB Connection");

    List<JButton> buttons = new ArrayList<JButton>();
    buttons.add(testDBConnection);
    buttons.add(resetToDefaults);
    buttons.add(cancel);
    buttons.add(apply);

    final PreferencesDialog thisDialog = this;

    PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(
        FORM_KEY_WIDTH, buttons, 2);

    pspf.sep("Database Connection");
    final JTextField dbURL = new JTextField(
        (String) p3jConfiguration.get(Misc.PREF_DB_URL));
    pspf.app(Misc.PREF_DB_URL + ":", dbURL);

    final JTextField dbUserName = new JTextField(
        (String) p3jConfiguration.get(Misc.PREF_DB_USER));
    pspf.app(Misc.PREF_DB_USER + ":", dbUserName);

    final JTextField dbPassword = new JPasswordField(
        (String) p3jConfiguration.get(Misc.PREF_DB_PWD));
    pspf.app(Misc.PREF_DB_PWD + ":", dbPassword);

    apply.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Exception ex = P3MDatabase.testConnection(dbURL.getText(),
            dbUserName.getText(), dbPassword.getText());
        if (ex == null) {
          p3jConfiguration.put(Misc.PREF_DB_URL, dbURL.getText());
          p3jConfiguration.put(Misc.PREF_DB_USER, dbUserName.getText());
          p3jConfiguration.put(Misc.PREF_DB_PWD, dbPassword.getText());
        } else {
          GUI.printErrorMessage(
              thisDialog,
              "DB Connection Failed",
              "Old database configuration retained - the new connection data caused an error:"
                  + ex.getMessage(), ex);
        }
        setVisible(false);
      }
    });

    cancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });

    testDBConnection.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Exception ex = P3MDatabase.testConnection(dbURL.getText(),
            dbUserName.getText(), dbPassword.getText());

        if (ex != null) {
          GUI.printErrorMessage(
              thisDialog,
              "DB Connection Failed",
              "The connection to the database could not be established:"
                  + ex.getMessage(), ex);
        } else {
          GUI.printMessage(thisDialog, "DB Connection Test",
              "A connection was established successfully.");
        }
      }
    });

    resetToDefaults.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dbURL.setText(Misc.DEFAULT_DB_URL);
        dbUserName.setText(Misc.DEFAULT_DB_USER);
        dbPassword.setText(Misc.DEFAULT_DB_PWD);
        contentPanel.repaint();
      }
    });


    contentPanel = pspf.constructPanel();
    this.getContentPane().add(contentPanel);
  }
}
