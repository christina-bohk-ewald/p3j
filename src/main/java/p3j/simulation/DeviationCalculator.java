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

import org.jamesii.core.math.random.distributions.NormalDistribution;
import org.jamesii.core.math.random.generators.IRandom;

import p3j.misc.Misc;
import p3j.misc.math.Matrix2D;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterType;
import p3j.simulation.calculation.deterministic.Constants;

/**
 * Calculates the deviation within a single {@link ParameterAssignment}. The
 * degree of deviation is queried by {@link ParameterAssignment#getDeviation()}.
 * If it is 0, the values queried by
 * {@link ParameterAssignment#getMatrixValue()} are returned as is.
 * 
 * Otherwise, a normal distribution with mean 1.0 and standard deviation as
 * prescribed by the {@link ParameterAssignment} is chosen. It is used to
 * calculate an error for each year of the projection horizon. The error term is
 * then multiplied by the data of each corresponding years.
 * 
 * 
 * @see ParameterAssignment
 * @see SingleExecution
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
final class DeviationCalculator {

  /**
   * Deviation calculator should not be instantiated.
   */
  private DeviationCalculator() {
  }

  /**
   * Calculates the (internal) deviation of the given assignment. See class
   * documentation for details.
   * 
   * @param assignment
   *          the parameter assignment
   * @param random
   *          the random number generator used to calculate the deviation
   * @return the deviated matrix values, ready to be used for calculation
   */
  static Matrix2D calculateAssignmentDeviation(ParameterAssignment assignment,
      IRandom random) {

    Matrix2D originalValues = assignment.getMatrixValue();
    double deviation = assignment.getDeviation();

    // Nothing to do if everything is fixed...
    if (Double.compare(deviation, 0) == 0) {
      return originalValues;
    }

    Matrix2D deviatedValues;

    // TODO: This should be replaced by checking against an enumeration.
    if (assignment.getParamInstance().getParameter().getName()
        .contains(ParameterType.SURVIVORS_AGE_X)) {
      deviatedValues = mortalityDeviation(random, originalValues, deviation);
    } else {
      deviatedValues = stochasticDeviation(random, originalValues, deviation);
    }

    return deviatedValues;
  }

  /**
   * Calculating the deviation for mortality parameters needs an extra
   * conversion step. The data is transformed to logarithmic mortality
   * probabilities, and than reverse-transformed after the deviations are
   * applied.
   * 
   * @param random
   *          the random number generator
   * @param originalValues
   *          the original values
   * @param deviation
   *          the deviation
   * 
   * @return the matrix containing the deviated values
   */
  private static Matrix2D mortalityDeviation(IRandom random,
      Matrix2D originalValues, double deviation) {
    Matrix2D lnQx = transformMortalityMatrix(originalValues);
    Matrix2D deviatedValues = stochasticDeviation(random, lnQx, deviation);
    return reverseTransformation(deviatedValues);
  }

  /**
   * Transforms mortality matrix from amounts of surviving people (L_x) to
   * natural logarithms of mortality probabilities.
   * 
   * @param originalValues
   *          the original L_x values
   * 
   * @return the transformed ln(Q_x)
   */
  private static Matrix2D transformMortalityMatrix(Matrix2D originalValues) {

    Matrix2D lnQx = new Matrix2D(originalValues.rows(),
        originalValues.columns());

    for (int year = 0; year < originalValues.rows(); year++) {
      for (int age = 0; age < originalValues.columns() - 1; age++) {
        double survProb = originalValues.getQuick(year, age + 1)
            / originalValues.getQuick(year, age);
        lnQx.setQuick(year, age, Math.log(1 - survProb));
      }
    }
    return lnQx;
  }

  /**
   * Reverts the transformation.
   * 
   * @param deviatedLnQx
   *          the deviated ln(Q_x)
   * 
   * @return the resulting L_x matrix
   */
  private static Matrix2D reverseTransformation(Matrix2D deviatedLnQx) {

    Matrix2D deviatedLx = new Matrix2D(deviatedLnQx.rows(),
        deviatedLnQx.columns());

    for (int year = 0; year < deviatedLnQx.rows(); year++) {
      deviatedLx.setQuick(year, 0, Constants.BASELINE_AMOUNT_MORT_MATRICES);
      for (int age = 1; age < deviatedLnQx.columns(); age++) {
        double survProb = (1 - Math.exp(deviatedLnQx.getQuick(year, age - 1)));
        double remainingPop = deviatedLx.getQuick(year, age - 1) * survProb;
        deviatedLx.setQuick(year, age, remainingPop);
      }
    }
    return deviatedLx;
  }

  /**
   * Carries out a simple stochastic transformation, the extent of which is
   * determined by the deviation.
   * 
   * @param random
   *          the random number generator
   * @param originalValues
   *          the original values
   * @param deviation
   *          the deviation
   * 
   * @return the matrix containing the deviating values
   */
  private static Matrix2D stochasticDeviation(IRandom random,
      Matrix2D originalValues, double deviation) {
    Matrix2D deviatedValues = originalValues.copy();
    double[] errors = generateErrors(random, deviation, deviatedValues.rows());
    applyErrors(deviatedValues, errors);
    return deviatedValues;
  }

  /**
   * Generates the array containing the errors. Its first element contain '1',
   * as no deviation is expected in the jump-off year.
   * 
   * @param random
   *          the random number generator to be used
   * @param deviation
   *          the standard deviation
   * @param years
   *          the number of years, i.e. thje size of the returned array
   * 
   * @return the double[]
   */
  private static double[] generateErrors(IRandom random, double deviation,
      int years) {
    double[] errors = new double[years];
    NormalDistribution normDist = new NormalDistribution(random, 1, deviation);
    errors[0] = 1.;
    for (int i = 1; i < years; i++) {
      errors[i] = errors[i - 1]
          * Math.max(Misc.EPSILON, normDist.getRandomNumber());
    }
    return errors;
  }

  /**
   * Applies errors to matrix.
   * 
   * @param deviatedValues
   *          the deviated values
   * @param errors
   *          the errors
   */
  private static void applyErrors(Matrix2D deviatedValues, double[] errors) {
    for (int year = 1; year < deviatedValues.rows(); year++) {
      for (int age = 0; age < deviatedValues.columns(); age++) {
        deviatedValues.setQuick(year, age, deviatedValues.getQuick(year, age)
            * errors[year]);
      }
    }
  }
}
