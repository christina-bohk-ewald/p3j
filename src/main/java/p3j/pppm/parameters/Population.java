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

import java.util.Comparator;

/**
 * 
 * Enumeration of the populations that are distinguished. Each {@link Parameter}
 * is assigned with a population type, which facilitates ordering and
 * presentation on the GUI.
 * 
 * Created: August 24, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public enum Population implements Comparator<Population> {

	/**
	 * The native population.
	 */
	NATIVES,

	/**
	 * The emigrant population.
	 */
	EMIGRANTS,

	/**
	 * The immigrant population.
	 */
	IMMIGRANTS;

	@Override
	public int compare(Population p1, Population p2) {
		if (p1.equals(p2)) {
			return 0;
		}
		if (p1 == NATIVES || (p1 == EMIGRANTS && p2 == IMMIGRANTS)) {
			return -1;
		}
		return 1;
	}

	@Override
	public String toString() {
		switch (this) {
		case NATIVES:
			return "Natives";
		case EMIGRANTS:
			return "Emigrants";
		case IMMIGRANTS:
			return "Immigrants";
		default:
			return "Unknown";
		}
	}

}
