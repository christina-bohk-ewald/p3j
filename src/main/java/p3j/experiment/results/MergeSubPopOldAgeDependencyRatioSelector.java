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
 * Computes the old age dependency ratio for certain sub-populations.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class MergeSubPopOldAgeDependencyRatioSelector extends
    MergeSubPopSumOverAgesSelector {

	/** The lower border for the summation. */
	static final int LOWER_BORDER = 15;

	/** The upper border for the summation. */
	static final int UPPER_BORDER = 65;

	/**
	 * Instantiates a new merge sub-population for old age dependency ratios
	 * selector.
	 * 
	 * @param selectorsForAddition
	 *          the selectors for addition
	 * @param selectorsForSubtraction
	 *          the selectors for subtraction
	 * @param customFileName
	 *          the custom file name
	 */
	public MergeSubPopOldAgeDependencyRatioSelector(
	    AbstractAggregationSelector[] selectorsForAddition,
	    AbstractAggregationSelector[] selectorsForSubtraction,
	    String customFileName) {
		super(selectorsForAddition, selectorsForSubtraction, customFileName);
	}

	/**
	 * Instantiates a new merge sub-populations for old age dependency ratio
	 * selector.
	 * 
	 * @param selectorsForAddition
	 *          the selectors for addition
	 * @param selectorsForSubtraction
	 *          the selectors for subtraction
	 * @param customFileName
	 *          the custom file name
	 */
	public MergeSubPopOldAgeDependencyRatioSelector(
	    List<AbstractAggregationSelector> selectorsForAddition,
	    List<AbstractAggregationSelector> selectorsForSubtraction,
	    String customFileName) {
		super(selectorsForAddition, selectorsForSubtraction, customFileName);
	}

	@Override
	public void consider(int trialCount, ResultsOfTrial result) {

		List<double[]> additionArraysYoungPop = new ArrayList<double[]>();
		List<double[]> additionArraysOldPop = new ArrayList<double[]>();
		List<double[]> subtractionArraysYoungPop = new ArrayList<double[]>();
		List<double[]> subtractionArraysOldPop = new ArrayList<double[]>();

		for (AbstractAggregationSelector selector : getAddAggregationSelectors()) {
			additionArraysYoungPop.add(Matrix2D.sumRows(selector.select(result),
			    LOWER_BORDER, UPPER_BORDER).toArray()[0]);
			additionArraysOldPop.add(Matrix2D.sumRows(selector.select(result),
			    UPPER_BORDER, null).toArray()[0]);
		}

		for (AbstractAggregationSelector selector : getSubtractAggregationSelectors()) {
			subtractionArraysYoungPop.add(Matrix2D.sumRows(selector.select(result),
			    LOWER_BORDER, UPPER_BORDER).toArray()[0]);
			subtractionArraysYoungPop.add(Matrix2D.sumRows(selector.select(result),
			    UPPER_BORDER, null).toArray()[0]);
		}

		double[] rowSumYoungPop = sumPerElement(additionArraysYoungPop,
		    subtractionArraysYoungPop);
		double[] rowSumOldPop = sumPerElement(additionArraysOldPop,
		    subtractionArraysOldPop);

		double[] rowQuotients = new double[rowSumYoungPop.length];
		for (int i = 0; i < rowQuotients.length; i++) {
			rowQuotients[i] = rowSumOldPop[i] / rowSumYoungPop[i];
		}

		System.arraycopy(rowQuotients, 0, aggregation[trialCount], 0,
		    rowQuotients.length);
	}

}
