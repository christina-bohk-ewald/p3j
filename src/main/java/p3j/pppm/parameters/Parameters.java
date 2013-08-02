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

import static p3j.pppm.parameters.ParameterType.FERTILITY;
import static p3j.pppm.parameters.ParameterType.JUMP_OFF;
import static p3j.pppm.parameters.ParameterType.MIGRATION;
import static p3j.pppm.parameters.ParameterType.MORTALITY;
import static p3j.pppm.parameters.ParameterType.PROP_INF_DEATHS_FIRST_6M;
import static p3j.pppm.parameters.ParameterType.PROP_MALE_LIVE_BIRTHS;
import static p3j.pppm.parameters.ParameterType.SURV_PROB_OPEN_END;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;

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
public class Parameters {

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
      addMaleFemaleParameters(parameters, JUMP_OFF, subPop, false,
          MatrixDimension.AGES, MatrixDimension.SINGLE);
    } else {
      addMaleFemaleParameters(parameters, MIGRATION, subPop, false,
          MatrixDimension.AGES, MatrixDimension.YEARS);
    }

    addMaleFemaleParameters(parameters, SURV_PROB_OPEN_END, subPop,
        MatrixDimension.SINGLE, MatrixDimension.YEARS);

    addMaleFemaleParameters(parameters, PROP_INF_DEATHS_FIRST_6M, subPop,
        MatrixDimension.SINGLE, MatrixDimension.YEARS);

    addParameter(parameters, PROP_MALE_LIVE_BIRTHS.getLabelFor(subPop),
        subPop.isConsistingOfDescendantGenerations(), MatrixDimension.SINGLE,
        MatrixDimension.YEARS);

    addMaleFemaleParameters(parameters, MORTALITY, subPop,
        MatrixDimension.AGES, MatrixDimension.YEARS);

    addParameter(parameters, FERTILITY.getLabelFor(subPop),
        subPop.isConsistingOfDescendantGenerations(), MatrixDimension.AGES,
        MatrixDimension.YEARS);

    return parameters;
  }

  private void addMaleFemaleParameters(List<Parameter> parameters,
      ParameterType paramType, SubPopulation subPopulation,
      MatrixDimension first, MatrixDimension second) {
    addMaleFemaleParameters(parameters, paramType, subPopulation,
        subPopulation.isConsistingOfDescendantGenerations(), first, second);
  }

  private void addMaleFemaleParameters(List<Parameter> parameters,
      ParameterType paramType, SubPopulation subPopulation,
      boolean genDependent, MatrixDimension first, MatrixDimension second) {
    addParameter(parameters, paramType.getMaleLabelFor(subPopulation),
        genDependent, first, second);
    addParameter(parameters, paramType.getFemaleLabelFor(subPopulation),
        genDependent, first, second);
  }

  private void addParameter(List<Parameter> parameters, String paramName,
      boolean genDependent, MatrixDimension first, MatrixDimension second) {
    parameters.add(new Parameter(nextSortingIndex(), genDependent, paramName,
        first, second, Population.CUSTOM));
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
