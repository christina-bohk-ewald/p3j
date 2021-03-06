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
package p3j.gui.misc;

import org.jamesii.core.data.DBConnectionData;
import org.jamesii.gui.application.preferences.config.ConfFile;

import p3j.misc.Misc;

/**
 * Main P3J configuration file.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class P3JConfigFile extends ConfFile {

  @Override
  public void setDefaults() {

    // DAtabase settings
    put(Misc.PREF_DB_TYPE, Misc.DEFAULT_DB_TYPE);
    Misc.DEFAULT_DB_TYPE.writePreferences(this,
        Misc.DEFAULT_DB_TYPE.getDefaults());

    // Execution settings
    put(Misc.PREF_NUM_TRIALS, Misc.DEFAULT_NUM_TRIALS);
    put(Misc.PREF_NUM_PARALLEL_THREADS, Misc.DEFAULT_NUM_PARALLEL_THREADS);
    put(Misc.PREF_EXECUTION_MODE, Misc.DEFAULT_EXEC_MODE);
  }

  /**
   * Gets the dB connection data as defined in the configuration.
   * 
   * @return the database connection data
   */
  public DBConnectionData getDBConnectionData() {
    return new DBConnectionData((String) get(Misc.PREF_DB_URL),
        (String) get(Misc.PREF_DB_USER), (String) get(Misc.PREF_DB_PWD),
        (String) get(Misc.PREF_HIBERNATE_DRIVER_PROPERTY));
  }

}
