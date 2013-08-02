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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.misc.Pair;

import p3j.misc.errors.GeneratorError;
import p3j.pppm.IProjectionModel;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;
import p3j.simulation.assignments.plugintype.IParamAssignmentGenerator;

/**
 * Generator that analyses probabilities to select the most probable
 * combinations first (and also avoid re-trying existing combinations). This
 * leads to an exhaustive enumeration of all assignments, ordered by their
 * probability.
 * 
 * Created: August 21, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class ExhaustiveAssignmentGenerator implements IParamAssignmentGenerator {

	/** Simulation parameters to use. */
	private ExhaustiveSimParameters parameters;

	/** Overall number of configurations. */
	private long overallCombinations;

	/** Number of current run. */
	private int currentRun;

	/**
	 * Flag that determines if the stopping criterion with respect for assignment
	 * probabilities are fulfilled.
	 */
	private boolean probStopCriterionFulfilled;

	/**
	 * Overall sum of probabilities of the parameter assignments generated so far.
	 */
	private double overallProbabilitySum;

	/**
	 * List of {@link SetTypeManager}. Positions in this list are used in
	 * {@link SetTypeAssignment} instances.
	 */
	private List<SetTypeManager> stManagers = new ArrayList<SetTypeManager>();

	/** Helps enumerating the assignments. */
	private AssignmentEnumerator assignmentEnumerator;

	@Override
	public void init(IProjectionModel proj) {
		overallCombinations = calculateNumOfCombinations(proj);

		// Initialize Settype managers
		List<Integer> maxProbIndices = new ArrayList<Integer>();
		for (SetType setType : proj.getAllSetTypes()) {
			stManagers.add(new SetTypeManager(setType));
			maxProbIndices.add(0);
		}

		// Add first Settype assignment to queue
		assignmentEnumerator = new AssignmentEnumerator(new Assignment(
		    maxProbIndices, calcAssignmentProbability(maxProbIndices)));
	}

	@Override
	public Pair<Map<ParameterInstance, ParameterAssignment>, List<GeneratorError>> chooseParamAssignments(
	    IRandom random) {

		List<GeneratorError> errorLog = new ArrayList<GeneratorError>();
		Pair<Map<ParameterInstance, ParameterAssignment>, Double> assignment = nextAssignment();
		double assignmentProb = assignment.getSecondValue();

		currentRun++;

		// Check probabilistic stopping criteria
		overallProbabilitySum += assignmentProb;
		if ((parameters.getDesiredOverallProbability() > 0 && overallProbabilitySum >= parameters
		    .getDesiredOverallProbability())
		    || (assignmentProb < parameters.getCutOffProbability())) {
			probStopCriterionFulfilled = true;
		}

		return new Pair<Map<ParameterInstance, ParameterAssignment>, List<GeneratorError>>(
		    assignment.getFirstValue(), errorLog);
	}

	/**
	 * Generates next assignment.
	 * 
	 * @return tuple (assignment, probability) containing the next most probable
	 *         assignment and its corresponding probability, null if there is none
	 *         left
	 */
	protected Pair<Map<ParameterInstance, ParameterAssignment>, Double> nextAssignment() {

		if (assignmentEnumerator.isEmpty()) {
			return null;
		}

		// Add children to queue
		Assignment top = assignmentEnumerator.removeMostProbable();
		assignmentEnumerator.add(getChildren(top));

		// Retrieve result
		Map<ParameterInstance, ParameterAssignment> overallAssignment = createMapping(top
		    .getAssignmentIndices());
		return new Pair<Map<ParameterInstance, ParameterAssignment>, Double>(
		    overallAssignment, top.getProbability());
	}

	/**
	 * Peeks for the next Settype assignment to choose.
	 * 
	 * @return the sets the type assignment
	 */
	protected Assignment peek() {
		return assignmentEnumerator.isEmpty() ? null : assignmentEnumerator
		    .getMostProbable();
	}

	/**
	 * Retrieves all possible children for given {@link SetTypeAssignment}.
	 * 
	 * @param assignment
	 *          the given assignment
	 * 
	 * @return set containing all possible children
	 */
	protected java.util.Set<Assignment> getChildren(Assignment assignment) {
		java.util.Set<Assignment> childs = new HashSet<Assignment>();
		final List<Integer> indices = assignment.getAssignmentIndices();
		for (int i = 0; i < stManagers.size(); i++) {
			int currentIndex = indices.get(i);
			if (!stManagers.get(i).hasAssignment(currentIndex + 1)) {
				continue;
			}
			List<Integer> childIndices = new ArrayList<Integer>(indices);
			childIndices.set(i, currentIndex + 1);
			childs.add(new Assignment(childIndices,
			    calcAssignmentProbability(childIndices)));
		}
		return childs;
	}

	@Override
	public long assignmentsLeft() {

		if (probStopCriterionFulfilled) {
			return 0;
		}

		if (parameters.getMaxNumRuns() > 0) {
			if (currentRun >= parameters.getMaxNumRuns()) {
				return 0;
			}
			return (long) parameters.getMaxNumRuns() - currentRun;
		}

		return (long) Math.ceil(overallCombinations
		    * parameters.getMaxRunFraction())
		    - currentRun;
	}

	/**
	 * Calculates number of different combinations.
	 * 
	 * @param proj
	 *          the given projection setup
	 * 
	 * @return the number of possible matrix assignments
	 */
	protected long calculateNumOfCombinations(IProjectionModel proj) {

		long num = 1;
		for (SetType defType : proj.getAllSetTypes()) {
			long numSetTypeCombinations = 0;
			for (Set set : defType.getSets()) {
				long numSetCombinations = 1;
				for (ParameterInstance instance : defType.getDefinedParameters()) {
					numSetCombinations *= set.getNumberOfAssignments(instance);
				}
				numSetTypeCombinations += numSetCombinations;
			}
			num *= numSetTypeCombinations;
		}

		return num;
	}

	/**
	 * Calculates number of distinct sets.
	 * 
	 * @param proj
	 *          the projection setup
	 * 
	 * @return number of possible set combinations
	 */
	protected long calculateNumOfSetCombinations(ProjectionModel proj) {
		long num = 1;
		int setTypes = proj.getNumOfSetTypes();
		for (int i = 0; i < setTypes; i++) {
			num *= proj.getSetType(i).getNumOfSets();
		}
		return num;
	}

	/**
	 * Calculates the assignment probability for a given list of assignment
	 * indices.
	 * 
	 * @param indices
	 *          list of assignment indices
	 * 
	 * @return calculated probability
	 */
	protected double calcAssignmentProbability(List<Integer> indices) {
		double prob = 1;
		for (int i = 0; i < indices.size(); i++) {
			prob *= stManagers.get(i).getProbability(indices.get(i));
		}
		return prob;
	}

	/**
	 * Puts all assignments for the given index list into one map.
	 * 
	 * @param indices
	 *          indices of Settype manager assignments
	 * 
	 * @return complete parameter map
	 */
	protected Map<ParameterInstance, ParameterAssignment> createMapping(
	    List<Integer> indices) {
		Map<ParameterInstance, ParameterAssignment> overallMap = new HashMap<ParameterInstance, ParameterAssignment>();
		for (int i = 0; i < indices.size(); i++) {
			overallMap.putAll(stManagers.get(i).getAssignment(indices.get(i)));
		}
		return overallMap;
	}

	/**
	 * Gets the parameters.
	 * 
	 * @return the parameters
	 */
	public ExhaustiveSimParameters getParameters() {
		return parameters;
	}

	/**
	 * Sets the parameters.
	 * 
	 * @param parameters
	 *          the new parameters
	 */
	public void setParameters(ExhaustiveSimParameters parameters) {
		this.parameters = parameters;
	}

}
