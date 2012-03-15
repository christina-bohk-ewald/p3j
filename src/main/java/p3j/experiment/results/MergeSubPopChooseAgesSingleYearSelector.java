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

/**
 * Merges some sub-populations and selects the ages for a specific years
 * afterwards.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class MergeSubPopChooseAgesSingleYearSelector extends
    ChooseAgesForSingleYearSelector {

	/** The aggregation selectors that point to added variables. */
	private final AbstractAggregationSelector[] addAggSelectors;

	/** The aggregation selectors that point to subtracted variables. */
	private final AbstractAggregationSelector[] subtractAggSelectors;

	/** The file name for result storage. */
	private final String fileName;

	/**
	 * Instantiates a new merge sub pop choose ages single year selector.
	 * 
	 * @param selectorsForAddition
	 *          the selectors for addition
	 * @param selectorsForSubtraction
	 *          the selectors for subtraction
	 * @param yearForSelection
	 *          the year for selection
	 * @param customFileName
	 *          the custom file name
	 */
	public MergeSubPopChooseAgesSingleYearSelector(
	    AbstractAggregationSelector[] selectorsForAddition,
	    AbstractAggregationSelector[] selectorsForSubtraction,
	    int yearForSelection, String customFileName) {
		// more than one selector is used - don't use super class here
		super(null, null, -1, yearForSelection);
		addAggSelectors = getCopy(selectorsForAddition);
		subtractAggSelectors = getCopy(selectorsForSubtraction);
		fileName = customFileName;
	}

	/**
	 * Instantiates a new merge sub pop choose ages single year selector.
	 * 
	 * @param mergeSumSelector
	 *          the merge sum selector
	 * @param yearForSelection
	 *          the year for selection
	 */
	public MergeSubPopChooseAgesSingleYearSelector(
	    MergeSubPopSumOverAgesSelector mergeSumSelector, int yearForSelection) {
		this(mergeSumSelector.getAddAggregationSelectors(), mergeSumSelector
		    .getSubtractAggregationSelectors(), yearForSelection, mergeSumSelector
		    .getFileName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see p3j.experiment.results.ChooseAgesForSingleYearSelector#getFileName()
	 */
	@Override
	protected String getFileName() {
		return getPrefix() + fileName;
	}

	@Override
	public void consider(int trialCount, ResultsOfTrial result) {
		double[] currentData = new double[getNumberOfAgeClasses()];
		processSelectors(result, currentData, addAggSelectors, true);
		processSelectors(result, currentData, subtractAggSelectors, false);
		System.arraycopy(currentData, 0, aggregation[trialCount], 0,
		    getNumberOfAgeClasses());
	}

	/**
	 * Process selectors.
	 * 
	 * @param result
	 *          the result
	 * @param currentData
	 *          the current data
	 * @param selectors
	 *          the selectors
	 * @param add
	 *          the add
	 */
	private void processSelectors(ResultsOfTrial result, double[] currentData,
	    AbstractAggregationSelector[] selectors, boolean add) {
		if (selectors != null) {
			for (AbstractAggregationSelector selector : selectors) {
				double[][] selectedData = selector.select(result).toArray();
				for (int i = 0; i < getNumberOfAgeClasses(); i++) {
					currentData[i] = currentData[i]
					    + (add ? selectedData[i][getYear()] : -1
					        * selectedData[i][getYear()]);
				}
			}
		}
	}
}
