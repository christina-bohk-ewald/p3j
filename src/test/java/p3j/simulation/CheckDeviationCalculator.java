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

import java.util.List;

import junit.framework.TestCase;

import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.CSVReader;
import org.jamesii.core.util.misc.Strings;

import p3j.misc.MatrixDimension;
import p3j.misc.math.Matrix;
import p3j.misc.math.Matrix2D;
import p3j.pppm.parameters.Parameter;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.parameters.ParameterType;
import p3j.pppm.parameters.Population;

/**
 * Simple test for deviation calculation.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class CheckDeviationCalculator extends TestCase {

  /** The number of years for the test matrix. */
  static final int YEARS = 50;

  /** The number of ages for the test matrix. */
  static final int AGES = 99;

  /** The value with which the original matrix shall be filled. */
  static final int FIXED_AMOUNT = 100000;

  /**
   * A large deviation, to check corner cases.
   */
  static final double LARGE_DEVIATION = 10.;

  /**
   * The default standard deviation.
   */
  static final double DEFAULT_DEVIATION = 0.01;

  /**
   * The file that contains the test data.
   */
  static final String FILE_TEST_DATA = "./p3j/simulation/surv_assumption_natives_male_blue.txt";

  /**
   * The original matrix.
   */
  Matrix2D originalMatrix;

  /**
   * The parameter assignment to pass.
   */
  ParameterAssignment assignment;

  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  public void setUp() {
    originalMatrix = new Matrix2D(YEARS, AGES);
    for (int i = 0; i < YEARS; i++) {
      for (int j = 0; j < AGES; j++) {
        originalMatrix.setQuick(i, j, FIXED_AMOUNT);
      }
    }

    assignment = new ParameterAssignment(new ParameterInstance(0,
        new Parameter(0, true, "Some parameter", MatrixDimension.YEARS,
            MatrixDimension.AGES, Population.NATIVES)), "test", "", 1.0, 0.0,
        new Matrix(originalMatrix));
    assignment.setDeviation(DEFAULT_DEVIATION);
  }

  /**
   * Tests case when there is no deviation calculation (because deviation is 0).
   */
  public void testNoDeviationCalculation() {
    assignment.setDeviation(0.);
    Matrix2D deviationMatrix = DeviationCalculator
        .calculateAssignmentDeviation(assignment, SimSystem.getRNGGenerator()
            .getNextRNG());
    for (int i = 0; i < YEARS; i++) {
      for (int j = 0; j < AGES; j++) {
        assertEquals(originalMatrix.get(i, j), deviationMatrix.get(i, j));
      }
    }
  }

  /**
   * Test deviation calculation.
   */
  public void testDeviationCalculation() {
    assignment.setDeviation(0.2);

    Matrix2D deviationMatrix = DeviationCalculator
        .calculateAssignmentDeviation(assignment, SimSystem.getRNGGenerator()
            .getNextRNG());

    // First year should always stay the same
    for (int j = 0; j < AGES; j++) {
      assertEquals(originalMatrix.get(0, j), deviationMatrix.get(0, j));
    }

    // Everything else should be different
    for (int i = 1; i < YEARS; i++) {
      for (int j = 0; j < AGES; j++) {
        assertFalse(Double.compare(originalMatrix.get(i, j),
            deviationMatrix.get(i, j)) == 0);
      }
    }

    // Just for manual checks
    System.out.println(Strings.displayMatrix(deviationMatrix.toArray()));
  }

  /**
   * Tests calculation with large standard deviation.
   */
  public void testLargeDeviationCalculation() {
    assignment.setDeviation(LARGE_DEVIATION);
    Matrix2D deviationMatrix = DeviationCalculator
        .calculateAssignmentDeviation(assignment, SimSystem.getRNGGenerator()
            .getNextRNG());

    // Results should never be negative.
    for (int i = 1; i < YEARS; i++) {
      for (int j = 0; j < AGES; j++) {
        assertTrue(deviationMatrix.get(i, j) >= 0);
      }
    }

    // Just for manual checks
    System.out.println(Strings.displayMatrix(deviationMatrix.toArray()));
  }

  /**
   * Test mortality deviation.
   */
  public void testMortalityDeviation() {

    Matrix mortMatrix = new Matrix(getTestMatrixForMortality());
    Matrix2D mortalityMatrix = mortMatrix.getValue();

    ParameterAssignment mortAssignment = new ParameterAssignment(
        new ParameterInstance(0, new Parameter(0, true,
            "Some parameter regarding " + ParameterType.SURVIVORS_AGE_X,
            MatrixDimension.YEARS, MatrixDimension.AGES, Population.NATIVES)),
        "test", "", 1.0, 0.0, mortMatrix);
    mortAssignment.setDeviation(DEFAULT_DEVIATION);

    Matrix2D deviationMatrix = DeviationCalculator
        .calculateAssignmentDeviation(mortAssignment, SimSystem
            .getRNGGenerator().getNextRNG());

    // First year should stay the same
    for (int age = 0; age < mortAssignment.getMatrix().getValue().columns(); age++) {
      assertEquals(mortalityMatrix.get(0, age), deviationMatrix.get(0, age));
    }

    // 0-aged should always stay the same
    for (int year = 0; year < mortAssignment.getMatrix().getValue().rows(); year++) {
      assertEquals(mortalityMatrix.get(year, 0), deviationMatrix.get(year, 0));
    }

    // Everything else should be different
    for (int year = 1; year < mortAssignment.getMatrix().getValue().rows(); year++) {
      for (int age = 1; age < mortAssignment.getMatrix().getValue().columns(); age++) {
        assertFalse(Double.compare(mortalityMatrix.getQuick(year, age),
            deviationMatrix.getQuick(year, age)) == 0);
      }
    }

    System.out.println(Strings.displayMatrix(deviationMatrix.toArray()));
  }

  /**
   * Gets the test matrix for mortality. Is transposed while being read.
   * 
   * @return the test matrix for mortality
   */
  private Matrix2D getTestMatrixForMortality() {

    List<String[]> data = (new CSVReader()).read(FILE_TEST_DATA, false);
    int years = data.get(0).length - 1;
    int ages = data.size();

    double[][] matrix = new double[years][ages];
    for (int age = 0; age < data.size(); age++) {
      for (int year = 0; year < years; year++) {
        matrix[year][age] = Double.parseDouble(data.get(age)[year + 1]);
      }
    }

    System.out.println(Strings.displayMatrix(matrix));

    return new Matrix2D(matrix, "", "");
  }
}
