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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import p3j.misc.math.Matrix2D;
import p3j.pppm.SubPopulation;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.simulation.calculation.deterministic.parameters.BasicParameters;
import p3j.simulation.calculation.deterministic.parameters.MigChildParameters;
import p3j.simulation.calculation.deterministic.parameters.MigParameters;
import p3j.simulation.calculation.deterministic.parameters.NativeParameters;

/**
 * Summary of a single PPPM execution.
 * 
 * Created on February 20, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ExecutionSummary {

  /** The parameter assignments. */
  private final Map<ParameterInstance, ParameterAssignment> paramAssignments;

  /** The sub-populations. */
  private final List<SubPopulation> subPopulations;

  /** Parameters for calculating the jump-off populations. */
  private Map<SubPopulation, NativeParameters> jumpOffParameters = new HashMap<>();

  /** Parameters to calculate (the first generation of) in-flow sub-populations. */
  private Map<SubPopulation, MigParameters> inFlowParameters = new HashMap<>();

  /** Parameters to calculate descendant generations of in-flow sub-populations. */
  private Map<SubPopulation, List<MigChildParameters>> inFlowDescParameters = new HashMap<>();

  /** Stores the results per sub-population. */
  private Map<SubPopulation, List<BasicResults>> results = new HashMap<>();

  /**
   * Instantiates a new execution summary.
   * 
   * @param subPopulations
   *          the existing sub populations
   * @param assignment
   *          the overall assignment used: parameter instance -> assignment
   */
  public ExecutionSummary(List<SubPopulation> subPopulations,
      Map<ParameterInstance, ParameterAssignment> assignment) {
    paramAssignments = Collections.unmodifiableMap(assignment);
    this.subPopulations = subPopulations;
  }

  /**
   * Calculates total end population.
   * 
   * @return total end population
   */
  public Matrix2D getTotalEndPopulation() {
    List<Matrix2D> addList = new ArrayList<Matrix2D>();
    List<Matrix2D> subList = new ArrayList<Matrix2D>();
    for (SubPopulation subPop : subPopulations)
      if (subPop.isAdditive())
        addTotalEndPopulation(subPop, addList);
      else
        addTotalEndPopulation(subPop, subList);
    return Matrix2D.add(addList).sub(subList);
  }

  /**
   * Adds the total end population matrices to the matrix list passed as second
   * argument.
   * 
   * @param subPop
   *          the sub pop
   * @param matrixList
   *          the matrix list
   */
  private void addTotalEndPopulation(SubPopulation subPop,
      List<Matrix2D> matrixList) {
    for (BasicResults result : results.get(subPop)) {
      matrixList.add(result.getEndXm());
      matrixList.add(result.getEndXf());
    }
  }

  public Map<ParameterInstance, ParameterAssignment> getParamAssignments() {
    return paramAssignments;
  }

  /**
   * Gets all results.
   * 
   * @return the results
   */
  public List<BasicResults> getAllResults() {
    List<BasicResults> allResults = new ArrayList<>();
    for (SubPopulation subPop : subPopulations)
      allResults.addAll(results.get(subPop));
    return allResults;
  }

  public void setJumpOffParameters(SubPopulation jumpOffPopulation,
      NativeParameters jumpOffParams) {
    jumpOffParameters.put(jumpOffPopulation, jumpOffParams);
  }

  public void addResults(SubPopulation jumpOffPopulation, int generation,
      BasicResults popResults) {
    List<BasicResults> resultList = results.get(jumpOffPopulation);
    if (resultList == null) {
      resultList = new ArrayList<>();
      results.put(jumpOffPopulation, resultList);
    }
    resultList.set(generation, popResults);
  }

  public void setInFlowParameters(SubPopulation subPopulation,
      MigParameters parameters) {
    inFlowParameters.put(subPopulation, parameters);
  }

  public BasicParameters getParameters(SubPopulation subPopulation,
      int generation) {
    if (!subPopulation.isConsistingOfDescendantGenerations())
      throw new IllegalArgumentException("Sub-population '"
          + subPopulation.getName()
          + "' does not have separate descendant generations.");

    if (generation == 0)
      return inFlowParameters.get(subPopulation);

    return inFlowDescParameters.get(subPopulation).get(generation - 1);
  }

  public void setDescendantParameters(SubPopulation subPopulation,
      int generation, MigChildParameters parameters) {
    if (generation <= 0 || !subPopulation.isConsistingOfDescendantGenerations())
      throw new IllegalArgumentException("There is no generation '"
          + generation + "' for sub-population '" + subPopulation.getName()
          + "'.");
    List<MigChildParameters> paramList = inFlowDescParameters
        .get(subPopulation);
    if (paramList == null) {
      paramList = new ArrayList<>();
      inFlowDescParameters.put(subPopulation, paramList);
    }
    paramList.set(generation - 1, parameters);
  }

  public BasicResults getResults(SubPopulation subPopulation, int generation) {
    return results.get(subPopulation).get(generation);
  }
}
