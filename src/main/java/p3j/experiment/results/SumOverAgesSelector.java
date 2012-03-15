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

import james.SimSystem;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import p3j.misc.math.Matrix2D;

/**
 * Selector to sum over ages, i.e. the result is a single number per year,
 * giving the overall amount of people in the selected population(s).
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class SumOverAgesSelector extends AbstractAggregationSelector {

	/**
	 * Instantiates a new sum over ages selector.
	 * 
	 * @param resultsSelector
	 *          the results selector
	 * @param trialSelector
	 *          the trial selector
	 * @param generationForSelection
	 *          the generation for selection
	 */
	public SumOverAgesSelector(IOutputVariableSelector resultsSelector,
	    ISubPopulationSelector trialSelector, int generationForSelection) {
		super(resultsSelector, trialSelector, generationForSelection);
	}

	@Override
	public void init(int numOfTrials, int numOfYears, int numOfAgeClasses) {
		aggregation = new double[numOfTrials][numOfYears];
	}

	@Override
	public void consider(int trialCount, ResultsOfTrial result) {
		double[] rowSum = Matrix2D.sumRows(select(result)).toArray()[0];
		System.arraycopy(rowSum, 0, aggregation[trialCount], 0, rowSum.length);
	}

	@Override
	public void finish(File destinationDir, List<Integer> indexOrdering,
	    ResultExport resultExport) throws IOException {

		double[][] aggregatedData = cutOffUnused(aggregation, indexOrdering.size());

		double[][] filteredResults = resultExport.filter(aggregatedData);
		double[][] quantiles = resultExport.calcQuantiles(aggregatedData);

		SimSystem.report(Level.INFO, "Writing aggregated data...");
		resultExport.writeResult(destinationDir,
		    reorderResults(indexOrdering, filteredResults), getFileName()
		        + "_sum.csv");
		resultExport.writeResult(destinationDir, quantiles, getFileName()
		    + "_quantiles.csv");
		SimSystem.report(Level.INFO, "\t\tdone.");
	}

}
