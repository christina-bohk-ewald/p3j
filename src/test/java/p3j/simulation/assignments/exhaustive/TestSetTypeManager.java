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
package p3j.simulation.assignments.exhaustive;

import junit.framework.TestCase;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.sets.Set;

/**
 * Tests {@link SetTypeManager}. Adds a second set to the test Settype. This is
 * more probable but just contains a single assignment combination.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class TestSetTypeManager extends TestCase {

  /** The Constant PRECISION. */
  final static double PRECISION = 0.000001;

  /** The overall number of assignments. */
  final static int NUM_ASSIGNMENTS = 5;

  /** The test for the set manager. */
  protected final TestSetManager setManagerTest = new TestSetManager();

  /** The single assignment for the first instance. */
  protected final ParameterAssignment assignmentInst1 = new ParameterAssignment(
      TestSetManager.instance1MigF, "#1", "", 1.0, 0.0, null);

  /** The single assignment for the second instance. */
  protected final ParameterAssignment assignmentInst2 = new ParameterAssignment(
      TestSetManager.instance2MigM, "#2", "", 1.0, 0.0, null);

  /** The second set. */
  Set secondSet = setManagerTest.setType.createSet("Second test set.",
      "This is just another test set.", TestSetManager.probSecond / 2);
  {
    setManagerTest.set.setProbability(1 - (TestSetManager.probSecond / 2));
    secondSet.addParameterAssignment(assignmentInst1);
    secondSet.addParameterAssignment(assignmentInst2);
  }

  /** The set manager. */
  SetTypeManager setTypeManager;

  @Override
  public void setUp() {
    setTypeManager = new SetTypeManager(setManagerTest.setType);
  }

  /**
   * Tests the Settype manager.
   */
  public void testSetTypeManager() {
    assertTrue(setTypeManager.createAssignments(NUM_ASSIGNMENTS));
    assertFalse(setTypeManager.createAssignments(NUM_ASSIGNMENTS + 1));
    assertFalse(setTypeManager.createAssignments(NUM_ASSIGNMENTS + 100));

    // Check most likely assignment
    for (ParameterAssignment pa : setTypeManager.getAssignment(0).values()) {
      assertEquals(TestSetManager.probFirst, pa.getProbability());
    }
    assertEquals((1.0 - (TestSetManager.probSecond / 2.0))
        * (TestSetManager.probFirst * TestSetManager.probFirst),
        setTypeManager.getProbability(0), PRECISION);

    // Check most unlikely assignment
    for (ParameterAssignment pa : setTypeManager.getAssignment(
        NUM_ASSIGNMENTS - 1).values()) {
      assertEquals(TestSetManager.probSecond, pa.getProbability());
    }
    assertEquals((1 - TestSetManager.probSecond / 2.0)
        * TestSetManager.probSecond * TestSetManager.probSecond,
        setTypeManager.getProbability(NUM_ASSIGNMENTS - 1), PRECISION);
  }

}
