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
}
