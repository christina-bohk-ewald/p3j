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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import p3j.pppm.IProjectionModel;
import p3j.pppm.ProjectionModel;
import p3j.pppm.SubPopulation;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.SetType;

/**
 * This class is intended to represent the results of a single trial as stored
 * in the database.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class ResultsOfTrial implements Serializable {

  /** Serialization ID. */
  private static final long serialVersionUID = 2484910276641194217L;

  /** The ID of this trial. */
  private int id = -1;

  /** The projection to which this trial belongs. */
  private ProjectionModel projection;

  /**
   * The assignment used to generate the trial: one parameter assignment per
   * instance.
   */
  private Map<ParameterInstance, ParameterAssignment> assignment = new HashMap<>();

  /** Results of the calculations per sub-population & generation. */
  private List<BasicResults> subPopulationResults = new ArrayList<>();

  /** The overall assignment probability. */
  private double assignmentProbability;

  /**
   * The probability that the given combination of sets was chosen. Dividing
   * {@link ResultsOfTrial#assignmentProbability} by this value yields the
   * probability of choosing the given assignments GIVEN the selected set
   * combination.
   */
  private double setCombinationProbability;

  /**
   * Empty constructor (for Bean compatibility).
   */
  public ResultsOfTrial() {
  }

  /**
   * Instantiates new results for a trial.
   * 
   * @param projectionModel
   *          the current projection model
   * @param execSummary
   *          the execution summary
   */
  public ResultsOfTrial(IProjectionModel projectionModel,
      ExecutionSummary execSummary) {
    subPopulationResults.addAll(execSummary.getAllResults());
    projection = (ProjectionModel) projectionModel;
    assignment.putAll(execSummary.getParamAssignments());
    calculateAssignmentProbability();
  }

  /**
   * Calculates the assignment's overall probability.
   */
  private void calculateAssignmentProbability() {

    double setCombProb = 1.0;
    double assignmentCombProb = 1.0;

    Set<Integer> checkedSetTypeIDs = new HashSet<Integer>();
    for (Entry<ParameterInstance, ParameterAssignment> entry : assignment
        .entrySet()) {

      ParameterInstance paramInstance = entry.getKey();
      ParameterAssignment paramAssignment = entry.getValue();
      assignmentCombProb *= paramAssignment.getProbability();

      SetType currentSetType = projection.getInstanceSetTypes().get(
          paramInstance);
      if (!checkedSetTypeIDs.contains(currentSetType.getID())) {
        setCombProb *= getSetProbability(paramInstance, paramAssignment,
            currentSetType);
        checkedSetTypeIDs.add(currentSetType.getID());
      }
    }

    setCombinationProbability = setCombProb;
    assignmentProbability = setCombProb * assignmentCombProb;
  }

  /**
   * Gets the probability of the set.
   * 
   * @param paramInstance
   *          a parameter instance belonging to the current Settype
   * @param paramAssignment
   *          a parameter assignment (belonging to the set in question)
   * @param currentSetType
   *          the current Settype
   * @return the occurrence probability of the set
   */
  private double getSetProbability(ParameterInstance paramInstance,
      ParameterAssignment paramAssignment, SetType currentSetType) {
    List<p3j.pppm.sets.Set> currentSets = currentSetType.getSets();
    for (p3j.pppm.sets.Set currentSet : currentSets) {
      for (ParameterAssignment currentAssignment : currentSet
          .getParameterAssignments(paramInstance).getAssignments()) {
        if (currentAssignment.getID() == paramAssignment.getID()) {
          return currentSet.getProbability();
        }
      }
    }
    return -1.0;
  }

  public int getID() {
    return id;
  }

  public void setID(int id) {
    this.id = id;
  }

  public ProjectionModel getProjection() {
    return projection;
  }

  public void setProjection(ProjectionModel projection) {
    this.projection = projection;
  }

  public Map<ParameterInstance, ParameterAssignment> getAssignment() {
    return assignment;
  }

  public void setAssignment(
      Map<ParameterInstance, ParameterAssignment> assignment) {
    this.assignment = assignment;
  }

  public double getAssignmentProbability() {
    return assignmentProbability;
  }

  public void setAssignmentProbability(double assignmentProbability) {
    this.assignmentProbability = assignmentProbability;
  }

  public double getSetCombinationProbability() {
    return setCombinationProbability;
  }

  public void setSetCombinationProbability(double setCombinationProbability) {
    this.setCombinationProbability = setCombinationProbability;
  }

  public List<BasicResults> getSubPopulationResults() {
    return subPopulationResults;
  }

  public void setSubPopulationResults(List<BasicResults> subPopulationResults) {
    this.subPopulationResults = subPopulationResults;
  }

  /**
   * Retrieves all results for a specific sub-population.
   * 
   * @param subPop
   *          the sub-population
   * @return the list of results referring to this sub-population
   */
  public List<BasicResults> retrieveFor(SubPopulation subPop) {
    List<BasicResults> subPopResults = new ArrayList<>();
    for (BasicResults subPopResult : subPopulationResults)
      if (subPopResult.getSubPopName().equals(subPop.getName()))
        subPopResults.add(subPopResult);
    return subPopResults;
  }

}
