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

import james.core.util.misc.Pair;
import james.core.util.misc.Strings;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import p3j.pppm.PPPModelFactory;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.parameters.Parameters;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * Tests {@link ExhaustiveAssignmentGenerator}.
 * 
 * TODO: Move general set-up to super class, add test for randomised assignment
 * generator.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class TestExhaustiveAssignmentGenerator extends TestCase {

  /** The high probability constant. */
  final static double PROB_HIGH = 0.8;

  /** The low probability constant, sums with high probability to 1. */
  final static double PROB_LOW = 1 - PROB_HIGH;

  /** The even probability. */
  final static double PROB_EVEN = 0.5;

  /** The overall number of combinations. */
  final int NUM_COMBINATIONS = 64;

  /** The parameter instances. */
  final ParameterInstance[] instances = new ParameterInstance[] {
      new ParameterInstance(0, Parameters.EMIG_MORT_X_F),
      new ParameterInstance(1, Parameters.EMIG_MORT_X_M),
      new ParameterInstance(2, Parameters.NAT_MORT_X_M),
      new ParameterInstance(3, Parameters.NAT_MORT_X_F) };

  /** The projection model. */
  final ProjectionModel projectionModel = new ProjectionModel(
      "Test Projection", "Just a test projection", 1, 2, 100,
      PPPModelFactory.DEFAULT_JUMP_OFF_YEAR,
      PPPModelFactory.DEFAULT_SUBPOPULATION_MODEL);

  /** The available Settypes. */
  SetType setType;

  /** The second Settype. */
  SetType secondSetType;

  /** The array of test sets. */
  final Set[] sets = new Set[4];

  /** The array of test parameter assignments. */
  ParameterAssignment[] assignments;

  /** The assignment generator. */
  ExhaustiveAssignmentGenerator aag;

  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  public void setUp() {

    List<ParameterInstance> instanceList = new ArrayList<ParameterInstance>();
    for (ParameterInstance instance : instances) {
      instanceList.add(instance);
    }
    projectionModel.setAllParameterInstances(instanceList);
    projectionModel.init();

    setUpSets();
    setUpAssignments();

    aag = new ExhaustiveAssignmentGenerator();
    ExhaustiveSimParameters asp = new ExhaustiveSimParameters();
    aag.setParameters(asp);
    aag.init(projectionModel);
  }

  /**
   * Sets the up sets.
   */
  private void setUpSets() {
    setType = projectionModel.createSetType("Test Settype",
        "This is just a test Settype");
    projectionModel.assignParameterInstance(instances[0], setType, false);
    projectionModel.assignParameterInstance(instances[1], setType, false);

    secondSetType = projectionModel.createSetType("Test Second Settype",
        "This is a second Settype.");
    projectionModel.assignParameterInstance(instances[2], secondSetType, false);

    sets[0] = setType.createSet("1-1", "", PROB_HIGH);
    sets[1] = setType.createSet("1-2", "", PROB_LOW);
    sets[2] = secondSetType.createSet("2-1", "", PROB_EVEN);
    sets[3] = secondSetType.createSet("2-2", "", PROB_EVEN);
  }

  /**
   * Sets the up assignments.
   */
  private void setUpAssignments() {

    // @formatter:off
    assignments = new ParameterAssignment[] {
        new ParameterAssignment(instances[0], "1-1 1-1", "", PROB_HIGH, 0.0,
            null),
        new ParameterAssignment(instances[0], "1-1 1-2", "", PROB_LOW, 0.0,
            null),
        new ParameterAssignment(instances[1], "1-1 2-1", "", PROB_EVEN, 0.0,
            null),
        new ParameterAssignment(instances[1], "1-1 2-2", "", PROB_EVEN, 0.0,
            null),
        new ParameterAssignment(instances[0], "1-2 1-1", "", PROB_EVEN, 0.0,
            null),
        new ParameterAssignment(instances[0], "1-2 1-2", "", PROB_EVEN, 0.0,
            null),
        new ParameterAssignment(instances[1], "1-2 2-1", "", PROB_EVEN, 0.0,
            null),
        new ParameterAssignment(instances[1], "1-2 2-2", "", PROB_EVEN, 0.0,
            null),
        new ParameterAssignment(instances[2], "2-1 1-1", "", PROB_HIGH, 0.0,
            null),
        new ParameterAssignment(instances[2], "2-1 1-2", "", PROB_LOW, 0.0,
            null),
        new ParameterAssignment(instances[2], "2-2 1-1", "", PROB_EVEN, 0.0,
            null),
        new ParameterAssignment(instances[2], "2-2 1-2", "", PROB_EVEN, 0.0,
            null),
        new ParameterAssignment(instances[3], "Def 1", "", PROB_HIGH, 0.0, null),
        new ParameterAssignment(instances[3], "Def 2", "", PROB_LOW, 0.0, null) };
    // @formatter:on
    sets[0].addParameterAssignment(assignments[0]);
    sets[0].addParameterAssignment(assignments[1]);
    sets[0].addParameterAssignment(assignments[2]);
    sets[0].addParameterAssignment(assignments[3]);

    sets[1].addParameterAssignment(assignments[4]);
    sets[1].addParameterAssignment(assignments[5]);
    sets[1].addParameterAssignment(assignments[6]);
    sets[1].addParameterAssignment(assignments[7]);

    sets[2].addParameterAssignment(assignments[8]);
    sets[2].addParameterAssignment(assignments[9]);

    sets[3].addParameterAssignment(assignments[10]);
    sets[3].addParameterAssignment(assignments[11]);

    projectionModel.getDefaultSet().addParameterAssignment(assignments[12]);
    projectionModel.getDefaultSet().addParameterAssignment(assignments[13]);
  }

  /**
   * Tests the overall set up.
   */
  public void testTheSetUp() {
    assertEquals(1, projectionModel.getDefaultSetType().getDefinedParameters()
        .size());
    assertEquals(3, projectionModel.getAllSetTypes().size());
  }

  /**
   * Test assignment generator.
   */
  public void testAssignmentGenerator() {

    assertEquals(NUM_COMBINATIONS,
        aag.calculateNumOfCombinations(projectionModel));
    Pair<?, ?>[] pairs = new Pair<?, ?>[NUM_COMBINATIONS];
    for (int i = 0; i < NUM_COMBINATIONS; i++) {
      Assignment assignment = aag.peek();
      if (assignment != null) {
        System.out.println(Strings.dispArray(assignment.getAssignmentIndices()
            .toArray()) + " / prob:" + assignment.getProbability());
      }
      pairs[i] = aag.nextAssignment();
      assertNotNull(i + "-th assignment does exist.", pairs[i]);
    }
    assertNull(aag.nextAssignment());
  }
}
