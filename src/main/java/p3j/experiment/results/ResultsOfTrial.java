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
package p3j.experiment.results;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import p3j.pppm.IProjectionModel;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.SetType;

/**
 * This class is intended to represent the results of a single trial as stored
 * in the database.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class ResultsOfTrial implements Serializable {

	/** Serialization ID. */
	private static final long serialVersionUID = 2484910276641194217L;

	/** The ID of this trial. */
	private int id = -1;

	/** The projection to which this trial belongs. */
	private ProjectionModel projection;

	/**
	 * The assignment used to generate the trial: one parameter assignment per
	 * instance.
	 */
	private Map<ParameterInstance, ParameterAssignment> assignment = new HashMap<ParameterInstance, ParameterAssignment>();

	/**
	 * Results of native population calculation. This list has only a single
	 * element, but this improves database performance significantly.
	 */
	private List<BasicResults> nativeResults;

	/** The results for the immigrant generations. */
	private List<BasicResults> immigrantResults = new ArrayList<BasicResults>();

	/** The results for the emigrant generations. */
	private List<BasicResults> emigrantResults = new ArrayList<BasicResults>();

	/** The overall assignment probability. */
	private double assignmentProbability;

	/**
	 * The probability that the given combination of sets was chosen. Dividing
	 * {@link ResultsOfTrial#assignmentProbability} by this value yields the
	 * probability of choosing the given assignments GIVEN the selected set
	 * combination.
	 */
	private double setCombinationProbability;

	/**
	 * Empty constructor (for Bean compatibility).
	 */
	public ResultsOfTrial() {
	}

	/**
	 * Instantiates new results for a trial.
	 * 
	 * @param projectionModel
	 *          the current projection model
	 * @param execSummary
	 *          the execution summary
	 */
	public ResultsOfTrial(IProjectionModel projectionModel,
	    ExecutionSummary execSummary) {
		nativeResults = new ArrayList<BasicResults>();
		nativeResults.add(execSummary.getNativeResults());
		projection = (ProjectionModel) projectionModel;
		assignment.putAll(execSummary.getParamAssignments());
		immigrantResults.addAll(execSummary.getImmigrantResults());
		emigrantResults.addAll(execSummary.getEmigrantResults());
		calculateAssignmentProbability();
	}

	/**
	 * Calculates the assignment's overall probability.
	 */
	private void calculateAssignmentProbability() {

		double setCombProb = 1.0;
		double assignmentCombProb = 1.0;

		Set<Integer> checkedSetTypeIDs = new HashSet<Integer>();
		for (Entry<ParameterInstance, ParameterAssignment> entry : assignment
		    .entrySet()) {

			ParameterInstance paramInstance = entry.getKey();
			ParameterAssignment paramAssignment = entry.getValue();
			assignmentCombProb *= paramAssignment.getProbability();

			SetType currentSetType = projection.getInstanceSetTypes().get(
			    paramInstance);
			if (!checkedSetTypeIDs.contains(currentSetType.getID())) {
				setCombProb *= getSetProbability(paramInstance, paramAssignment,
				    currentSetType);
				checkedSetTypeIDs.add(currentSetType.getID());
			}
		}

		setCombinationProbability = setCombProb;
		assignmentProbability = setCombProb * assignmentCombProb;
	}

	/**
	 * Gets the probability of the set.
	 * 
	 * @param paramInstance
	 *          a parameter instance belonging to the current Settype
	 * @param paramAssignment
	 *          a parameter assignment (belonging to the set in question)
	 * @param currentSetType
	 *          the current Settype
	 * @return the occurrence probability of the set
	 */
	private double getSetProbability(ParameterInstance paramInstance,
	    ParameterAssignment paramAssignment, SetType currentSetType) {
		List<p3j.pppm.sets.Set> currentSets = currentSetType.getSets();
		for (p3j.pppm.sets.Set currentSet : currentSets) {
			for (ParameterAssignment currentAssignment : currentSet
			    .getParameterAssignments(paramInstance).getAssignments()) {
				if (currentAssignment.getID() == paramAssignment.getID()) {
					return currentSet.getProbability();
				}
			}
		}
		return -1.0;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public ProjectionModel getProjection() {
		return projection;
	}

	public void setProjection(ProjectionModel projection) {
		this.projection = projection;
	}

	public Map<ParameterInstance, ParameterAssignment> getAssignment() {
		return assignment;
	}

	public void setAssignment(
	    Map<ParameterInstance, ParameterAssignment> assignment) {
		this.assignment = assignment;
	}

	public List<BasicResults> getNativeResults() {
		return nativeResults;
	}

	/**
	 * Gets the results of the natives. Checks that there is exactly one result
	 * set for the natives.
	 * 
	 * @return the results of the natives
	 */
	public BasicResults getNativesResults() {
		if (nativeResults.size() != 1) {
			throw new IllegalStateException(
			    "Number of native results does not match!");
		}
		return nativeResults.get(0);
	}

	public void setNativeResults(List<BasicResults> nativeResults) {
		this.nativeResults = nativeResults;
	}

	public double getAssignmentProbability() {
		return assignmentProbability;
	}

	public void setAssignmentProbability(double assignmentProbability) {
		this.assignmentProbability = assignmentProbability;
	}

	public List<BasicResults> getImmigrantResults() {
		return immigrantResults;
	}

	public void setImmigrantResults(List<BasicResults> immigrantResults) {
		this.immigrantResults = immigrantResults;
	}

	public List<BasicResults> getEmigrantResults() {
		return emigrantResults;
	}

	public void setEmigrantResults(List<BasicResults> emigrantResults) {
		this.emigrantResults = emigrantResults;
	}

	public double getSetCombinationProbability() {
		return setCombinationProbability;
	}

	public void setSetCombinationProbability(double setCombinationProbability) {
		this.setCombinationProbability = setCombinationProbability;
	}
}
