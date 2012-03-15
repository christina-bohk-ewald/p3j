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
package p3j.experiment.results.filters;

import p3j.experiment.results.ResultsOfTrial;

/**
 * Simple interface to filter out results. This allows conditional projection
 * analysis.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public interface IResultFilter {

  /**
   * Checks whether the given result shall be considered or not.
   * 
   * @param results
   *          the results in question
   * 
   * @return true if results should be considered as well
   */
  boolean considerResult(ResultsOfTrial results);

}
