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
package p3j.simulation.calculation.deterministic;

/**
 * Container for some basic constants.
 * 
 * Created on July 16, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public final class Constants {

	/**
	 * This class should not be instantiated.
	 */
	private Constants() {
	}

	/**
	 * Default maximum age that can be reached (all people older are not
	 * distinguished by their age anymore).
	 */
	public static final short DEFAULT_MAXIMUM_AGE = 100;

	/**
	 * The baseline amount of persons in the mortality matrices. Can be set
	 * arbitrarily in principle, since the simulator uses relative values (i.e.
	 * estimates for probabilities).
	 */
	public static final int BASELINE_AMOUNT_MORT_MATRICES = 100000;

	/**
	 * Beginning of the fertility ages.
	 */
	public static final short FERT_AGE_BEGIN = 0;

	/**
	 * End of the fertility ages.
	 */
	public static final short FERT_AGE_END = 50;

	/**
	 * Default number of generations to be considered.
	 */
	public static final byte DEFAULT_NUM_GENERATIONS = 7;

	/**
	 * Default number of years to be predicted.
	 */
	public static final short DEFAULT_NUM_YEARS = 100;
}
