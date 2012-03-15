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
package p3j.simulation.assignments.exhaustive;

import p3j.simulation.ISimulationParameters;

/**
 * Holds parameters for the exhaustive assignment generation approach.
 * 
 * Created: August 21, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class ExhaustiveSimParameters implements ISimulationParameters {

	/** Serialization ID. */
	private static final long serialVersionUID = -2083782392926642137L;

	/** Maximal number of runs. Default is -1 (not restricted). */
	private Integer maxNumRuns = -1;

	/**
	 * Maximal fraction of overall runs to be done. Default is 1 (not restricted).
	 */
	private Double maxRunFraction = 1.0;

	/**
	 * Minimal probability a certain setup must at least have in order to be
	 * calculated. Default is 0 (unrestricted, all setups maybe computed).
	 */
	private Double cutOffProbability = 0.0;

	/**
	 * Summing up the probabilities of all setups that already have been executed,
	 * when this sum is reached the simulator should stop.
	 */
	private Double desiredOverallProbability = 1.0;

	/**
	 * Gets the max num runs.
	 * 
	 * @return the max num runs
	 */
	public int getMaxNumRuns() {
		return maxNumRuns;
	}

	/**
	 * Sets the max num runs.
	 * 
	 * @param maxNumRuns
	 *          the new max num runs
	 */
	public void setMaxNumRuns(int maxNumRuns) {
		this.maxNumRuns = maxNumRuns;
	}

	/**
	 * Gets the max run fraction.
	 * 
	 * @return the max run fraction
	 */
	public double getMaxRunFraction() {
		return maxRunFraction;
	}

	/**
	 * Sets the max run fraction.
	 * 
	 * @param maxRunFraction
	 *          the new max run fraction
	 */
	public void setMaxRunFraction(double maxRunFraction) {
		this.maxRunFraction = maxRunFraction;
	}

	/**
	 * Gets the cut off probability.
	 * 
	 * @return the cut off probability
	 */
	public double getCutOffProbability() {
		return cutOffProbability;
	}

	/**
	 * Sets the cut off probability.
	 * 
	 * @param cutOffProbability
	 *          the new cut off probability
	 */
	public void setCutOffProbability(double cutOffProbability) {
		this.cutOffProbability = cutOffProbability;
	}

	/**
	 * Gets the desired overall probability.
	 * 
	 * @return the desired overall probability
	 */
	public double getDesiredOverallProbability() {
		return desiredOverallProbability;
	}

	/**
	 * Sets the desired overall probability.
	 * 
	 * @param desiredOverallProbability
	 *          the new desired overall probability
	 */
	public void setDesiredOverallProbability(double desiredOverallProbability) {
		this.desiredOverallProbability = desiredOverallProbability;
	}

}
