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

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jamesii.core.data.DBConnectionData;
import org.jamesii.core.util.misc.Pair;

import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.misc.Misc;

/**
 * GUI to configure database connection to MySQL server.
 * 
 * CAn be easily re-used for other (non-file-based) DBMS.
 * 
 * Created on 28.10.2012
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class MySQLPreferencesUIProvider implements IPreferencesUIProvider {

  /** The url. */
  final JTextField dbURL = new JTextField();

  /** The user name. */
  final JTextField dbUserName = new JTextField();

  /** The password. */
  final JTextField dbPassword = new JPasswordField();

  /** The database type. */
  private static final DatabaseType DB_TYPE = DatabaseType.MYSQL;

  @Override
  public int getHeight() {
    return 70;
  }

  @Override
  public void addUIElements(PropertiesShowPanelFactory pspf,
      Pair<DBConnectionData, String> connData) {
    setDBPreferences(connData);
    pspf.app(Misc.PREF_DB_URL + ": " + Misc.MYSQL_URL_PREFIX, dbURL);
    pspf.app(Misc.PREF_DB_USER + ":", dbUserName);
    pspf.app(Misc.PREF_DB_PWD + ":", dbPassword);
  }

  @Override
  public Pair<DBConnectionData, String> getDBPreferences() {
    return new Pair<>(new DBConnectionData(Misc.MYSQL_URL_PREFIX
        + dbURL.getText(), dbUserName.getText(), dbPassword.getText(),
        Misc.JDBC_DRIVERS.get(DB_TYPE)), Misc.HIBERNATE_DIALECTS.get(DB_TYPE));
  }

  @Override
  public void setDBPreferences(Pair<DBConnectionData, String> connData) {
    dbURL.setText(connData.getFirstValue().getURL()
        .substring(Misc.MYSQL_URL_PREFIX.length()));
    dbUserName.setText(connData.getFirstValue().getUser());
    dbPassword.setText(connData.getFirstValue().getPassword());
  }

}
