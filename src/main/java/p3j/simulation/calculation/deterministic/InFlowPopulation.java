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
import p3j.misc.math.Matrix2D;
import p3j.simulation.calculation.deterministic.parameters.InFlowParameters;

/**
 * Calculation regarding the sub-populations with an in-flow matrix.
 * 
 * Created on July 22, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class InFlowPopulation extends
    AbstractPopulation<InFlowParameters, BasicResults> {

  @Override
  public BasicResults calculatePopulation(String subPopName, int generation,
      InFlowParameters parameters) {

    if (generation != 0)
      throw new IllegalArgumentException(
          "Only supports generation-0 populations");

    BasicResults results = new BasicResults(subPopName, generation,
        parameters.getNumOfYears(), parameters.getMaxAge());

    calculateSurvivalProbabilities(parameters, results);

    // Calculate rest:

    // ...for females
    calculateMeanAndEndPopulation(results.getMeanXf(), results.getEndXf(),
        parameters.getMigrantsXf(), parameters.getSurviveProbO100f(),
        results.getP1f(), results.getP2f(), parameters.getNumOfYears(),
        parameters.getMaxAge());

    // ...for males
    calculateMeanAndEndPopulation(results.getMeanXm(), results.getEndXm(),
        parameters.getMigrantsXm(), parameters.getSurviveProbO100m(),
        results.getP1m(), results.getP2m(), parameters.getNumOfYears(),
        parameters.getMaxAge());

    return results;
  }

  /**
   * Calculates mean and end populations.
   * 
   * @param meanPopulation
   *          mean population (will be filled)
   * @param endPopulation
   *          end population (will be filled)
   * @param migrants
   *          the mirgants
   * @param surviveProbO100
   *          survival probability of the over-100-years-olds
   * @param p1
   *          the P_1 matrix (infant survival probability first half of first
   *          year)
   * @param p2
   *          the P_2 matrix (infant survival probability second half of first
   *          year)
   * @param numberOfYears
   *          number of years to be predicted
   * @param maximumAge
   *          the maximum age class
   */
  protected static void calculateMeanAndEndPopulation(Matrix2D meanPopulation,
      Matrix2D endPopulation, Matrix2D migrants, Matrix2D surviveProbO100,
      Matrix2D p1, Matrix2D p2, int numberOfYears, int maximumAge) {

    for (int year = 1; year < numberOfYears; year++) {
      for (int age = 0; age < maximumAge + 1; age++) {

        // Calculate mean population
        if (age == maximumAge) {
          meanPopulation.setQuick(
              age,
              year,
              endPopulation.getQuick(age - 1, year - 1)
                  * p2.getQuick(age - 1, year - 1)
                  + (migrants.getQuick(year, age) / 2)
                  * surviveProbO100.getQuick(year, 0));
        } else {
          switch (age) {
          case 0:
            meanPopulation.setQuick(age, year, 0);
            break;
          case 1:
            meanPopulation.setQuick(
                age,
                year,
                (migrants.getQuick(year - 1, age - 1) + migrants.getQuick(year,
                    age) / 2) * p2.getQuick(age - 1, year - 1));
            break;
          default:
            meanPopulation.setQuick(
                age,
                year,
                (migrants.getQuick(year, age) / 2 + endPopulation.getQuick(
                    age - 1, year - 1)) * p2.getQuick(age - 1, year - 1));
            break;
          }
        }

        // Calculate end population
        if (age == maximumAge) {
          endPopulation.setQuick(
              age,
              year,
              meanPopulation.getQuick(age, year)
                  * surviveProbO100.getQuick(year, 0)
                  + migrants.getQuick(year, age) / 2);

        } else {
          switch (age) {
          case 0:
            endPopulation.setQuick(age, year,
                meanPopulation.getQuick(age, year) * p1.getQuick(age, year)
                    + migrants.getQuick(year, age));
            break;
          default:
            endPopulation.setQuick(age, year,
                meanPopulation.getQuick(age, year) * p1.getQuick(age, year)
                    + migrants.getQuick(year, age) / 2);
            break;
          }
        }
      }
    }
  }
}
