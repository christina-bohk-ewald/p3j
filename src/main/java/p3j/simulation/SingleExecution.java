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
import p3j.pppm.parameters.Parameters;
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
      NativeParameters nativeParameters = setupBasicNativeParameters(years,
          jumpOffPopulation);
      NativePopulation nativePopulation = new NativePopulation();
      executionSummary.setNativeParameters(nativeParameters);
      executionSummary.setNativeResults(nativePopulation
          .calculatePopulation(nativeParameters));
    }

    for (int i = 0; i < projection.getGenerations(); i++) {
      // Calculate emigration population
      if (i == 0) {
        calculateFirstEmigrantPopulation(executionSummary, years);
      } else {
        calculateEmigrantChildPopulation(executionSummary, i, years);
      }
      // Calculate immigration population
      if (i == 0) {
        calculateFirstImmigrantPopulation(executionSummary, years);
      } else {
        calculateImmigrantChildPopulation(executionSummary, i, years);
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
   * @param years
   *          the number of years for which shall be predicted
   */
  void calculateFirstEmigrantPopulation(ExecutionSummary experimentSummary,
      int years) {
    MigParameters parameters = new MigParameters(years,
        projection.getMaximumAge());
    parameters.setMigrantsXm(getGenIndepParameter(Parameters.EMIGRANTS_M));
    parameters.setMigrantsXf(getGenIndepParameter(Parameters.EMIGRANTS_F));
    setupBasicEmigrantParameters(parameters, 0);
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
   * @param years
   *          the number of years for which shall be predicted
   */
  void calculateFirstImmigrantPopulation(ExecutionSummary experimentSummary,
      int years) {
    MigParameters immigrantsParameters = new MigParameters(years,
        projection.getMaximumAge());
    immigrantsParameters
        .setMigrantsXm(getGenIndepParameter(Parameters.IMMIG_M));
    immigrantsParameters
        .setMigrantsXf(getGenIndepParameter(Parameters.IMMIG_F));
    setupBasicImmigrantParameters(immigrantsParameters, 0);

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
   * @param generation
   *          the generation of the child population
   * @param years
   *          the number of years for which shall be predicted
   */
  void calculateEmigrantChildPopulation(ExecutionSummary experimentSummary,
      int generation, int years) {

    MigChildParameters parameters = new MigChildParameters(years,
        projection.getMaximumAge());
    parameters.setOldFertX((generation == 1) ? experimentSummary
        .getFirstEmigrantParameters().getFertX() : experimentSummary
        .getEmigrantParameters().get(generation - 2).getFertX());
    parameters.setOldMeanXf(experimentSummary.getEmigrantResults()
        .get(generation - 1).getMeanXf());
    setupBasicEmigrantParameters(parameters, generation);

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
   * @param generation
   *          the generation of the child population
   * @param years
   *          the number of years for which shall be predicted
   */
  void calculateImmigrantChildPopulation(ExecutionSummary experimentSummary,
      int generation, int years) {

    MigChildParameters parameters = new MigChildParameters(years,
        projection.getMaximumAge());
    parameters.setOldFertX((generation == 1) ? experimentSummary
        .getFirstImmigrantParameters().getFertX() : experimentSummary
        .getImmigrantParameters().get(generation - 2).getFertX());
    parameters.setOldMeanXf(experimentSummary.getImmigrantResults()
        .get(generation - 1).getMeanXf());
    setupBasicImmigrantParameters(parameters, generation);

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
  NativeParameters setupBasicNativeParameters(int years,
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
   * @param generation
   *          the generation for which the parameters should be set
   */
  void setupBasicImmigrantParameters(BasicParameters parameters, int generation) {
    parameters.setDeathProbInfant1halfFemale(getGenDepParameter(
        Parameters.IMMIG_DEATHPROB_INFANT_1STHALF_F, generation));
    parameters.setDeathProbInfant1halfMale(getGenDepParameter(
        Parameters.IMMIG_DEATHPROB_INFANT_1STHALF_M, generation));
    parameters.setFertX(getGenDepParameter(Parameters.IMMIG_FERT, generation));
    parameters.setMortXf(getGenDepParameter(Parameters.IMMIG_MORT_X_F,
        generation));
    parameters.setMortXm(getGenDepParameter(Parameters.IMMIG_MORT_X_M,
        generation));
    parameters.setSurviveProbO100f(getGenDepParameter(
        Parameters.IMMIG_SURV_PROB_O100_F, generation));
    parameters.setSurviveProbO100m(getGenDepParameter(
        Parameters.IMMIG_SURV_PROB_O100_M, generation));
    parameters.setMaleRateLiveBirth(getGenDepParameter(
        Parameters.IMMIG_PROP_LIVEBIRTH_M, generation));
  }

  /**
   * Sets up emigrant parameters.
   * 
   * @param parameters
   *          the basic parameters (to be filled)
   * @param generation
   *          the generation for which the parameters should be set
   */
  void setupBasicEmigrantParameters(BasicParameters parameters, int generation) {
    parameters.setDeathProbInfant1halfFemale(getGenDepParameter(
        Parameters.EMIG_DEATHPROB_INFANT_1STHALF_F, generation));
    parameters.setDeathProbInfant1halfMale(getGenDepParameter(
        Parameters.EMIG_DEATHPROB_INFANT_1STHALF_M, generation));
    parameters.setFertX(getGenDepParameter(Parameters.EMIG_FERT, generation));
    parameters.setMortXf(getGenDepParameter(Parameters.EMIG_MORT_X_F,
        generation));
    parameters.setMortXm(getGenDepParameter(Parameters.EMIG_MORT_X_M,
        generation));
    parameters.setSurviveProbO100f(getGenDepParameter(
        Parameters.EMIG_SURV_PROB_O100_F, generation));
    parameters.setSurviveProbO100m(getGenDepParameter(
        Parameters.EMIG_SURV_PROB_O100_M, generation));
    parameters.setMaleRateLiveBirth(getGenDepParameter(
        Parameters.EMIG_PROP_LIVEBIRTH_M, generation));
  }

}
