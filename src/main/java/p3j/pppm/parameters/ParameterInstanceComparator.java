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
import java.util.Comparator;
import java.util.List;

/**
 * Compares {@link ParameterInstance} objects based on their comparison index.
 * The comparison index is used in the GUI to always keep the same order of
 * parameters. This makes it much easier for the user to find parameters.
 * 
 * 
 * Created on February 4, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ParameterInstanceComparator implements
    Comparator<ParameterInstance>, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3663405828303366226L;

	@Override
	public int compare(ParameterInstance instance1, ParameterInstance instance2) {
		return instance1.getComparisonIndex() - instance2.getComparisonIndex();
	}

	/**
	 * Inserts instance in a sorted parameter instance list.
	 * 
	 * @param parameterInstances
	 *          list of parameter instances
	 * @param insertInstance
	 *          parameter instance to be inserted in this list
	 */
	public static void insert(List<ParameterInstance> parameterInstances,
	    ParameterInstance insertInstance) {
		int insertIndex = 0;
		for (int i = 0; i < parameterInstances.size(); i++) {
			ParameterInstance instance = parameterInstances.get(i);
			if (instance.getComparisonIndex() > insertInstance.getComparisonIndex()) {
				break;
			}
			insertIndex++;
		}
		parameterInstances.add(insertIndex, insertInstance);

	}

}
