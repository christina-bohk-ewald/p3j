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
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;

/**
 * Selectors of this type select data from a particular year across all trials.
 * The leave the age groups untouched; the aggregated data can, e.g., be used to
 * create population pyramids.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ChooseAgesForSingleYearSelector extends
    AbstractAggregationSelector {

	/**
	 * The year of interest. Represents column index, i.e. 0 selects the first
	 * year and so on.
	 */
	private final int year;

	/** The number of age classes. */
	private int numberOfAgeClasses = -1;

	/**
	 * Instantiates a new choose ages for single year selector.
	 * 
	 * @param resultsSelector
	 *          the results selector
	 * @param trialSelector
	 *          the trial selector
	 * @param generationForSelection
	 *          the generation for selection
	 * @param yearForSelection
	 *          the year for selection
	 */
	public ChooseAgesForSingleYearSelector(
	    IOutputVariableSelector resultsSelector,
	    ISubPopulationSelector trialSelector, int generationForSelection,
	    int yearForSelection) {
		super(resultsSelector, trialSelector, generationForSelection);
		year = yearForSelection;
	}

	@Override
	public void init(int numOfTrials, int numOfYears, int numOfAgeClasses) {
		aggregation = new double[numOfTrials][numOfAgeClasses];
		setNumberOfAgeClasses(numOfAgeClasses);
	}

	@Override
	public void consider(int trialCount, ResultsOfTrial result) {
		double[][] currentData = select(result).toArray();
		for (int j = 0; j < getNumberOfAgeClasses(); j++) {
			aggregation[trialCount][j] = currentData[j][getYear()];
		}
	}

	@Override
	public void finish(File destinationDir, List<Integer> indexOrdering,
	    ResultExport resultExport) throws IOException {
		double[][] aggregatedData = cutOffUnused(aggregation, indexOrdering.size());
		double[][] quantiles = resultExport.calcQuantiles(aggregatedData);
		SimSystem.report(Level.INFO, "Writing aggregated data...");
		resultExport.writeResult(destinationDir, quantiles, getFileName()
		    + "_quantiles.csv");
		SimSystem.report(Level.INFO, "\t\tdone.");
	}

	@Override
	protected String getFileName() {
		return getPrefix() + super.getFileName();
	}

	protected final String getPrefix() {
		return "year_" + getYear() + "_";
	}

	public int getNumberOfAgeClasses() {
		return numberOfAgeClasses;
	}

	public void setNumberOfAgeClasses(int numberOfAgeClasses) {
		this.numberOfAgeClasses = numberOfAgeClasses;
	}

	public int getYear() {
		return year;
	}

}
