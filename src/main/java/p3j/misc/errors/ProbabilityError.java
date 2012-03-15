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
package p3j.misc.errors;

/**
 * An exception that represents an error with a set of probabilities. This
 * happen if, e.g., it is 0 or doesn't sum up to 1.
 * 
 * Created on January 21, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ProbabilityError extends GeneratorError {

	/** Serialization ID. */
	private static final long serialVersionUID = 2294871670420743398L;

	/**
	 * Flag if probability sum equals zero. If this is false, the exception marks
	 * a list of stochastically occurring objects whose probabilities do not sum
	 * up to 1.
	 */
	private final boolean equalZero;

	/**
	 * Default constructor.
	 * 
	 * @param eqZero
	 *          flag that indicates the sum is equal to zero
	 * @param msg
	 *          the message to be displayed to the user
	 */
	public ProbabilityError(boolean eqZero, String msg) {
		super(msg);
		this.equalZero = eqZero;
	}

	public boolean isEqualZero() {
		return equalZero;
	}
}
