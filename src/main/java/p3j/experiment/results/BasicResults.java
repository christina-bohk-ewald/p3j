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
package p3j.experiment.results;

import p3j.misc.math.Matrix2D;

/**
 * Represents the fundamental set of results calculated by the system.
 * 
 * Only the end and mean populations are considered as final results and will
 * per persisted (NOT the survival probabilities!).
 * 
 * Created on July 04, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class BasicResults {

  /** The id. */
  private int id;

  /** The name of the sub-population this result belongs to. */
  private String subPopName;

  /**
   * The generation of the sub-population (0 if no generations are
   * distinguished).
   */
  private int generation;

  /**
   * Year-by-year age-specific male population at the end of the year (101 x n
   * matrix).
   */
  private Matrix2D endXm;

  /**
   * Year-by-year age-specific female population at the end of the year (101 x n
   * matrix).
   */
  private Matrix2D endXf;

  /** Year-by-year age-specific mean male population (101 x n matrix). */
  private Matrix2D meanXm;

  /**
   * Year-by-year age-specific mean female population (101 x n matrix).
   */
  private Matrix2D meanXf;

  /**
   * Year-by-year age-specific male survival probability in the first half of
   * the year.
   */
  private Matrix2D p1m;

  /**
   * Year-by-year age-specific female survival probability in the first half of
   * the year.
   */
  private Matrix2D p1f;

  /**
   * Year-by-year age-specific male survival probability in the second half of
   * the year.
   */
  private Matrix2D p2m;

  /**
   * Year-by-year age-specific female survival probability in the second half of
   * the year.
   */
  private Matrix2D p2f;

  /**
   * Default constructor.
   * 
   * @param subPopulationName
   *          the sub-population name
   * @param numOfYears
   *          number of years to be predicted
   * @param maxAge
   *          the maximum age
   */
  public BasicResults(String subPopulationName, int subPopGeneration,
      int numOfYears, int maxAge) {

    this.subPopName = subPopulationName;
    this.generation = subPopGeneration;

    this.endXm = new Matrix2D(maxAge + 1, numOfYears);
    this.endXf = new Matrix2D(maxAge + 1, numOfYears);
    this.meanXm = new Matrix2D(maxAge + 1, numOfYears);
    this.meanXf = new Matrix2D(maxAge + 1, numOfYears);

    this.p1m = new Matrix2D(maxAge, numOfYears);
    this.p1f = new Matrix2D(maxAge, numOfYears);
    this.p2m = new Matrix2D(maxAge, numOfYears);
    this.p2f = new Matrix2D(maxAge, numOfYears);

  }

  /**
   * Constructor for bean compliance. Do NOT use manually.
   */
  public BasicResults() {
  }

  public Matrix2D getEndXm() {
    return endXm;
  }

  public void setEndXm(Matrix2D endXm) {
    this.endXm = endXm;
  }

  public Matrix2D getEndXf() {
    return endXf;
  }

  public void setEndXf(Matrix2D endXf) {
    this.endXf = endXf;
  }

  public Matrix2D getMeanXm() {
    return meanXm;
  }

  public void setMeanXm(Matrix2D meanXm) {
    this.meanXm = meanXm;
  }

  public Matrix2D getMeanXf() {
    return meanXf;
  }

  public void setMeanXf(Matrix2D meanXf) {
    this.meanXf = meanXf;
  }

  public Matrix2D getP1m() {
    return p1m;
  }

  public void setP1m(Matrix2D p1m) {
    this.p1m = p1m;
  }

  public Matrix2D getP1f() {
    return p1f;
  }

  public void setP1f(Matrix2D p1f) {
    this.p1f = p1f;
  }

  public Matrix2D getP2m() {
    return p2m;
  }

  public void setP2m(Matrix2D p2m) {
    this.p2m = p2m;
  }

  public Matrix2D getP2f() {
    return p2f;
  }

  public void setP2f(Matrix2D p2f) {
    this.p2f = p2f;
  }

  public int getID() {
    return id;
  }

  public void setID(int id) {
    this.id = id;
  }

  public String getSubPopName() {
    return subPopName;
  }

  public void setSubPopName(String subPopName) {
    this.subPopName = subPopName;
  }

  public int getGeneration() {
    return generation;
  }

  public void setGeneration(int generation) {
    this.generation = generation;
  }

}
