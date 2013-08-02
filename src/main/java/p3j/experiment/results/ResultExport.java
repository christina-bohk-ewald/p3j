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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.core.util.misc.Triple;

import p3j.database.DatabaseFactory;
import p3j.database.IProjectionResultsIterator;
import p3j.experiment.results.filters.IResultFilter;
import p3j.gui.P3J;
import p3j.misc.IProgressObserver;
import p3j.misc.gui.GUI;
import p3j.misc.math.Matrix2D;
import p3j.pppm.ProjectionModel;
import p3j.pppm.SubPopulation;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Class responsible for exporting results. There are three modes:
 * 
 * Create report: aggregates all data to be displayed in the result report.
 * 
 * Export aggregated data: exports various data aggregations, does not copy or
 * generate report-related files.
 * 
 * Export data: exports all raw data, to be analyzed with other tools.
 * 
 * TODO: Remove hard-coded density-plot years here and in plotting.R!
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class ResultExport {

  /** The name of the R library required for report generation. */
  public static final String R_LIBRARY = "plotting.R";

  /** The template directory. */
  public static final String TEMPLATE_DIRECTORY = "report_template";

  /** The buffer size for copying the plotting library. */
  private static final int COPY_BUFFER_SIZE = 1024;

  /** The result filter to be used. */
  private final IResultFilter resultFilter;

  /** The year offsets to consider for plotting probability densities. */
  private final int[] yearOffsetToConsider;

  /** The number of years to be projected. */
  private final int numOfYears;

  /** The number of trials. */
  private final int numOfTrials;

  /** The target directory. */
  private final File targetDir;

  /** Reference to the projection of which the results shall be exported. */
  private final ProjectionModel projection;

  /**
   * The current quantiles to be used. TODO: This should be parameterisable via
   * the UI.
   */
  static final double[] QUANTILES = new double[] { 0, .025, .05, .1, .5, .9,
      0.95, 0.975, 1 };

  /** The number of sub-plots to be put in a density plot. */
  static final int DENSITY_PLOT_NUM_SUBPLOTS = 9;

  /** The offset between jump-off year and first year to be plotted. */
  static final int DENSITY_PLOT_FIRST_OFFSET = 3;

  /** The regular offset between years to be plotted. */
  static final int DENSITY_PLOT_REGULAR_OFFSET = 5;

  /**
   * Instantiates a new result export.
   * 
   * @param projectionModel
   *          the projection model
   * @param targetDirectory
   *          the target directory
   * @param resultFilter
   *          the result filter to be used
   */
  public ResultExport(ProjectionModel projectionModel, File targetDirectory,
      IResultFilter resultFilter) {

    projection = projectionModel;
    targetDir = targetDirectory;
    this.resultFilter = resultFilter;

    // Check whether target directory exists
    if (!targetDir.exists() && !targetDir.mkdir()) {
      throw new IllegalArgumentException("Could not create directory: "
          + targetDir.getAbsolutePath());
    }

    // Consider each year for aggregation
    yearOffsetToConsider = new int[projectionModel.getYears() - 1];
    for (int i = 0; i < yearOffsetToConsider.length; i++) {
      yearOffsetToConsider[i] = 1;
    }

    // Store the number overall number of years and trials
    numOfYears = projectionModel.getYears();
    numOfTrials = DatabaseFactory.getDatabaseSingleton()
        .getAllResults(projection).size();
  }

  /**
   * Exports all results.
   * 
   * @throws IOException
   *           if export fails
   */
  public void exportAllResults() throws IOException {
    File dataDirectory = initializeSubDirectory("data");
    exportData(dataDirectory);
    GUI.printMessage(P3J.getInstance(), "Results Export Finished",
        "Results export is finished.");
  }

  /**
   * Exports aggregated data.
   * 
   * @param progress
   *          the progress observer
   * @throws IOException
   *           if data storage failed
   */
  public void exportAggregatedResults(IProgressObserver progress)
      throws IOException {
    File aggregatedDirectory = initializeSubDirectory("aggregated_data");
    aggregateData(aggregatedDirectory,
        (new ResultAggregation(projection.getGenerations(), numOfYears,
            projection.getSubPopulationModel()))
            .getSelectorsForAggregatedDataExport(), progress);
    if (progress.isCancelled())
      return;
    GUI.printMessage(P3J.getInstance(), "Aggregated Results Export Finished",
        "Aggregated results export is finished.");
  }

  /**
   * Creates result report displaying aggregated results.
   * 
   * @param progress
   *          the progress observation mechanism
   * @throws IOException
   *           if aggregation of data fails
   * @throws TemplateException
   *           if processing of report template fails
   */
  public void createResultReport(IProgressObserver progress)
      throws IOException, TemplateException {
    progress.addWaypoints(5);
    progress.incrementProgress("Initializing sub-directory...");
    File aggregatedDirectory = initializeSubDirectory("aggregated");
    progress.incrementProgress("Retrieving data selectors...");
    IAggregationSelector[] selectors = (new ResultAggregation(
        projection.getGenerations(), numOfYears,
        projection.getSubPopulationModel())).getSelectorsForReport();
    progress.incrementProgress("Data aggregation...");
    Map<String, Object> aggregationInfoMap = aggregateData(aggregatedDirectory,
        selectors, progress);
    if (aggregationInfoMap.isEmpty()) {
      GUI.printMessage(P3J.getInstance(), "No results to report",
          "No results could be reported, as none matched you filter.");
      return;
    }
    if (progress.isCancelled())
      return;
    progress.incrementProgress("Coppy plotting library...");
    copyPlottingLib();
    progress.incrementProgress("Creating Sweave file...");
    createSweaveFile(aggregationInfoMap);
    GUI.printMessage(
        P3J.getInstance(),
        "Report Generation Finished",
        "Result report generation is finished. Now run \"Sweave('report.Rtex')\" in R, then use LaTeX to process the 'report.tex' file.'");
  }

  /**
   * Stores assumption encoding.
   * 
   * @param projection
   *          the projection
   * @param targetDir
   *          the target directory
   * 
   * @return the corresponding parameter assumption encoder
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private ParameterAssumptionEncoder storeAssumptionEncoding(
      ProjectionModel projection, File targetDir) throws IOException {
    ParameterAssumptionEncoder assumptionEncoder = new ParameterAssumptionEncoder(
        projection);
    assumptionEncoder.writeMappingSummary(targetDir);
    return assumptionEncoder;
  }

  /**
   * Aggregate data.
   * 
   * @param destinationDir
   *          the destination directory
   * @param selectors
   *          the selectors
   * @param progress
   *          the progress observer
   * @return a map containing the data to parse into the report template
   * @throws IOException
   *           if data storage failed
   */
  private Map<String, Object> aggregateData(File destinationDir,
      IAggregationSelector[] selectors, IProgressObserver progress)
      throws IOException {

    // Create assumption encoder
    ParameterAssumptionEncoder assumptionEncoder = storeAssumptionEncoding(
        projection, destinationDir);
    Map<String, Object> results = new HashMap<String, Object>();

    // TODO: Implement multi-threaded report-generation.
    // Each selector should have its own thread.

    initializeSelectors(projection, selectors);
    List<Triple<Integer, Double, int[]>> trialAssumptions = analyzeResults(
        projection, selectors, assumptionEncoder, progress);
    if (progress.isCancelled() || trialAssumptions.isEmpty())
      return results;

    storeData(destinationDir, selectors, trialAssumptions);

    results.put(
        "densityYears",
        createDensityYearList(projection.getJumpOffYear(),
            DENSITY_PLOT_FIRST_OFFSET, DENSITY_PLOT_REGULAR_OFFSET));
    results.put("finalYearIndex", numOfYears - 1);
    results.put("allYears",
        createFullYearList(projection.getJumpOffYear(), numOfYears - 1));

    return results;
  }

  /**
   * Store data.
   * 
   * @param destinationDir
   *          the destination dir
   * @param selectors
   *          the selectors
   * @param trialAssumptions
   *          the trial assumptions
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void storeData(File destinationDir, IAggregationSelector[] selectors,
      List<Triple<Integer, Double, int[]>> trialAssumptions) throws IOException {
    List<Integer> indexOrder = sortAndStore(destinationDir, trialAssumptions);
    for (IAggregationSelector selector : selectors) {
      selector.finish(destinationDir, indexOrder, this);
    }
  }

  /**
   * Sorts assumption sets of trial by probability (descending order) and stores
   * them into file.
   * 
   * @param destinationDir
   *          the destination directory
   * @param trialAssumptions
   *          the trial assumptions
   * 
   * @return the list of trial indices, which defines the reordering
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private List<Integer> sortAndStore(File destinationDir,
      List<Triple<Integer, Double, int[]>> trialAssumptions) throws IOException {

    // Sort in *descending* order
    Collections.sort(trialAssumptions,
        new Comparator<Triple<Integer, Double, int[]>>() {
          @Override
          public int compare(Triple<Integer, Double, int[]> o1,
              Triple<Integer, Double, int[]> o2) {
            return -1 * o1.getB().compareTo(o2.getB());
          }
        });

    // Write file
    FileWriter fw = new FileWriter(destinationDir.getAbsolutePath()
        + File.separatorChar + "assumptions_sorted.txt");
    List<Integer> resultingOrder = new ArrayList<Integer>();
    for (Triple<Integer, Double, int[]> trialAssumption : trialAssumptions) {
      resultingOrder.add(trialAssumption.getA());
      fw.append(trialAssumption.getA() + "\t" + trialAssumption.getB() + "\t");
      for (int elem : trialAssumption.getC()) {
        fw.append(elem + "\t");
      }
      fw.append("\n");
    }
    fw.close();

    return resultingOrder;
  }

  /**
   * Analyzes and filters results. Applies all selectors to the results that are
   * not filtered.
   * 
   * @param projection
   *          the projection
   * @param selectors
   *          the selectors
   * @param assumptionEncoder
   *          the assumption encoder
   * @param progress
   *          the progress observer
   * @return the assumptions of the trials - triples of (#trial, probability,
   *         encoded assumptions) - in correct order
   */
  private List<Triple<Integer, Double, int[]>> analyzeResults(
      ProjectionModel projection, IAggregationSelector[] selectors,
      ParameterAssumptionEncoder assumptionEncoder, IProgressObserver progress) {

    // This does not read out all results completely, as they are accessed
    // lazily...
    progress.addWaypoints(DatabaseFactory.getDatabaseSingleton()
        .getAllResults(projection).size());

    // ... using an iterator
    IProjectionResultsIterator resultsIterator = DatabaseFactory
        .getDatabaseSingleton().getResultIterator(projection);

    ResultsOfTrial result = resultsIterator.getNextResult();
    int trialCount = 0;
    List<Triple<Integer, Double, int[]>> trialAssumptions = new ArrayList<Triple<Integer, Double, int[]>>();

    while (result != null) {

      // If user cancelled the task, stop
      if (progress.isCancelled()) {
        progress.taskCanceled();
        break;
      }

      if (!resultFilter.considerResult(result)) {
        publishInfo(
            progress,
            "Results with ID " + result.getID()
                + " are dismissed by the result filter '"
                + Strings.dispClassName(resultFilter.getClass()));
        result = resultsIterator.getNextResult();
        continue;
      }
      publishInfo(progress, "Analyzing trial #" + (trialCount + 1));
      try {
        trialAssumptions.add(new Triple<Integer, Double, int[]>(trialCount,
            result.getAssignmentProbability(), assumptionEncoder.encode(result
                .getAssignment())));
      } catch (RuntimeException ex) {
        GUI.printErrorMessage("Encoding trial failed.", ex);
        result = resultsIterator.getNextResult();
        continue;
      }

      for (IAggregationSelector selector : selectors) {
        selector.consider(trialCount, result);
      }
      trialCount++;

      result = resultsIterator.getNextResult();
    }
    return trialAssumptions;
  }

  /**
   * Publishes information to the logging mechanism and the progress observer.
   * 
   * @param progress
   *          the progress observer
   * @param msg
   *          the information message
   */
  private void publishInfo(IProgressObserver progress, String msg) {
    progress.incrementProgress(msg);
    SimSystem.report(Level.INFO, msg);
  }

  /**
   * Initialize selectors.
   * 
   * @param projection
   *          the projection
   * @param selectors
   *          the selectors
   */
  private void initializeSelectors(ProjectionModel projection,
      IAggregationSelector[] selectors) {
    for (IAggregationSelector selector : selectors) {
      selector
          .init(numOfTrials, numOfYears, projection.getNumberOfAgeClasses());
    }
  }

  /**
   * Calculates quantiles per column.
   * 
   * @param inputMatrix
   *          the input matrix
   * 
   * @return the double[][]
   */
  protected double[][] calcQuantiles(double[][] inputMatrix) {
    if (inputMatrix.length == 0) {
      return new double[0][0];
    }
    int maxRowIndex = inputMatrix.length - 1;
    int cols = inputMatrix[0].length;
    double[][] result = new double[QUANTILES.length][cols];
    for (int col = 0; col < cols; col++) {
      List<Double> currentValues = new ArrayList<Double>();
      for (int i = 0; i < inputMatrix.length; i++) {
        currentValues.add(inputMatrix[i][col]);
      }
      Collections.sort(currentValues);
      for (int i = 0; i < QUANTILES.length; i++) {
        result[i][col] = currentValues.get((int) (QUANTILES[i] * maxRowIndex));
      }
    }
    return result;
  }

  /**
   * An alternative way to create the list of years for the density plot.
   * 
   * TODO: Should be replaced as soon as possible by
   * {@link ResultExport#createEquidistantDensityYearList()}, this will also
   * require a change to the densityPlot function in plotting.R.
   * 
   * 
   * @param jumpOffYear
   *          the jump off year
   * @param firstOffset
   *          the first offset
   * @param regularOffset
   *          the regular offset between years
   * @return the string containing the density year list
   * 
   */
  private String createDensityYearList(int jumpOffYear, int firstOffset,
      int regularOffset) {
    StringBuffer yearList = new StringBuffer();
    for (int i = 0; i < DENSITY_PLOT_NUM_SUBPLOTS; i++) {
      yearList.append(jumpOffYear + firstOffset + i * regularOffset
          + (i == DENSITY_PLOT_NUM_SUBPLOTS - 1 ? "" : ","));
    }
    return yearList.toString();
  }

  /**
   * Creates the density year list. The density years are equidistant among
   * jump-off year and the last year of the projection.
   * 
   * @return the string containing the density year list
   */
  protected String createEquidistantDensityYearList(int jumpOffYear) {
    StringBuffer yearList = new StringBuffer();
    for (int i = 0; i < DENSITY_PLOT_NUM_SUBPLOTS; i++) {
      yearList.append((jumpOffYear + (int) Math.round(i * numOfYears
          / (DENSITY_PLOT_NUM_SUBPLOTS - 1.0)))
          + (i == DENSITY_PLOT_NUM_SUBPLOTS - 1 ? "" : ","));
    }
    return yearList.toString();
  }

  /**
   * Creates the full year list.
   * 
   * @param jumpOffYear
   *          the jump off year
   * @param projectionYears
   *          the projection years
   * @return the string
   */
  private String createFullYearList(int jumpOffYear, int projectionYears) {
    StringBuffer yearList = new StringBuffer(Integer.toString(jumpOffYear));
    yearList.append(',');
    int lastYear = jumpOffYear;
    for (int i = 0; i < projectionYears - 1; i++) {
      lastYear++;
      yearList.append(lastYear);
      yearList.append(',');
    }
    yearList.append("" + (lastYear + 1));
    return yearList.toString();
  }

  /**
   * Filter out all unnecessary data.
   * 
   * @param original
   *          the original
   * 
   * @return the double[][]
   */
  protected double[][] filter(double[][] original) {
    double[][] filteredResult = new double[original.length][yearOffsetToConsider.length + 1];
    for (int i = 0; i < original.length; i++) {
      filteredResult[i][0] = original[i][0];
      int currentOffset = 0;
      for (int j = 0; j < yearOffsetToConsider.length; j++) { // NOSONAR
        currentOffset += yearOffsetToConsider[j];
        filteredResult[i][j + 1] = original[i][currentOffset];
      }
    }
    return filteredResult;
  }

  /**
   * Write result.
   * 
   * @param destinationDir
   *          the destination dir
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void writeResult(File destinationDir, double[][] result,
      String fileName) throws IOException {
    FileWriter fw = new FileWriter(destinationDir.getAbsolutePath()
        + File.separatorChar + fileName);
    fw.append(toCSV(result, ','));
    fw.close();
  }

  /**
   * Export data.
   * 
   * @param dataDirectory
   *          the data directory
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void exportData(File dataDirectory) throws IOException {
    SimSystem.report(Level.INFO, "Exporting CSV data...");
    IProjectionResultsIterator resultsSet = DatabaseFactory
        .getDatabaseSingleton().getResultIterator(projection);

    ParameterAssumptionEncoder assumptionEncoder = storeAssumptionEncoding(
        projection, dataDirectory);

    int resultCount = 1;
    for (ResultsOfTrial results : resultsSet) {
      SimSystem.report(Level.INFO, "Export Trial #" + resultCount + " (ID: "
          + results.getID() + ")");
      if (!resultFilter.considerResult(results)) {
        SimSystem.report(
            Level.INFO,
            "Results with ID " + results.getID()
                + " are dismissed by the result filter '"
                + Strings.dispClassName(resultFilter.getClass()));
        continue;
      }

      File trialDirectory = new File(dataDirectory.getAbsolutePath()
          + File.separatorChar + "trial_" + resultCount);
      if (!trialDirectory.mkdir()) {
        throw new IllegalStateException("Could not create directory: "
            + trialDirectory.getAbsolutePath());
      }

      for (SubPopulation subPop : projection.getSubPopulationModel()
          .getSubPopulations()) {
        if (subPop.isConsistingOfDescendantGenerations())
          writeGenerationResults(trialDirectory, results.retrieveFor(subPop),
              subPop.getSimplifiedName());
        else
          exportBasicResults(trialDirectory,
              results.retrieveFor(subPop).get(0), subPop.getSimplifiedName());
      }

      writeAssumptionsUsed(trialDirectory, results, assumptionEncoder);
      resultCount++;
    }
  }

  /**
   * Writes a file that specifies which assumptions are used for the parameters
   * in a given trial.
   * 
   * @param trialDirectory
   *          the trial directory
   * @param results
   *          the results of the trial
   * @param assumptionEncoder
   *          the encoder for the assumptions that are used
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void writeAssumptionsUsed(File trialDirectory,
      ResultsOfTrial results, ParameterAssumptionEncoder assumptionEncoder)
      throws IOException {

    FileWriter fw = new FileWriter(trialDirectory.getAbsolutePath()
        + File.separatorChar + "assumptions.txt");
    fw.append("Assumption probability: \t" + results.getAssignmentProbability()
        + "\n");
    fw.append("Numerical encoding: \t"
        + Strings.dispArray(assumptionEncoder.encode(results.getAssignment()))
        + "\n");
    fw.append("\n\nAssumptions: \n"
        + assumptionEncoder.verboseEncoding(results.getAssignment()) + "\n");
    fw.close();
  }

  /**
   * Write results for a list of basic results that correspond to different
   * generations of the same population.
   * 
   * @param trialDirectory
   *          the trial directory
   * @param results
   *          the results to be written to file
   * @param populationName
   *          the name of the population
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void writeGenerationResults(File trialDirectory,
      List<BasicResults> results, String populationName) throws IOException {
    for (int i = 0; i < results.size(); i++) {
      exportBasicResults(trialDirectory, results.get(i), populationName
          + "_gen_" + i);
    }
  }

  /**
   * Export basic results.
   * 
   * @param trialDirectory
   *          the trial directory
   * @param basicResults
   *          the basic results
   * @param exportPrefix
   *          the export prefix
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void exportBasicResults(File trialDirectory,
      BasicResults basicResults, String exportPrefix) throws IOException {
    exportMatrix(trialDirectory, basicResults.getEndXm(), exportPrefix
        + "_end_x_m.csv");
    exportMatrix(trialDirectory, basicResults.getEndXf(), exportPrefix
        + "_end_x_f.csv");
    exportMatrix(trialDirectory, basicResults.getMeanXm(), exportPrefix
        + "_mean_x_m.csv");
    exportMatrix(trialDirectory, basicResults.getMeanXf(), exportPrefix
        + "_mean_x_f.csv");
  }

  /**
   * Export matrix.
   * 
   * @param trialDirectory
   *          the trial directory
   * @param data
   *          the data
   * @param fileName
   *          the file name
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private static void exportMatrix(File trialDirectory, Matrix2D data,
      String fileName) throws IOException {
    if (data == null) {
      SimSystem.report(Level.INFO, "WARNING: Cannot write '" + fileName
          + "' - data is null.");
      return;
    }
    FileWriter fw = new FileWriter(trialDirectory.getAbsolutePath()
        + File.separatorChar + fileName);
    fw.append(toCSV(data.toArray(), ','));
    fw.close();
  }

  /**
   * Initialize sub directory.
   * 
   * @param subDirName
   *          the name of the sub-directory
   * 
   * @return the file
   */
  private File initializeSubDirectory(String subDirName) {
    File subDirectory = new File(targetDir.getAbsolutePath()
        + File.separatorChar + subDirName);

    if (subDirectory.exists()
        && (!deleteAllDirFiles(subDirectory) || !subDirectory.delete())) {
      throw new IllegalArgumentException("Could not delete directory: "
          + subDirectory.getAbsolutePath());
    }

    if (!subDirectory.mkdir()) {
      throw new IllegalArgumentException("Could not create directory: "
          + subDirectory.getAbsolutePath());
    }
    return subDirectory;
  }

  /**
   * Delete all files in a directory.
   * 
   * @param dir
   *          the directory
   * 
   * @return true, if successful
   */
  private boolean deleteAllDirFiles(File dir) {
    for (File subFile : dir.listFiles()) {
      if (!subFile.delete()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Copies plotting library to target directory.
   * 
   * @throws IOException
   *           if copying fails
   */
  private void copyPlottingLib() throws IOException {

    // TODO: User JAMESII result reporting instead

    String sourcePath = '/' + TEMPLATE_DIRECTORY + '/' + R_LIBRARY;
    String targetPath = targetDir.getAbsolutePath() + File.separatorChar
        + R_LIBRARY;

    BufferedInputStream source = null;
    BufferedOutputStream target = null;
    final String errorMessage = "Could not copy library file from '"
        + sourcePath + "' to '" + targetPath
        + "' (required to execute R code).";
    try {
      source = new BufferedInputStream(getClass().getResourceAsStream(
          sourcePath));
      target = new BufferedOutputStream(new FileOutputStream(targetPath));
      byte[] buffer = new byte[COPY_BUFFER_SIZE];
      int len = source.read(buffer);
      while (len != -1) {
        target.write(buffer, 0, len);
        len = source.read(buffer);
      }
    } catch (IOException e) {
      GUI.printErrorMessage(errorMessage, e);
    } finally {
      try {
        if (target != null) {
          target.close();
        }
      } catch (IOException e) {
        GUI.printErrorMessage(errorMessage, e);
      }
      try {
        if (source != null) {
          source.close();
        }
      } catch (IOException e) {
        GUI.printErrorMessage(errorMessage, e);
      }
    }
  }

  /**
   * Export to CSV.
   * 
   * @param matrix
   *          the matrix
   * @param delim
   *          the delimiter
   * 
   * @return the string builder
   */
  public static StringBuilder toCSV(double[][] matrix, char delim) {
    if (matrix == null) {
      return new StringBuilder("null");
    }
    StringBuilder matrixString = new StringBuilder();
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        matrixString.append(Double.toString(matrix[i][j]));
        if (j < matrix[i].length - 1) {
          matrixString.append(delim);
        }
      }
      matrixString.append('\n');
    }
    return matrixString;
  }

  /**
   * Creates the Sweave file, inserts all data contianed in map.
   * 
   * @param dataToBeInserted
   *          map containing the pointers to the aggregated data
   * @throws IOException
   *           if file creation fails
   * @throws TemplateException
   *           if template processing fails
   */
  private void createSweaveFile(Map<String, Object> dataToBeInserted)
      throws IOException, TemplateException {

    SimSystem.report(Level.INFO, "Creating report template.");

    Configuration cfg = new Configuration();
    cfg.setClassForTemplateLoading(ResultExport.class, "/" + TEMPLATE_DIRECTORY);
    cfg.setObjectWrapper(new DefaultObjectWrapper());

    dataToBeInserted.put("projection", projection);
    dataToBeInserted.put("ages", projection.getMaximumAge() + 1);
    dataToBeInserted.put("years", projection.getYears() + 1);
    dataToBeInserted.put("trials", numOfTrials);
    dataToBeInserted.put("date", (new SimpleDateFormat(
        "dd. MM. yyyy (HH:mm:ss)")).format(Calendar.getInstance().getTime()));
    dataToBeInserted.put("numsettypes", projection.getAllSetTypes().size());

    // Add list of sub-populations
    dataToBeInserted.put("subPopulations", projection.getSubPopulationModel()
        .getSubPopulations());

    // Add index list of generations
    List<Integer> generations = new ArrayList<Integer>();
    for (int i = 0; i < projection.getGenerations(); i++) {
      generations.add(i);
    }
    dataToBeInserted.put("generations", generations);

    // Add index list of descendant generations
    List<Integer> descendantGenerations = new ArrayList<Integer>();
    for (int i = 1; i < projection.getGenerations(); i++) {
      descendantGenerations.add(i);
    }
    dataToBeInserted.put("desc_generations", descendantGenerations);

    Template basicTemplate = cfg.getTemplate("template.Rtfm");
    Writer out = new FileWriter(targetDir.getAbsolutePath()
        + File.separatorChar + "report.Rtex");
    basicTemplate.process(dataToBeInserted, out);
    out.close();
  }

  /**
   * Gets the result filter.
   * 
   * @return the result filter
   */
  public IResultFilter getResultFilter() {
    return resultFilter;
  }

}
