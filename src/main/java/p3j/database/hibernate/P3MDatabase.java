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
package p3j.database.hibernate;

import james.SimSystem;
import james.core.data.DBConnectionData;
import james.core.util.StopWatch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import p3j.database.IP3MDatabase;
import p3j.database.IProjectionResultsIterator;
import p3j.experiment.results.ResultsOfTrial;
import p3j.misc.MatrixDimension;
import p3j.misc.Misc;
import p3j.misc.math.Matrix;
import p3j.misc.math.Matrix2D;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.Parameter;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.parameters.Parameters;
import p3j.pppm.parameters.Population;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * Implementation of {@link IP3MDatabase} based on Hibernate.
 * 
 * Created: August 17, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class P3MDatabase implements IP3MDatabase {

  /** Path to hibernate configuration file. */
  private static String hibernateConfigFile = Misc.DEFAULT_HIBERNATE_CONFIG_FILE;

  /** The default flush frequency (once every x commits). */
  private static final int DEFAULT_FLUSH_FREQ = 50;

  /** Flag to determine the flushing policy of the database. */
  private boolean alwaysFlush = true;

  /** Hibernate configuration. */
  private final Configuration config;

  /** Factory to create sessions. */
  private SessionFactory sessionFactory;

  /** Current Hibernate session. */
  private Session session;

  /** Session to store results. */
  private Session resultStorageSession;

  /** Counts how many results have been stored so far. */
  private int resultStorageCounter;

  /**
   * Gives the frequency with which the session to store the results will be
   * flushed and cleared.
   */
  private int resultStorageFlushFrequency = DEFAULT_FLUSH_FREQ;

  /**
   * Constructor using the default configuration file.
   */
  public P3MDatabase() {
    this(P3MDatabase.hibernateConfigFile);
  }

  /**
   * Constructor for custom configuration.
   * 
   * @param configurationFile
   *          the name of the configuration file
   */
  public P3MDatabase(String configurationFile) {
    config = new Configuration().configure(configurationFile);
  }

  @Override
  public void init(DBConnectionData dbConn) {
    getConfig().setProperty("hibernate.connection.username", dbConn.getUser())
        .setProperty("hibernate.connection.password", dbConn.getPassword())
        .setProperty("hibernate.connection.url", dbConn.getUrl());
    sessionFactory = getConfig().buildSessionFactory();
  }

  @Override
  public void open() {
    if (session == null) {
      session = sessionFactory.openSession();
    }
  }

  @Override
  public void clear() {
    SimSystem.report(Level.INFO, "CLEARING DB...");
    SchemaExport export = new SchemaExport(getConfig());
    export.create(true, true);
  }

  @Override
  public void close() {
    if (session != null) {
      session.close();
    }
    if (sessionFactory != null) {
      sessionFactory.close();
    }
  }

  /**
   * DB-write hook for switching flushing on or off.
   */
  protected void dbChanged() {
    if (alwaysFlush) {
      session.flush();
    }
  }

  /**
   * Saves an object with hibernate.
   * 
   * @param o
   *          the object to be saved
   */
  public void save(Object o) {
    Transaction t = session.beginTransaction();
    session.save(o);
    t.commit();
    dbChanged();
  }

  /**
   * Deletes an object with hibernate.
   * 
   * @param o
   *          the object to be deleted
   */
  public void delete(Object o) {
    Transaction t = session.beginTransaction();
    session.delete(o);
    t.commit();
    dbChanged();
  }

  // Parameter

  @Override
  public Parameter newParameter(String name, boolean genDep,
      MatrixDimension height, MatrixDimension width, Population population) {
    Parameter param = getParameter(name);
    if (param == null) {
      param = new Parameter(-1, genDep, name, height, width, population);
      save(param);
    }
    return param;
  }

  @Override
  public Parameter getParameter(String name) {
    List<Parameter> parameters = Misc.autoCast(session.createQuery(
        "from Parameter where name='" + name + "'").list());
    if (parameters.size() > 1) {
      throw new ConstraintException(
          "Ambiguity: there are two parameters with the same name '" + name
              + "'");
    }
    if (parameters.size() == 1) {
      return parameters.get(0);
    }
    return null;
  }

  @Override
  public List<Parameter> getAllParameters() {
    List<Parameter> parameters = Misc.autoCast(session.createQuery(
        "from Parameter").list());
    return parameters;
  }

  @Override
  public boolean deleteParameter(Parameter parameter) {
    delete(parameter);
    return true;
  }

  // ParameterInstance

  @Override
  public ParameterInstance newParameterInstance(int comparisonIndex,
      Parameter param, int generation) {
    ParameterInstance instance = getParameterInstance(param, generation);
    if (instance == null) {
      instance = new ParameterInstance(comparisonIndex, param, generation);
      save(instance);
    }
    return instance;
  }

  @Override
  public ParameterInstance getParameterInstance(Parameter param, int generation) {
    List<ParameterInstance> instances = Misc.autoCast(session
        .createCriteria(ParameterInstance.class)
        .add(Restrictions.eq("parameter", param))
        .add(Restrictions.eq("generation", generation)).list());
    if (instances.size() > 1) {
      throw new ConstraintException(
          "Ambiguity: there are two parameter instances for parameter '"
              + param.getName() + "' and generation " + generation);
    }
    if (instances.size() == 1) {
      return instances.get(0);
    }
    return null;
  }

  @Override
  public List<ParameterInstance> getAllParameterInstances() {
    List<ParameterInstance> instances = Misc.autoCast(session.createQuery(
        "from ParameterInstance").list());
    return instances;
  }

  @Override
  public boolean deleteParameterInstance(ParameterInstance instance) {
    delete(instance);
    return true;
  }

  // TODO: Add method to remove orphaned matrices from the database.
  // Ensure that changed matrices will be saved as new ones.
  @Override
  public Matrix newMatrix(Matrix2D value) {
    Matrix matrix = getMatrix(value);
    if (matrix == null) {
      matrix = new Matrix(value);
      save(matrix);
    }
    return matrix;
  }

  @Override
  public Matrix getMatrix(Matrix2D value) {
    List<Matrix> matrices = Misc
        .autoCast(session.createCriteria(Matrix.class)
            .add(Restrictions.eq("hash", Matrix2D.calculateHashCode(value)))
            .list());
    for (Matrix matrix : matrices) {
      if (matrix.getValue().equals(value)) {
        return matrix;
      }
    }
    return null;
  }

  @Override
  public List<Matrix> getAllMatrices() {
    List<Matrix> matrices = Misc.autoCast(session.createQuery("from Matrix")
        .list());
    return matrices;
  }

  @Override
  public boolean deleteMatrix(Matrix matrix) {
    delete(matrix);
    return true;
  }

  // ParameterAssignment

  @Override
  public ParameterAssignment newParameterAssignment(
      ParameterInstance paramInstance, String name, String description,
      double probability, double deviation, Matrix2D value) {
    Matrix matrix = newMatrix(value);
    ParameterAssignment assignment = new ParameterAssignment(paramInstance,
        name, description, probability, deviation, matrix);
    save(assignment);
    return assignment;
  }

  @Override
  public List<ParameterAssignment> getAllParameterAssignments(
      ParameterInstance param) {
    List<ParameterAssignment> assignments = Misc.autoCast(session
        .createCriteria(ParameterAssignment.class)
        .add(Restrictions.eq("paramInstance", param)).list());
    return assignments;
  }

  @Override
  public boolean deleteParameterAssignment(ParameterAssignment assignment) {
    delete(assignment);
    return true;
  }

  @Override
  public void saveParameterAssignment(ParameterAssignment assignment) {
    save(assignment);
  }

  // Set

  @Override
  public Set newSet(List<ParameterInstance> defParams, String name,
      String desc, double prob) {
    Set set = new Set(defParams, name, desc, prob);
    save(set);
    return set;
  }

  @Override
  public void saveSet(Set set) {
    save(set);
  }

  @Override
  public List<Set> getAllSets() {
    return Misc.autoCast(session.createCriteria(Set.class).list());
  }

  @Override
  public boolean deleteSet(Set set) {
    delete(set);
    return true;
  }

  // SetType

  @Override
  public SetType newSetType(String name, String description) {
    SetType setType = new SetType(name, description);
    save(setType);
    return setType;
  }

  @Override
  public List<SetType> getAllSetTypes() {
    List<SetType> setTypes = Misc.autoCast(session
        .createCriteria(SetType.class).list());
    return setTypes;
  }

  @Override
  public void saveSetType(SetType setType) {
    save(setType);
  }

  @Override
  public boolean deleteSeType(SetType setType) {
    delete(setType);
    return true;
  }

  // Projection

  @Override
  public void newProjection(ProjectionModel projection) {
    // Initializing all parameter instances
    List<Parameter> parameters = Parameters.getInstance().getParams();
    List<ParameterInstance> allInstances = projection
        .getAllParameterInstances();
    int comparisonIndex = 0;
    for (int j = 0; j < projection.getGenerations(); j++) {
      for (int i = 0; i < parameters.size(); i++) {
        Parameter parameter = parameters.get(i);
        if (parameter.isGenerationDependent()) {
          allInstances
              .add(newParameterInstance(++comparisonIndex, parameter, j));
        } else if (j == 0) {
          allInstances.add(newParameterInstance(++comparisonIndex, parameter,
              -1));
        }
      }
    }
    projection.init();
    save(projection);
  }

  @Override
  public boolean deleteProjection(ProjectionModel projection) {
    deleteAllResults(projection);
    delete(projection);
    return true;
  }

  @Override
  public List<ProjectionModel> getAllProjections() {
    List<ProjectionModel> projections = Misc.autoCast(session.createQuery(
        "from ProjectionModel").list());
    return projections;
  }

  @Override
  public ProjectionModel getProjectionByID(int id) {
    List<ProjectionModel> pModels = Misc.autoCast(session
        .createCriteria(ProjectionModel.class).add(Restrictions.eq("ID", id))
        .list());
    if (pModels.size() > 1) {
      throw new ConstraintException(
          "DB consistency: Two duplicate projection models with the same ID '"
              + id + "' !");
    }
    if (pModels.size() == 1) {
      return pModels.get(0);
    }
    return null;
  }

  @Override
  public void saveProjection(ProjectionModel projection) {
    save(projection);
  }

  /**
   * Test connection to database.
   * 
   * @param dbConnData
   *          the database connection data
   * 
   * @return the exception (null in case of success)
   */
  public static Exception testConnection(DBConnectionData dbConnData) {
    Connection c = null;
    try {
      c = DriverManager.getConnection(dbConnData.getUrl(),
          dbConnData.getUser(), dbConnData.getPassword());
      if (c == null) {
        throw new IllegalArgumentException(
            "Connection created by driver manager is null.");
      }
    } catch (Exception ex) {
      return ex;
    } finally {
      try {
        if (c != null) {
          c.close();
        }
      } catch (SQLException e) {
        return e;
      }
    }
    return null;
  }

  /**
   * Test connection to database.
   * 
   * @param dbURL
   *          the URL of the database
   * @param dbUserName
   *          the DB user name
   * @param dbPassword
   *          the DB password
   * 
   * @return the exception
   */
  public static Exception testConnection(String dbURL, String dbUserName,
      String dbPassword) {
    return testConnection(new DBConnectionData(dbURL, dbUserName, dbPassword,
        null));
  }

  @Override
  public synchronized void saveTrialResults(ResultsOfTrial resultOfTrial) {
    if (resultStorageSession == null) {
      resultStorageSession = sessionFactory.openSession();
    }
    StopWatch sw = new StopWatch();
    sw.start();
    Transaction resultStorageTransaction = resultStorageSession
        .beginTransaction();
    resultStorageSession.save(resultOfTrial);
    resultStorageTransaction.commit();
    resultStorageCounter++;
    if (resultStorageCounter % resultStorageFlushFrequency == 0) {
      resultStorageSession.flush();
      resultStorageSession.clear();
    }
    sw.stop();
    SimSystem.report(Level.INFO,
        "Time for result storage in database:" + sw.elapsedMilliseconds());
  }

  @Override
  public List<ResultsOfTrial> getAllResults(ProjectionModel projection) {
    List<ResultsOfTrial> results = Misc.autoCast(session
        .createCriteria(ResultsOfTrial.class)
        .add(Restrictions.eq("projection", projection))
        .addOrder(Order.asc("ID")).list());
    return results;
  }

  @Override
  public void deleteResult(ResultsOfTrial resultOfTrial) {
    delete(resultOfTrial);
  }

  @Override
  public void deleteAllResults(ProjectionModel projection) {
    Transaction t = session.beginTransaction();
    for (ResultsOfTrial results : getAllResults(projection)) {
      session.delete(results);
    }
    t.commit();
    dbChanged();
  }

  @Override
  public IProjectionResultsIterator getResultIterator(ProjectionModel projection) {
    return new ProjectionResultsIterator(sessionFactory, projection.getID());
  }

  @Override
  public void clearCache(Object o) {
    session.evict(o);
    session.flush();
  }

  /**
   * Gets the hibernate configuration file.
   * 
   * @return the hibernate configuration file
   */
  public static String getHibernateConfigFile() {
    return hibernateConfigFile;
  }

  /**
   * Sets the hibernate configuration file.
   * 
   * @param hibernateConfigFile
   *          the new hibernate configuration file
   */
  public static void setHibernateConfigFile(String hibernateConfigFile) {
    P3MDatabase.hibernateConfigFile = hibernateConfigFile;
  }

  public Configuration getConfig() {
    return config;
  }
}
