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

import org.hibernate.cfg.Configuration;

import james.SimSystem;
import james.core.data.DBConnectionData;
import p3j.database.hibernate.P3MDatabase;
import p3j.misc.Misc;
import p3j.misc.gui.GUI;

/**
 * A factory for the database layer. Not implemented properly yet.
 * 
 * Created on January 11, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public final class DatabaseFactory {

  /**
   * This class should not be instantiated.
   */
  private DatabaseFactory() {
  }

  /** Implementing singleton pattern. */
  private static IP3MDatabase sqlDatabase;

  /** Connection data to be used. */
  private static DBConnectionData dbConnData = Misc.DEFAULT_DB_CONN;

  /**
   * Get database interface.
   * 
   * @return database interface
   */
  public static IP3MDatabase getDatabaseSingleton() {
    if (sqlDatabase == null) {
      sqlDatabase = createDatabase();
    }
    return sqlDatabase;
  }

  /**
   * Creates a new Database object.
   * 
   * @return the newly created database interface object
   */
  public static IP3MDatabase createDatabase() {
    P3MDatabase database = new P3MDatabase();
    database.init(dbConnData);
    try {
      database.open();
      attemptDBSpecificOptimizations(database.getConfig());
    } catch (Exception ex) {
      SimSystem.report(ex);
    }
    return database;
  }

  /**
   * Attempt some db-specific optimizations that cannot be easily covered by
   * (the used version of) hibernate. Establishes a JDBC connection with the
   * driver that is also used by hibernate and executes some raw SQL statements,
   * e.g. to create multi-column indices.
   * 
   * @param config
   *          the hibernate configuration
   */
  private static void attemptDBSpecificOptimizations(Configuration config) {
    // TODO: add strings as constants, make this easier in the preferences
    // dialog
    if (config.getProperty("dialect").equals(
        "org.hibernate.dialect.MySQL5Dialect")) {
      // TODO: open JDBC connection and add index on matrices ID+hash
    }

  }

  /**
   * Creates a new Database object, given a certain hibernate configuration
   * file.
   * 
   * @param hibernateConfigFile
   *          the hibernate config file
   * 
   * @return the database
   */
  public static IP3MDatabase createDatabase(String hibernateConfigFile) {
    P3MDatabase.setHibernateConfigFile(hibernateConfigFile);
    return createDatabase();
  }

  /**
   * Gets the DB connection data.
   * 
   * @return the DB connection data
   */
  public static DBConnectionData getDbConnData() {
    return dbConnData;
  }

  /**
   * Sets the DB connection data.
   * 
   * @param dbConnData
   *          the new DB connection data
   */
  public static void setDbConnData(DBConnectionData dbConnData) {
    try {
      reset();
    } catch (Exception ex) {
      GUI.printErrorMessage("Error closing database connection", ex);
    }
    DatabaseFactory.dbConnData = dbConnData;
  }

  /**
   * Reset.
   */
  public static void reset() {
    if (sqlDatabase != null) {
      sqlDatabase.close();
    }
    sqlDatabase = null;
  }

}
