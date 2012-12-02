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
import p3j.simulation.calculation.deterministic.parameters.MigChildParameters;

/**
 * Calculation of the population containing children of migrants. One function
 * does not conform to ESCA-JAVA0138 (9 parameters are required for
 * calculation).
 * 
 * Created on July 22, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class MigChildPopulation extends
    AbstractPopulation<MigChildParameters, BasicResults> {

  @Override
  public BasicResults calculatePopulation(String subPopName, int generation,
      MigChildParameters parameters) {

    BasicResults results = new BasicResults(subPopName, generation,
        parameters.getNumOfYears(), parameters.getMaxAge());

    calculateSurvivalProbabilities(parameters, results);

    // Calculate rest

    // ...for females
    calculateMeanAndEndPopulation(parameters.getOldMeanXf(),
        parameters.getOldFertX(), parameters.getFemalePropLiveBirth(),
        results.getMeanXf(), results.getEndXf(),
        parameters.getSurviveProbO100f(), results.getP1f(), results.getP2f(),
        parameters.getNumOfYears(), parameters.getMaxAge());

    // ...for males
    calculateMeanAndEndPopulation(parameters.getOldMeanXf(),
        parameters.getOldFertX(), parameters.getMalePropLiveBirth(),
        results.getMeanXm(), results.getEndXm(),
        parameters.getSurviveProbO100m(), results.getP1m(), results.getP2m(),
        parameters.getNumOfYears(), parameters.getMaxAge());

    return results;
  }

  /**
   * Calculates mean and end populations.
   * 
   * @param oldMeanXf
   *          old mean population of females
   * @param oldFertX
   *          old fertility
   * @param propLiveBirth
   *          proportion of live birth of this sex
   * @param meanPopulation
   *          mean population (filled by this method)
   * @param endPopulation
   *          end population (filled by this method)
   * @param surviveProbO100
   *          survival probability of the over-100-years-olds
   * @param p1
   *          P_1 matrix (infant survival probability first 6 months)
   * @param p2
   *          P_2 matrix (infant survival probability second half year)
   * @param numberOfYears
   *          number of years to be predicted
   * @param maximumAge
   *          the maximum age
   */
  protected static void calculateMeanAndEndPopulation(Matrix2D oldMeanXf,
      Matrix2D oldFertX, Matrix2D propLiveBirth, Matrix2D meanPopulation,
      Matrix2D endPopulation, Matrix2D surviveProbO100, Matrix2D p1,
      Matrix2D p2, int numberOfYears, int maximumAge) {

    for (int year = 1; year < numberOfYears; year++) {

      double numOfChilds = AbstractPopulation.getNumOfChilds(oldMeanXf,
          oldFertX, year);

      for (int age = 0; age < maximumAge + 1; age++) {

        // Calculate mean population
        if (age == maximumAge) {
          meanPopulation.setQuick(
              age,
              year,
              endPopulation.getQuick(age - 1, year - 1)
                  * p2.getQuick(age - 1, year - 1)
                  + endPopulation.getQuick(age, year - 1)
                  * surviveProbO100.getQuick(year, 0));
        } else {
          switch (age) {
          case 0:
            meanPopulation.setQuick(age, year,
                numOfChilds * propLiveBirth.getQuick(year, 0));
            break;
          default:
            meanPopulation.setQuick(
                age,
                year,
                endPopulation.getQuick(age - 1, year - 1)
                    * p2.getQuick(age - 1, year - 1));
            break;
          }
        }

        // Calculate end population
        if (age == maximumAge) {
          endPopulation.setQuick(age, year, meanPopulation.getQuick(age, year)
              * surviveProbO100.getQuick(year, 0));
        } else {
          endPopulation.setQuick(age, year, meanPopulation.getQuick(age, year)
              * p1.getQuick(age, year));
        }

      }
    }
  }
}
