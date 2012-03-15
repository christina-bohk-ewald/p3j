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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import p3j.misc.Misc;
import p3j.misc.errors.GeneratorError;
import p3j.misc.gui.GUI;
import p3j.pppm.IStochasticOccurrence;

/**
 * Tests for the probability configuration validity checks.
 * 
 * Created: August 17, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class TestRandomNumberChecker extends TestCase {

	/** Assertion message. */
	static final String ONE_ERR_MSG = "There should be one error message in the log.";

	/** The checker for random numbers. */
	RandomNumberChecks rnc;

	/** Error log. */
	List<GeneratorError> errorLog;

	/** Number of test entities to be used. */
	static final int NUM_TEST_ENTITIES = 10;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		rnc = RandomNumberChecks.getInstance();
		errorLog = new ArrayList<GeneratorError>();
		GUI.setHeadless(true);
	}

	@Override
	public void tearDown() throws Exception {
		GUI.setHeadless(false);
		super.tearDown();
	}

	/**
	 * Tests for bad lists as parameters.
	 */
	public void testEmptyOrNullList() {
		createListWithBadParameter(null);
		createListWithBadParameter(new ArrayList<TestEntity>());
	}

	/**
	 * Checks given list as a bad parameter.
	 * 
	 * @param list
	 *          the bad parameter
	 */
	public void createListWithBadParameter(List<TestEntity> list) {
		try {
			rnc.checkProbabilitySetting("badParamTest", list, errorLog);
			fail("No exception has been thrown.");
		} catch (RuntimeException ex) {
			assertEquals(
			    "IllegalArgumentException should be thrown when confronted with list '"
			        + list + "'", IllegalArgumentException.class, ex.getClass());
		}
	}

	/**
	 * Tests for probability normalisation.
	 */
	public void testNormalisation() {
		List<TestEntity> testList = new ArrayList<TestEntity>();
		double testEntitySum = 0;
		for (int i = 0; i < NUM_TEST_ENTITIES; i++) {
			testList.add(new TestEntity(i));
			testEntitySum += i;
		}

		rnc.checkProbabilitySetting("normalisation", testList, errorLog);
		assertEquals(ONE_ERR_MSG, 1, errorLog.size());
		for (int i = 0; i < testList.size(); i++) {
			assertTrue(
			    "If probabilities sum is larger than 1, probabilities need to be normalised.",
			    Misc.numEqual(i / testEntitySum, testList.get(i).getProbability()));
		}
		rnc.checkProbabilitySetting("test3", testList, errorLog);
	}

	/**
	 * Checks behaviour in case of zero probability.
	 */
	public void testZeroProbability() {
		List<TestEntity> testList = new ArrayList<TestEntity>();
		for (int i = 0; i < NUM_TEST_ENTITIES; i++) {
			testList.add(new TestEntity(0));
		}

		rnc.checkProbabilitySetting("zeroProbability", testList, errorLog);
		assertEquals(ONE_ERR_MSG, 1, errorLog.size());
		for (TestEntity testEntity : testList) {
			assertEquals(
			    "If no probabilities were set, uniform distribution shall be assumed.",
			    1.0 / testList.size(), testEntity.getProbability());
		}
	}
}

/**
 * Test entity for probability validity check.
 * 
 * Created: August 17, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
class TestEntity implements IStochasticOccurrence {

	/**
	 * Probability of test entity.
	 */
	double probability;

	TestEntity(double prob) {
		probability = prob;
	}

	@Override
	public double getProbability() {
		return probability;
	}

	@Override
	public void setProbability(double prob) {
		probability = prob;
	}

}
