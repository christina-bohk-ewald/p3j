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
package p3j.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hsqldb.jdbcDriver;
import org.jamesii.core.data.DBConnectionData;
import org.w3c.dom.Document;

import p3j.database.DatabaseType;
import p3j.simulation.ExecutionMode;

import com.mysql.jdbc.Driver;

/**
 * Miscellaneous (auxiliary) functions and constants. For simulation-related
 * constants, see {@link p3j.simulation.calculation.deterministic.Constants}.
 * 
 * Created on February 12, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public final class Misc {

  /**
   * The delimiter used by hibernate to name classes. For example, a hibernate
   * object representing p3j.pppm.sets.Set would be of type
   * p3j.pppm.sets.Set$$CGLibetc.
   */
  private static final String DELIMITER_OF_HIBERNATE_TYPES = "$$";

  /** Base of numerical system that is used. */
  public static final int BASE_NUM = 10;

  /** Number format to be used when editing matrices. */
  public static final DecimalFormat NUMBER_FORMAT = new DecimalFormat(
      "0.00000000");

  /** Minimal precision value (used to avoid rounding problems). */
  public static final double EPSILON = 0.0000000001;

  /** The name of the configuration file. */
  public static final String CONFIG_FILE = "conf/config.xml";

  /** The default execution mode. */
  public static final ExecutionMode DEFAULT_EXEC_MODE = ExecutionMode.MONTE_CARLO;

  /** The default location of the hibernate configuration file. */
  public static final String DEFAULT_HIBERNATE_CONFIG_FILE = "conf/hibernate.cfg.xml";

  /** The default location of the hibernate configuration file for testing. */
  public static final String TEST_HIBERNATE_CONFIG_FILE = "test.hibernate.cfg.xml";

  /** The default number of trials. */
  public static final int DEFAULT_NUM_TRIALS = 1000;

  /** The default number of parallel threads. */
  public static final int DEFAULT_NUM_PARALLEL_THREADS = 1;

  /**
   * The maximal number of sub-node elements to be shown in the panel. Limit
   * this improves GUI performance when selecting large aggregates, such as the
   * overall trial results.
   */
  public static final int MAX_SUBNODE_SUMMARY_ELEMENTS = 200;

  // Keys for the configuration file:

  /** The key for the number of trials. */
  public static final String PREF_DB_TYPE = "Database Type";

  /** The key for the database URL. */
  public static final String PREF_DB_URL = "Database URL";

  /** The key for the database user name. */
  public static final String PREF_DB_USER = "Database User Name";

  /** The key for the database password. */
  public static final String PREF_DB_PWD = "Database Password (currently stored unencrpyted!)";

  /** The key for the number of trials. */
  public static final String PREF_NUM_TRIALS = "Number of Trials (multiples of Parallel Threads only)";

  /** The key for the number of parallel threads. */
  public static final String PREF_NUM_PARALLEL_THREADS = "Parallel Threads (Monte-Carlo only)";

  /** The key for the execution mode. */
  public static final String PREF_EXECUTION_MODE = "Execution Mode";

  // Default database configuration

  /** The URL prefix for MySQL URLs. */
  public static final String MYSQL_URL_PREFIX = "jdbc:mysql://";

  /** The URL prefix for HSQLDB URLs. */
  public static final String HSQLDB_URL_PREFIX = "jdbc:hsqldb:file:";

  /** The file name for HSQLDB. */
  public static final String HSQLDB_FILE_NAME = "pppm_db";

  /** The hibernate property to read out the dialect that is used. */
  public static final String PREF_HIBERNATE_DIALECT_PROPERTY = "hibernate.dialect";

  /** The hibernate property to read out the JDBC driver that is used. */
  public static final String PREF_HIBERNATE_DRIVER_PROPERTY = "hibernate.connection.driver_class";

  /** The default database type. */
  public static final DatabaseType DEFAULT_DB_TYPE = DatabaseType.HSQLDB;

  /** The supported database dialects. */
  public static final Map<DatabaseType, String> HIBERNATE_DIALECTS = new HashMap<>();
  static {
    HIBERNATE_DIALECTS.put(DatabaseType.HSQLDB,
        HSQLDialect.class.getCanonicalName());
    HIBERNATE_DIALECTS.put(DatabaseType.MYSQL,
        MySQL5Dialect.class.getCanonicalName());
    HIBERNATE_DIALECTS.put(DatabaseType.GENERIC,
        MySQL5Dialect.class.getCanonicalName());
  }

  public static final Map<DatabaseType, String> JDBC_DRIVERS = new HashMap<>();
  static {
    JDBC_DRIVERS.put(DatabaseType.HSQLDB, jdbcDriver.class.getCanonicalName());
    JDBC_DRIVERS.put(DatabaseType.MYSQL, Driver.class.getCanonicalName());
    JDBC_DRIVERS.put(DatabaseType.GENERIC, Driver.class.getCanonicalName());
  }

  /** The default database URLs. */
  public static final Map<DatabaseType, String> DEFAULT_DB_URLS = new HashMap<>();
  static {
    DEFAULT_DB_URLS.put(DatabaseType.HSQLDB, HSQLDB_URL_PREFIX + "."
        + File.separator + HSQLDB_FILE_NAME);
    DEFAULT_DB_URLS.put(DatabaseType.MYSQL, MYSQL_URL_PREFIX
        + "localhost/pppm_db");
    DEFAULT_DB_URLS.put(DatabaseType.GENERIC, MYSQL_URL_PREFIX
        + "localhost/pppm_db");
  }

  /** The default database user names. */
  public static final Map<DatabaseType, String> DEFAULT_DB_USERS = new HashMap<>();
  static {
    DEFAULT_DB_USERS.put(DatabaseType.HSQLDB, "user");
    DEFAULT_DB_USERS.put(DatabaseType.MYSQL, "root");
    DEFAULT_DB_USERS.put(DatabaseType.GENERIC, "root");
  }

  /** The default database passwords. */
  public static final Map<DatabaseType, String> DEFAULT_DB_PWDS = new HashMap<>();
  static {
    DEFAULT_DB_PWDS.put(DatabaseType.HSQLDB, "");
    DEFAULT_DB_PWDS.put(DatabaseType.MYSQL, "root");
    DEFAULT_DB_PWDS.put(DatabaseType.GENERIC, "");
  }

  /** Default setup for database connection. */
  public static final DBConnectionData DEFAULT_DB_CONN = DEFAULT_DB_TYPE
      .getDefaults().getFirstValue();

  // Some common GUI Labels

  /** The Constant GUI_LABEL_PROBABILITY. */
  public static final String GUI_LABEL_PROBABILITY = "Probability (in [0,1]):";

  /** The Constant GUI_LABEL_NAME. */
  public static final String GUI_LABEL_NAME = "Name:";

  /** The Constant GUI_LABEL_DESCRIPTION. */
  public static final String GUI_LABEL_DESCRIPTION = "Description:";

  /** The Constant GUI_LABEL_DEVIATION. */
  public static final String GUI_LABEL_DEVIATION = "Standard deviation";

  /** The label for the jump-off year in the GUI. */
  public static final String GUI_LABEL_JUMP_OFF_YEAR = "Jump-Off Year";

  /** The number of age classes. */
  public static final String GUI_LABEL_NUM_AGE_CLASSES = "Number of Age Classes:";

  /** The number of descendant generations. */
  public static final String GUI_LABEL_DESCENDANT_GENERATIONS = "Descendant Generations:";

  /** The projection horizon. */
  public static final String GUI_LABEL_PROJECTION_HORIZON = "Projection Horizon:";

  /** The label for the JDBC driver. */
  public static final String GUI_LABEL_DB_DRIVER_CLASS = "JDBC Driver (needs to be in classpath)";

  /** The label for the Hibernate dialect. */
  public static final String GUI_LABEL_HIBERNATE_DIALECT = "Hibernate dialect class";

  /** The label for entering the location of the database. */
  public static final String GUI_LABEL_DB_FILE_LOCATION = "Location of database";

  /**
   * This class should not be instantiated.
   */
  private Misc() {
  }

  /**
   * Retrieves file ending.
   * 
   * @param file
   *          the file name
   * 
   * @return file ending
   */
  public static String getFileEnding(File file) {
    return file.getName().substring(file.getName().lastIndexOf('.') + 1);
  }

  /**
   * Parses string to double. Replaces ',' by '.'.
   * 
   * @param value
   *          string to be parsed
   * 
   * @return double value
   */
  public static double parseToDouble(Object value) {
    return Double.parseDouble(value.toString().replace(',', '.'));
  }

  /**
   * Converts a string into a probability (a double that has to be in [0,1]).
   * 
   * @param probString
   *          string containing a probability
   * 
   * @return double value if valid, otherwise 0
   */
  public static double parseToDoubleProb(String probString) {
    double probability = parseToDouble(probString);
    if (probability < 0 || probability > 1) {
      return 0;
    }
    return probability;
  }

  /**
   * Two values are recognised as equal as long as their absolute differece does
   * not exceed EPSILON.
   * 
   * @param x
   *          the first value
   * @param y
   *          the second value
   * 
   * @return true if |x-y| < e
   */
  public static boolean numEqual(double x, double y) {
    return Math.abs(x - y) < EPSILON;
  }

  /**
   * Parses string to integer.
   * 
   * @param numString
   *          the string containing the number
   * 
   * @return integer value of string
   */
  public static int parseToInt(String numString) {
    return Integer.parseInt(numString);
  }

  /**
   * Rounds a value to a certain number of digits after the comma.
   * 
   * @param value
   *          the value
   * @param digits
   *          the desired maximum number of digits after the comma
   * 
   * @return value value rounded to a given number of digits after the comma
   */
  public static double round(double value, int digits) {
    return ((int) (value * Math.pow(BASE_NUM, digits)) / Math.pow(BASE_NUM,
        digits));
  }

  /**
   * Writes a document to a file.
   * 
   * @param fileName
   *          the name of the file
   * @param document
   *          the document to be written
   * 
   * @throws FileNotFoundException
   *           if destination file could not be found/created
   * @throws TransformerException
   *           if transformation of document to XML failed
   */
  @Deprecated
  public static void writeDocumentToFile(String fileName, Document document)
      throws FileNotFoundException, TransformerException {

    File f = new File(fileName);

    FileOutputStream fop = new FileOutputStream(f);

    // Writing document...
    TransformerFactory tff = TransformerFactory.newInstance();
    Transformer tf = tff.newTransformer();

    tf.setOutputProperty(OutputKeys.METHOD, "xml");
    tf.setOutputProperty(OutputKeys.INDENT, "yes");

    DOMSource source = new DOMSource(document);
    StreamResult result = new StreamResult(fop);

    tf.transform(source, result);
  }

  /**
   * Auto-casts any kind of object to the desired case. This is a work-around
   * for Java's many insufficiencies regarding generics etc. Handle with care,
   * only use this when you know what you are doing!
   * 
   * @param o
   *          the object
   * 
   * @param <X>
   *          the desired type
   * 
   * @return the object, now casted to the desired type
   */
  @SuppressWarnings("unchecked")
  public static <X> X autoCast(Object o) {
    return (X) o;
  }

  /**
   * Checks whether two classes are the same. This is tricky here: Hibernate
   * enhances/replaces existing types, so classes from DB and from JVM are not
   * the same! Classes for the DB have '_$$_...' etc. with them.
   * 
   * @param class1
   *          one class
   * @param class2
   *          the other class
   * 
   * @return true if classes are the same, otherwise false
   */
  public static boolean checkClassEquality(Class<?> class1, Class<?> class2) {
    return getCleanedClassName(class1).compareTo(getCleanedClassName(class2)) == 0;
  }

  /**
   * Cleans up class name by removing everything after '_$$_', which is used by
   * Hibernate to identify generated custom classes
   * ('my.Class_$$_javassist...').
   * 
   * @param theClass
   *          the class for which the name should be cleaned up
   * 
   * @return the cleaned-up class name
   */
  public static String getCleanedClassName(Class<?> theClass) {
    String name = theClass.getCanonicalName();
    int cutOffIndex = name.indexOf(DELIMITER_OF_HIBERNATE_TYPES);
    return cutOffIndex < 0 ? name : name.substring(0, cutOffIndex);
  }

  /**
   * Merges list of lists to a newly created {@link ArrayList}.
   * 
   * @param listsToMerge
   *          the lists to merge
   * @param <D>
   *          the type of the list elements
   * @return the new list, containing all elements of the others
   */
  public static <D> List<D> mergeList(List<D>... listsToMerge) {
    List<D> result = new ArrayList<D>();
    for (List<D> listToMerge : listsToMerge) {
      result.addAll(listToMerge);
    }
    return result;
  }

  /**
   * Inverts a map.
   * 
   * @param src
   *          the content
   * @param dest
   *          the destination map to be filled (will be cleared at first)
   * 
   * @param <X>
   *          the type of the original keys
   * @param <Y>
   *          the type of the original values
   */
  public static <X, Y> void invertMap(Map<X, Y> src, Map<Y, X> dest) {
    dest.clear();
    for (Entry<X, Y> entry : src.entrySet()) {
      if (dest.containsKey(entry.getValue())) {
        throw new IllegalArgumentException("Passed map contains value '"
            + entry + "' twice, cannot be inverted.");
      }
      dest.put(entry.getValue(), entry.getKey());
    }
  }

}
