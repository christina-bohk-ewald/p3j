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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * 
 * Manages a {@link SetType} for the {@link ExhaustiveAssignmentGenerator}.
 * Relies on a {@link SetManager} for each {@link Set} defined for its
 * {@link SetType}.
 * 
 * Created: August 22, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class SetTypeManager {

	/** List of sets associated with the Settype. */
	private List<SetManager> setManagers = new ArrayList<SetManager>();

	/** The comparator for {@link SetManager} instances. */
	private SetManagerComparator smComp = new SetManagerComparator();

	/**
	 * The history of past set assignment.
	 * 
	 * TODO: The first entries of the history can be deleted every now and then,
	 * this will otherwise cause memory problems for large projections.
	 */
	private List<SetAssignment> history = new ArrayList<SetAssignment>();

	/**
	 * Default constructor.
	 * 
	 * @param setType
	 *          the Settype to be managed
	 */
	public SetTypeManager(SetType setType) {
		if (setType.getSets().isEmpty()) {
			throw new IllegalArgumentException("Fatal error: Settype '"
			    + setType.getName() + "' has no sets!");
		}

		for (Set set : setType.getSets()) {
			if (set.isValid()) {
				setManagers.add(new SetManager(set, setType));
			}
		}

		Collections.sort(setManagers, smComp);
		createAssignments(1);
	}

	/**
	 * Gets i-th most probable assignment probability.
	 * 
	 * @param assignmentIndex
	 *          the desired index
	 * @return the probability of the i-th most probable assignment, -1 if there
	 *         is no i-th assignment
	 */
	public double getProbability(int assignmentIndex) {

		if (assignmentIndex < history.size()) {
			return history.get(assignmentIndex).getProbability();
		}

		if (!createAssignments(assignmentIndex + 1)) {
			return -1;
		}
		return history.get(assignmentIndex).getProbability();
	}

	/**
	 * Gets i-th most probable assignment.
	 * 
	 * @param assignmentIndex
	 *          the desired index
	 * @return the i-th most probable assignment, null if nothing could be
	 *         found(i.e., there are no more possible assignments)
	 */
	public Map<ParameterInstance, ParameterAssignment> getAssignment(
	    int assignmentIndex) {
		if (assignmentIndex < history.size()) {
			return history.get(assignmentIndex).getAssignment();
		}

		if (!createAssignments(assignmentIndex + 1)) {
			return null;
		}
		return history.get(assignmentIndex).getAssignment();
	}

	/**
	 * Checks whether there are (at least) as many assignments as specified by the
	 * given index.
	 * 
	 * @param assignmentIndex
	 *          the desired assignment index
	 * @return true, if assignment with given index exists
	 */
	public boolean hasAssignment(int assignmentIndex) {
		if (assignmentIndex < history.size()) {
			return true;
		}
		return createAssignments(assignmentIndex + 1);
	}

	/**
	 * Creates most probable assignments up to the given desired size of the
	 * history.
	 * 
	 * TODO: When history is truncated every once in a while, this method needs to
	 * be adapted. It now assumes that the history is growing constantly.
	 * 
	 * @param desiredSize
	 *          the desired size of the history
	 * @return true, if new assignments could be created, otherwise false
	 */
	protected final boolean createAssignments(int desiredSize) {

		while (history.size() < desiredSize && !setManagers.isEmpty()) {

			// Add assignment of top set to history
			history.add(new SetAssignment(setManagers.get(0).getCurrentMapping(),
			    setManagers.get(0).calcSetAssignmentProbability()));

			// Create next assignment for top set (as this has already been used)
			boolean hasNext = setManagers.get(0).nextAssignment();

			// Sort list or remove finished set
			if (!hasNext) {
				setManagers.remove(0);
				continue;
			}
			Collections.sort(setManagers, smComp);
		}
		return (history.size() == desiredSize);
	}
}

/**
 * Represents the assignment as generated by a {@link Set}. The probability is
 * calculated by the {@link SetManager}, via
 * {@link SetManager#calcSetAssignmentProbability()}.
 * 
 * Created: August 23, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
class SetAssignment {

	/** The assignment. */
	private final Map<ParameterInstance, ParameterAssignment> assignment;

	/** The probability of the assignment. */
	private final double probability;

	/**
	 * Default constructor for set assignments.
	 * 
	 * @param assignm
	 *          the assignment chosen by the set
	 * @param prob
	 *          the probability of that assignment
	 */
	SetAssignment(Map<ParameterInstance, ParameterAssignment> assignm, Double prob) {
		assignment = assignm;
		probability = prob;
	}

	public Map<ParameterInstance, ParameterAssignment> getAssignment() {
		return assignment;
	}

	public double getProbability() {
		return probability;
	}

}

/**
 * Comparator for set managers. Sorts {@link SetManager} instances in decreasing
 * order of their set and assignment probabilities.
 * 
 * Created: August 23, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
class SetManagerComparator implements Comparator<SetManager>, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7821654659763832183L;

	@Override
	public int compare(SetManager man1, SetManager man2) {
		return Double.compare(man2.calcSetAssignmentProbability(),
		    man1.calcSetAssignmentProbability());
	}

}