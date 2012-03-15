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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * 
 * Manages all {@link Set} objects for a certain {@link SetType}. It is
 * responsible to create new parameter assignments for all
 * {@link ParameterInstance} objects hat are covered by the {@link SetType} of
 * its {@link Set}. The assignments should have a *decreasing* probability, new
 * assignments can be triggered by calling {@link SetManager#nextAssignment()},
 * the current assignment can be retrieved with
 * {@link SetManager#getCurrentMapping()}. Basically, the set manager conducts a
 * breadth-first search, guided by the probabilities of the index tuples.
 * 
 * Created: August 22, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class SetManager {

	/** The set to be managed. */
	private Set set;

	/** List of all instances covered by the corresponding {@link SetType}. */
	private List<ParameterInstance> instances;

	/** List of assumption lists (ordered by probability). */
	private List<List<ParameterAssignment>> assumptions = new ArrayList<List<ParameterAssignment>>();

	/** The assignment enumerator. */
	private AssignmentEnumerator assignmentEnumerator;

	/** Reference to the current assignment. */
	private Assignment currentAssignment;

	/**
	 * Default constructor.
	 * 
	 * @param s
	 *          the set to be managed
	 * @param setType
	 *          the type of the set
	 */
	public SetManager(Set s, SetType setType) {
		set = s;
		instances = setType.getDefinedParameters();
		List<Integer> maxProbCombination = new ArrayList<Integer>();
		for (ParameterInstance instance : instances) {
			List<ParameterAssignment> assignments = new ArrayList<ParameterAssignment>(
			    set.getParameterAssignments(instance).getAssignments());

			// Sort assignments in decreasing order
			Collections.sort(assignments, new Comparator<ParameterAssignment>() {
				@Override
				public int compare(ParameterAssignment a1, ParameterAssignment a2) {
					return Double.compare(a2.getProbability(), a1.getProbability());
				}
			});

			assumptions.add(assignments);
			maxProbCombination.add(0);
		}

		// Add first element to assignment queue
		assignmentEnumerator = new AssignmentEnumerator(new Assignment(
		    maxProbCombination, calcAssignmentProbability(maxProbCombination)));
		nextAssignment();
	}

	/**
	 * Calculate probability that current set and the most probable assignment are
	 * chosen.
	 * 
	 * @return the probability of the set, multiplied with the probability of the
	 *         currently most probable assignment combination, -1 if there is no
	 *         assignment left
	 */
	protected double calcSetAssignmentProbability() {
		if (currentAssignment == null) {
			return -1;
		}
		return set.getProbability() * currentAssignment.getProbability();
	}

	/**
	 * Calculate probability that given assignment is chosen.
	 * 
	 * @param assumptionIndices
	 *          list of indices of the selected assumptions
	 * @return the probability of choosing the given assignment combination
	 */
	private double calcAssignmentProbability(List<Integer> assumptionIndices) {
		double prob = 1;
		for (int i = 0; i < assumptionIndices.size(); i++) {
			Integer index = assumptionIndices.get(i);
			if (index >= assumptions.get(i).size()) {
				return 0;
			}
			prob *= assumptions.get(i).get(index).getProbability();
		}
		return prob;
	}

	/**
	 * Increments a single index to goto the next-probable combination of sets.
	 * 
	 * @return true, if new assignment could be selected, otherwise false
	 */
	protected final boolean nextAssignment() {

		if (assignmentEnumerator.isEmpty()) {
			return false;
		}

		currentAssignment = assignmentEnumerator.removeMostProbable();
		java.util.Set<Assignment> childs = createChildAssignments(currentAssignment);
		assignmentEnumerator.add(childs);
		return true;
	}

	/**
	 * Generates children of given assignment.
	 * 
	 * @param assignment
	 *          the assignment for which the children shall be generated
	 * @return set of all children assignments
	 */
	private java.util.Set<Assignment> createChildAssignments(Assignment assignment) {
		java.util.Set<Assignment> childs = new HashSet<Assignment>();
		final List<Integer> indices = assignment.getAssignmentIndices();
		for (int i = 0; i < instances.size(); i++) {
			int currentIndex = indices.get(i);
			if (currentIndex >= assumptions.get(i).size() - 1) {
				continue;
			}
			List<Integer> childIndices = new ArrayList<Integer>(indices);
			childIndices.set(i, currentIndex + 1);
			childs.add(new Assignment(childIndices,
			    calcAssignmentProbability(childIndices)));
		}
		return childs;
	}

	/**
	 * Get probability of <i>next</i> assignment combination.
	 * 
	 * @return probability of next assignment combination
	 */
	protected double getNextAssignmentProb() {
		return assignmentEnumerator.getMostProbable().getProbability();
	}

	/**
	 * Gets the current assignment probability.
	 * 
	 * @return the current assignment probability
	 */
	protected double getCurrentAssignmentProb() {
		return currentAssignment.getProbability();
	}

	/**
	 * Creates mapping from all {@link ParameterInstance} objects covered by the
	 * {@link SetType} of the managed {@link Set} to the currently selected
	 * {@link ParameterAssignment} instances.
	 * 
	 * @return the mapping instance -> assignment
	 */
	protected Map<ParameterInstance, ParameterAssignment> getCurrentMapping() {
		Map<ParameterInstance, ParameterAssignment> mapping = new HashMap<ParameterInstance, ParameterAssignment>();
		List<Integer> assumptionIndices = currentAssignment.getAssignmentIndices();
		for (int i = 0; i < instances.size(); i++) {
			mapping.put(instances.get(i),
			    assumptions.get(i).get(assumptionIndices.get(i)));
		}
		return mapping;
	}
}
