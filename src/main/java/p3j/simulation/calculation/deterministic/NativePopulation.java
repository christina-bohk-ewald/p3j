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
package p3j.simulation.calculation.deterministic;

import p3j.experiment.results.BasicResults;
import p3j.simulation.calculation.deterministic.parameters.NativeParameters;

/**
 * Handles calculations for the native population.
 * 
 * Created on July 16, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class NativePopulation extends
    AbstractPopulation<NativeParameters, BasicResults> {

  @Override
  public BasicResults calculatePopulation(NativeParameters parameters) {

    BasicResults results = new BasicResults(parameters.getNumOfYears(),
        parameters.getMaxAge());

    // Storing start year populations to results
    results.getEndXm().assignColumn(0, parameters.getPEndSYm().toArray()[0]);
    results.getEndXf().assignColumn(0, parameters.getPEndSYf().toArray()[0]);

    calculateSurvivalProbabilities(parameters, results);

    for (int year = 1; year < parameters.getNumOfYears(); year++) {

      // Mean female population
      calculateByMultMin1(1, parameters.getMaxAge(), year, results.getMeanXf(),
          results.getEndXf(), results.getP2f());

      // Get number of children born in current year
      double numOfChilds = getNumOfChilds(results.getMeanXf(), parameters
          .getFertX(), year);

      calculateRestOfMeanPopulation(results.getMeanXf(), results.getEndXf(),
          results.getP2f(), parameters.getSurviveProbO100f(), parameters
              .getFemalePropLiveBirth(), year, numOfChilds, parameters
              .getMaxAge());

      // Female end population
      calculateByMult(0, parameters.getMaxAge(), year, results.getEndXf(),
          results.getMeanXf(), results.getP1f());

      results.getEndXf().setQuick(
          parameters.getMaxAge(),
          year,
          results.getMeanXf().get(parameters.getMaxAge(), year)
              * parameters.getSurviveProbO100f().get(year, 0));

      // Mean male population
      calculateByMultMin1(1, parameters.getMaxAge(), year, results.getMeanXm(),
          results.getEndXm(), results.getP2m());

      calculateRestOfMeanPopulation(results.getMeanXm(), results.getEndXm(),
          results.getP2m(), parameters.getSurviveProbO100m(), parameters
              .getMalePropLiveBirth(), year, numOfChilds, parameters
              .getMaxAge());

      // Male end population
      calculateByMult(0, parameters.getMaxAge(), year, results.getEndXm(),
          results.getMeanXm(), results.getP1m());

      results.getEndXm().setQuick(
          parameters.getMaxAge(),
          year,
          results.getMeanXm().get(parameters.getMaxAge(), year)
              * parameters.getSurviveProbO100m().get(year, 0));

    }

    return results;
  }
}
