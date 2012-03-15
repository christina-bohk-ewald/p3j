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
package p3j.pppm.parameters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import p3j.misc.errors.GeneratorError;
import p3j.misc.math.RandomNumberChecks;

/**
 * Stores a set of parameter assignments. This is a somewhat artificial class
 * that is solely required for a clean object-relational mapping.
 * 
 * Created: August 18, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ParameterAssignmentSet implements Serializable {

	/** Serialization ID. */
	private static final long serialVersionUID = -1138437071774695491L;

	/** The ID of the set. */
	private int id;

	/** The set of assignments. */
	private Set<ParameterAssignment> assignments = new HashSet<ParameterAssignment>();

	/**
	 * Sums up the probabilities of all assignments.
	 * 
	 * @return sum of assignment probabilities
	 */
	public double getProbabilitySum() {
		double sum = 0;
		for (ParameterAssignment assignment : assignments) {
			sum += assignment.getProbability();
		}
		return sum;
	}

	/**
	 * Adjusts probability using the {@link RandomNumberChecks} singleton.
	 * 
	 * @return error log
	 */
	public List<GeneratorError> adjustProbability() {
		List<GeneratorError> errorLog = new ArrayList<GeneratorError>();
		if (assignments.size() == 0) {
			return errorLog;
		}
		RandomNumberChecks.getInstance().checkProbabilitySetting(
		    assignments.iterator().next().getParamInstance().toString(),
		    assignments, errorLog);
		return errorLog;
	}

	public Set<ParameterAssignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(Set<ParameterAssignment> assignments) {
		this.assignments = assignments;
	}

	public int getID() {
		return id;
	}

	public void setID(int uniqueID) {
		id = uniqueID;
	}

	/**
	 * Adds a parameter assignment to the set.
	 * 
	 * @param paramAssignment
	 *          the parameter assignment to be added
	 */
	public void add(ParameterAssignment paramAssignment) {
		assignments.add(paramAssignment);
	}

	/**
	 * Removes the parameter assignment from the set.
	 * 
	 * @param paramAssignment
	 *          the parameter assignment to be removed
	 */
	public void remove(ParameterAssignment paramAssignment) {
		assignments.remove(paramAssignment);
	}

	/**
	 * Get size of the set.
	 * 
	 * @return the size of the set
	 */
	public int size() {
		return assignments.size();
	}

}
