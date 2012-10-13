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

import james.SimSystem;
import james.core.data.DBConnectionData;
import james.core.data.model.IModelReader;
import james.core.model.IModel;
import james.core.model.symbolic.ISymbolicModel;
import james.core.util.misc.Pair;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import p3j.database.IP3MDatabase;
import p3j.database.hibernate.P3MDatabase;
import p3j.pppm.ProjectionModel;
import p3j.pppm.SymbolicProjectionModel;

/**
 * Model reader to access the PPP model database.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class PPPMDatabaseReader implements IModelReader {

  @Override
  public ISymbolicModel<?> read(URI ident) {

    ProjectionModel model = null;

    try {
      // Analyse model location, open DB connection
      Pair<DBConnectionData, Integer> readerInfo = resolveModelDBURI(ident);
      IP3MDatabase sqlDatabase = new P3MDatabase();
      sqlDatabase.init(readerInfo.getFirstValue());
      try {
        sqlDatabase.open();
        model = sqlDatabase.getProjectionByID(readerInfo.getSecondValue());
      } catch (Exception ex) {
        SimSystem.report(ex);
        return null;
      }
      if (model == null) {
        throw new IllegalArgumentException("PPP Model with ID '"
            + readerInfo.getSecondValue() + "' was not found.");
      }
    } catch (Throwable t) {
      t.printStackTrace();
      SimSystem.report(Level.SEVERE, "Could not load model", t);
    }
    return new SymbolicProjectionModel(model);
  }

  @Override
  public IModel read(URI source, Map<String, ?> parameters) {
    return (IModel) read(source).getAsDataStructure();
  }

  /**
   * Returns important information for the reader, extracted from the
   * {@link URI}.
   * 
   * @param modelURI
   *          the URI of the model
   * @return a tuple containing DB connection information and the ID of the
   *         {@link ProjectionModel} to be read
   */
  protected static Pair<DBConnectionData, Integer> resolveModelDBURI(
      URI modelURI) {
    String[] queryElems = modelURI.getQuery().split("&");
    Map<String, String> queryData = new HashMap<>();
    for (String queryElement : queryElems) {
      String[] keyValPair = queryElement.split("=");
      queryData.put(keyValPair[0], keyValPair.length < 2 ? "" : keyValPair[1]);
    }
    String dbURL = modelURI.getHost() + modelURI.getPath();
    DBConnectionData dbConnData = new DBConnectionData(queryData.get("url"),
        queryData.get("user"), queryData.get("password"), null); 
    // TODO: unique constants, move creation here
    // as well!
    return new Pair<DBConnectionData, Integer>(dbConnData,
        Integer.parseInt(queryData.get("projId")));
  }
}
