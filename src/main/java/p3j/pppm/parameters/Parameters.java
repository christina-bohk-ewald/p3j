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
package p3j.pppm.parameters;

import james.SimSystem;

import java.util.ArrayList;
import java.util.List;

import p3j.database.DatabaseFactory;
import p3j.database.IP3MDatabase;
import p3j.misc.MatrixDimension;
import p3j.pppm.ProjectionModel;
import p3j.pppm.SubPopulation;
import p3j.pppm.SubPopulationModel;

/**
 * Specifies the {@link Parameter} definitions necessary for a
 * {@link ProjectionModel} with a certain {@link SubPopulationModel}.
 * 
 * Created on August 7, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public final class Parameters {

  private static final String LABEL_DELIM = ": ";

  private static final String LABEL_FERTILITY = LABEL_DELIM + "Fertility";

  private static final String LABEL_PROPORTION_MALE_LIVE_BIRTHS = LABEL_DELIM
      + "Proportion of male live births";

  private static final String LABEL_PROPORTION_OF_INFANT_DEATHS_FIRST_6_MONTHS = LABEL_DELIM
      + "Proportion of infant deaths dying in the first 6 months";

  private static final String LABEL_SURVIVAL_PROBABILITY_OF_OPEN_END_AGE_CLASS = LABEL_DELIM
      + "Survival probability of open-end age class";

  private static final String LABEL_JUMP_OFF_POPULATION = LABEL_DELIM
      + "Jump-off population";

  private static final String LABEL_MALES = " (male)";

  private static final String LABEL_FEMALES = " (female)";

  /**
   * The survivors at age x. Since this string is later used to check whether a
   * given parameter refers to mortality, it should be constant over all
   * {@link Parameter} instances.
   * 
   * TODO: Add an enumeration to define the type of a {@link Parameter}.
   */
  public static final String SURVIVORS_AGE_X = "Survivors at age x";

  public static final String LABEL_SURVIVORS_AGE_X = ": " + SURVIVORS_AGE_X;

  /** List with all parameters. */
  private final List<Parameter> params = new ArrayList<>();

  /**
   * The counter for the sorting indices. See
   * {@link Parameter#getSortingIndex()} etc.
   */
  private int sortIndexCounter = 0;

  /**
   * Creates parameters for a given sub-population model.
   * 
   * @param subPopModel
   *          the underlying sub-population model
   */
  public Parameters(SubPopulationModel subPopModel) {

    IP3MDatabase db = DatabaseFactory.getDatabaseSingleton();

    List<Parameter> paramsToRegister = createParamsForModel(subPopModel);

    // Set IDs
    for (Parameter p : paramsToRegister) {
      Parameter registeredParameter = null;
      try {
        registeredParameter = db.newParameter(p.getName(), p.getSortingIndex(),
            p.isGenerationDependent(), p.getValueHeight(), p.getValueWidth(),
            p.getPopulation());
      } catch (Exception ex) {
        SimSystem.report(ex);
      }
      if (registeredParameter == null)
        throw new IllegalStateException("Could not register parameter: " + p);
      params.add(registeredParameter);
    }
  }

  private List<Parameter> createParamsForModel(SubPopulationModel subPopModel) {
    List<Parameter> parameters = new ArrayList<>();
    for (SubPopulation subPop : subPopModel.getSubPopulations()) {
      parameters.addAll(createParamsForSubPop(subPop));
    }
    return parameters;
  }

  private List<Parameter> createParamsForSubPop(SubPopulation subPop) {
    List<Parameter> parameters = new ArrayList<>();

    if (subPop.isJumpOffPopulation()) {
      addMaleFemaleParameters(parameters, subPop.getName()
          + LABEL_JUMP_OFF_POPULATION, false, MatrixDimension.AGES,
          MatrixDimension.SINGLE);
    } else {
      addMaleFemaleParameters(parameters, subPop.getName(), false,
          MatrixDimension.AGES, MatrixDimension.YEARS);
    }

    addMaleFemaleParameters(parameters, subPop.getName()
        + LABEL_SURVIVAL_PROBABILITY_OF_OPEN_END_AGE_CLASS,
        subPop.isConsistingOfDescendantGenerations(), MatrixDimension.SINGLE,
        MatrixDimension.YEARS);

    addMaleFemaleParameters(parameters, subPop.getName()
        + LABEL_PROPORTION_OF_INFANT_DEATHS_FIRST_6_MONTHS,
        subPop.isConsistingOfDescendantGenerations(), MatrixDimension.SINGLE,
        MatrixDimension.YEARS);

    addParameter(parameters, subPop.getName()
        + LABEL_PROPORTION_MALE_LIVE_BIRTHS,
        subPop.isConsistingOfDescendantGenerations(), MatrixDimension.SINGLE,
        MatrixDimension.YEARS, "");

    addMaleFemaleParameters(parameters, subPop.getName()
        + LABEL_SURVIVORS_AGE_X, subPop.isConsistingOfDescendantGenerations(),
        MatrixDimension.AGES, MatrixDimension.YEARS);

    addParameter(parameters, subPop.getName() + LABEL_FERTILITY,
        subPop.isConsistingOfDescendantGenerations(), MatrixDimension.AGES,
        MatrixDimension.YEARS, "");

    return parameters;
  }

  private void addMaleFemaleParameters(List<Parameter> parameters,
      String paramName, boolean genDependent, MatrixDimension first,
      MatrixDimension second) {
    for (String suffix : new String[] { LABEL_MALES, LABEL_FEMALES })
      addParameter(parameters, paramName, genDependent, first, second, suffix);
  }

  private void addParameter(List<Parameter> parameters, String paramName,
      boolean genDependent, MatrixDimension first, MatrixDimension second,
      String suffix) {
    parameters.add(new Parameter(nextSortingIndex(), genDependent, paramName
        + suffix, first, second, Population.CUSTOM));
  }

  /**
   * Creates next sorting index.
   * 
   * @return the next sorting index to be used
   */
  private int nextSortingIndex() {
    return ++sortIndexCounter;
  }

  public List<Parameter> getParams() {
    return params;
  }

}
