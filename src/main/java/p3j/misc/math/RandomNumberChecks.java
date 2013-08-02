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
package p3j.misc.math;

import java.util.Collection;
import java.util.List;

import org.jamesii.core.math.random.generators.IRandom;

import p3j.gui.P3J;
import p3j.misc.Misc;
import p3j.misc.errors.GeneratorError;
import p3j.misc.errors.ProbabilityError;
import p3j.misc.gui.GUI;
import p3j.pppm.IStochasticOccurrence;

/**
 * Class to hold static functions for checking random numbers regarding their
 * mathematical validity, and some non-static functions for selecting an object
 * randomly. Singleton pattern.
 * 
 * Created on January 21, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public final class RandomNumberChecks {

	/** Reference to singleton. */
	private static RandomNumberChecks instance = new RandomNumberChecks();

	/** Standard text for error messages. */
	static final String PROB_SUM = "Probability sum of ";

	/**
	 * Default constructor.
	 */
	private RandomNumberChecks() {
	}

	/**
	 * Gets the single instance of RandomNumberChecks.
	 * 
	 * @return single instance of RandomNumberChecks
	 */
	public static RandomNumberChecks getInstance() {
		return instance;
	}

	/**
	 * Tries to repeat values before throwing an exceptions. All values get
	 * normalised (also all 0's: this will be changed to a uniform distribution).
	 * 
	 * @param <V>
	 *          type of entity that shall be chosen by chance
	 * @param name
	 *          name of the objects to be checked (for generation of a suitable
	 *          error messages)
	 * @param objects
	 *          the collection of objects to be checked
	 * @param errors
	 *          list of error messages (to be filled if something goes wrong)
	 */
	public <V extends IStochasticOccurrence> void checkProbabilitySetting(
	    String name, Collection<V> objects, List<GeneratorError> errors) {

		if (objects == null || objects.size() == 0) {
			GUI.printErrorMessage(P3J.getInstance(), "Missing parameter assignment",
			    "No assumptions are defined for parameter: " + name);
			throw new IllegalArgumentException(
			    "There are no assignments for variable '" + name + "'!");
		}

		final Double probSum = getProbSum(objects);

		// Either assign uniform probabilities...
		if (Misc.numEqual(probSum, 0)) {
			final double uniformProb = 1.0 / objects.size();
			for (V object : objects) {
				object.setProbability(uniformProb);
			}
			errors.add(new ProbabilityError(true, PROB_SUM + name
			    + " is zero. Probabilities have been distributed equally."));
		}
		// or normalise everything
		else if (!Misc.numEqual(probSum, 1.0)) {
			final double probFactor = 1 / probSum;
			for (V object : objects) {
				object.setProbability(object.getProbability() * probFactor);
			}
			errors
			    .add(new ProbabilityError(
			        true,
			        PROB_SUM
			            + name
			            + " does not equal 100%. Probabilities have been normalized accordingly."));
		}

	}

	/**
	 * Choose random object if the sum of all probabilities is 100.
	 * 
	 * @param <V>
	 *          type of randomly chosen entity
	 * @param objects
	 *          collection of objects from which to choose
	 * @param rand
	 *          randomiser to be used
	 * @return randomly chosen object
	 */
	public <V extends IStochasticOccurrence> V chooseNormalizedRandomObject(
	    Collection<V> objects, IRandom rand) {
		return chooseRandomObject(objects, rand, getProbSum(objects));
	}

	/**
	 * Choose random object if the sum of all probabilities is 100.
	 * 
	 * @param <V>
	 *          type of randomly chosen entity
	 * @param objects
	 *          collection of objects from which to choose
	 * @param rand
	 *          the randomiser to be used
	 * @return randomly chosen object
	 */
	public <V extends IStochasticOccurrence> V chooseRandomObject(
	    Collection<V> objects, IRandom rand) {
		return chooseRandomObject(objects, rand, getProbSum(objects));
	}

	/**
	 * Choose random entity based on a certain probability sum.
	 * 
	 * @param <V>
	 *          type of randomly chosen entity
	 * @param objects
	 *          collection of objects from which to choose
	 * @param rand
	 *          the randomiser to be used
	 * @param overallProbSum
	 *          overall probability sum
	 * @return randomly chosen object
	 */
	protected <V extends IStochasticOccurrence> V chooseRandomObject(
	    Collection<V> objects, IRandom rand, double overallProbSum) {
		double prob = rand.nextDouble();
		double currentProbSum = 0;
		for (V object : objects) {
			currentProbSum += object.getProbability() / overallProbSum;
			if (prob < currentProbSum) {
				return object;
			}
		}
		return null;
	}

	/**
	 * Get the probability sum from a list of of {@link IStochasticOccurrence}
	 * instances.
	 * 
	 * @param objects
	 *          collection of objects from which to choose
	 * @return sum of the occurrence probabilities of all objects in the list
	 */
	public double getProbSum(Collection<? extends IStochasticOccurrence> objects) {
		double probSum = 0;
		for (IStochasticOccurrence object : objects) {
			probSum += object.getProbability();
		}
		return probSum;
	}

}
