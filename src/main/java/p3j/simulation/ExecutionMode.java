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
package p3j.simulation;

import p3j.simulation.assignments.exhaustive.ExhaustiveParamAssignmentGenFactory;
import p3j.simulation.assignments.random.RandomParamAssignmentGenFactory;

/**
 * Simple enumeration to easily summarise all execution modes.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public enum ExecutionMode {

	/** Draw parameter instances at random. */
	MONTE_CARLO,

	/** Draw parameter instances in highest-overall-probability order. */
	EXHAUSTIVE;

	/** The string representation of the monte-carlo method. */
	public static final String DESC_MONTE_CARLO = "Monte-Carlo";

	/** The string representation of the exhaustive method. */
	public static final String DESC_EXHAUSTIVE = "Exhaustive";

	@Override
	public String toString() {
		switch (this) {
		case MONTE_CARLO:
			return DESC_MONTE_CARLO;
		case EXHAUSTIVE:
			return DESC_EXHAUSTIVE;
		default:
			return "unknown";
		}
	}

	/**
	 * Gets the execution mode associated with the given text.
	 * 
	 * @param text
	 *          the text (retrievable via toString())
	 * @return the execution mode associated with the given text, null if none was
	 *         found
	 */
	public static ExecutionMode forString(String text) {
		if (text.equals(DESC_EXHAUSTIVE)) {
			return EXHAUSTIVE;
		} else if (text.equals(DESC_MONTE_CARLO)) {
			return MONTE_CARLO;
		}
		return null;
	}

	/**
	 * Gets the parameter assignment generator factory name for a given execution
	 * name.
	 * 
	 * @return the parameter assignment generator factory name
	 */
	public String getFactoryName() {
		switch (this) {
		case MONTE_CARLO:
			return RandomParamAssignmentGenFactory.class.getName();
		case EXHAUSTIVE:
			return ExhaustiveParamAssignmentGenFactory.class.getName();
		default:
			return "";
		}
	}

}
