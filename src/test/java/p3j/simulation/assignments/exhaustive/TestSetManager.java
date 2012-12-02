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

import java.util.Map;

import junit.framework.TestCase;
import p3j.misc.MatrixDimension;
import p3j.pppm.SubPopulation;
import p3j.pppm.parameters.Parameter;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.parameters.ParameterType;
import p3j.pppm.parameters.Population;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * Test for the {@link SetManager}.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class TestSetManager extends TestCase {

  /** The set manager. */
  SetManager setManager;

  /**
   * The probability of the first assignment. Has to be larger or equal than
   * 0.5.
   */
  protected final static double probFirst = 0.8;

  /** The probability of the second assignment (have to sum up to 1). */
  protected final static double probSecond = 1 - probFirst;

  /** The first parameter instance. */
  public static final ParameterInstance instance1MigF = new ParameterInstance(
      0, new Parameter(0, false,
          ParameterType.MIGRATION.getFemaleLabelFor(new SubPopulation()),
          MatrixDimension.AGES, MatrixDimension.YEARS, Population.CUSTOM));

  /** The second parameter instance. */
  public static final ParameterInstance instance2MigM = new ParameterInstance(
      1, new Parameter(1, false,
          ParameterType.MIGRATION.getMaleLabelFor(new SubPopulation()),
          MatrixDimension.AGES, MatrixDimension.YEARS, Population.CUSTOM));

  /** The first assignment for the first inst. */
  final ParameterAssignment assignment1Inst1 = new ParameterAssignment(
      instance1MigF, "#1-1", "", probFirst, 0.0, null);

  /** The second assignment for the first instance. */
  final ParameterAssignment assignment2Inst1 = new ParameterAssignment(
      instance1MigF, "#1-2", "", probSecond, 0.0, null);

  /** The first assignment1 for the second instance. */
  final ParameterAssignment assignment1Inst2 = new ParameterAssignment(
      instance2MigM, "#2-1", "", probFirst, 0.0, null);

  /** The second assignment for the second instance. */
  final ParameterAssignment assignment2Inst2 = new ParameterAssignment(
      instance2MigM, "#2-2", "", probSecond, 0.0, null);

  /** The test Settype. */
  protected final SetType setType = new SetType("Test Settype",
      "This is just a Settype of testing.");
  {
    setType.addInstance(instance1MigF);
    setType.addInstance(instance2MigM);
  }

  /** The test set. */
  protected final Set set = setType.createSet("Test Set",
      "This is just a set for testing.", 1.0);
  {
    set.addParameterAssignment(assignment1Inst1);
    set.addParameterAssignment(assignment1Inst2);
    set.addParameterAssignment(assignment2Inst1);
    set.addParameterAssignment(assignment2Inst2);
  }

  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  public void setUp() {
    setManager = new SetManager(set, setType);
  }

  /**
   * Test set manager.
   */
  public void testSetManager() {
    assertEquals(probFirst * probFirst, setManager.getCurrentAssignmentProb());
    testMapping(setManager.getCurrentMapping(), assignment1Inst1,
        assignment1Inst2);

    assertTrue(setManager.nextAssignment());
    assertEquals(probFirst * probSecond, setManager.getCurrentAssignmentProb());

    assertTrue(setManager.nextAssignment());
    assertEquals(probFirst * probSecond, setManager.getCurrentAssignmentProb());

    assertTrue(setManager.nextAssignment());
    assertEquals(probSecond * probSecond, setManager.getCurrentAssignmentProb());
    testMapping(setManager.getCurrentMapping(), assignment2Inst1,
        assignment2Inst2);

    assertFalse(setManager.nextAssignment());
  }

  /**
   * Test mapping.
   * 
   * @param currentMapping
   *          the current mapping
   * @param assignmentFirstInst
   *          the assignment for the first instance
   * @param assignmentSecondInst
   *          the assignment for the second instance
   */
  private void testMapping(
      Map<ParameterInstance, ParameterAssignment> currentMapping,
      ParameterAssignment assignmentFirstInst,
      ParameterAssignment assignmentSecondInst) {
    assertEquals(2, currentMapping.size());
    assertEquals(assignmentFirstInst, currentMapping.get(instance1MigF));
    assertEquals(assignmentSecondInst, currentMapping.get(instance2MigM));
  }

}
