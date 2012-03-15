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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class provides all data structures and methods required to enumerate a
 * set of {@link Assignment} instances step-by-step. Ensures no assignment is
 * added twice.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class AssignmentEnumerator {

	/** Sorted list with most probably assignment combinations in front. */
	private final List<Assignment> assignmentQueue = new ArrayList<Assignment>();

	/** The set of assignment IDs. */
	private final java.util.Set<String> idSet = new HashSet<String>();

	/**
	 * Instantiates a new assignment enumerator. Puts first assignment in the
	 * queue.
	 * 
	 * @param assignment
	 *          the first assignment
	 */
	AssignmentEnumerator(Assignment assignment) {
		addToQueue(assignment);
	}

	/**
	 * Instantiates a new assignment enumerator.
	 */
	AssignmentEnumerator() {
	}

	/**
	 * Adds the assignment to the queue if it has not been added before.
	 * 
	 * @param assignment
	 *          the assignment
	 */
	private void addToQueue(Assignment assignment) {
		if (idSet.contains(assignment.getID())) {
			return;
		}
		assignmentQueue.add(assignment);
		idSet.add(assignment.getID());
	}

	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return assignmentQueue.isEmpty();
	}

	/**
	 * Removes the most probable assignment.
	 * 
	 * @return the most probable assignment, null if there is none
	 */
	public Assignment removeMostProbable() {
		return isEmpty() ? null : assignmentQueue.remove(0);
	}

	/**
	 * Gets the most probable assignment.
	 * 
	 * @return the most probable assignment
	 */
	public Assignment getMostProbable() {
		return assignmentQueue.get(0);
	}

	/**
	 * Adds a set of assignments.
	 * 
	 * @param assignments
	 *          the assignments
	 */
	public void add(Set<Assignment> assignments) {
		for (Assignment assignment : assignments) {
			addToQueue(assignment);
		}
		Collections.sort(assignmentQueue);
	}

}
