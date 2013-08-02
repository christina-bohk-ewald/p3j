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
package p3j.gui.panels.dboverview;

import org.jamesii.core.data.DBConnectionData;

import p3j.gui.panels.projections.ProjectionTreeNode;

/**
 * Node to display database information.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class DatabaseNode extends ProjectionTreeNode<DBConnectionData> {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -9217966942347941310L;

  /**
   * Default constructor.
   * 
   * @param pppmEntity
   *          the database connection information
   * @param name
   *          the name of the node
   */
  public DatabaseNode(DBConnectionData pppmEntity, String name) {
    super(pppmEntity, name);
  }

}
