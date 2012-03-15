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
package p3j;

import junit.framework.Test;
import junit.framework.TestSuite;
import p3j.database.hibernate.TestHibernateConnection;
import p3j.gui.P3J;
import p3j.misc.math.TestRandomNumberChecker;
import p3j.pppm.readerwriter.database.TestDBReaderWriter;
import p3j.simulation.assignments.exhaustive.TestAssignmentAndAssignmentEnumerator;
import p3j.simulation.assignments.exhaustive.TestExhaustiveAssignmentGenerator;

/**
 * Creates general test suite for {@link P3J}.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class AllP3JTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("All P3J Tests");
    suite.addTestSuite(TestHibernateConnection.class);
    suite.addTestSuite(TestDBReaderWriter.class);
    suite.addTestSuite(TestRandomNumberChecker.class);
    suite.addTestSuite(TestExhaustiveAssignmentGenerator.class);
    suite.addTestSuite(TestAssignmentAndAssignmentEnumerator.class);
    return suite;
  }
}
