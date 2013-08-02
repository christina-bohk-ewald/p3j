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
package p3j.simulation.assignments.random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.misc.Pair;

import p3j.misc.errors.GeneratorError;
import p3j.misc.math.RandomNumberChecks;
import p3j.pppm.IProjectionModel;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterAssignmentSet;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;
import p3j.simulation.assignments.plugintype.IParamAssignmentGenerator;

/**
 * Simple parameter assignment generator that just employs random sampling.
 * 
 * Created: August 17, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class RandomParamAssignmentGenerator implements
    IParamAssignmentGenerator {

	/** The projection for which random assignments shall be generated. */
	private IProjectionModel projection;

	@Override
	public void init(IProjectionModel proj) {
		projection = proj;
	}

	@Override
	public Pair<Map<ParameterInstance, ParameterAssignment>, List<GeneratorError>> chooseParamAssignments(
	    IRandom random) {

		Map<ParameterInstance, ParameterAssignment> assignments = new HashMap<ParameterInstance, ParameterAssignment>();
		RandomNumberChecks rnc = RandomNumberChecks.getInstance();
		List<GeneratorError> errors = new ArrayList<GeneratorError>();

		// Get all Settypes
		List<SetType> setTypes = projection.getAllSetTypes();

		// Choose a set from each type randomly
		List<Set> sets = new ArrayList<Set>();
		for (SetType setType : setTypes) {
			sets.add(chooseSetRandomly(setType, rnc, random, errors));
		}

		// Choose parameter from each set randomly
		for (Set set : sets) {
			assignments.putAll(chooseParameterAssignmentsRandomly(set, rnc, random,
			    errors));
		}

		return new Pair<Map<ParameterInstance, ParameterAssignment>, List<GeneratorError>>(
		    assignments, errors);
	}

	/**
	 * Chooses a set randomly.
	 * 
	 * @param setType
	 *          the Settype from which to choose a set at random
	 * @param rnc
	 *          the {@link RandomNumberChecks} instance for validity checks
	 * @param rand
	 *          the random number generator
	 * @param errorLog
	 *          the errors that might occur due erroneous specification of
	 *          scenario (to be filled, see {@link RandomNumberChecks})
	 * @return a randomly chosen set
	 */
	public Set chooseSetRandomly(SetType setType, RandomNumberChecks rnc,
	    IRandom rand, List<GeneratorError> errorLog) {
		rnc.checkProbabilitySetting("Sets of Settype '" + setType.getName() + "'",
		    setType.getSets(), errorLog);
		return rnc.chooseNormalizedRandomObject(setType.getSets(), rand);
	}

	/**
	 * Executes second level of Monte-Carlo simulation. For each
	 * {@link ParameterInstance}, one {@link ParameterAssignment} is chosen.
	 * 
	 * @param set
	 *          the set from which the mapping should be chosen
	 * @param rnc
	 *          the checker for probability validity
	 * @param rand
	 *          the randomiser to be used
	 * @param errorLog
	 *          the error log
	 * @return maps that gives one assignment for each instance defined by the
	 *         {@link SetType}
	 */
	public Map<ParameterInstance, ParameterAssignment> chooseParameterAssignmentsRandomly(
	    Set set, RandomNumberChecks rnc, IRandom rand,
	    List<GeneratorError> errorLog) {
		Map<ParameterInstance, ParameterAssignment> result = new HashMap<ParameterInstance, ParameterAssignment>();
		Map<ParameterInstance, ParameterAssignmentSet> setData = set.getSetData();
		for (Entry<ParameterInstance, ParameterAssignmentSet> instEntry : setData
		    .entrySet()) {
			ParameterInstance instance = instEntry.getKey();
			ParameterAssignmentSet assignments = instEntry.getValue();
			rnc.checkProbabilitySetting("matrices of set '" + set.getName()
			    + "' for parameter instance '" + instance + "'",
			    assignments.getAssignments(), errorLog);
			result.put(instance,
			    rnc.chooseNormalizedRandomObject(assignments.getAssignments(), rand));
		}
		return result;
	}

	/**
	 * The number of assignments is practically unlimited in all realistic
	 * situations.
	 * 
	 * TODO: Retrieve calculation of exact number
	 * 
	 * @return Long.MAX_VALUE
	 */
	@Override
	public long assignmentsLeft() {
		return Long.MAX_VALUE;
	}

}
