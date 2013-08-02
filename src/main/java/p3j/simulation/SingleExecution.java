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
package p3j.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.misc.Pair;

import p3j.database.IP3MDatabase;
import p3j.experiment.results.ExecutionSummary;
import p3j.experiment.results.ResultsOfTrial;
import p3j.misc.errors.GeneratorError;
import p3j.misc.gui.GUI;
import p3j.misc.math.Matrix2D;
import p3j.pppm.IProjectionModel;
import p3j.pppm.SubPopulation;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.parameters.ParameterType;
import p3j.simulation.assignments.plugintype.IParamAssignmentGenerator;
import p3j.simulation.calculation.deterministic.InFlowDescendantPopulation;
import p3j.simulation.calculation.deterministic.InFlowPopulation;
import p3j.simulation.calculation.deterministic.JumpOffPopulation;
import p3j.simulation.calculation.deterministic.parameters.BasicParameters;
import p3j.simulation.calculation.deterministic.parameters.InFlowDescendantParameters;
import p3j.simulation.calculation.deterministic.parameters.InFlowParameters;
import p3j.simulation.calculation.deterministic.parameters.JumpOffParameters;

/**
 * This class holds all data structures and methods that are required to use an
 * {@link IParamAssignmentGenerator} for a single execution of the PPPM.
 * 
 * Created: August 17, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class SingleExecution {

  /**
   * Auxiliary mapping of generation-independent parameters. The key is the name
   * of the associated parameters.
   */
  private Map<String, ParameterAssignment> genIndepParameters;

  /**
   * Auxiliary mapping of generation-dependent parameters. The i-th element is a
   * map containing all parameter assignments for the i-th generation. The key
   * is the name of the associated parameters.
   */
  private List<Map<String, ParameterAssignment>> genDepParameters;

  /** The projection setup, contains all input data. */
  private final IProjectionModel projection;

  /** The list containing the populations that define a jump-off population. */
  private final List<SubPopulation> jumpOffPopulations;

  /** The list containing the populations that define a yearly in-flow. */
  private final List<SubPopulation> inFlowPopulations;

  /** The database to store results in. */
  private final IP3MDatabase database;

  /** The random number generator to be used. */
  private final IRandom random;

  /**
   * Default constructor.
   * 
   * @param proj
   *          the projection containing all input data
   * @param dataBase
   *          the database to store results in
   */
  public SingleExecution(IProjectionModel proj, IP3MDatabase dataBase) {
    projection = proj;
    jumpOffPopulations = projection.getSubPopulationModel()
        .getJumpOffPopulations();
    inFlowPopulations = projection.getSubPopulationModel()
        .getInFlowPopulations();
    database = dataBase;
    random = SimSystem.getRNGGenerator().getNextRNG();
  }

  /**
   * Sets up and executes a single variable assignment for the PPPM.
   * 
   * @param generator
   *          the assignment generator to be used
   * @return error log from the {@link IParamAssignmentGenerator}
   */
  public Pair<ExecutionSummary, List<GeneratorError>> execute(
      IParamAssignmentGenerator generator) {

    Pair<ExecutionSummary, List<GeneratorError>> result = null;

    try {
      Pair<Map<ParameterInstance, ParameterAssignment>, List<GeneratorError>> assignment = chooseAssignment(
          generator, random);

      // Create parameter classes
      int years = projection.getYears();
      ExecutionSummary executionSummary = new ExecutionSummary(projection
          .getSubPopulationModel().getSubPopulations(),
          assignment.getFirstValue());

      for (SubPopulation jumpOffPopulation : jumpOffPopulations) {
        JumpOffParameters jumpOffParameters = setupBasicJumpOffParameters(
            years, jumpOffPopulation);
        JumpOffPopulation nativePopulation = new JumpOffPopulation();
        executionSummary.setJumpOffParameters(jumpOffPopulation,
            jumpOffParameters);
        executionSummary.addResults(jumpOffPopulation, 0, nativePopulation
            .calculatePopulation(jumpOffPopulation.getName(), 0,
                jumpOffParameters));

        if (jumpOffPopulation.isConsistingOfDescendantGenerations())
          throw new UnsupportedOperationException(); // TODO
      }

      for (SubPopulation inFlowPopulation : inFlowPopulations) {
        calculateFirstInFlowPopulation(executionSummary, inFlowPopulation,
            years);
        if (inFlowPopulation.isConsistingOfDescendantGenerations())
          for (int i = 1; i < projection.getGenerations(); i++) {
            calculateInFlowChildPopulation(executionSummary, inFlowPopulation,
                i, years);
          }
      }
      storeResultsToDB(executionSummary);
      result = new Pair<ExecutionSummary, List<GeneratorError>>(
          executionSummary, assignment.getSecondValue());
    } catch (Throwable t) {
      GUI.printErrorMessage("Execution failed", t);
    }
    return result;
  }

  /**
   * Stores results to database.
   * 
   * @param executionSummary
   *          the execution summary
   */
  private void storeResultsToDB(ExecutionSummary executionSummary) {
    ResultsOfTrial results = new ResultsOfTrial(projection, executionSummary);
    database.saveTrialResults(results);
  }

  /**
   * Lets the {@link IParamAssignmentGenerator} choose a valid assignment and
   * sets up all auxiliary data structures according to this.
   * 
   * @param generator
   *          the component for generating the assignment to be calculated
   * @param rng
   *          the random number generator
   * @return error log from the {@link IParamAssignmentGenerator}
   */
  protected Pair<Map<ParameterInstance, ParameterAssignment>, List<GeneratorError>> chooseAssignment(
      IParamAssignmentGenerator generator, IRandom rng) {

    Pair<Map<ParameterInstance, ParameterAssignment>, List<GeneratorError>> generatorResults = generator
        .chooseParamAssignments(rng);

    // Generate parameter assignment
    Map<ParameterInstance, ParameterAssignment> assignments = generatorResults
        .getFirstValue();

    // Create data structures to hold generation-dependent and
    // generation-independent parameters
    genIndepParameters = new HashMap<String, ParameterAssignment>();
    genDepParameters = new ArrayList<Map<String, ParameterAssignment>>();
    for (int i = 0; i < projection.getGenerations(); i++) {
      genDepParameters.add(new HashMap<String, ParameterAssignment>());
    }

    for (Entry<ParameterInstance, ParameterAssignment> assignmentEntry : assignments
        .entrySet()) {
      if (assignmentEntry.getKey().getParameter().isGenerationDependent()) {
        genDepParameters.get(assignmentEntry.getKey().getGeneration()).put(
            assignmentEntry.getKey().getParameter().getName(),
            assignmentEntry.getValue());
      } else {
        genIndepParameters.put(assignmentEntry.getKey().getParameter()
            .getName(), assignmentEntry.getValue());
      }
    }
    return generatorResults;
  }

  /**
   * Retrieve the value for a generation-independent parameter.
   * 
   * @param parameterName
   *          the parameter name
   * @return value for the given parameter
   */
  protected Matrix2D getGenIndepParameter(String parameterName) {
    ParameterAssignment parameterAssignment = genIndepParameters
        .get(parameterName);
    return DeviationCalculator.calculateAssignmentDeviation(
        parameterAssignment, random);
  }

  /**
   * Retrieve the value for a generation-dependent parameter.
   * 
   * @param parameterName
   *          the parameter name
   * @param generation
   *          the generation of the parameter
   * @return value for the given parameter
   */
  protected Matrix2D getGenDepParameter(String parameterName, int generation) {
    ParameterAssignment parameterAssignment = genDepParameters.get(generation)
        .get(parameterName);
    return DeviationCalculator.calculateAssignmentDeviation(
        parameterAssignment, random);
  }

  /**
   * Calculates the first generation of emigrants.
   * 
   * @param executionSummary
   *          execution summary to be filled with the results
   * @param subPopulation
   *          the sub-population
   * @param years
   *          the number of years for which shall be predicted
   */
  void calculateFirstInFlowPopulation(ExecutionSummary executionSummary,
      SubPopulation subPopulation, int years) {
    InFlowParameters parameters = new InFlowParameters(years,
        projection.getMaximumAge());
    parameters.setMigrantsXm(getGenIndepParameter(ParameterType.MIGRATION
        .getMaleLabelFor(subPopulation)));
    parameters.setMigrantsXf(getGenIndepParameter(ParameterType.MIGRATION
        .getFemaleLabelFor(subPopulation)));
    setupBasicInFlowPopulationParameters(parameters, subPopulation, 0);
    InFlowPopulation migPopulation = new InFlowPopulation();
    executionSummary.setInFlowParameters(subPopulation, parameters);
    executionSummary.addResults(subPopulation, 0, migPopulation
        .calculatePopulation(subPopulation.getName(), 0, parameters));
  }

  /**
   * Calculates a child population of the emigrants sub-population.
   * 
   * @param executionSummary
   *          execution summary to be filled with the results
   * @param subPopulation
   *          the sub-population
   * @param generation
   *          the generation of the child population
   * @param years
   *          the number of years for which shall be predicted
   */
  void calculateInFlowChildPopulation(ExecutionSummary executionSummary,
      SubPopulation subPopulation, int generation, int years) {
    InFlowDescendantParameters parameters = new InFlowDescendantParameters(
        years, projection.getMaximumAge());
    parameters.setOldFertX(executionSummary.getParameters(subPopulation,
        generation - 1).getFertX());
    parameters.setOldMeanXf(executionSummary.getResults(subPopulation,
        generation - 1).getMeanXf());
    setupBasicInFlowPopulationParameters(parameters, subPopulation, generation);
    InFlowDescendantPopulation migChildPopulation = new InFlowDescendantPopulation();
    executionSummary.setDescendantParameters(subPopulation, generation,
        parameters);
    executionSummary.addResults(subPopulation, generation, migChildPopulation
        .calculatePopulation(subPopulation.getName(), generation, parameters));
  }

  /**
   * Creates basic native parameters.
   * 
   * @param years
   *          number of years for which shall be predicted
   * @param jumpOffPopulation
   * @return parameters to calculate native population prediction
   */
  JumpOffParameters setupBasicJumpOffParameters(int years,
      SubPopulation jumpOffPopulation) {

    JumpOffParameters nativeParameters = new JumpOffParameters(years,
        projection.getMaximumAge());

    nativeParameters.setPEndSYm(getGenIndepParameter(ParameterType.JUMP_OFF
        .getMaleLabelFor(jumpOffPopulation)));
    nativeParameters.setPEndSYf(getGenIndepParameter(ParameterType.JUMP_OFF
        .getFemaleLabelFor(jumpOffPopulation)));

    nativeParameters.setMortXm(getGenIndepParameter(ParameterType.MORTALITY
        .getMaleLabelFor(jumpOffPopulation)));
    nativeParameters.setMortXf(getGenIndepParameter(ParameterType.MORTALITY
        .getFemaleLabelFor(jumpOffPopulation)));

    nativeParameters
        .setDeathProbInfant1halfMale(getGenIndepParameter(ParameterType.PROP_INF_DEATHS_FIRST_6M
            .getMaleLabelFor(jumpOffPopulation)));
    nativeParameters
        .setDeathProbInfant1halfFemale(getGenIndepParameter(ParameterType.PROP_INF_DEATHS_FIRST_6M
            .getFemaleLabelFor(jumpOffPopulation)));

    nativeParameters
        .setSurviveProbO100m(getGenIndepParameter(ParameterType.SURV_PROB_OPEN_END
            .getMaleLabelFor(jumpOffPopulation)));
    nativeParameters
        .setSurviveProbO100f(getGenIndepParameter(ParameterType.SURV_PROB_OPEN_END
            .getFemaleLabelFor(jumpOffPopulation)));

    nativeParameters
        .setMaleRateLiveBirth(getGenIndepParameter(ParameterType.PROP_MALE_LIVE_BIRTHS
            .getLabelFor(jumpOffPopulation)));

    nativeParameters.setFertX(getGenIndepParameter(ParameterType.FERTILITY
        .getLabelFor(jumpOffPopulation)));

    return nativeParameters;
  }

  /**
   * Sets up immigrant parameters.
   * 
   * @param parameters
   *          basic parameters (to be filled)
   * @param subPopulation
   *          the sub-population
   * @param generation
   *          the generation for which the parameters should be set
   */
  void setupBasicInFlowPopulationParameters(BasicParameters parameters,
      SubPopulation subPopulation, int generation) {

    parameters.setDeathProbInfant1halfMale(getGenDepParameter(
        ParameterType.PROP_INF_DEATHS_FIRST_6M.getMaleLabelFor(subPopulation),
        generation));
    parameters
        .setDeathProbInfant1halfFemale(getGenDepParameter(
            ParameterType.PROP_INF_DEATHS_FIRST_6M
                .getFemaleLabelFor(subPopulation), generation));

    parameters.setMortXm(getGenDepParameter(
        ParameterType.MORTALITY.getMaleLabelFor(subPopulation), generation));
    parameters.setMortXf(getGenDepParameter(
        ParameterType.MORTALITY.getFemaleLabelFor(subPopulation), generation));

    parameters.setSurviveProbO100m(getGenDepParameter(
        ParameterType.SURV_PROB_OPEN_END.getMaleLabelFor(subPopulation),
        generation));
    parameters.setSurviveProbO100f(getGenDepParameter(
        ParameterType.SURV_PROB_OPEN_END.getFemaleLabelFor(subPopulation),
        generation));

    parameters.setFertX(getGenDepParameter(
        ParameterType.FERTILITY.getLabelFor(subPopulation), generation));

    parameters.setMaleRateLiveBirth(getGenDepParameter(
        ParameterType.PROP_MALE_LIVE_BIRTHS.getLabelFor(subPopulation),
        generation));
  }
}
