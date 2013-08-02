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
 * Created on 28.10.2012
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class GenericPreferencesUIProvider implements IPreferencesUIProvider {

  final JTextField dbURL = new JTextField();

  final JTextField dbUserName = new JTextField();

  final JTextField dbPassword = new JPasswordField();

  final JTextField jdbcDriver = new JTextField();

  final JTextField dialect = new JTextField();

  @Override
  public int getHeight() {
    return 110;
  }

  @Override
  public void addUIElements(PropertiesShowPanelFactory pspf,
      Pair<DBConnectionData, String> connData) {
    setDBPreferences(connData);
    pspf.app(Misc.PREF_DB_URL + ":", dbURL);
    pspf.app(Misc.PREF_DB_USER + ":", dbUserName);
    pspf.app(Misc.PREF_DB_PWD + ":", dbPassword);
    pspf.app(Misc.GUI_LABEL_DB_DRIVER_CLASS + ":", jdbcDriver);
    pspf.app(Misc.GUI_LABEL_HIBERNATE_DIALECT + ":", dialect);
  }

  @Override
  public Pair<DBConnectionData, String> getDBPreferences() {
    return new Pair<>(new DBConnectionData(dbURL.getText(),
        dbUserName.getText(), dbPassword.getText(), jdbcDriver.getText()),
        dialect.getText());
  }

  @Override
  public void setDBPreferences(Pair<DBConnectionData, String> connData) {
    dbURL.setText(connData.getFirstValue().getURL());
    dbUserName.setText(connData.getFirstValue().getUser());
    dbPassword.setText(connData.getFirstValue().getPassword());
    jdbcDriver.setText(connData.getFirstValue().getDriver());
    dialect.setText(connData.getSecondValue());
  }

}
