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
import p3j.gui.panels.PropertiesShowPanelFactory;

/**
 * Provides the means to configure a database connection via the user interface.
 * 
 * @see p3j.gui.dialogs.PreferencesDialog
 * 
 *      Created on 28.10.2012
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public interface IPreferencesUIProvider {

  /**
   * Gets the required height of the UI elements.
   * 
   * @return the height of the UI elements
   */
  int getHeight();

  /**
   * Adds the UI elements via a properties panel factory.
   * 
   * @param pspf
   *          the properties panel factory
   * @param connData
   *          the current connection data
   */
  void addUIElements(PropertiesShowPanelFactory pspf,
      Pair<DBConnectionData, String> connData);

  /**
   * Gets the database connection preferences.
   * 
   * @return the connection preferences
   */
  Pair<DBConnectionData, String> getDBPreferences();

  /**
   * Sets the database connection preferences.
   * 
   * @param prefs
   *          the preferences
   */
  void setDBPreferences(Pair<DBConnectionData, String> prefs);

}
