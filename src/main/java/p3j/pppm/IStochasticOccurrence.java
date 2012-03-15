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
package p3j.pppm;

/**
 * This is an interface for all objects that might occur by some probability.
 * E.g., sets and parameter assignments.
 * 
 * Created on January 21, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public interface IStochasticOccurrence {

  /**
   * Get the probability that this entity will be chosen.
   * 
   * @return probability to be chosen, in [0,1]
   */
  double getProbability();

  /**
   * Set probability for being chosen.
   * 
   * @param probability
   *          the probability (in [0,1])
   */
  void setProbability(double probability);

}
