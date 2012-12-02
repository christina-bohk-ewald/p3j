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
import p3j.simulation.calculation.deterministic.parameters.BasicParameters;

/**
 * Base class for population calculations.
 * 
 * This method does not conform to ESCA-JAVA0138, but the methods that have more
 * than 5 parameters are very short and therefore still easy to understand.
 * 
 * Created on July 22, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 * @param <P>
 *          the parameter class
 * @param <R>
 *          the result class
 * 
 */
public abstract class AbstractPopulation<P extends BasicParameters, R extends BasicResults> {

  /**
   * Distribution of deaths in half years of an entire single age year. First
   * half of deaths die in first and the other half die in last half year if set
   * to 0.5.
   */
  public static final double FORCE_MORT_INTERVAL = 0.5;

  /**
   * Calculate population.
   * 
   * @param subPopName
   *          the sub-population name
   * @param generation
   *          the generation
   * @param parameters
   *          the parameters requires to population calculation
   * @return results the results of the calculation
   */
  public abstract R calculatePopulation(String subPopName, int generation,
      P parameters);

  /**
   * Calculates survival probabilities for the first half of a year.
   * 
   * @param target
   *          matrix to store results in
   * @param deathProbInfant1half
   *          death probabilities of infants within the first 6 months
   * @param mortality
   *          mortality matrix
   * @param numOfYears
   *          number of years to be calculated
   * @param maxAge
   *          the maximum age
   */
  protected static void getFirstHalfyearSurvProb(Matrix2D target,
      Matrix2D deathProbInfant1half, Matrix2D mortality, int numOfYears,
      int maxAge) {

    for (int year = 0; year < numOfYears; year++) {

      double mortCurrentYear = mortality.getQuick(year, 0);
      double mortNextYear = mortality.getQuick(year, 1);

      // Age 0 to 0.5
      target
          .setQuick(
              0,
              year,
              1.0 - ((deathProbInfant1half.getQuick(year, 0) * (mortCurrentYear - mortNextYear)) / mortCurrentYear));

      // Other ages
      for (int age = 1; age < maxAge; age++) {
        mortCurrentYear = mortNextYear;
        mortNextYear = mortality.getQuick(year, age + 1);
        target
            .setQuick(
                age,
                year,
                1.0 - ((FORCE_MORT_INTERVAL * (mortCurrentYear - mortNextYear)) / mortCurrentYear));
      }
    }
  }

  /**
   * Calculates survival probabilities for the second half of a year.
   * 
   * @param target
   *          matrix to store results in
   * @param deathProbInfant1half
   *          death probabilities of infants within the first 6 months
   * @param mortality
   *          mortality matrix
   * @param numOfYears
   *          number of years to be calculated
   * @param maxAge
   *          the maximum age
   */
  protected static void getSecondHalfyearSurvProb(Matrix2D target,
      Matrix2D deathProbInfant1half, Matrix2D mortality, int numOfYears,
      int maxAge) {

    for (int year = 0; year < numOfYears; year++) {

      double deathProbInfant = deathProbInfant1half.getQuick(year, 0);
      double mortCurrentYear = mortality.getQuick(year, 0);
      double mortNextYear = mortality.getQuick(year, 1);
      double mortDiff = mortCurrentYear - mortNextYear;

      // Age 0 to 0.5
      target
          .setQuick(
              0,
              year,
              1.0 - (((1.0 - deathProbInfant) * mortDiff) / (mortCurrentYear - deathProbInfant
                  * mortDiff)));

      // Other ages
      for (int age = 1; age < maxAge; age++) {
        mortCurrentYear = mortNextYear;
        mortNextYear = mortality.getQuick(year, age + 1);
        mortDiff = mortCurrentYear - mortNextYear;
        target
            .setQuick(
                age,
                year,
                1.0 - ((FORCE_MORT_INTERVAL * mortDiff) / (mortCurrentYear - FORCE_MORT_INTERVAL
                    * mortDiff)));
      }
    }

  }

