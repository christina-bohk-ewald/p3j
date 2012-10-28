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

import james.core.data.DBConnectionData;
import james.core.util.misc.Pair;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import p3j.database.DatabaseType;
import p3j.database.IPreferencesUIProvider;
import p3j.database.hibernate.P3MDatabase;
import p3j.gui.misc.P3JConfigFile;
import p3j.gui.panels.PropertiesShowPanelFactory;
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
  public static final int DIALOG_HEIGHT = 150;

  /** The width of the key column in the form. */
  private static final int FORM_KEY_WIDTH = 200;

  /** The content panel. */
  private final JPanel contentPanel;

  /** The chosen database type. */
  private final DatabaseType dbType;

  /** The configuration file. */
  private final P3JConfigFile configFile;

  /**
   * Instantiates a new preferences dialog.
   * 
   * @param owner
   *          the owner
   * @param p3jConfiguration
   *          the p3j configuration
   * @param databaseType
   *          the chosen database type
   */
  public PreferencesDialog(Frame owner, final P3JConfigFile p3jConfiguration,
      DatabaseType databaseType) {
    super(owner, "Edit Preferences", true);
    dbType = databaseType;
    configFile = p3jConfiguration;

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

    final IPreferencesUIProvider uiProvider = dbType.getPreferencesUIProvider();
    setSize(DIALOG_WIDTH, DIALOG_HEIGHT + uiProvider.getHeight());
    GUI.centerOnScreen(this);

    uiProvider.addUIElements(pspf, dbType.readPreferences(p3jConfiguration));

    apply.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Pair<DBConnectionData, String> connData = uiProvider.getDBPreferences();
        Exception ex = P3MDatabase.testConnection(connData.getFirstValue()
            .getUrl(), connData.getFirstValue().getUser(), connData
            .getFirstValue().getPassword());
        if (ex == null) {
          dbType.writePreferences(configFile, connData);
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
        Pair<DBConnectionData, String> connData = uiProvider.getDBPreferences();
        Exception ex = P3MDatabase.testConnection(connData.getFirstValue()
            .getUrl(), connData.getFirstValue().getUser(), connData
            .getFirstValue().getPassword());
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
        uiProvider.setDBPreferences(dbType.getDefaults());
        contentPanel.repaint();
      }
    });

    contentPanel = pspf.constructPanel();
    this.getContentPane().add(contentPanel);
  }
}
