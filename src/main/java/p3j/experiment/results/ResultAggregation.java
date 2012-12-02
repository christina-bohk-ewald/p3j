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

  /**
   * Map to easily access selectors for different subpopulations. Map format:
   * SUB-POPULATION (immigrants || emigrants) => ( [END_X_M || END_X_F] =>
   * List[Generation_0, ..., Generation_n] )
   */
  private final Map<ISubPopulationSelector, Map<IOutputVariableSelector, List<AbstractAggregationSelector>>> subPopSelectorMap;

  // Several default selectors, for further usage:

  /** End population of male natives. */
  private final AbstractAggregationSelector nativesEndXm;

  /** End population of female natives. */
  private final AbstractAggregationSelector nativesEndXf;

  /**
   * All male sub-populations to be added for total population (natives +
   * immigrants).
   */
  private final List<AbstractAggregationSelector> allMaleAdditions;

  /**
   * All male sub-populations to be added for total population (natives +
   * immigrants).
   */
  private final List<AbstractAggregationSelector> allFemaleAdditions;

  /**
   * All male sub-populations to be subtracted for total population (emigrants).
   */
  private final List<AbstractAggregationSelector> allMaleSubtractions;

  /**
   * All female sub-populations to be subtracted for total population
   * (emigrants).
   */
  private final List<AbstractAggregationSelector> allFemaleSubtractions;

  /** All sub-populations to be added for total population. */
  private final List<AbstractAggregationSelector> allTotalAdditions;

  /** All sub-populations to be subtracted for total population. */
  private final List<AbstractAggregationSelector> allTotalSubtractions;

  /**
   * Instantiates a new result aggregation helper.
   * 
   * @param numberOfGenerations
   *          the number of generations
   * @param numberOfYears
   *          the number of years
   */
  public ResultAggregation(int numberOfGenerations, int numberOfYears) {

    // Define general structure
    generations = numberOfGenerations;
    numOfYears = numberOfYears;
    subPopSelectorMap = initSubPopSelectorMap();

    // Define often-used selectors
    nativesEndXm = new SumOverAgesSelector(END_X_M, NATIVES, -1);
    nativesEndXf = new SumOverAgesSelector(END_X_F, NATIVES, -1);

    // Extracting those selector lists from the above that are required to
    // define further selectors on top
    allMaleAdditions = new ArrayList<AbstractAggregationSelector>(
        subPopSelectorMap.get(IMMIGRANTS).get(END_X_M));
    allMaleAdditions.add(0, nativesEndXm);
    allFemaleAdditions = new ArrayList<AbstractAggregationSelector>(
        subPopSelectorMap.get(IMMIGRANTS).get(END_X_F));
    allFemaleAdditions.add(0, nativesEndXf);
    allMaleSubtractions = new ArrayList<AbstractAggregationSelector>(
        subPopSelectorMap.get(EMIGRANTS).get(END_X_M));
    allFemaleSubtractions = new ArrayList<AbstractAggregationSelector>(
        subPopSelectorMap.get(EMIGRANTS).get(END_X_F));

    // Note: total population = natives + immigrants - emigrants
    allTotalAdditions = getAllForSubPop(IMMIGRANTS);
    allTotalAdditions.add(0, nativesEndXm);
    allTotalAdditions.add(0, nativesEndXf);
    allTotalSubtractions = getAllForSubPop(EMIGRANTS);
  }

  /**
   * Initializes the sub-population selector map.
   * 
   * Map format:
   * 
   * [immigrants || emigrants] => ( [END_X_M || END_X_F] => List[Generation_0,
   * ..., Generation_n] )
   * 
   * @return map of sub-population selectors
   */
  private Map<ISubPopulationSelector, Map<IOutputVariableSelector, List<AbstractAggregationSelector>>> initSubPopSelectorMap() {
    Map<ISubPopulationSelector, Map<IOutputVariableSelector, List<AbstractAggregationSelector>>> resultMap = new HashMap<ISubPopulationSelector, Map<IOutputVariableSelector, List<AbstractAggregationSelector>>>();
    for (ISubPopulationSelector trialSelector : GEN_POPULATIONS) {
      Map<IOutputVariableSelector, List<AbstractAggregationSelector>> outputMap = new HashMap<IOutputVariableSelector, List<AbstractAggregationSelector>>();
      outputMap.put(END_X_M, new ArrayList<AbstractAggregationSelector>());
      outputMap.put(END_X_F, new ArrayList<AbstractAggregationSelector>());
      resultMap.put(trialSelector, outputMap);
      for (int i = 0; i < generations; i++) {
        SumOverAgesSelector subPopEndXm = new SumOverAgesSelector(END_X_M,
            trialSelector, i);
        SumOverAgesSelector subPopEndXf = new SumOverAgesSelector(END_X_F,
            trialSelector, i);
        resultMap.get(trialSelector).get(END_X_M).add(subPopEndXm);
        resultMap.get(trialSelector).get(END_X_F).add(subPopEndXf);
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

  /** The selector for natives. */
  private static final ISubPopulationSelector NATIVES = new ISubPopulationSelector() {
    @Override
    public String getPrefix() {
      return "natives";
    }

    @Override
    public BasicResults select(ResultsOfTrial results, int generation) {
      return null; // FIXME: results.getNativesResults();
    }
  };

  /** The selector for immigrants. */
  private static final ISubPopulationSelector IMMIGRANTS = new ISubPopulationSelector() {
    @Override
    public String getPrefix() {
      return "immigrants";
    }

    @Override
    public BasicResults select(ResultsOfTrial results, int generation) {
      return null; // FIXME: results.getImmigrantResults().get(generation);
    }
  };

  /** The selector for emigrants. */
  private static final ISubPopulationSelector EMIGRANTS = new ISubPopulationSelector() {
    @Override
    public String getPrefix() {
      return "emigrants";
    }

    @Override
    public BasicResults select(ResultsOfTrial results, int generation) {
      return null; // FIXME: results.getEmigrantResults().get(generation);
    }
  };

  /** The array of result-type selectors. */
  private static final IOutputVariableSelector[] RESULT_TYPES = { END_X_M,
      END_X_F, MEAN_X_M, MEAN_X_F };

  /** The array of sub-population selectors (without natives). */
  private static final ISubPopulationSelector[] GEN_POPULATIONS = { IMMIGRANTS,
      EMIGRANTS };

  /**
   * Gets the selectors to be used for report generation.
   * 
   * @param generations
   *          the number of generations
   * @param numOfYears
   *          the number of years to be predicted
   * 
   * @return the selectors
   */
  public IAggregationSelector[] getSelectorsForReport() {

    List<IAggregationSelector> selectors = defineSubPopMergingSelectors();

    // Add selectors for natives
    for (IOutputVariableSelector resultType : new IOutputVariableSelector[] {
        END_X_M, END_X_F }) {
      selectors.add(new ChooseAgesForSingleYearSelector(resultType, NATIVES,
          -1, 0));
      selectors.add(new ChooseAgesForSingleYearSelector(resultType, NATIVES,
          -1, numOfYears - 1));
    }
    for (IOutputVariableSelector resultType : RESULT_TYPES) {
      selectors.add(new SumOverAgesSelector(resultType, NATIVES, -1));
    }

    // Add selectors for all sub-populations and their descendants
    for (ISubPopulationSelector trialSelector : GEN_POPULATIONS) {
      for (int i = 0; i < generations; i++) {
        for (IOutputVariableSelector resultType : new IOutputVariableSelector[] {
            END_X_M, END_X_F }) {
          selectors.add(new ChooseAgesForSingleYearSelector(resultType,
              trialSelector, i, 0));
          selectors.add(new ChooseAgesForSingleYearSelector(resultType,
              trialSelector, i, numOfYears - 1));
        }
        for (IOutputVariableSelector resultType : RESULT_TYPES) {
          selectors.add(new SumOverAgesSelector(resultType, trialSelector, i));
        }
      }
    }

    return selectors.toArray(new IAggregationSelector[0]);
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
    selectors.add(new MergeSubPopSumOverAgesSelector(
        new AbstractAggregationSelector[] { nativesEndXm, nativesEndXf },
        "natives_end_x_mf"));

    selectors.addAll(defineMFSelectorsForAllMigrantGenerations());

    // Specifying the total male/female/male+female population
    selectors.addAll(defineTotalMaleFemaleCombinedSelectors());

    // Specifying emigrant/immigrant total population
    selectors.addAll(defineMigrantSelectors(subPopSelectorMap));

    return selectors;
  }

  /**
   * Specify selectors for total migrant populations.
   * 
   * @param aggrSelectors
   *          the aggregation selectors
   * @param subPopSelectorMap
   *          the sub-population selector map
   */
  @SuppressWarnings("unchecked")
  private List<IAggregationSelector> defineMigrantSelectors(
      Map<ISubPopulationSelector, Map<IOutputVariableSelector, List<AbstractAggregationSelector>>> subPopSelectorMap) {

    List<IAggregationSelector> selectors = new ArrayList<IAggregationSelector>();

    // Add merging selectors for immigrants
    selectors.addAll(defineSingleYearSelectors(
        new MergeSubPopSumOverAgesSelector(subPopSelectorMap.get(IMMIGRANTS)
            .get(END_X_M), "immigrants_end_x_m"), numOfYears));
    selectors.addAll(defineSingleYearSelectors(
        new MergeSubPopSumOverAgesSelector(subPopSelectorMap.get(IMMIGRANTS)
            .get(END_X_F), "immigrants_end_x_f"), numOfYears));
    selectors.add(new MergeSubPopSumOverAgesSelector(
        getAllForSubPop(IMMIGRANTS), "immigrants_end_x_mf"));

    // Add merging selectors for emigrants
    selectors.addAll(defineSingleYearSelectors(
        new MergeSubPopSumOverAgesSelector(subPopSelectorMap.get(EMIGRANTS)
            .get(END_X_M), null, "emigrants_end_x_m"), numOfYears));
    selectors.addAll(defineSingleYearSelectors(
        new MergeSubPopSumOverAgesSelector(subPopSelectorMap.get(EMIGRANTS)
            .get(END_X_F), null, "emigrants_end_x_f"), numOfYears));
    selectors.add(new MergeSubPopSumOverAgesSelector(
        getAllForSubPop(EMIGRANTS), null, "emigrants_end_x_mf"));

    // Define selectors for descendant immigrant/emigrants
    List<AbstractAggregationSelector> allMaleDescImmigrants = new ArrayList<AbstractAggregationSelector>(
        subPopSelectorMap.get(IMMIGRANTS).get(END_X_M));
    allMaleDescImmigrants.remove(0);
    List<AbstractAggregationSelector> allFemaleDescImmigrants = new ArrayList<AbstractAggregationSelector>(
        subPopSelectorMap.get(IMMIGRANTS).get(END_X_F));
    allFemaleDescImmigrants.remove(0);
    List<AbstractAggregationSelector> allMaleDescEmigrants = new ArrayList<AbstractAggregationSelector>(
        subPopSelectorMap.get(EMIGRANTS).get(END_X_M));
    allMaleDescEmigrants.remove(0);
    List<AbstractAggregationSelector> allFemaleDescEmigrants = new ArrayList<AbstractAggregationSelector>(
        subPopSelectorMap.get(EMIGRANTS).get(END_X_F));
    allFemaleDescEmigrants.remove(0);

    // Add merging selectors for descendant immigrants
    selectors.addAll(defineSingleYearSelectors(
        new MergeSubPopSumOverAgesSelector(allMaleDescImmigrants, null,
            "immigrants_desc_end_x_m"), numOfYears));
    selectors.addAll(defineSingleYearSelectors(
        new MergeSubPopSumOverAgesSelector(allFemaleDescImmigrants, null,
            "immigrants_desc_end_x_f"), numOfYears));
    selectors.add(new MergeSubPopSumOverAgesSelector(Misc
        .<AbstractAggregationSelector> mergeList(allMaleDescImmigrants,
            allFemaleDescImmigrants), null, "immigrants_desc_end_x_mf"));

    // Add merging selectors for descendant emigrants
    selectors.addAll(defineSingleYearSelectors(
        new MergeSubPopSumOverAgesSelector(allMaleDescEmigrants, null,
            "emigrants_desc_end_x_m"), numOfYears));
    selectors.addAll(defineSingleYearSelectors(
        new MergeSubPopSumOverAgesSelector(allFemaleDescEmigrants, null,
            "emigrants_desc_end_x_f"), numOfYears));
    selectors.add(new MergeSubPopSumOverAgesSelector(Misc
        .<AbstractAggregationSelector> mergeList(allMaleDescEmigrants,
            allFemaleDescEmigrants), null, "emigrants_desc_end_x_mf"));

    return selectors;
  }

  /**
   * Defines selectors that focus on total male/female population.
   * 
   * @return list of total male/female population selectors
   */
  private List<IAggregationSelector> defineTotalMaleFemaleCombinedSelectors() { // FIXME
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
  private List<IAggregationSelector> defineMFSelectorsForAllMigrantGenerations() {
    List<IAggregationSelector> selectors = new ArrayList<IAggregationSelector>();
    for (ISubPopulationSelector trialSelector : GEN_POPULATIONS) {
      for (int i = 0; i < generations; i++) {
        selectors.add(new MergeSubPopSumOverAgesSelector(
            new AbstractAggregationSelector[] {
                subPopSelectorMap.get(trialSelector).get(END_X_M).get(i),
                subPopSelectorMap.get(trialSelector).get(END_X_F).get(i) },
            new AbstractAggregationSelector[0], trialSelector.getPrefix()
                + "_gen_" + i + "_end_x_mf"));
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
   * Gets the all for sub pop.
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
