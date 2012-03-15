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
package p3j.simulation.calculation.deterministic.parameters;

import p3j.misc.math.Matrix2D;
import p3j.pppm.PPPModelFactory;
import p3j.simulation.calculation.deterministic.Constants;

/**
 * 
 * Parameters for calculating the basic population.
 * 
 * Basic parameter class
 * 
 * Created on July 09, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class NativeParameters extends BasicParameters {

	/** Age-specific male population at the end of the start year. */
	private Matrix2D pEndSYm;

	/** Age-specific female population at the end of the start year. */
	private Matrix2D pEndSYf;

	/**
	 * Default constructor.
	 * 
	 * @param numOfYears
	 *          number of years to be projected
	 * @param maximumAge
	 *          the maximum age to be considered
	 */
	public NativeParameters(int numOfYears, int maximumAge) {
		super(numOfYears, maximumAge);
	}

	/**
	 * Constructor for bean compliance. Do NOT use manually.
	 */
	public NativeParameters() {
		super(PPPModelFactory.DEFAULT_YEARS, Constants.DEFAULT_MAXIMUM_AGE);
	}

	public Matrix2D getPEndSYm() {
		return pEndSYm;
	}

	public void setPEndSYm(Matrix2D endSYm) {
		pEndSYm = endSYm;
	}

	public Matrix2D getPEndSYf() {
		return pEndSYf;
	}

	public void setPEndSYf(Matrix2D endSYf) {
		pEndSYf = endSYf;
	}

}
