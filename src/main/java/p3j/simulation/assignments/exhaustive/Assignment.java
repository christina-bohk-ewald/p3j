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

import java.util.List;

import p3j.misc.Misc;

/**
 * Stores a selection of (Settype, parameter,...) assignments and their overall
 * probability.
 * 
 * Created: August 23, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
class Assignment implements Comparable<Assignment> {

	/** Assignment indices. */
	private final List<Integer> assignmentIndices;

	/** The probability of this assignment. */
	private final Double probability;

	/** Index sum (required for sorting). */
	private final Integer indexSum;

	/** The id of this assignment: index1+index2+etc (concatenation). */
	private final String id;

	/**
	 * Default constructor.
	 * 
	 * @param stAssigns
	 *          indices of assignments
	 * @param prob
	 *          their probability
	 */
	Assignment(List<Integer> stAssigns, double prob) {
		assignmentIndices = stAssigns;
		probability = prob;

		int sum = 0;
		StringBuffer strBuf = new StringBuffer();
		for (Integer index : stAssigns) {
			sum += index;
			strBuf.append(index);
			strBuf.append('-');
		}

		id = strBuf.toString();
		indexSum = sum;
	}

	public List<Integer> getAssignmentIndices() {
		return assignmentIndices;
	}

	public Double getProbability() {
		return probability;
	}

	@Override
	public int compareTo(Assignment a) {
		if (!Misc.numEqual(probability, a.probability)) {
			return a.probability.compareTo(probability);
		}
		return id.compareTo(a.id);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Assignment)) {
			return false;
		}
		return compareTo(((Assignment) o)) == 0;
	}

	@Override
	public int hashCode() {
		int code = 0;
		int multiplier = 1;
		for (int index : assignmentIndices) {
			code += multiplier * index;
			multiplier *= Misc.BASE_NUM;
		}
		return code;
	}

	/**
	 * Gets the ID.
	 * 
	 * @return the id
	 */
	public String getID() {
		return id;
	}

	public Integer getIndexSum() {
		return indexSum;
	}

}
