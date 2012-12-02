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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Specifies which sub-populations the population to be projected consists of.
 * 
 * Each sub-population may either be an additive element of the overall
 * population, or a subtractive.
 * 
 * The default configuration corresponds to the (fixed) setup in previous
 * versions.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class SubPopulationModel implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3963551700851569506L;

  /** The list of sub-populations. */
  private List<SubPopulation> subPopulations = new ArrayList<>();

  /** Creates default configuration of sub-populations. */
  public SubPopulationModel() {
  }

  /**
   * Instantiates a new sub-population model based on existing sub-populations.
   * 
   * @param subPopulations
   *          the sub-populations that determine the model
   */
  public SubPopulationModel(List<SubPopulation> subPopulations) {
    for (SubPopulation subPop : subPopulations)
      this.subPopulations.add(subPop.newSubPopulation());
  }

  /**
   * Gets the sub-populations.
   * 
   * @return the sub populations
   */
  public List<SubPopulation> getSubPopulations() {
    return subPopulations;
  }

  /**
   * Sets the sub-populations.
   * 
   * @param subPopulations
   *          the new sub populations
   */
  public void setSubPopulations(List<SubPopulation> subPopulations) {
    this.subPopulations = subPopulations;
  }

  /**
   * Gets the jump off populations.
   * 
   * @return the jump off populations
   */
  public List<SubPopulation> getJumpOffPopulations() {
    List<SubPopulation> jumpOffPopulations = new ArrayList<>();
    for (SubPopulation subPopulation : subPopulations)
      if (subPopulation.isJumpOffPopulation())
        jumpOffPopulations.add(subPopulation);
    return jumpOffPopulations;
  }

}
