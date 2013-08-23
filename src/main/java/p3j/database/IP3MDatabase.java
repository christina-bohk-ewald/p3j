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
package p3j.database;

import java.util.List;

import org.jamesii.core.data.DBConnectionData;

import p3j.experiment.results.ResultsOfTrial;
import p3j.gui.misc.P3JConfigFile;
import p3j.misc.IProgressObserver;
import p3j.misc.MatrixDimension;
import p3j.misc.math.Matrix;
import p3j.misc.math.Matrix2D;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.Parameter;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.parameters.Population;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * Database interface for P3J. It provides loading, saving, and look-up means
 * for all parts of the PPPM that ought to be persistent (e.g.,
 * {@link Parameter}, {@link ParameterInstance}, {@link Set}, {@link SetType}).
 * 
 * Created on January 11, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public interface IP3MDatabase {

  /**
   * Initialize database connection.
   * 
   * @param dbConn
   *          the connection details
   * @param configFile
   *          the config file with additional information
   */
  void init(DBConnectionData dbConn, P3JConfigFile configFile);

  /**
   * Establishes database connection.
   */
  void open();

  /**
   * Clears the database. I.e., it removes *all* entries and re-initializes the
   * (potentially changed) table structure.
   */
  void clear();

  /**
   * Closes the database connection.
   */
  void close();

  // Parameter

  /**
   * Creates a new parameter (if not already existent) and returns it.
   * 
   * @param name
   *          name of the parameter
   * @param sortIndex
   *          the sorting index
   * @param genDep
   *          flag to determine generation dependency
   * @param height
   *          height of parameter values
   * @param width
   *          width of parameter values
   * @param population
   *          the population to which this parameter refers
   * @return newly created parameter (or retrieved from DB)
   */
  Parameter newParameter(String name, int sortIndex, boolean genDep,
      MatrixDimension height, MatrixDimension width, Population population);

  /**
   * Retrieves a parameter with the given name from the database.
   * 
   * @param name
   *          the name of the parameter to be retrieved
   * @return the parameter, or null if not exists
   */
  Parameter getParameter(String name);

  /**
   * Retrieves list with all parameters.
   * 
   * @return list of all {@link Parameter} instances stored in the database
   */
  List<Parameter> getAllParameters();

  /**
   * Deletes given parameter from database.
   * 
   * @param parameter
   *          the parameter to be deleted
   * @return true if deletion was successful, otherwise false
   */
  boolean deleteParameter(Parameter parameter);

  // ParameterInstance

  /**
   * Creates new parameter instance.
   * 
   * @param comparisonIndex
   *          comparison index of the instance
   * @param param
   *          the associated parameter
   * @param generation
   *          the generation of the instance
   * @return a newly created instance, or instance with the same generation and
   *         parameter from the database
   */
  ParameterInstance newParameterInstance(int comparisonIndex, Parameter param,
      int generation);

  /**
   * Retrieves a parameter instance from the database.
   * 
   * @param param
   *          the associated parameter
   * @param generation
   *          the generation for which the parameter shall be instantiated
   * @return existing parameter from database, or null
   */
  ParameterInstance getParameterInstance(Parameter param, int generation);

  /**
   * Retrieves list with all parameter instances.
   * 
   * @return list of all parameter instances that are stored in the database
   */
  List<ParameterInstance> getAllParameterInstances();

  /**
   * Deletes given parameter instances from the data base.
   * 
   * @param instance
   *          the instance to be deleted
   * @return true, if deletion was successful
   */
  boolean deleteParameterInstance(ParameterInstance instance);

  // Matrix

  /**
   * Creates a new matrix if a matrix with the same values is not already
   * existing in the system.
   * 
   * @param value
   *          the value of the matrix
   * @return the newly created (or retrieved) matrix
   */
  Matrix newMatrix(Matrix2D value);

  /**
   * Deletes given matrix from the database.
   * 
   * @param matrix
   *          the matrix to be deleted
   * @return true if deletion was successful
   */
  boolean deleteMatrix(Matrix matrix);

  // ParameterAssignment

  /**
   * Creates a new parameter assignment.
   * 
   * @param paramInstance
   *          the associated parameter instance
   * @param name
   *          the name of the assignment
   * @param description
   *          the description of the assignment
   * @param probability
   *          the probability of the assignment
   * @param deviation
   *          the assumption-inherent deviation, i.e. the noise
   * @param value
   *          the assigned value
   * @return the instantiated assignment
   */
  ParameterAssignment newParameterAssignment(ParameterInstance paramInstance,
      String name, String description, double probability, double deviation,
      Matrix2D value);

  /**
   * Retrieves all {@link ParameterAssignment} entities for a certain
   * {@link ParameterInstance}.
   * 
   * @param paramInstance
   *          the parameter instance
   * @return list containing all parameter assignments from database
   */
  List<ParameterAssignment> getAllParameterAssignments(
      ParameterInstance paramInstance);

  /**
   * Deletes given parameter assignment.
   * 
   * @param assignment
   *          the assignment to be deleted
   * @return true, if deletion was successful
   */
  boolean deleteParameterAssignment(ParameterAssignment assignment);

  /**
   * Saves given parameter assignment.
   * 
   * @param assignment
   *          the parameter assignment
   */
  void saveParameterAssignment(ParameterAssignment assignment);

  // Sets

  /**
   * Creates a new set.
   * 
   * @param params
   *          parameter instance for which assignments can be defined
   * @param name
   *          name of the set
   * @param desc
   *          description of the set
   * @param prob
   *          probability of the set
   * @return newly created set
   */
  Set newSet(List<ParameterInstance> params, String name, String desc,
      double prob);

  /**
   * Updates the set. This stores all changed/added/removed
   * {@link p3j.pppm.parameters.ParameterAssignmentSet} instances as well.
   * 
   * @param set
   *          the set to be updated
   */
  void saveSet(Set set);

  /**
   * Deletes given set from database.
   * 
   * @param set
   *          the set to be deleted
   * @return true, if deletion was successful
   */
  boolean deleteSet(Set set);

  /**
   * Retrieves all sets from database.
   * 
   * @return list of all sets
   */
  List<Set> getAllSets();

  // Settype

  /**
   * Create new Settype.
   * 
   * @param name
   *          name of the Settype to be created
   * @param description
   *          description of the Settype to be created
   * @return newly created Settype
   */
  SetType newSetType(String name, String description);

  /**
   * Updates the Settype. This stores all changed/added/removed {@link Set}
   * instances as well.
   * 
   * @param setType
   *          the Settype to be updated
   */
  void saveSetType(SetType setType);

  /**
   * Retrieve all Settypes from data base.
   * 
   * @return list with all Settypes in the database
   */
  List<SetType> getAllSetTypes();

  /**
   * Delete {@link SetType} from database.
   * 
   * @param setType
   *          the Settype to be deleted
   * @return true, if deletion was successful
   */
  boolean deleteSeType(SetType setType);

  // Scenario

  /**
   * Adds new projection to database, automatically adds any {@link Parameter}
   * or {@link ParameterInstance} entities that have not yet been created.
   * 
   * @param projection
   *          the new projection
   */
  void newProjection(ProjectionModel projection);

  /**
   * Retrieves list of all projections from the database.
   * 
   * @return list of all projections from the database
   * @throws Exception
   *           if lookup fails
   */
  List<ProjectionModel> getAllProjections();

  /**
   * Retrieves projection by id.
   * 
   * @param id
   *          the id of the projection
   * @return the projection, null if none was found
   */
  ProjectionModel getProjectionByID(int id);

  /**
   * Delete projection from database.
   * 
   * @param projection
   *          the projection to be deleted
   * @param observer
   *          the progress observer (may be null)
   * @return true, if deletion was successful
   */
  boolean deleteProjection(ProjectionModel projection,
      IProgressObserver observer);

  /**
   * Saves projection.
   * 
   * @param projection
   *          the projection to be saved/updated
   */
  void saveProjection(ProjectionModel projection);

  // Results

  /**
   * Saves results of a single trial. They are reproducible, as they include the
   * specific parameter assignments that were used.
   * 
   * @param resultOfTrial
   *          the result of the trial
   */
  void saveTrialResults(ResultsOfTrial resultOfTrial);

  /**
   * Retrieves all results for the given projection. Take care:
   * 
   * @param projection
   *          the projection
   * 
   * @return the all results
   */
  List<ResultsOfTrial> getAllResults(ProjectionModel projection);

  /**
   * Deletes a result.
   * 
   * @param resultOfTrial
   *          the result of trial
   */
  void deleteResult(ResultsOfTrial resultOfTrial);

  /**
   * Delete all results of the projection.
   * 
   * @param projection
   *          the projection
   * @param observer
   *          the progress observer (may be null)
   */
  void deleteAllResults(ProjectionModel projection, IProgressObserver observer);

  /**
   * Gets the result iterator.
   * 
   * @param projection
   *          the projection for which the results shall be gathered
   * 
   * @return the result iterator
   */
  IProjectionResultsIterator getResultIterator(ProjectionModel projection);

  /**
   * Clears the given object from cache. Use only if you know this object is not
   * going to be needed again (e.g., during a result export).
   * 
   * @param o
   *          the object to be cleared from cache
   */
  void clearCache(Object o);

}
