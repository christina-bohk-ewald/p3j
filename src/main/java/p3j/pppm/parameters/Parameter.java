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

import java.io.Serializable;

import p3j.misc.MatrixDimension;

/**
 * Represents a parameter in the PPPM. A parameter is basically a name,
 * associated with an ID. It denotes a certain basic sort of parameter within
 * the PPPM, e.g. the fertility of emigrants, the fertility of natives, etc.
 * Each parameter has to come in certain (matrix) dimensions, which are defined
 * here as {@link Parameter#valueWidth} and {@link Parameter#valueHeight}. These
 * are given in {@link MatrixDimension}. A parameter might be
 * generation-dependent, expressed by {@link Parameter#generationDependent},
 * e.g., fertility of X-th generation of emigrants. Note that this class only
 * stores the general aspects of the parameter. The definition of an actual
 * input parameter kind, e.g. the fertility of the 5-th generation of emigrants,
 * is done by {@link ParameterInstance} objects. All instances of this class are
 * defined as constants in {@link Parameters}.
 * 
 * Created on January 18, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class Parameter implements Serializable {

  /** Serialization ID. */
  private static final long serialVersionUID = 7301913452917113893L;

  /** ID of the parameter. */
  private int id;

  /** Flag to mark generation-dependent parameters. */
  private boolean generationDependent;

  /** Name of the parameter. */
  private String name;

  /**
   * The sorting index of this parameter. This is required to allow a custom
   * ordering (e.g. by specific sub-populations). As the order is only defined
   * when considering the sorting index of the other parameters involved, this
   * needs to be stored per parameter.
   */
  private Integer sortingIndex = -1;

  /** Width of a valid value matrix. */
  private MatrixDimension valueWidth = MatrixDimension.YEARS;

  /** Height of a valid value matrix. */
  private MatrixDimension valueHeight = MatrixDimension.AGES;

  /** The population this parameter refers to. */
  private Population population;

  /**
   * Default constructor.
   * 
   * @param sortIndex
   *          the sorting index of the parameter
   * @param genDependent
   *          if true, this parameter is generation-dependent
   * @param paramName
   *          name of the parameter
   * @param height
   *          height of the parameter matrix
   * @param width
   *          width of the parameter matrix
   * @param pop
   *          the population this parameter belongs to
   */
  public Parameter(int sortIndex, boolean genDependent, String paramName,
      MatrixDimension height, MatrixDimension width, Population pop) {
    sortingIndex = sortIndex;
    generationDependent = genDependent;
    name = paramName;
    valueHeight = height;
    valueWidth = width;
    population = pop;
  }

  /**
   * Constructor for bean compatibility.
   */
  public Parameter() {
    this(-1, false, "", MatrixDimension.AGES, MatrixDimension.YEARS,
        Population.NATIVES);
  }

  public MatrixDimension getValueWidth() {
    return valueWidth;
  }

  public void setValueWidth(MatrixDimension valWidth) {
    this.valueWidth = valWidth;
  }

  public MatrixDimension getValueHeight() {
    return valueHeight;
  }

  public void setValueHeight(MatrixDimension valHeight) {
    this.valueHeight = valHeight;
  }

  public boolean isGenerationDependent() {
    return generationDependent;
  }

  public void setGenerationDependent(boolean generationDependent) {
    this.generationDependent = generationDependent;
  }

  public int getID() {
    return id;
  }

  public final void setID(int paramID) {
    id = paramID;
  }

  public String getName() {
    return name;
  }

  public Integer getSortingIndex() {
    return sortingIndex;
  }

  public void setSortingIndex(Integer sortingIndex) {
    if (sortingIndex != null)
      this.sortingIndex = sortingIndex;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Population getPopulation() {
    return population;
  }

  public void setPopulation(Population population) {
    this.population = population;
  }

  @Override
  public String toString() {
    return name;
  }
}
