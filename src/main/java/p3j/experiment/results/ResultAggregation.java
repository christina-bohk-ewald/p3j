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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import p3j.misc.Misc;
import p3j.misc.math.Matrix2D;
import p3j.pppm.SubPopulation;
import p3j.pppm.SubPopulationModel;

// TODO: Auto-generated Javadoc
/**
 * Simple helper class to define the result aggregation.
 * 
 * Use only for a *single* export!
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class ResultAggregation {

  /** The number of generations. */
  private final int generations;

  /** The number of years. */
  private final int numOfYears;

  /** The sub-population model. */
  private final SubPopulationModel subPopulationModel;

  /**
   * Map to easily access selectors for different sub-populations. Map format:
   * Sub-Population => ( Output Variable => List[Generation_0, ...,
   * Generation_n] )
   */
  private final Map<ISubPopulationSelector, Map<IOutputVariableSelector, List<AbstractAggregationSelector>>> subPopSelectorMap;

  /** Stores sub-population selectors (their names are unique). */
  private final Map<SubPopulation, ISubPopulationSelector> subPopulationSelectors;

  // Several default selectors, for further usage:

  /**
   * All male sub-populations to be added for total population.
   */
  private final List<AbstractAggregationSelector> allMaleAdditions = new ArrayList<>();

  /**
   * All male sub-populations to be added for total population.
   */
  private final List<AbstractAggregationSelector> allFemaleAdditions = new ArrayList<>();

  /**
   * All male sub-populations to be subtracted for total population.
   */
  private final List<AbstractAggregationSelector> allMaleSubtractions = new ArrayList<>();

  /**
   * All female sub-populations to be subtracted for total population.
   */
  private final List<AbstractAggregationSelector> allFemaleSubtractions = new ArrayList<>();

  /** All sub-populations to be added for total population. */
  private final List<AbstractAggregationSelector> allTotalAdditions = new ArrayList<>();

  /** All sub-populations to be subtracted for total population. */
  private final List<AbstractAggregationSelector> allTotalSubtractions = new ArrayList<>();

  /**
   * Instantiates a new result aggregation helper.
   * 
   * @param numberOfGenerations
   *          the number of generations
   * @param numberOfYears
   *          the number of years
   * @param subPopModel
   *          the sub pop model
   */
  public ResultAggregation(int numberOfGenerations, int numberOfYears,
      SubPopulationModel subPopModel) {

    // Define general structure
    generations = numberOfGenerations;
    numOfYears = numberOfYears;
    subPopulationModel = subPopModel;

    // Create and manage sub-population selectors
    subPopulationSelectors = createSubPopSelectors();
    subPopSelectorMap = initSubPopSelectorMap();

    // Create lists of relevant sub-population selectors
    for (SubPopulation subPopulation : subPopulationModel.getSubPopulations()) {
      ISubPopulationSelector subPopSelector = subPopulationSelectors
          .get(subPopulation);
      if (subPopulation.isAdditive()) {
        allMaleAdditions.addAll(subPopSelectorMap.get(subPopSelector).get(
            END_X_M));
        allFemaleAdditions.addAll(subPopSelectorMap.get(subPopSelector).get(
            END_X_F));
        allTotalAdditions.addAll(getAllForSubPop(subPopSelector));
      } else {
        allMaleSubtractions.addAll(subPopSelectorMap.get(subPopSelector).get(
            END_X_M));
        allFemaleSubtractions.addAll(subPopSelectorMap.get(subPopSelector).get(
            END_X_F));
        allTotalSubtractions.addAll(getAllForSubPop(subPopSelector));
      }
    }
  }

  /**
   * Creates the sub-population selectors.
   * 
   * @return the map sub-population => sub-population selector
   */
  private Map<SubPopulation, ISubPopulationSelector> createSubPopSelectors() {
    Map<SubPopulation, ISubPopulationSelector> result = new HashMap<>();
    for (SubPopulation subPopulation : subPopulationModel.getSubPopulations()) {
      result.put(subPopulation,
          new SubPopulationSelector(subPopulation.getName()));
    }
    return Collections.unmodifiableMap(result);
  }

  /**
   * Initializes the sub-population selector map.
   * 
   * @return map of sub-population selectors
   * @see ResultAggregation#subPopSelectorMap
   */
  private Map<ISubPopulationSelector, Map<IOutputVariableSelector, List<AbstractAggregationSelector>>> initSubPopSelectorMap() {
    Map<ISubPopulationSelector, Map<IOutputVariableSelector, List<AbstractAggregationSelector>>> resultMap = new HashMap<>();
    for (SubPopulation subPopulation : subPopulationModel.getSubPopulations()) {

      ISubPopulationSelector subPopSelector = subPopulationSelectors
          .get(subPopulation);

      Map<IOutputVariableSelector, List<AbstractAggregationSelector>> outputMap = new HashMap<>();
      outputMap.put(END_X_M, new ArrayList<AbstractAggregationSelector>());
      outputMap.put(END_X_F, new ArrayList<AbstractAggregationSelector>());
      resultMap.put(subPopSelector, outputMap);

      if (subPopulation.isConsistingOfDescendantGenerations())
        for (int i = 0; i < generations; i++) {
          resultMap.get(subPopSelector).get(END_X_M)
              .add(new SumOverAgesSelector(END_X_M, subPopSelector, i));
          resultMap.get(subPopSelector).get(END_X_F)
              .add(new SumOverAgesSelector(END_X_F, subPopSelector, i));
        }
      else {
        resultMap.get(subPopSelector).get(END_X_M)
            .add(new SumOverAgesSelector(END_X_M, subPopSelector, -1));
        resultMap.get(subPopSelector).get(END_X_F)
            .add(new SumOverAgesSelector(END_X_F, subPopSelector, -1));
      }
    }
    return Collections.unmodifiableMap(resultMap);
  }

  /** The selector for male year-end population. */
  private static final IOutputVariableSelector END_X_M = new IOutputVariableSelector() {
    @Override
    public String getSuffix() {
      return "end_x_m";
    }

    @Override
    public Matrix2D select(BasicResults basicResults) {
      return basicResults.getEndXm();
    }
  };

  /** The selector for female year-end population. */
  private static final IOutputVariableSelector END_X_F = new IOutputVariableSelector() {
    @Override
    public String getSuffix() {
      return "end_x_f";
    }

    @Override
    public Matrix2D select(BasicResults basicResults) {
      return basicResults.getEndXf();
    }
  };

  /** The selector for male mean population. */
  private static final IOutputVariableSelector MEAN_X_M = new IOutputVariableSelector() {
    @Override
    public String getSuffix() {
      return "mean_x_m";
    }

    @Override
    public Matrix2D select(BasicResults basicResults) {
      return basicResults.getMeanXm();
    }
  };

  /** The selector for female mean population. */
  private static final IOutputVariableSelector MEAN_X_F = new IOutputVariableSelector() {
    @Override
    public String getSuffix() {
      return "mean_x_f";
    }

    @Override
    public Matrix2D select(BasicResults basicResults) {
      return basicResults.getMeanXf();
    }
  };

  /**
   * Generic selector for sub-populations.
   * 
   * Created on 29.07.2013
   * 
   * @author Christina Bohk
   * @author Roland Ewald
   */
  class SubPopulationSelector implements ISubPopulationSelector {

    /** The sub pop name. */
    private final String subPopName;

    /**
     * Instantiates a new sub population selector.
     * 
     * @param subPopulationName
     *          the sub population name
     */
    SubPopulationSelector(String subPopulationName) {
      subPopName = subPopulationName;
    }

    @Override
    public BasicResults select(ResultsOfTrial resultsOfTrial, int generation) {
      // TODO: this is inefficient; improve if necessary
      for (BasicResults result : resultsOfTrial.getSubPopulationResults())
        // The (gen == -1 & resu.gen==0) check accounts for an incompatibility
        // between results and aggregation selectors: 0 is the default
        // generation index for the former, but the latter use -1 if there is no
        // generation
        if (result.getSubPopName().equals(subPopName)
            && (result.getGeneration() == generation || generation == -1
                && result.getGeneration() == 0))
          return result;
      throw new IllegalArgumentException(
          "Could not find result for generation '" + generation
              + "' of sub-population '" + subPopName + "'");
    }

    @Override
    public String getPrefix() {
      return subPopName;
    }

  }

  /** The array of result-type selectors. */
  private static final IOutputVariableSelector[] RESULT_TYPES = { END_X_M,
      END_X_F, MEAN_X_M, MEAN_X_F };

  /**
   * Gets the selectors to be used for report generation.
   * 
   * @return the selectors
   */
  public IAggregationSelector[] getSelectorsForReport() {
    List<IAggregationSelector> selectors = defineSubPopMergingSelectors();

    for (SubPopulation subPopulation : subPopulationModel.getSubPopulations()) {
      ISubPopulationSelector subPopSelector = subPopulationSelectors
          .get(subPopulation);
      if (subPopulation.isConsistingOfDescendantGenerations())
        for (int i = 0; i < generations; i++)
          selectors.addAll(createSelectorsPerGeneration(subPopSelector, i));
      else
        selectors.addAll(createSelectorsPerGeneration(subPopSelector, -1));
    }

    return selectors.toArray(new IAggregationSelector[0]);
  }

  /**
   * Creates the selectors for a sub-population selector and a specific
   * generation.
   * 
   * @param subPopSelector
   *          the sub pop selector
   * @param generation
   *          the generation, use -1 for sub-populations without descendants
   * @return list of generation-specific selectors
   */
  private List<IAggregationSelector> createSelectorsPerGeneration(
      ISubPopulationSelector subPopSelector, int generation) {
    List<IAggregationSelector> result = new ArrayList<>();
    for (IOutputVariableSelector resultType : new IOutputVariableSelector[] {
        END_X_M, END_X_F }) {
      result.add(new ChooseAgesForSingleYearSelector(resultType,
          subPopSelector, generation, 0));
      result.add(new ChooseAgesForSingleYearSelector(resultType,
          subPopSelector, generation, numOfYears - 1));
    }
    for (IOutputVariableSelector resultType : RESULT_TYPES) {
      result
          .add(new SumOverAgesSelector(resultType, subPopSelector, generation));
    }
    return result;
  }

  /**
   * Gets the selectors for aggregated data export.
   * 
   * @return the selectors for aggregated data export
   */
  public IAggregationSelector[] getSelectorsForAggregatedDataExport() {
    List<IAggregationSelector> selectors = new ArrayList<IAggregationSelector>();

    selectors.add(new MergeSubPopOldAgeDependencyRatioSelector(
        allTotalAdditions, allTotalSubtractions, "oadr_total_end_mf"));

    for (int i = 0; i < numOfYears; i++) {
      selectors.add(new YearlyAgeTrialMatrixSelector(allMaleAdditions,
          allMaleSubtractions, "age_trial_matrix_m_for_year_", i));
      selectors.add(new YearlyAgeTrialMatrixSelector(allFemaleAdditions,
          allFemaleSubtractions, "age_trial_matrix_f_for_year_", i));
    }

    return selectors.toArray(new IAggregationSelector[0]);
  }

  /**
   * Generate selectors that merge certain sub-populations (e.g. all immigrants,
   * all females, all descendant immigrants, both males and females, etc.).
   * 
   * @return the list of defined selectors
   */
  private List<IAggregationSelector> defineSubPopMergingSelectors() {

    // Total population for natives (m+f)
    List<IAggregationSelector> selectors = new ArrayList<IAggregationSelector>();

    selectors.addAll(defineMFSelectorsForAllGenerations());

    // Specifying the total male/female/male+female population
    selectors.addAll(defineTotalMaleFemaleCombinedSelectors());

    // Specifying emigrant/immigrant total population
    selectors
        .addAll(defineSelectorsForSubPopsWithGenerations(subPopSelectorMap));

    return selectors;
  }

  /**
   * Specify selectors for total migrant populations.
   * 
   * @param subPopSelectorMap
   *          the sub-population selector map
   * @return the list
   */
  @SuppressWarnings("unchecked")
  private List<IAggregationSelector> defineSelectorsForSubPopsWithGenerations(
      Map<ISubPopulationSelector, Map<IOutputVariableSelector, List<AbstractAggregationSelector>>> subPopSelectorMap) {

    List<IAggregationSelector> selectors = new ArrayList<IAggregationSelector>();

    for (SubPopulation subPopulation : subPopulationModel.getSubPopulations()) {

      if (!subPopulation.isConsistingOfDescendantGenerations())
        continue;

      ISubPopulationSelector subPopSelector = subPopulationSelectors
          .get(subPopulation);
      String prefix = subPopSelector.getPrefix();

      // Add merging selectors
      selectors.addAll(defineSingleYearSelectors(
          new MergeSubPopSumOverAgesSelector(subPopSelectorMap.get(
              subPopSelector).get(END_X_M), prefix + "_end_x_m"), numOfYears));
      selectors.addAll(defineSingleYearSelectors(
          new MergeSubPopSumOverAgesSelector(subPopSelectorMap.get(
              subPopSelector).get(END_X_F), prefix + "_end_x_f"), numOfYears));
      selectors.add(new MergeSubPopSumOverAgesSelector(
          getAllForSubPop(subPopSelector), prefix + "_end_x_mf"));

      // Define selectors for male and female descendants
      List<AbstractAggregationSelector> allMaleDescendants = new ArrayList<>(
          subPopSelectorMap.get(subPopSelector).get(END_X_M));
      allMaleDescendants.remove(0);
      List<AbstractAggregationSelector> allFemaleDescendants = new ArrayList<>(
          subPopSelectorMap.get(subPopSelector).get(END_X_F));
      allFemaleDescendants.remove(0);

      // Add merging selectors for descendants
      selectors.addAll(defineSingleYearSelectors(
          new MergeSubPopSumOverAgesSelector(allMaleDescendants, null, prefix
              + "_desc_end_x_m"), numOfYears));
      selectors.addAll(defineSingleYearSelectors(
          new MergeSubPopSumOverAgesSelector(allFemaleDescendants, null, prefix
              + "_desc_end_x_f"), numOfYears));
      selectors.add(new MergeSubPopSumOverAgesSelector(Misc
          .<AbstractAggregationSelector> mergeList(allMaleDescendants,
              allFemaleDescendants), null, prefix + "_desc_end_x_mf"));
    }

    return selectors;
  }

  /**
   * Defines selectors that focus on total male/female population.
   * 
   * @return list of total male/female population selectors
   */
  private List<IAggregationSelector> defineTotalMaleFemaleCombinedSelectors() {
    List<IAggregationSelector> selectors = new ArrayList<IAggregationSelector>();
    selectors.addAll(defineSingleYearSelectors(
        new MergeSubPopSumOverAgesSelector(allMaleAdditions,
            allMaleSubtractions, "total_end_x_m"), numOfYears));
    selectors.addAll(defineSingleYearSelectors(
        new MergeSubPopSumOverAgesSelector(allFemaleAdditions,
            allFemaleSubtractions, "total_end_x_f"), numOfYears));
    selectors.addAll(defineSingleYearSelectors(
        new MergeSubPopSumOverAgesSelector(allTotalAdditions,
            allTotalSubtractions, "total_end_x_mf"), numOfYears));
    return selectors;
  }

  /**
   * Defines male+female selectors for all single migrant generations.
   * 
   * @return list of all end_x_mf selectors, for all generations and migrant
   *         populations
   */
  private List<IAggregationSelector> defineMFSelectorsForAllGenerations() {
    List<IAggregationSelector> selectors = new ArrayList<IAggregationSelector>();

    for (SubPopulation subPopulation : subPopulationModel.getSubPopulations()) {
      ISubPopulationSelector subPopSelector = subPopulationSelectors
          .get(subPopulation);

      if (subPopulation.isConsistingOfDescendantGenerations()) {
        for (int i = 0; i < generations; i++) {
          selectors.add(new MergeSubPopSumOverAgesSelector(
              new AbstractAggregationSelector[] {
                  subPopSelectorMap.get(subPopSelector).get(END_X_M).get(i),
                  subPopSelectorMap.get(subPopSelector).get(END_X_F).get(i) },
              new AbstractAggregationSelector[0], subPopSelector.getPrefix()
                  + "_gen_" + i + "_end_x_mf"));
        }
      } else {
        selectors.add(new MergeSubPopSumOverAgesSelector(
            new AbstractAggregationSelector[] {
                subPopSelectorMap.get(subPopSelector).get(END_X_M).get(0),
                subPopSelectorMap.get(subPopSelector).get(END_X_F).get(0) },
            subPopSelector.getPrefix() + "_end_x_mf"));
      }
    }
    return selectors;
  }

  /**
   * Adds the given selector and defines two selectors for first/last year for
   * it.
   * 
   * @param mergeSubPopSumOverAgesSelector
   *          the merge sub pop sum over ages selector
   * @param numOfYears
   *          the number of years
   * @return the list
   */
  private static List<IAggregationSelector> defineSingleYearSelectors(
      MergeSubPopSumOverAgesSelector mergeSubPopSumOverAgesSelector,
      int numOfYears) {
    List<IAggregationSelector> selectors = new ArrayList<IAggregationSelector>();
    selectors.add(mergeSubPopSumOverAgesSelector);

    // Define selector for first year
    selectors.add(new MergeSubPopChooseAgesSingleYearSelector(
        mergeSubPopSumOverAgesSelector, 0));

    // Define selector for last year
    selectors.add(new MergeSubPopChooseAgesSingleYearSelector(
        mergeSubPopSumOverAgesSelector, numOfYears - 1));

    return selectors;
  }

  /**
   * Gets all selectors for a sub-population selector.
   * 
   * @param subPopSelector
   *          the sub-population selector
   * 
   * @return all selectors for the specified sub-population
   */
  private List<AbstractAggregationSelector> getAllForSubPop(
      ISubPopulationSelector subPopSelector) {
    List<AbstractAggregationSelector> result = new ArrayList<AbstractAggregationSelector>();
    for (List<AbstractAggregationSelector> selectorList : subPopSelectorMap
        .get(subPopSelector).values()) {
      result.addAll(selectorList);
    }
    return result;
  }

}
