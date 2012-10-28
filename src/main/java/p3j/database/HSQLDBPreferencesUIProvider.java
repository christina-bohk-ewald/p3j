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
package p3j.database;

import james.core.data.DBConnectionData;
import james.core.util.misc.Pair;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import p3j.gui.P3J;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.misc.Misc;

/**
 * The user interface to configure HSQLDB database connections.
 * 
 * Created on 28.10.2012
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class HSQLDBPreferencesUIProvider implements IPreferencesUIProvider {

  /** The width of the text field. */
  private static final int TEXT_FIELD_WIDTH = 20;

  /** The text field containing the Database location. */
  final JTextField dbLocation = new JTextField();

  /** The file chooser button. */
  final JButton chooseFile = new JButton("...");
  {
    chooseFile.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(dbLocation.getText());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(P3J
            .getInstance())) {
          dbLocation.setText(chooser.getSelectedFile().getAbsolutePath());
        }
      }
    });
  }

  /**
   * Panel that contains the location text field and a button for the file
   * chooser.
   */
  final JPanel dbLocationPanel = new JPanel();

  /**
   * Instantiates a new UI provider for HSQLDB preferences.
   */
  public HSQLDBPreferencesUIProvider() {
    dbLocation.setColumns(TEXT_FIELD_WIDTH);
    dbLocationPanel.add(dbLocation);
    dbLocationPanel.add(chooseFile);
  }

  @Override
  public int getHeight() {
    return 30;
  }

  @Override
  public void addUIElements(PropertiesShowPanelFactory pspf,
      Pair<DBConnectionData, String> connData) {
    setDBPreferences(connData);
    pspf.app(Misc.GUI_LABEL_DB_FILE_LOCATION, dbLocationPanel);
  }

  @Override
  public Pair<DBConnectionData, String> getDBPreferences() {
    return new Pair<>(new DBConnectionData(Misc.HSQLDB_URL_PREFIX
        + dbLocation.getText() + File.separator + Misc.HSQLDB_FILE_NAME,
        Misc.DEFAULT_DB_USERS.get(DatabaseType.HSQLDB),
        Misc.DEFAULT_DB_PWDS.get(DatabaseType.HSQLDB),
        Misc.JDBC_DRIVERS.get(DatabaseType.HSQLDB)),
        Misc.HIBERNATE_DIALECTS.get(DatabaseType.HSQLDB));
  }

  @Override
  public void setDBPreferences(Pair<DBConnectionData, String> connData) {
    dbLocation.setText((new File(connData
        .getFirstValue()
        .getUrl()
        .substring(
            Misc.HSQLDB_URL_PREFIX.length(),
            connData.getFirstValue().getUrl().length()
                - Misc.HSQLDB_FILE_NAME.length())).getAbsolutePath()));
  }

}
