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
package p3j.pppm;

import james.core.model.IModel;

import java.util.List;
import java.util.Map;

import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.SetType;

/**
 * Currently this is a simple marker interface.
 * 
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public interface IProjectionModel extends IModel {

	/**
	 * Gets the number of projected years.
	 * 
	 * @return the number of years
	 */
	int getYears();

	/**
	 * Gets the number of projected generations.
	 * 
	 * @return the number of generations
	 */
	int getGenerations();

	/**
	 * Gets the map parameter instance => Settype.
	 * 
	 * @return the map from parameter instance to Settypes
	 */
	Map<ParameterInstance, SetType> getInstanceSetTypes();

	/**
	 * Gets the maximum age.
	 * 
	 * @return the maximum age
	 */
	int getMaximumAge();

	/**
	 * Returns list of all {@link SetType} objects, the custom ones and the
	 * default one.
	 * 
	 * @return list of all {@link SetType} objects defined in this scenario (at
	 *         least the default {@link SetType} is defined)
	 */
	List<SetType> getAllSetTypes();

}
