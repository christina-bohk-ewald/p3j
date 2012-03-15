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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Tests for {@link Assignment} and {@link AssignmentEnumerator}.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class TestAssignmentAndAssignmentEnumerator extends TestCase {

  /** The Constant SOME_PROBABILITY. Has to be in [0,1). */
  final static double SOME_PROBABILITY = .5;

  /** Test assignment #1. */
  Assignment a1;

  /** Test assignment #2. */
  Assignment a2;

  /** Test assignment #3. */
  Assignment a3;

  /** A deep copy of test assignment #1. */
  Assignment a1Copy;

  @Override
  public void setUp() {

    List<Integer> indexList1 = new ArrayList<Integer>();
    indexList1.add(0);
    indexList1.add(0);
    indexList1.add(0);
    a1 = new Assignment(indexList1, SOME_PROBABILITY);

    List<Integer> indexList2 = new ArrayList<Integer>(indexList1);
    a2 = new Assignment(indexList2, SOME_PROBABILITY * SOME_PROBABILITY);

    List<Integer> indexList3 = new ArrayList<Integer>(indexList1);
    indexList3.remove(0);
    indexList3.add(0, 1);
    a3 = new Assignment(indexList3, SOME_PROBABILITY);

    a1Copy = new Assignment(new ArrayList<Integer>(a1.getAssignmentIndices()),
        a1.getProbability());
  }

  public void testAssignment() {

    assertEquals("0-0-0-", a1.getID());
    assertEquals("0-0-0-", a2.getID());
    assertEquals("1-0-0-", a3.getID());

    assertFalse(a1.equals(a2));
    assertFalse(a1.equals(a3));
    assertTrue(a1.equals(a1Copy));
  }

  public void testAssignmentEnumerator() {
    AssignmentEnumerator assignmentEnumerator = new AssignmentEnumerator(a2);
    assertEquals(a2, assignmentEnumerator.getMostProbable());

    Set<Assignment> assignmentSet = new HashSet<Assignment>();
    assignmentSet.add(a1);
    assignmentEnumerator.add(assignmentSet);
    // Should be assignment #2, assignment #1 should have been rejected because
    // of identical ID
    assertNotSame(a1, assignmentEnumerator.getMostProbable());
    assertEquals(a2, assignmentEnumerator.getMostProbable());

    assignmentSet.clear();
    assignmentSet.add(a3);
    assignmentEnumerator.add(assignmentSet);
    // Assignment #3 should be accepted
    assertNotSame(a2, assignmentEnumerator.getMostProbable());
    assertEquals(a3, assignmentEnumerator.getMostProbable());

    // Only two elements in there:
    assertNotNull(assignmentEnumerator.removeMostProbable());
    assertNotNull(assignmentEnumerator.removeMostProbable());
    assertNull(assignmentEnumerator.removeMostProbable());
  }
}
