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
import java.util.ArrayList;
import java.util.List;

import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.parameters.ParameterInstanceComparator;

/**
 * Represents a Settype. Basically, a Settype defines a list of
 * {@link ParameterInstance} objects. This list represents parameter instances
 * that loosely depend on each other. Each element of this list is exclusive to
 * one Settype (by default, this is the default Settype, see
 * {@link p3j.pppm.ProjectionModel} ), i.e., the parameter instance lists of all
 * Settypes are disjunct. Each {@link Set} of a {@link SetType} contains a list
 * of {@link p3j.pppm.parameters.ParameterAssignment} objects for each instances
 * and has an occurrence probability itself. Hence, the Monte-Carlo simulation
 * firstly selects a {@link Set} for each {@link SetType} randomly, and then
 * selects a {@link p3j.pppm.parameters.ParameterAssignment} from the options
 * offered by the chosen {@link Set}, for each {@link ParameterInstance} that is
 * defined in the {@link SetType}.
 * 
 * Basically, {@link Set} and {@link SetType} serve the purpose of avoiding
 * implausible combinations of input matrices for the PPPM (e.g., high fertility
 * of native population and immigrants, but low fertility of emigrants could be
 * such a combination). Each Settype manages the list of {@link Set} instances
 * that are associated with it. Hence, sets need to be added/removed from the
 * Settype.
 * 
 * Created on August 7, 2006
 * 
 * @see Set
 * @see p3j.pppm.ProjectionModel
 * @see p3j.pppm.parameters.ParameterAssignment
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class SetType implements Serializable {

	/** Serialization ID. */
	private static final long serialVersionUID = 3111687701869447727L;

	/** ID of this Settype. */
	private int id;

	/** List of defined parameters. */
	private List<ParameterInstance> definedParameters = new ArrayList<ParameterInstance>();

	/** List of all sets defined for this type. */
	private List<Set> sets = new ArrayList<Set>();

	/** Name of Settype. */
	private String name = "";

	/** Description of Settype. */
	private String description = "";

	/**
	 * Default constructor.
	 * 
	 * @param typeName
	 *          name of the Settype
	 * @param typeDescription
	 *          description for this Settype
	 */
	public SetType(String typeName, String typeDescription) {
		this.name = typeName;
		this.description = typeDescription;
	}

	/**
	 * Constructor for bean compatibility.
	 */
	public SetType() {
	}

	/**
	 * Creates a new set for a type.
	 * 
	 * @param setName
	 *          name of the set to be created
	 * @param setDescription
	 *          description of the set to be created
	 * @param probability
	 *          probability of the set to be created
	 * @return the newly created set
	 */
	public Set createSet(String setName, String setDescription, double probability) {
		Set set = new Set(getDefinedParameters(), setName, setDescription,
		    probability);
		sets.add(set);
		return set;
	}

	/**
	 * Removes a set from this Settype.
	 * 
	 * @param set
	 *          the set to be removed
	 * @return true, if set could be removed; otherwise false
	 */
	public boolean removeSet(Set set) {
		return sets.remove(set);
	}

	/**
	 * Removes a parameter instance from this Settype. Removes all
	 * {@link p3j.pppm.parameters.ParameterAssignment} objects from all
	 * {@link Set} objects associated with this Settype.
	 * 
	 * @param instance
	 *          the instance to be removed
	 */
	public void removeInstance(ParameterInstance instance) {
		definedParameters.remove(instance);
		for (Set set : sets) {
			set.removeParameterInstance(instance);
		}
	}

	/**
	 * Adds a parameter instance for this Settype. Updates all associated
	 * {@link Set} objects as well.
	 * 
	 * @param instance
	 *          the instance to be added
	 */
	public void addInstance(ParameterInstance instance) {
		ParameterInstanceComparator.insert(definedParameters, instance);
		for (Set set : sets) {
			set.addParameterInstance(instance);
		}
	}

	@Override
	public String toString() {
		return name;
	}

	// Getter/Setter for bean compatibility

	public void setDefinedParameters(List<ParameterInstance> definedParameters) {
		this.definedParameters = definedParameters;
	}

	public void setSets(List<Set> sets) {
		this.sets = sets;
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

	public List<ParameterInstance> getDefinedParameters() {
		return definedParameters;
	}

	public List<Set> getSets() {
		return sets;
	}

	/**
	 * Gets the number of sets for this Settype.
	 * 
	 * @return the number of sets
	 */
	public int getNumOfSets() {
		return sets.size();
	}

	public int getID() {
		return id;
	}

	public void setID(int uniqueID) {
		id = uniqueID;
	}

}
