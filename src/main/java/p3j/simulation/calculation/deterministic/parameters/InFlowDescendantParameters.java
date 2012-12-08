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
package p3j.simulation.calculation.deterministic.parameters;

import p3j.misc.math.Matrix2D;
import p3j.pppm.PPPModelFactory;
import p3j.simulation.calculation.deterministic.Constants;

/**
 * Parameters for descendant generations of in-flow sub-populations.
 * 
 * Created on July 22, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class InFlowDescendantParameters extends BasicParameters {

  /** Mean female age-specific year-by-year population. */
  private Matrix2D oldMeanXf;

  /** Fertility of old generation (age-specific, year-by-year). */
  private Matrix2D oldFertX;

  /**
   * Default constructor.
   * 
   * @param numOfYears
   *          number of years to be predicted
   * @param maximumAge
   *          the maximum age to be considered
   */
  public InFlowDescendantParameters(int numOfYears, int maximumAge) {
    super(numOfYears, maximumAge);
  }

  /**
   * Constructor for bean compliance. Do NOT use manually.
   */
  public InFlowDescendantParameters() {
    super(PPPModelFactory.DEFAULT_YEARS, Constants.DEFAULT_MAXIMUM_AGE);
  }

  public Matrix2D getOldMeanXf() {
    return oldMeanXf;
  }

  public void setOldMeanXf(Matrix2D oldMeanXf) {
    this.oldMeanXf = oldMeanXf;
  }

  public Matrix2D getOldFertX() {
    return oldFertX;
  }

  public void setOldFertX(Matrix2D oldFertX) {
    this.oldFertX = oldFertX;
  }

}
