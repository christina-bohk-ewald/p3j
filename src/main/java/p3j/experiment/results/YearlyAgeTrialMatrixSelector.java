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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;

/**
 * Aggregates a trial x age matrix for a given year.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class YearlyAgeTrialMatrixSelector extends AbstractAggregationSelector {

	/** The list of selectors to be added. */
	private final AbstractAggregationSelector[] addSelectors;

	/** The list of selectors to be subtracted. */
	private final AbstractAggregationSelector[] subSelectors;

	/** The file name for the results. */
	private final String fileName;

	/** The year of interest. */
	private final int year;

	/**
	 * Creates a age x trial matrix selector for a single year.
	 * 
	 * @param addSels
	 *          the list of selected results to be added
	 * @param subSels
	 *          the list of selected results to be subtracted
	 * @param customFileName
	 *          a custom file name
	 * @param yearOfInterest
	 *          the year of interest
	 */
	public YearlyAgeTrialMatrixSelector(AbstractAggregationSelector[] addSels,
	    AbstractAggregationSelector[] subSels, String customFileName,
	    int yearOfInterest) {
		super(null, null, -1);
		addSelectors = getCopy(addSels);
		subSelectors = getCopy(subSels);
		year = yearOfInterest;
		fileName = customFileName + year + ".csv";
	}

	/**
	 * Creates a age x trial matrix selector for a single year.
	 * 
	 * @param addSels
	 *          the list of selected results to be added
	 * @param subSels
	 *          the list of selected results to be subtracted
	 * @param customFileName
	 *          a custom file name
	 * @param yearOfInterest
	 *          the year of interest
	 */
	public YearlyAgeTrialMatrixSelector(
	    List<AbstractAggregationSelector> selectorsForAddition,
	    List<AbstractAggregationSelector> selectorsForSubtraction,
	    String customFileName, int yearOfInterest) {
		this(selectorsForAddition != null ? selectorsForAddition
		    .toArray(new AbstractAggregationSelector[0]) : null,
		    selectorsForSubtraction != null ? selectorsForSubtraction
		        .toArray(new AbstractAggregationSelector[0]) : null,
		    customFileName, yearOfInterest);
	}

	@Override
	public void init(int numOfTrials, int numOfYears, int numOfAgeClasses) {
		aggregation = new double[numOfTrials][numOfAgeClasses];
	}

	@Override
	public void consider(int trialCount, ResultsOfTrial result) {
		List<double[]> additionArrays = new ArrayList<double[]>();
		List<double[]> subtractionArrays = new ArrayList<double[]>();

		for (AbstractAggregationSelector selector : addSelectors) {
			additionArrays.add(selector.select(result).viewColumn(year).toArray());
		}

		for (AbstractAggregationSelector selector : subSelectors) {
			subtractionArrays.add(selector.select(result).viewColumn(year).toArray());
		}

		double[] ageStructure = sumPerElement(additionArrays, subtractionArrays);

		System.arraycopy(ageStructure, 0, aggregation[trialCount], 0,
		    ageStructure.length);
	}

	@Override
	public void finish(File destinationDir, List<Integer> indexOrdering,
	    ResultExport resultExport) throws IOException {
		double[][] aggregatedData = cutOffUnused(aggregation, indexOrdering.size());
		SimSystem.report(Level.INFO, "Writing aggregated data...");
		resultExport.writeResult(destinationDir, aggregatedData, fileName);
		SimSystem.report(Level.INFO, "\t\tdone.");
	}

}
