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
package p3j.experiment.results.filters;

import java.util.Set;

import p3j.experiment.results.ResultsOfTrial;
import p3j.pppm.parameters.ParameterAssignment;

/**
 * This is a configurable result filter. It is initialized with a list of
 * assignment IDs. Each result is checked: only if it contains at least *one* of
 * the given IDs, it will be considered. In other words, the set of IDs can be
 * considered as a white list.
 * 
 * @see ParameterAssignment
 * @see ResultsOfTrial
 * @see p3j.experiment.results.ResultExport
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ConfigurableResultFilter implements IResultFilter {

	/** The assignment IDs. */
	private final Set<Integer> assignmentIDs;

	/**
	 * Instantiates a new configurable result filter.
	 * 
	 * @param requiredIDs
	 *          the list of assignment IDs defining the filter
	 */
	public ConfigurableResultFilter(Set<Integer> requiredIDs) {
		assignmentIDs = requiredIDs;
	}

	@Override
	public boolean considerResult(ResultsOfTrial results) {
		for (ParameterAssignment assignment : results.getAssignment().values()) {
			if (assignmentIDs.contains(assignment.getID())) {
				return true;
			}
		}
		return false;
	}
}