  /**
   * Calculate survival probabilities for both halves of the year.
   * 
   * @param parameters
   *          the parameters
   * @param results
   *          the results
   */
  protected static void calculateSurvivalProbabilities(
      BasicParameters parameters, BasicResults results) {

    // First half of the year
    getFirstHalfyearSurvProb(results.getP1f(),
        parameters.getDeathProbInfant1halfFemale(), parameters.getMortXf(),
        parameters.getNumOfYears(), parameters.getMaxAge());
    getFirstHalfyearSurvProb(results.getP1m(),
        parameters.getDeathProbInfant1halfMale(), parameters.getMortXm(),
        parameters.getNumOfYears(), parameters.getMaxAge());

    // Second half of the year
    getSecondHalfyearSurvProb(results.getP2f(),
        parameters.getDeathProbInfant1halfFemale(), parameters.getMortXf(),
        parameters.getNumOfYears(), parameters.getMaxAge());
    getSecondHalfyearSurvProb(results.getP2m(),
        parameters.getDeathProbInfant1halfMale(), parameters.getMortXm(),
        parameters.getNumOfYears(), parameters.getMaxAge());
  }

  /**
   * Calculates target matrix by multiplying source and the field from the
   * multiplication matrix that correspond to the year and age (column and row)
   * above/before (- 1).
   * 
   * @param startAge
   *          start age
   * @param endAge
   *          end age
   * @param year
   *          current year
   * @param target
   *          target matrix
   * @param source
   *          source matrix
   * @param mult
   *          multiplication matrix
   */
  protected static void calculateByMultMin1(int startAge, int endAge, int year,
      Matrix2D target, Matrix2D source, Matrix2D mult) {
    for (int age = startAge; age < endAge; age++) {
      target
          .setQuick(
              age,
              year,
              source.getQuick(age - 1, year - 1)
                  * mult.getQuick(age - 1, year - 1));
    }
  }

  /**
   * Calculates target matrix by multiplying source and the field from the
   * multiplication matrix at the same age and year position.
   * 
   * @param startAge
   *          start age
   * @param endAge
   *          end age
   * @param year
   *          current year
   * @param target
   *          target matrix
   * @param source
   *          source matrix
   * @param mult
   *          multiplication matrix
   */
  protected static void calculateByMult(int startAge, int endAge, int year,
      Matrix2D target, Matrix2D source, Matrix2D mult) {
    for (int age = startAge; age < endAge; age++) {
      target.setQuick(age, year,
          source.getQuick(age, year) * mult.getQuick(age, year));
    }
  }

  /**
   * Calculates number of children in current year.
   * 
   * @param meanFemalePop
   *          mean female population
   * @param fertility
   *          fertility
   * @param year
   *          current year
   * @return number of children in current year
   */
  protected static double getNumOfChilds(Matrix2D meanFemalePop,
      Matrix2D fertility, int year) {

    double numOfChilds = 0;

    for (int age = Constants.FERT_AGE_BEGIN; age < Constants.FERT_AGE_END; age++) {
      numOfChilds += meanFemalePop.getQuick(age + 1, year)
          * fertility.getQuick(year, age + 1);
    }

    return numOfChilds;
  }

  /**
   * Calculates the first and the last age group of the mean population.
   * 
   * @param target
   *          mean population matrix
   * @param endPopulation
   *          end population matrix
   * @param p2
   *          P_2 matrix (survival probabilities of infants in second half-year
   *          of their first year)
   * @param survO100
   *          survival probabilities of the over-100-years-olds
   * @param liveBirthProp
   *          proportions of live births belonging to this sex
   * @param year
   *          current year
   * @param numberOfChilds
   *          (overall) number of children
   * @param maxAge
   *          the maximal age
   */
  protected static void calculateRestOfMeanPopulation(Matrix2D target,
      Matrix2D endPopulation, Matrix2D p2, Matrix2D survO100,
      Matrix2D liveBirthProp, int year, double numberOfChilds, int maxAge) {

    target.setQuick(0, year, numberOfChilds * liveBirthProp.getQuick(year, 0));

    target.setQuick(
        maxAge,
        year,
        endPopulation.getQuick(maxAge - 1, year - 1)
            * p2.getQuick(maxAge - 1, year)
            + endPopulation.getQuick(maxAge, year - 1)
            * survO100.getQuick(year, 0));
  }

}
