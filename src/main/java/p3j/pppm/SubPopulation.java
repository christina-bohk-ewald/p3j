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

/**
 * Represents a sub-population.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class SubPopulation implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1501278366716906252L;

  /** The name of the sub-population. */
  private String name = "";

  /**
   * Flag to determine whether this sub-population adds to the overall
   * population.
   */
  private boolean additive = true;

  /**
   * Flag to determine whether this sub-population consists of descendant
   * generations as well.
   */
  private boolean consistingOfDescendantGenerations = false;

  /**
   * Flag to determine whether this sub-population is part of the jump-off
   * population.
   */
  private boolean jumpOffPopulation = false;

  /** Default constructor. */
  public SubPopulation() {
  }

  /**
   * Full constructor.
   * 
   * @param name
   *          the name of the sub-population
   * @param jumpOffPop
   *          flag to indicate that this population is part of the jump-off
   *          population
   * @param additive
   *          the flag to determine whether this sub-population adds to the
   *          overall population
   * @param consistOfDescGenerations
   *          the flag to determine whether this sub-population consists of
   *          descendant generations
   */
  public SubPopulation(String name, boolean jumpOffPop, boolean additive,
      boolean consistOfDescGenerations) {
    this.name = name;
    this.jumpOffPopulation = jumpOffPop;
    this.additive = additive;
    this.consistingOfDescendantGenerations = consistOfDescGenerations;
  }

  /**
   * Creates new sub-population with same properties.
   * 
   * @return the new sub-population
   */
  public SubPopulation newSubPopulation() {
    return new SubPopulation(name, jumpOffPopulation, additive,
        consistingOfDescendantGenerations);
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Checks if sub-population is additive.
   * 
   * @return true, if is additive
   */
  public boolean isAdditive() {
    return additive;
  }

  /**
   * Sets the 'additive'-flag.
   * 
   * @param additive
   *          the new additive
   */
  public void setAdditive(boolean additive) {
    this.additive = additive;
  }

  /**
   * Checks if sub-population is consisting of descendant generations.
   * 
   * @return true, if is consisting of descendant generations
   */
  public boolean isConsistingOfDescendantGenerations() {
    return consistingOfDescendantGenerations;
  }

  /**
   * Sets the 'consisting of descendant generations'-flag.
   * 
   * @param consistingOfDescendantGenerations
   *          the new consisting of descendant generations
   */
  public void setConsistingOfDescendantGenerations(
      boolean consistingOfDescendantGenerations) {
    this.consistingOfDescendantGenerations = consistingOfDescendantGenerations;
  }

  @Override
  public String toString() {
    return getName()
        + " ("
        + (additive ? "+" : "-")
        + (jumpOffPopulation ? ", jump-off" : "")
        + (consistingOfDescendantGenerations ? ", has descendant generations"
            : "") + ")";
  }

  public boolean isJumpOffPopulation() {
    return jumpOffPopulation;
  }

  public void setJumpOffPopulation(boolean jumpOffPopulation) {
    this.jumpOffPopulation = jumpOffPopulation;
  }

}
