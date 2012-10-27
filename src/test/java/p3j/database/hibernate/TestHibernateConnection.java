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

import james.core.data.DBConnectionData;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import p3j.database.DatabaseFactory;
import p3j.database.IP3MDatabase;
import p3j.misc.MatrixDimension;
import p3j.misc.Misc;
import p3j.misc.math.Matrix;
import p3j.misc.math.Matrix2D;
import p3j.pppm.PPPModelFactory;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.Parameter;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.parameters.Population;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * Tests Hibernate connection.
 * 
 * Created: August 17, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class TestHibernateConnection extends TestCase {

  /**
   * Explanation for assertion regarding instance generation.
   */
  public static final String EXPL_INSTANTIATION = "Instance should have been generated";

  /**
   * Explanation for assertion regarding automated ID generation.
   */
  public static final String EXPL_ID_SAVE = "ID should be set by hibernate on save";

  /**
   * Explanation for asserting the retrieval of identical entities.
   */
  public static final String EXPL_UNIQUE = "ID should be the same as before.";

  /**
   * Explanation for asserting the size of the table (1 row).
   */
  public static final String EXPL_ONE_ENTITY = "Only one entity should be in the DB.";

  /**
   * Explanation for asserting the size of the table (2 rows).
   */
  public static final String EXPL_TWO_ENTITIES = "Two entities should be in the database.";

  /**
   * Explanation for asserting a proper deletion.
   */
  public static final String EXPL_DELETION = "Deletion of entityfrom data base should work";

  /**
   * Explanation for asserting object equality.
   */
  public static final String EXPL_EQUAL_FIRST = "The remaining instance should equal the first one.";

  /**
   * Width of the square used for testing.
   */
  public static final int TEST_SQUARE_MATRIX_WIDTH = 128;

  /**
   * Number of generations for which to test.
   */
  public static final int TEST_GENERATIONS = 7;

  /**
   * Maximum age for testing.
   */
  public static final int TEST_MAX_AGE = 100;

  /** The jump-off year for testing. */
  public static final int TEST_JUMP_OFF_YEAR = PPPModelFactory.DEFAULT_JUMP_OFF_YEAR;

  /**
   * Number of years to be predicted when testing.
   */
  public static final int TEST_PRED_YEARS = 100;

  /**
   * The hibernate configuration.
   */
  IP3MDatabase db;

  /**
   * Test parameter.
   */
  Parameter param;

  /**
   * Test parameter instance.
   */
  ParameterInstance instance;

  /**
   * Test matrix number one.
   */
  Matrix matrix1;

  /**
   * Test matrix number two.
   */
  Matrix matrix2;

  /**
   * Test parameter assignment one.
   */
  ParameterAssignment assignment1;

  /**
   * Test parameter assignment two.
   */
  ParameterAssignment assignment2;

  /**
   * Test set, containing both assignments.
   */
  Set set;

  /**
   * Test Settype, containing the set.
   */
  SetType setType;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    DatabaseFactory.setDbConnData(new DBConnectionData(
        "jdbc:hsqldb:mem:ppm_db", "testuser", "", null));
    db = DatabaseFactory.createDatabase(Misc.TEST_HIBERNATE_CONFIG_FILE);
  }

  @Override
  public void tearDown() throws Exception {
    db.clear();
    DatabaseFactory.reset();
  }

  /**
   * Tests all database operations regarding {@link Parameter} objects.
   * 
   * @throws Exception
   *           when something fails
   */
  public void testParameterOperations() throws Exception {
    param = db.newParameter("test1", true, MatrixDimension.SINGLE,
        MatrixDimension.AGES, Population.NATIVES);
    assertNotNull(EXPL_INSTANTIATION, param);
    assertEquals(EXPL_ID_SAVE, 1, param.getID());
    Parameter param2 = db.newParameter("test1", true, MatrixDimension.SINGLE,
        MatrixDimension.AGES, Population.NATIVES);
    assertEquals(EXPL_UNIQUE, 1, param2.getID());
    Parameter param3 = db.getParameter("test1");
    assertEquals(EXPL_ONE_ENTITY, param2, param3);
    param2 = db.newParameter("test2", true, MatrixDimension.SINGLE,
        MatrixDimension.AGES, Population.NATIVES);
    List<Parameter> parameters = db.getAllParameters();
    assertEquals(EXPL_TWO_ENTITIES, 2, parameters.size());
    assertTrue(EXPL_DELETION, db.deleteParameter(param2));
    parameters = db.getAllParameters();
    assertEquals(EXPL_ONE_ENTITY, 1, parameters.size());
    assertEquals(EXPL_EQUAL_FIRST, param, parameters.get(0));
  }

  /**
   * Tests database operations for {@link ParameterInstance} objects.
   * 
   * @throws Exception
   *           when something fails
   */
  public void testParameterInstanceOperations() throws Exception {
    testParameterOperations();
    parameterInstanceOperations();
  }

  public void parameterInstanceOperations() throws Exception {
    instance = db.newParameterInstance(1, param, 1);
    assertNotNull(EXPL_INSTANTIATION, instance);
    assertEquals(EXPL_ID_SAVE, 1, instance.getID());
    ParameterInstance instance2 = db.newParameterInstance(1, param, 1);
    assertEquals(EXPL_UNIQUE, 1, instance2.getID());
    ParameterInstance instance3 = db.getParameterInstance(param, 1);
    assertEquals(EXPL_ONE_ENTITY, instance2, instance3);
    instance2 = db.newParameterInstance(2, param, 2);
    List<ParameterInstance> instances = db.getAllParameterInstances();
    assertEquals(EXPL_TWO_ENTITIES, 2, instances.size());
    assertTrue(EXPL_DELETION, db.deleteParameterInstance(instance2));
    instances = db.getAllParameterInstances();
    assertEquals(EXPL_ONE_ENTITY, 1, instances.size());
    assertEquals(EXPL_EQUAL_FIRST, instance, instances.get(0));
  }

  /**
   * Tests all matrix-related operations.
   * 
   * @throws Exception
   *           if database operations fail
   */
  public void testMatrixOperations() throws Exception {
    testParameterOperations();
    parameterInstanceOperations();
    matrixOperations();
  }

  public void matrixOperations() throws Exception {
    // Set up everything required
    matrix1 = db.newMatrix(new Matrix2D(TEST_SQUARE_MATRIX_WIDTH,
        TEST_SQUARE_MATRIX_WIDTH));
    assertNotNull(EXPL_INSTANTIATION, matrix1);
    assertEquals(EXPL_ID_SAVE, 1, matrix1.getID());
    matrix2 = db.newMatrix(new Matrix2D(TEST_SQUARE_MATRIX_WIDTH,
        TEST_SQUARE_MATRIX_WIDTH));
    assertEquals(EXPL_UNIQUE, 1, matrix2.getID());
    Matrix matrix3 = db.getMatrix(new Matrix2D(TEST_SQUARE_MATRIX_WIDTH,
        TEST_SQUARE_MATRIX_WIDTH));
    assertEquals(EXPL_ONE_ENTITY, matrix2, matrix3);
    Matrix2D matValue = new Matrix2D(TEST_SQUARE_MATRIX_WIDTH,
        TEST_SQUARE_MATRIX_WIDTH);
    matValue.setQuick(0, 0, 1);
    matrix2 = db.newMatrix(matValue);
    List<Matrix> matrices = db.getAllMatrices();
    assertEquals(EXPL_TWO_ENTITIES, 2, matrices.size());
    assertTrue(EXPL_DELETION, db.deleteMatrix(matrix2));
    matrices = db.getAllMatrices();
    assertEquals(EXPL_ONE_ENTITY, 1, matrices.size());
    assertEquals(EXPL_EQUAL_FIRST, matrix1, matrices.get(0));
    matrix2 = db.newMatrix(matValue);

  }

  public void testParameterAssignmentOperations() throws Exception {
    testParameterOperations();
    parameterInstanceOperations();
    matrixOperations();
    parameterAssignmentOperations();
  }

  public void parameterAssignmentOperations() throws Exception {
    assignment1 = db.newParameterAssignment(instance, "assignment1", "no desc",
        1, 0.2, matrix1.getValue().copy());
    assignment2 = db.newParameterAssignment(instance, "assignment2",
        "no desc, again", 1, 0.2, matrix2.getValue().copy());
    assertEquals("Two assignments have been saved.", 2, db
        .getAllParameterAssignments(instance).size());
    ParameterAssignment pa = db.newParameterAssignment(instance,
        "special assignment", "-", 1, 0.2, new Matrix2D(1, 1));
    assertEquals("Three assignments have been saved.", 3, db
        .getAllParameterAssignments(instance).size());
    db.deleteParameterAssignment(pa);
    assertEquals("Two assignments due to deletion.", 2, db
        .getAllParameterAssignments(instance).size());
  }

  public void testSetOperations() throws Exception {
    testParameterOperations();
    parameterInstanceOperations();
    matrixOperations();
    parameterAssignmentOperations();
    setOperations();
  }

  public void setOperations() throws Exception {
    List<ParameterInstance> instances = new ArrayList<ParameterInstance>();
    instances.add(instance);
    set = db.newSet(instances, "set1", "set1desc", 0.5);
    set.addParameterAssignment(assignment1);
    set.addParameterAssignment(assignment2);
    db.saveSet(set);
    assertEquals(EXPL_ONE_ENTITY, 1, db.getAllSets().size());
    Set set2 = db.newSet(instances, "set2", "set2desc", 0.5);
    assertEquals(EXPL_TWO_ENTITIES, 2, db.getAllSets().size());
    db.deleteSet(set2);
    assertEquals(EXPL_ONE_ENTITY, 1, db.getAllSets().size());
  }

  public void testSetTypeOperations() throws Exception {
    testParameterOperations();
    parameterInstanceOperations();
    matrixOperations();
    parameterAssignmentOperations();
    setOperations();
    setTypeOperations();
  }

  public void setTypeOperations() throws Exception {
    setType = db.newSetType("SetType1", "setType description");
    setType.addInstance(instance);
    setType.createSet("myTestSet", "Set of SetType1", 1);
    db.saveSetType(setType);
    assertNotNull(EXPL_INSTANTIATION, setType);
    assertEquals(EXPL_ONE_ENTITY, 1, db.getAllSetTypes().size());
    assertEquals(EXPL_TWO_ENTITIES, 2, db.getAllSets().size());
    SetType st2 = db.newSetType("SetType2", "Another setType description");
    assertEquals(EXPL_TWO_ENTITIES, 2, db.getAllSetTypes().size());
    db.deleteSeType(st2);
    assertEquals(EXPL_ONE_ENTITY, 1, db.getAllSetTypes().size());
  }

  public void testProjectionOperations() throws Exception {
    projectionOperations();
  }

  public void projectionOperations() throws Exception {
    ProjectionModel projection = new ProjectionModel("test scenario",
        "Scenario descrption", TEST_GENERATIONS, TEST_PRED_YEARS, TEST_MAX_AGE,
        TEST_JUMP_OFF_YEAR);
    db.newProjection(projection);
    assertNotNull(EXPL_INSTANTIATION, projection);
    SetType sType = projection.createSetType("SetType1",
        "setType description for a concrete Projection");
    projection.assignParameterInstance(projection.getAllParameterInstances()
        .get(0), sType, false);
    db.saveProjection(projection);
    List<ProjectionModel> projections = db.getAllProjections();
    assertEquals(EXPL_ONE_ENTITY, 1, projections.size());
    assertEquals(EXPL_ONE_ENTITY, 1, projections.get(0).getUserDefinedTypes()
        .get(0).getDefinedParameters().size());
    ProjectionModel projection2 = new ProjectionModel("test scenario2",
        "Scenario descrption 2", TEST_GENERATIONS + 1, TEST_PRED_YEARS,
        TEST_MAX_AGE, TEST_JUMP_OFF_YEAR);
    db.newProjection(projection2);
    projections = db.getAllProjections();
    assertEquals(EXPL_TWO_ENTITIES, 2, projections.size());
    db.deleteProjection(projection2, null);
    projections = db.getAllProjections();
    assertEquals(EXPL_ONE_ENTITY, 1, projections.size());
  }

  public void testResultStorage() {

  }
}
