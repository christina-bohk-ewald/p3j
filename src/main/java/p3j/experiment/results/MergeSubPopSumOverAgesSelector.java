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
package p3j.experiment.results;

import java.util.ArrayList;
import java.util.List;

import p3j.misc.math.Matrix2D;

/**
 * This aggregator allows to add and subtract certain population, before summing
 * over their ages and afterwards behaving like {@link SumOverAgesSelector}. All
 * combinations of result/trial selectors for addition/subtraction will be
 * chosen.
 * 
 * @see IAggregationSelector
 * @see SumOverAgesSelector
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class MergeSubPopSumOverAgesSelector extends SumOverAgesSelector {

	/** The aggregation selectors that point to added variables. */
	private final AbstractAggregationSelector[] addAggSelectors;

	/** The aggregation selectors that point to subtracted variables. */
	private final AbstractAggregationSelector[] subtractAggSelectors;

	/** The file name for result storage. */
	private final String fileName;

	/**
	 * Instantiates aggregator that merges subpopulations.
	 * 
	 * @param selectorsForAddition
	 *          the selectors for addition
	 * @param selectorsForSubtraction
	 *          the selectors for subtraction
	 * @param customFileName
	 *          the custom file name for the aggregated results
	 */
	public MergeSubPopSumOverAgesSelector(
	    AbstractAggregationSelector[] selectorsForAddition,
	    AbstractAggregationSelector[] selectorsForSubtraction,
	    String customFileName) {
		// more than one selector is used - don't use super class here
		super(null, null, -1);
		addAggSelectors = getCopy(selectorsForAddition);
		subtractAggSelectors = getCopy(selectorsForSubtraction);
		fileName = customFileName;
	}

	/**
	 * Instantiates a new merge sub pop sum over ages selector.
	 * 
	 * @param selectorsForAddition
	 *          the selectors for addition
	 * @param customFileName
	 *          the custom file name
	 */
	public MergeSubPopSumOverAgesSelector(
	    AbstractAggregationSelector[] selectorsForAddition, String customFileName) {
		this(selectorsForAddition, new AbstractAggregationSelector[0],
		    customFileName);
	}

	/**
	 * Instantiates a new merge-sub-pop-sum-over-ages selector.
	 * 
	 * @param selectorsForAddition
	 *          the selectors for addition
	 * @param selectorsForSubtraction
	 *          the selectors for subtraction
	 * @param customFileName
	 *          the custom file name
	 */
	public MergeSubPopSumOverAgesSelector(
	    List<AbstractAggregationSelector> selectorsForAddition,
	    List<AbstractAggregationSelector> selectorsForSubtraction,
	    String customFileName) {
		this(selectorsForAddition != null ? selectorsForAddition
		    .toArray(new AbstractAggregationSelector[0]) : null,
		    selectorsForSubtraction != null ? selectorsForSubtraction
		        .toArray(new AbstractAggregationSelector[0]) : null, customFileName);
	}

	/**
	 * Instantiates a new merge sub pop sum over ages selector.
	 * 
	 * @param selectorsForAddition
	 *          the selectors for addition
	 * @param customFileName
	 *          the custom file name
	 */
	public MergeSubPopSumOverAgesSelector(
	    List<AbstractAggregationSelector> selectorsForAddition,
	    String customFileName) {
		this(selectorsForAddition, null, customFileName);
	}

	@Override
	public void consider(int trialCount, ResultsOfTrial result) {
		List<double[]> additionArrays = new ArrayList<double[]>();
		List<double[]> subtractionArrays = new ArrayList<double[]>();

		for (AbstractAggregationSelector selector : getAddAggregationSelectors()) {
			additionArrays
			    .add(Matrix2D.sumRows(selector.select(result)).toArray()[0]);
		}
		for (AbstractAggregationSelector selector : getSubtractAggregationSelectors()) {
			subtractionArrays
			    .add(Matrix2D.sumRows(selector.select(result)).toArray()[0]);
		}

		double[] rowSum = sumPerElement(additionArrays, subtractionArrays);

		System.arraycopy(rowSum, 0, aggregation[trialCount], 0, rowSum.length);
	}

	@Override
	protected String getFileName() {
		return fileName;
	}

	public AbstractAggregationSelector[] getAddAggregationSelectors() {
		return addAggSelectors;
	}

	public AbstractAggregationSelector[] getSubtractAggregationSelectors() {
		return subtractAggSelectors;
	}

}
