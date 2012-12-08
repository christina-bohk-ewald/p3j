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
 * Population calculation for in-flow sub-populations.
 * 
 * Created on July 22, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class InFlowParameters extends BasicParameters {

  /** Year-by year age-specific matrix of female migrants. */
  private Matrix2D migrantsXf;

  /** Year-by year age-specific matrix of male migrants. */
  private Matrix2D migrantsXm;

  /**
   * Default constructor.
   * 
   * @param numOfYears
   *          number of years to be predicted
   * @param maximumAge
   *          the maximum age to be considered
   */
  public InFlowParameters(int numOfYears, int maximumAge) {
    super(numOfYears, maximumAge);
  }

  /**
   * Constructor for bean compliance. Do NOT use manually.
   */
  public InFlowParameters() {
    super(PPPModelFactory.DEFAULT_YEARS, Constants.DEFAULT_MAXIMUM_AGE);
  }

  public Matrix2D getMigrantsXf() {
    return migrantsXf;
  }

  public void setMigrantsXf(Matrix2D migrantsXf) {
    this.migrantsXf = migrantsXf;
  }

  public Matrix2D getMigrantsXm() {
    return migrantsXm;
  }

  public void setMigrantsXm(Matrix2D migrantsXm) {
    this.migrantsXm = migrantsXm;
  }

}
