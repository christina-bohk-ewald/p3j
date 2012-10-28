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
import p3j.gui.misc.P3JConfigFile;
import p3j.misc.Misc;

/**
 * Represent the database type to be used.
 * 
 * Created on 27.10.2012
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public enum DatabaseType {

  /** HyperSQL data storage (default, file-based). */
  HSQLDB,

  /** MySQL-based data storage. */
  MYSQL,

  /** Generic data storage. */
  GENERIC;

  @Override
  public String toString() {
    switch (this) {
    case HSQLDB:
      return "File-based (default, relatively slow)";
    case MYSQL:
      return "MySQL (requires access to MySQL server)";
    case GENERIC:
      return "Generic (requires manual configuration)";
    default:
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Prefix to associate certain configuration values with a database type.
   * Should be unique per type.
   * 
   * @return the prefix
   */
  public String asPrefix() {
    switch (this) {
    case HSQLDB:
      return "HSQLDB";
    case MYSQL:
      return "MySQL";
    case GENERIC:
      return "Generic";
    default:
      throw new UnsupportedOperationException();
    }
  }

  public Pair<DBConnectionData, String> readPreferences(P3JConfigFile conf) {
    if (conf.get(asPrefix() + Misc.PREF_DB_URL) == null)
      return getDefaults();
    return readPreferences(asPrefix(), conf);
  }

  public void writePreferences(P3JConfigFile conf,
      Pair<DBConnectionData, String> connData) {
    writePreferences("", conf, connData);
    writePreferences(asPrefix(), conf, readPreferences("", conf));
  }

  public Pair<DBConnectionData, String> getDefaults() {
    return new Pair<>(new DBConnectionData(Misc.DEFAULT_DB_URLS.get(this),
        Misc.DEFAULT_DB_USERS.get(this), Misc.DEFAULT_DB_PWDS.get(this),
        Misc.JDBC_DRIVERS.get(this)), Misc.HIBERNATE_DIALECTS.get(this));
  }

  private Pair<DBConnectionData, String> readPreferences(String prefix,
      P3JConfigFile conf) {
    return new Pair<>(new DBConnectionData(conf.get(prefix + Misc.PREF_DB_URL)
        .toString(), conf.get(prefix + Misc.PREF_DB_USER).toString(), conf.get(
        prefix + Misc.PREF_DB_PWD).toString(), conf.get(
        prefix + Misc.PREF_HIBERNATE_DRIVER_PROPERTY).toString()), conf.get(
        prefix + Misc.PREF_HIBERNATE_DIALECT_PROPERTY).toString());
  }

  private void writePreferences(String prefix, P3JConfigFile conf,
      Pair<DBConnectionData, String> connData) {
    conf.put(prefix + Misc.PREF_DB_URL, connData.getFirstValue().getUrl());
    conf.put(prefix + Misc.PREF_DB_USER, connData.getFirstValue().getUser());
    conf.put(prefix + Misc.PREF_DB_PWD, connData.getFirstValue().getPassword());
    conf.put(prefix + Misc.PREF_HIBERNATE_DRIVER_PROPERTY, connData
        .getFirstValue().getDriver());
    conf.put(prefix + Misc.PREF_HIBERNATE_DIALECT_PROPERTY,
        connData.getSecondValue());
  }

  public IPreferencesUIProvider getPreferencesUIProvider() {
    switch (this) {
    case HSQLDB:
      return new HSQLDBPreferencesUIProvider();
    case MYSQL:
      return new MySQLPreferencesUIProvider();
    case GENERIC:
      return new GenericPreferencesUIProvider();
    default:
      throw new UnsupportedOperationException();
    }
  }

}
