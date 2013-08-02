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

import org.jamesii.core.data.DBConnectionData;
import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.data.model.read.plugintype.IMIMEType;
import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;

import p3j.pppm.ProjectionModel;
import p3j.pppm.SymbolicProjectionModel;

/**
 * Factory for reader of PPPM files.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class PPPModelDatabaseReaderFactory extends ModelReaderFactory {

  /** Serialization ID. */
  private static final long serialVersionUID = 2423298767414579313L;

  /** Scheme for all supported database URIs. */
  private static final String GENERAL_DB_URI_SCHEME = "db-p3j";

  /**
   * The actual parameters of the {@link PPPMDatabaseReader} are encoded in the
   * query of this default URL.
   */
  public static final String DEFAULT_URI = GENERAL_DB_URI_SCHEME
      + "://localhost";

  /** The parameter block name to store the url in. */
  private static final String KEY_CONN_DATA_URL = "url";

  /** The parameter block name to store the user name for the database in. */
  private static final String KEY_CONN_DATA_USER = "user";

  /** The parameter block name to store the password for the database in. */
  private static final String KEY_CONN_DATA_PWD = "pwd";

  /**
   * The parameter block name to store the name of the database driver class in.
   */
  private static final String KEY_CONN_DATA_DRIVER = "driver";

  /** The parameter block name to store the projection ID in. */
  private static final String KEY_PROJECT_ID = "projId";

  @Override
  public IModelReader create(ParameterBlock params) {
    Pair<DBConnectionData, Integer> readerParams = retrieveReaderParams(params);
    return new PPPMDatabaseReader(readerParams.getFirstValue(),
        readerParams.getSecondValue());
  }

  @Override
  public boolean supportsModel(IModel model) {
    return model instanceof ProjectionModel;
  }

  @Override
  public boolean supportsModel(ISymbolicModel<?> model) {
    return model instanceof SymbolicProjectionModel;
  }

  @Override
  public boolean supportsURI(URI uri) {
    return uri.getScheme().equals(GENERAL_DB_URI_SCHEME);
  }

  @Override
  public boolean supportsMIMEType(IMIMEType mime) {
    return false;
  }

  /**
   * Creates a parameter block that contains all relevant data for a database
   * model reader.
   * 
   * @param connData
   *          the connection data
   * @param projectionID
   *          the projection id
   * @return the parameter block all relevant parameters for the database model
   *         reader
   */
  public static ParameterBlock createReaderParams(DBConnectionData connData,
      int projectionID) {
    ParameterBlock readerParams = new ParameterBlock();
    readerParams.addSubBlock(KEY_CONN_DATA_URL, connData.getURL());
    readerParams.addSubBlock(KEY_CONN_DATA_USER, connData.getUser());
    readerParams.addSubBlock(KEY_CONN_DATA_PWD, connData.getPassword());
    readerParams.addSubBlock(KEY_CONN_DATA_DRIVER, connData.getDriver());
    readerParams.addSubBlock(KEY_PROJECT_ID, projectionID);
    return readerParams;
  }

  /**
   * Retrieve reader parameters from parameter block.
   * 
   * @param params
   *          the parameter block
   * @return the connection data and the projection id
   */
  public static Pair<DBConnectionData, Integer> retrieveReaderParams(
      ParameterBlock params) {
    return new Pair<DBConnectionData, Integer>(new DBConnectionData(
        params.getSubBlockValue(KEY_CONN_DATA_URL, ""),
        params.getSubBlockValue(KEY_CONN_DATA_USER, ""),
        params.getSubBlockValue(KEY_CONN_DATA_PWD, ""),
        params.getSubBlockValue(KEY_CONN_DATA_DRIVER, "")),
        params.getSubBlockValue(KEY_PROJECT_ID, -1));
  }

}
