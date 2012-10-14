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

import james.core.data.DBConnectionData;
import james.core.util.misc.Pair;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

/**
 * So far only tests {@link PPPMDatabaseReader}.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class TestDBReaderWriter extends TestCase {

  /**
   * Tests how the {@link PPPMDatabaseReader} interprets the delivered
   * {@link URI}.
   * 
   * @throws URISyntaxException
   *           if test URI has a wrong syntax
   */
  public void testURIInterpretation() throws Exception {

    String dbUser = "root";
    String dbPasswd = "pass";
    String dbURL = "jdbc:mysql://localhost/pppm_db";
    String dbDrv = "some.Driver";
    int projectionID = 12;

    Pair<DBConnectionData, Integer> modelReaderInfo = PPPModelDatabaseReaderFactory
        .retrieveReaderParams((PPPModelDatabaseReaderFactory
            .createReaderParams(new DBConnectionData(dbURL, dbUser, dbPasswd,
                dbDrv), projectionID)));

    DBConnectionData dbConn = modelReaderInfo.getFirstValue();
    assertEquals(dbUser, dbConn.getUser());
    assertEquals(dbPasswd, dbConn.getPassword());
    assertEquals(dbURL, dbConn.getUrl());
    assertEquals(dbDrv, dbConn.getDriver());

    assertEquals(projectionID, modelReaderInfo.getSecondValue().intValue());
  }
}
