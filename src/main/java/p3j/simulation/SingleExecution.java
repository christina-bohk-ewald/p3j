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

import james.SimSystem;
import james.core.math.random.generators.IRandom;
import james.core.util.misc.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import p3j.database.IP3MDatabase;
import p3j.experiment.results.ExecutionSummary;
import p3j.experiment.results.ResultsOfTrial;
import p3j.misc.errors.GeneratorError;
import p3j.misc.math.Matrix2D;
import p3j.pppm.IProjectionModel;
import p3j.pppm.SubPopulation;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.parameters.ParameterType;
import p3j.simulation.assignments.plugintype.IParamAssignmentGenerator;
import p3j.simulation.calculation.deterministic.MigChildPopulation;
import p3j.simulation.calculation.deterministic.MigPopulation;
import p3j.simulation.calculation.deterministic.NativePopulation;
import p3j.simulation.calculation.deterministic.parameters.BasicParameters;
import p3j.simulation.calculation.deterministic.parameters.MigChildParameters;
import p3j.simulation.calculation.deterministic.parameters.MigParameters;
import p3j.simulation.calculation.deterministic.parameters.NativeParameters;

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

    Pair<Map<ParameterInstance, ParameterAssignment>, List<GeneratorError>> assignment = chooseAssignment(
        generator, random);

    // Create parameter classes
    int years = projection.getYears();
    ExecutionSummary executionSummary = new ExecutionSummary(
        assignment.getFirstValue());

    for (SubPopulation jumpOffPopulation : jumpOffPopulations) {
      NativeParameters jumpOffParameters = setupBasicJumpOffParameters(years,
          jumpOffPopulation);
      NativePopulation nativePopulation = new NativePopulation();
      executionSummary.setNativeParameters(jumpOffParameters);
      executionSummary.setNativeResults(nativePopulation
          .calculatePopulation(jumpOffParameters));
    }

    SubPopulation subPop = null; // FIXME

    for (int i = 0; i < projection.getGenerations(); i++) {
      // Calculate emigration population
      if (i == 0) {
        calculateFirstEmigrantPopulation(executionSummary, subPop, years);
      } else {
        calculateEmigrantChildPopulation(executionSummary, subPop, i, years);
      }
      // Calculate immigration population
      if (i == 0) {
        calculateFirstImmigrantPopulation(executionSummary, subPop, years);
      } else {
        calculateImmigrantChildPopulation(executionSummary, subPop, i, years);
      }
    }

    storeResultsToDB(executionSummary);

    return new Pair<ExecutionSummary, List<GeneratorError>>(executionSummary,
        assignment.getSecondValue());
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
   * @param experimentSummary
   *          experiment summary to be filled with the results
   * @param subPopulation
   *          the sub-population
   * @param years
   *          the number of years for which shall be predicted
   */
  void calculateFirstEmigrantPopulation(ExecutionSummary experimentSummary,
      SubPopulation subPopulation, int years) {
    MigParameters parameters = new MigParameters(years,
        projection.getMaximumAge());
    parameters.setMigrantsXm(getGenIndepParameter(ParameterType.MIGRATION
        .getMaleLabelFor(subPopulation)));
    parameters.setMigrantsXf(getGenIndepParameter(ParameterType.MIGRATION
        .getFemaleLabelFor(subPopulation)));
    setupBasicMigrantParameters(parameters, subPopulation, 0);
    MigPopulation migPopulation = new MigPopulation();
    experimentSummary.setFirstEmigrantParameters(parameters);
    experimentSummary.addEmigrantResult(migPopulation
        .calculatePopulation(parameters));
  }

  /**
   * Calculates the first generation of immigrants.
   * 
   * @param experimentSummary
   *          experiment summary to be filled with the results
   * @param subPopulation
   *          the sub-population
   * @param years
   *          the number of years for which shall be predicted
   */
  void calculateFirstImmigrantPopulation(ExecutionSummary experimentSummary,
      SubPopulation subPopulation, int years) {
    MigParameters immigrantsParameters = new MigParameters(years,
        projection.getMaximumAge());
    immigrantsParameters
        .setMigrantsXm(getGenIndepParameter(ParameterType.MIGRATION
            .getMaleLabelFor(subPopulation)));
    immigrantsParameters
        .setMigrantsXf(getGenIndepParameter(ParameterType.MIGRATION
            .getFemaleLabelFor(subPopulation)));
    setupBasicMigrantParameters(immigrantsParameters, subPopulation, 0);

    MigPopulation migPopulation = new MigPopulation();
    experimentSummary.setFirstImmigrantParameters(immigrantsParameters);
    experimentSummary.addImmigrantResult(migPopulation
        .calculatePopulation(immigrantsParameters));
  }

  /**
   * Calculates a child population of the emigrants sub-population.
   * 
   * @param experimentSummary
   *          experiment summary to be filled with the results
   * @param subPopulation
   *          the sub-population
   * @param generation
   *          the generation of the child population
   * @param years
   *          the number of years for which shall be predicted
   */
  void calculateEmigrantChildPopulation(ExecutionSummary experimentSummary,
      SubPopulation subPopulation, int generation, int years) {

    MigChildParameters parameters = new MigChildParameters(years,
        projection.getMaximumAge());
    parameters.setOldFertX((generation == 1) ? experimentSummary
        .getFirstEmigrantParameters().getFertX() : experimentSummary
        .getEmigrantParameters().get(generation - 2).getFertX());
    parameters.setOldMeanXf(experimentSummary.getEmigrantResults()
        .get(generation - 1).getMeanXf());
    setupBasicMigrantParameters(parameters, subPopulation, generation);

    MigChildPopulation migChildPopulation = new MigChildPopulation();
    experimentSummary.addEmigrantParameters(parameters);
    experimentSummary.addEmigrantResult(migChildPopulation
        .calculatePopulation(parameters));
  }

  /**
   * Calculates a child population of the immigrants sub-population.
   * 
   * @param experimentSummary
   *          experiment summary to be filled with the results
   * @param subPopulation
   *          the sub-population
   * @param generation
   *          the generation of the child population
   * @param years
   *          the number of years for which shall be predicted
   */
  void calculateImmigrantChildPopulation(ExecutionSummary experimentSummary,
      SubPopulation subPopulation, int generation, int years) {

    MigChildParameters parameters = new MigChildParameters(years,
        projection.getMaximumAge());
    parameters.setOldFertX((generation == 1) ? experimentSummary
        .getFirstImmigrantParameters().getFertX() : experimentSummary
        .getImmigrantParameters().get(generation - 2).getFertX());
    parameters.setOldMeanXf(experimentSummary.getImmigrantResults()
        .get(generation - 1).getMeanXf());
    setupBasicMigrantParameters(parameters, subPopulation, generation);

    MigChildPopulation migChildPopulation = new MigChildPopulation();
    experimentSummary.addImmigrantParameter(parameters);
    experimentSummary.addImmigrantResult(migChildPopulation
        .calculatePopulation(parameters));
  }

  /**
   * Creates basic native parameters.
   * 
   * @param years
   *          number of years for which shall be predicted
   * @param jumpOffPopulation
   * @return parameters to calculate native population prediction
   */
  NativeParameters setupBasicJumpOffParameters(int years,
      SubPopulation jumpOffPopulation) {

    NativeParameters nativeParameters = new NativeParameters(years,
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
  void setupBasicMigrantParameters(BasicParameters parameters,
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
