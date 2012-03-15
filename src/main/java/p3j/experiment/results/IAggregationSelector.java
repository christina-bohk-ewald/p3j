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

/**
 * A simple interface to define functions that select data for aggregation.
 * 
 * See {@link AbstractAggregationSelector} for auxiliary methods.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public interface IAggregationSelector {

  /**
   * Initialize the number of age classes etc.
   * 
   * @param numOfTrials
   *          the number of trials
   * @param numOfYears
   *          the number of years
   * @param numOfAgeClasses
   *          the number of age classes
   */
  void init(int numOfTrials, int numOfYears, int numOfAgeClasses);

  /**
   * Called to consider a single result.
   * 
   * @param trialCount
   *          the trial count (i.e. index)
   * @param result
   *          the result
   */
  void consider(int trialCount, ResultsOfTrial result);

  /**
   * Finishing the aggregation. Called after all relevant results have been
   * considered, use this to write results.
   * 
   * @param destinationDir
   *          the destination dir
   * @param indexOrdering
   *          the index ordering
   * @param resultExport
   *          the result export
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  void finish(File destinationDir, List<Integer> indexOrdering,
      ResultExport resultExport) throws IOException;

}
