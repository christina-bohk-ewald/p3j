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
package p3j.pppm.sets;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import p3j.pppm.IStochasticOccurrence;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterAssignmentSet;
import p3j.pppm.parameters.ParameterInstance;

/**
 * Represents a set in PPPM. A set is associated with a certain {@link SetType},
 * which defines for which {@link ParameterInstance} objects this set may define
 * eligible {@link ParameterAssignment} objects. A set has a certain probability
 * of occurrence and will be randomly chosen from a list with all sets
 * associated with the same {@link SetType}. If it is chosen, for each
 * {@link ParameterInstance} that is defined by its {@link SetType} one
 * {@link ParameterAssignment} from this {@link Set} will be chosen randomly
 * (so, there are two levels of random choosing, one in the {@link SetType}, one
 * in the {@link Set}). The eligible {@link ParameterAssignment} objects are
 * stored in a {@link Map}, there is a {@link List} for each
 * {@link ParameterInstance}.
 * 
 * See documentation of {@link SetType} for more information.
 * 
 * Created on August 7, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class Set implements Serializable, IStochasticOccurrence {

	/** Serialization ID. */
	private static final long serialVersionUID = -4332835814925514014L;

	/** ID of this set. */
	private int id;

	/** Name of the set. */
	private String name = "";

	/** Probability that this set will be chosen. */
	private double probability;

	/** Description of the set. */
	private String description = "";

	/**
	 * Mapping from {@link ParameterInstance} objects to lists of their eligible
	 * {@link ParameterAssignment} objects.
	 */
	private Map<ParameterInstance, ParameterAssignmentSet> setData = new HashMap<ParameterInstance, ParameterAssignmentSet>();

	/**
	 * Default constructor.
	 * 
	 * @param definedParameters
	 *          set of parameter instances for which this set may define
	 *          assignments
	 * @param setName
	 *          name of the set
	 * @param desc
	 *          description of the set
	 * @param prob
	 *          probability of the set
	 */
	public Set(List<ParameterInstance> definedParameters, String setName,
	    String desc, double prob) {
		this.name = setName;
		this.description = desc;
		this.probability = prob;

		for (ParameterInstance instance : definedParameters) {
			addParameterInstance(instance);
		}
	}

	/**
	 * Constructor for bean compatibility.
	 */
	public Set() {
	}

	/**
	 * Get eligible {@link ParameterAssignment} objects for given
	 * {@link ParameterInstance}.
	 * 
	 * @param paramInstance
	 *          the parameter instance
	 * @return list of parameter assignments
	 */
	public ParameterAssignmentSet getParameterAssignments(
	    ParameterInstance paramInstance) {
		return setData.get(paramInstance);
	}

	/**
	 * Adds a parameter instance to the set. This adds an empty list of eligible
	 * {@link ParameterAssignment} objects to {@link Set#setData}. This is a
	 * management method for the associated {@link SetType}.
	 * 
	 * @param parameterInstance
	 *          the parameter instance to be added
	 */
	public final void addParameterInstance(ParameterInstance parameterInstance) {
		setData.put(parameterInstance, new ParameterAssignmentSet());
	}

	/**
	 * Removes a parameter instance from the set. All associated
	 * {@link ParameterAssignment} objects will be removed from
	 * {@link Set#setData}. This is a management method for the associated
	 * {@link SetType}.
	 * 
	 * @param parameterInstance
	 *          the parameter instance to be removed
	 * @return assignments associated with removed parameter instance (null if not
	 *         existing)
	 */
	public ParameterAssignmentSet removeParameterInstance(
	    ParameterInstance parameterInstance) {
		return setData.remove(parameterInstance);
	}

	/**
	 * Adds a new parameter assignment.
	 * 
	 * @param paramAssign
	 *          parameter assignment to be added
	 */
	public void addParameterAssignment(ParameterAssignment paramAssign) {
		ParameterAssignmentSet paramAssignments = setData.get(paramAssign
		    .getParamInstance());
		if (paramAssignments == null) {
			setProblemOccurred(paramAssign);
		} else {
			paramAssignments.add(paramAssign);
		}
	}

	/**
	 * Removes a parameter assignment.
	 * 
	 * @param paramAssign
	 *          parameter assignment to be removed
	 */
	public void removeParameterAssignment(ParameterAssignment paramAssign) {
		ParameterAssignmentSet paramAssignments = setData.get(paramAssign
		    .getParamInstance());
		if (paramAssignments == null) {
			setProblemOccurred(paramAssign);
		} else {
			paramAssignments.remove(paramAssign);
		}
	}

	/**
	 * Throws a {@link RuntimeException} reporting on problems while processing a
	 * {@link ParameterAssignment}. This method is called when the
	 * {@link ParameterInstance} associated with the {@link ParameterAssignment}
	 * is not managed by the {@link SetType} of this set.
	 * 
	 * @param assignment
	 *          the parameter assignment to be processed
	 */
	protected void setProblemOccurred(ParameterAssignment assignment) {
		throw new IllegalArgumentException(
		    "PPPM Set: Cannot process parameter assignment '"
		        + assignment.getName() + "'. Its instance '"
		        + assignment.getParamInstance()
		        + "' seems to be managed by another Settype !");
	}

	/**
	 * Retrieve the number of eligible {@link ParameterAssignment} objects for a
	 * given {@link ParameterInstance}.
	 * 
	 * @param instance
	 *          the parameter instance
	 * @return number of assignments for given instance
	 */
	public int getNumberOfAssignments(ParameterInstance instance) {
		ParameterAssignmentSet assignments = setData.get(instance);
		return assignments != null ? assignments.size() : 0;
	}

	/**
	 * Checks whether this set contains at least one assignment for every
	 * parameter instance.
	 * 
	 * @return true, if set is valid (i.e. above condition is satisfied)
	 */
	public boolean isValid() {
		for (ParameterAssignmentSet paramAssignmentSet : setData.values()) {
			if (paramAssignmentSet.size() == 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void setProbability(double probability) {
		if (probability < 0 || probability > 1) {
			throw new IllegalArgumentException("A probability value of "
			    + probability + " is not meaningful!");
		}
		this.probability = probability;
	}

	@Override
	public String toString() {
		return name;
	}

	// Getter/Setter for bean compatibility

	public Map<ParameterInstance, ParameterAssignmentSet> getSetData() {
		return setData;
	}

	public void setSetData(Map<ParameterInstance, ParameterAssignmentSet> setData) {
		this.setData = setData;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public double getProbability() {
		return this.probability;
	}

	public int getID() {
		return id;
	}

	public void setID(int uniqueID) {
		id = uniqueID;
	}

}
