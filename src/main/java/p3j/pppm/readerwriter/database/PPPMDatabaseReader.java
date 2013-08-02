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
package p3j.pppm.readerwriter.database;

import java.net.URI;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.core.data.DBConnectionData;
import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;

import p3j.database.IP3MDatabase;
import p3j.database.hibernate.P3MDatabase;
import p3j.gui.P3J;
import p3j.misc.gui.GUI;
import p3j.pppm.ProjectionModel;
import p3j.pppm.SymbolicProjectionModel;

/**
 * Model reader to access the PPP model database.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class PPPMDatabaseReader implements IModelReader {

  /** The data about the connection to the database. */
  private final DBConnectionData connData;

  /** The ID of the projection to be loaded. */
  private final int projID;

  /**
   * Instantiates a new PPPM database reader.
   * 
   * @param connectionData
   *          the connection data
   * @param projectionID
   *          the projection id
   */
  public PPPMDatabaseReader(DBConnectionData connectionData,
      Integer projectionID) {
    connData = connectionData;
    projID = projectionID;
  }

  @Override
  public ISymbolicModel<?> read(URI ident) {

    ProjectionModel model = null;

    try {
      IP3MDatabase sqlDatabase = new P3MDatabase();
      sqlDatabase.init(connData, P3J.getInstance().getConfigFile());
      try {
        sqlDatabase.open();
        model = sqlDatabase.getProjectionByID(projID);
      } catch (Exception ex) {
        SimSystem.report(ex);
        return null;
      }
      if (model == null) {
        throw new IllegalArgumentException("PPP Model with ID '" + projID
            + "' was not found.");
      }
    } catch (Throwable t) {
      GUI.printErrorMessage("Could not load model", t);
    }
    return new SymbolicProjectionModel(model);
  }

  @Override
  public IModel read(URI source, Map<String, ?> parameters) {
    return (IModel) read(source).getAsDataStructure();
  }

}
