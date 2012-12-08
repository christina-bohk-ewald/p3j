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

  /** Parameters to calculate descendant generations. */
  private Map<SubPopulation, MigChildParameters> descendantGenParameters = new HashMap<>();

  /** Stores the results per sub-population. */
  private Map<SubPopulation, Map<Integer, BasicResults>> results = new HashMap<>();

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
    for (BasicResults result : results.get(subPop).values()) {
      matrixList.add(result.getEndXm());
      matrixList.add(result.getEndXf());
    }
  }

  public Map<ParameterInstance, ParameterAssignment> getParamAssignments() {
    return paramAssignments;
  }

  public List<BasicResults> getSubPopulationResults() {
    // TODO Auto-generated method stub
    return null;
  }

  public void addParameters(SubPopulation jumpOffPopulation,
      NativeParameters jumpOffParameters) {
    // TODO Auto-generated method stub

  }

  public void addResults(SubPopulation jumpOffPopulation, int i,
      BasicResults calculatePopulation) {
    // TODO Auto-generated method stub

  }

  public void setFirstParameters(SubPopulation subPopulation,
      MigParameters parameters) {
    // TODO Auto-generated method stub

  }

  public BasicParameters getFirstParameters(SubPopulation subPopulation) {
    // TODO Auto-generated method stub
    return null;
  }

  public void setParameters(SubPopulation subPopulation, int i,
      MigParameters parameters) {
    // TODO Auto-generated method stub

  }

  public BasicParameters getParameters(SubPopulation subPopulation, int i) {
    // TODO Auto-generated method stub
    return null;
  }

  public void setDescendantParameters(SubPopulation subPopulation,
      int generation, MigChildParameters parameters) {
    // TODO Auto-generated method stub

  }

  public BasicResults getResults(SubPopulation subPopulation, int i) {
    // TODO Auto-generated method stub
    return null;
  }
}
