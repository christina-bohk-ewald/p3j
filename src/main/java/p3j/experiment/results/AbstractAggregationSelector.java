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

import java.util.List;

import p3j.misc.math.Matrix2D;

/**
 * An abstract class containing functionality all aggregation selectors may
 * share. The basic idea here is that, for the given structure in
 * {@link ResultsOfTrial}, aggregation is essentially a three-step process:
 * 
 * 1) Select the sub-population and generation of interest: this means to select
 * a {@link BasicResults} object from the lists in {@link ResultsOfTrial}. This
 * is handled by an {@link ISubPopulationSelector}.
 * 
 * 2) Select the variable of interest from the {@link BasicResults} object, i.e.
 * which aspect of the given sub-population's results are of interest (e.g. the
 * year-end numbers of males). The result of this selection is a matrix. This is
 * handled by an {@link IOutputVariableSelector}.
 * 
 * 3) The actual aggregation of the matrices to some result; this is the only
 * aspect that cannot be re-used and has to be implemented for any kind of
 * aggregation. This is handled by a sub-class of
 * {@link AbstractAggregationSelector}.
 * 
 * @see BasicResults
 * @see ResultsOfTrial
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public abstract class AbstractAggregationSelector implements
    IAggregationSelector {

  /** The basic results selector. */
  private final IOutputVariableSelector outputVariableSelector;

  /** The trial results selector. */
  private final ISubPopulationSelector subPopulationSelector;

  /** The generation. */
  private final int generation;

  /** The aggregated data. */
  protected double[][] aggregation; // NOSONAR

  /**
   * Instantiates a new abstract aggregation selector.
   * 
   * @param outputVarSelector
   *          the results selector
   * @param subPopSelector
   *          the trial selector
   * @param generationForSelection
   *          the generation for selection
   */
  public AbstractAggregationSelector(IOutputVariableSelector outputVarSelector,
      ISubPopulationSelector subPopSelector, int generationForSelection) {
    outputVariableSelector = outputVarSelector;
    subPopulationSelector = subPopSelector;
    generation = generationForSelection;
  }

  /**
   * Select the result of interest.
   * 
   * @param resultsOfTrial
   *          the results of trial
   * 
   * @return the matrix2 d
   */
  protected Matrix2D select(ResultsOfTrial resultsOfTrial) {
    return outputVariableSelector.select(subPopulationSelector.select(
        resultsOfTrial, generation));
  }

  /**
   * Gets the file name.
   * 
   * @return the file name
   */
  protected String getFileName() {
    return subPopulationSelector.getPrefix()
        + (generation == -1 ? "" : "_gen_" + generation) + "_"
        + outputVariableSelector.getSuffix();
  }

  /**
   * Reorders results according to list of indices.
   * 
   * @param indexOrdering
   *          the index ordering
   * @param orderedResults
   *          the ordered results
   * 
   * @return the reordered results
   */
  protected double[][] reorderResults(List<Integer> indexOrdering,
      double[][] orderedResults) {
    double[][] results = new double[indexOrdering.size()][orderedResults[0].length];
    for (int i = 0; i < indexOrdering.size(); i++) {
      results[i] = orderedResults[indexOrdering.get(i)];
    }
    return results;
  }

  /**
   * Cuts off unused lines in data. This is necessary for any conditional
   * results, i.e. where not all trial results are considered.
   * 
   * @param aggregatedData
   *          the aggregation
   * @param numOfLines
   *          the size
   * 
   * @return the trimmed results
   */
  protected double[][] cutOffUnused(double[][] aggregatedData, int numOfLines) {
    double[][] results = new double[numOfLines][aggregatedData[0].length];
    System.arraycopy(aggregatedData, 0, results, 0, numOfLines);
    return results;
  }

  /**
   * Gets a copy of an array.
   * 
   * @param selArray
   *          the array of selectors
   * 
   * @return the copy of the array
   */
  protected static AbstractAggregationSelector[] getCopy(
      AbstractAggregationSelector[] selArray) {
    if (selArray == null) {
      return new AbstractAggregationSelector[0];
    }
    AbstractAggregationSelector[] copy = new AbstractAggregationSelector[selArray.length];
    System.arraycopy(selArray, 0, copy, 0, selArray.length);
    return copy;
  }

  /**
   * Sum to array by element-wise adding all array in the first list to one
   * another, and then subtracting all arrays in the second list.
   * 
   * @param additionArrays
   *          the addition arrays
   * @param subtractionArrays
   *          the subtraction arrays
   * 
   * @return the double[]
   */
  protected double[] sumPerElement(List<double[]> additionArrays,
      List<double[]> subtractionArrays) {
    double[] sum = new double[aggregation[0].length];
    for (double[] additionData : additionArrays) {
      for (int j = 0; j < sum.length; j++) {
        sum[j] += additionData[j];
      }
    }
    for (double[] subtrData : subtractionArrays) {
      for (int j = 0; j < sum.length; j++) {
        sum[j] -= subtrData[j];
      }
    }
    return sum;
  }

}

/**
 * Selects a certain element (variable) from {@link BasicResults}. Used by
 * {@link AbstractAggregationSelector}.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
interface IOutputVariableSelector {

  /**
   * Selects a certain element of the basic results.
   * 
   * @param basicResults
   *          the basic results
   * 
   * @return the selected data (as a matrix)
   */
  Matrix2D select(BasicResults basicResults);

  /**
   * Gets the suffix describing the kind of selection.
   * 
   * @return the suffix
   */
  String getSuffix();
}

/**
 * Selects a {@link BasicResults} instance from a {@link ResultsOfTrial}
 * instance. User by {@link AbstractAggregationSelector}.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
interface ISubPopulationSelector {

  /**
   * Select a {@link BasicResults} from the {@link ResultsOfTrial}, given a
   * certain generation number.
   * 
   * @param resultsOfTrial
   *          the results of trial
   * @param generation
   *          the generation
   * 
   * @return the basic results
   */
  BasicResults select(ResultsOfTrial resultsOfTrial, int generation);

  /**
   * Gets the prefix describing the kind of selection.
   * 
   * @return the prefix
   */
  String getPrefix();
}