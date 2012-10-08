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

import james.SimSystem;
import james.core.util.misc.Strings;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import p3j.database.IP3MDatabase;
import p3j.misc.math.Matrix;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.Parameter;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterAssignmentSet;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * 
 * Class that stores and loads serializable classes. Needed to store and load
 * parameter files.
 * 
 * Created on February 12, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class Serializer {

  /**
   * Flag that indicates whether objects should be stored in XML or in binary
   * encoding.
   */
  private boolean usingXML = true;

  /**
   * Flag that indicates whether GZIP compression (LZW) is used.
   */
  private boolean usingCompression = false;

  /**
   * Loads object from file.
   * 
   * @param file
   *          path to file with the object to be loaded
   * @return object the object that has been loaded
   * @throws IOException
   *           if file was not found, file input failed, etc.
   * @throws ClassNotFoundException
   *           if class of persistent object could not be found
   */
  public Object load(String file) throws IOException, ClassNotFoundException {
    if (usingXML) {
      return loadFromXML(file);
    }
    return loadFromBinary(file);
  }

  /**
   * Load object from a binary file.
   * 
   * @param file
   *          path and file name
   * @return deserialised object
   * @throws IOException
   *           if file was not found, etc.
   * @throws ClassNotFoundException
   *           if class of persistent object could not be found
   */
  public Object loadFromBinary(String file) throws IOException,
      ClassNotFoundException {
    ObjectInputStream input = new ObjectInputStream(getInputStream(file));
    Object o = input.readObject();
    input.close();
    return o;
  }

  /**
   * Load object from XML file.
   * 
   * @param file
   *          path and file name
   * @return deserialised object
   * @throws IOException
   *           if file was not found, a read error occurred, etc.
   */
  public Object loadFromXML(String file) throws IOException {
    XMLDecoder xmlDecoder = new XMLDecoder(getInputStream(file));
    Object object = xmlDecoder.readObject();
    xmlDecoder.close();
    return object;
  }

  /**
   * Create an input stream.
   * 
   * @param file
   *          source file
   * @return input stream from file
   * @throws IOException
   *           if stream creation fails
   */
  protected InputStream getInputStream(String file) throws IOException {
    InputStream in = new BufferedInputStream(new FileInputStream(file));
    if (isUsingCompression()) {
      in = new GZIPInputStream(in);
    }
    return in;
  }

  /**
   * Create an output stream.
   * 
   * @param file
   *          target file
   * @return output stream to file
   * @throws IOException
   *           if stream creation fails
   */
  protected OutputStream getOutputStream(String file) throws IOException {
    OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
    if (isUsingCompression()) {
      out = new GZIPOutputStream(out);
    }
    return out;
  }

  /**
   * Save object to file.
   * 
   * @param object
   *          the object to be saved in the file
   * @param file
   *          the file
   * @throws IOException
   *           if outputting went wrong
   */
  public void save(ProjectionModel pm, String file) throws IOException {
    ProjectionModel modelToSave = copyProjection(pm);
    if (usingXML) {
      saveToXML(modelToSave, file);
    } else {
      saveToBinary(modelToSave, file);
    }
  }

  /**
   * Save object to binary file.
   * 
   * @param object
   *          the object to be written
   * @param file
   *          the target file
   * @throws IOException
   *           if writing fails
   */
  public void saveToBinary(Object object, String file) throws IOException {
    ObjectOutputStream output = new ObjectOutputStream(getOutputStream(file));
    output.writeObject(object);
    output.close();
  }

  /**
   * Save object to XML file.
   * 
   * @param object
   *          the object to be written
   * @param file
   *          the target file
   * @throws IOException
   *           if writing fails
   */
  public void saveToXML(Object object, String file) throws IOException {
    XMLEncoder xmlEncoder = new XMLEncoder(getOutputStream(file));
    xmlEncoder.writeObject(object);
    xmlEncoder.close();
  }

  public boolean isUsingXML() {
    return usingXML;
  }

  public void setUsingXML(boolean usingXML) {
    this.usingXML = usingXML;
  }

  public boolean isUsingCompression() {
    return usingCompression;
  }

  public void setUsingCompression(boolean usingCompression) {
    this.usingCompression = usingCompression;
  }

  /**
   * Creates a full, deep copy of this projection model. This is necessary for
   * serialization, because hibernate is used with lazy evaluation, so that
   * non-serializable hibernate-specific collections are used internally. The
   * copy only relies on serializable collections, so it can be serialized
   * easily.
   * 
   * @return a full copy of the projection model, including copies of all
   *         sub-elements (set types, matrices, etc.)
   */
  private ProjectionModel copyProjection(ProjectionModel original) {
    ProjectionModel copy = new ProjectionModel();
    copySimpleFields(original, copy);
    Map<ParameterInstance, ParameterInstance> paramInstances = copyParameterInstances(
        original, copy);
    Map<Set, Set> sets = copySets(original, copy, paramInstances);
    Map<SetType, SetType> setTypes = copySetTypes(original, copy,
        paramInstances, sets);
    copyParamInstToSetTypeMapping(original, copy, paramInstances, setTypes);
    return copy;
  }

  /**
   * Copies all simple fields of the projection model.
   * 
   * @param original
   *          the original
   * @param copy
   *          the copy
   */
  private void copySimpleFields(ProjectionModel original, ProjectionModel copy) {
    copy.setName(original.getName());
    copy.setDescription(original.getDescription());
    copy.setJumpOffYear(original.getJumpOffYear());
    copy.setMaximumAge(original.getMaximumAge());
    copy.setYears(original.getYears());
    copy.setGenerations(original.getGenerations());
  }

  /**
   * Copies parameter instances (and parameters) from the original projection to
   * the copy.
   * 
   * @param original
   *          the original
   * @param copy
   *          the copy
   * @return mapping from old parameter instances to their corresponding new
   *         parameter instances, which is necessary for creating sets and set
   *         types with the same structure
   */
  private Map<ParameterInstance, ParameterInstance> copyParameterInstances(
      ProjectionModel original, ProjectionModel copy) {
    Map<ParameterInstance, ParameterInstance> paramInstances = new HashMap<>();
    List<ParameterInstance> listOfNewParamInstances = new ArrayList<>();
    for (ParameterInstance paramInstance : original.getAllParameterInstances()) {
      ParameterInstance piCopy = copyParameterInstance(paramInstance);
      paramInstances.put(paramInstance, piCopy);
      listOfNewParamInstances.add(piCopy);
    }
    copy.setAllParameterInstances(listOfNewParamInstances);
    return paramInstances;
  }

  /**
   * Copies all sets of the original model to the copy.
   * 
   * @param original
   *          the original
   * @param copy
   *          the copy
   * @param paramInstances
   *          the mapping from old to new parameter instances
   * @return the mapping from old sets to their corresponding copies, which is
   *         necessary to construct set types with the same structure
   */
  private Map<Set, Set> copySets(ProjectionModel original,
      ProjectionModel copy,
      Map<ParameterInstance, ParameterInstance> paramInstances) {
    Map<Set, Set> sets = new HashMap<>();
    Set newDefaultSet = copySet(original.getDefaultSet(), paramInstances);
    copy.setDefaultSet(newDefaultSet);
    sets.put(original.getDefaultSet(), newDefaultSet);
    for (SetType setType : original.getAllSetTypes())
      for (Set set : setType.getSets()) {
        Set setCopy = copySet(set, paramInstances);
        sets.put(set, setCopy);
      }
    return sets;
  }

  /**
   * Copies set types from the original projection to the copy.
   * 
   * @param original
   *          the original
   * @param copy
   *          the copy
   * @param paramInstances
   *          the mapping from old to new parameter instances
   * @param sets
   *          the mapping from old to new sets
   * @return the mapping from old to new set types, required to correctly update
   *         the internal structure of the projection (see
   *         {@link Serializer#copyParamInstToSetTypeMapping(ProjectionModel, ProjectionModel, Map, Map)}
   *         )
   */
  private Map<SetType, SetType> copySetTypes(ProjectionModel original,
      ProjectionModel copy,
      Map<ParameterInstance, ParameterInstance> paramInstances,
      Map<Set, Set> sets) {
    Map<SetType, SetType> setTypes = new HashMap<>();
    SetType newDefaultSetType = copySetType(original.getDefaultSetType(),
        paramInstances, sets);
    copy.setDefaultType(newDefaultSetType);
    setTypes.put(original.getDefaultSetType(), newDefaultSetType);
    List<SetType> listOfNewUserDefSetTypes = new ArrayList<>();
    for (SetType setType : original.getUserDefinedTypes()) {
      SetType stCopy = copySetType(setType, paramInstances, sets);
      setTypes.put(setType, stCopy);
      listOfNewUserDefSetTypes.add(stCopy);
    }
    copy.setUserDefinedTypes(listOfNewUserDefSetTypes);
    return setTypes;
  }

  /**
   * Copies the mapping from parameter instances to the set types that manage
   * them from the original projection to the copy.
   * 
   * @param copy
   *          the copy
   * @param original
   *          the original
   * @param paramInstances
   *          the mapping from old to new parameter instances
   * @param setTypes
   *          the mapping from old to new set types
   */
  private void copyParamInstToSetTypeMapping(ProjectionModel original,
      ProjectionModel copy,
      Map<ParameterInstance, ParameterInstance> paramInstances,
      Map<SetType, SetType> setTypes) {
    Map<ParameterInstance, SetType> newInstanceSetTypesMap = new HashMap<>();
    for (Entry<ParameterInstance, SetType> instSetTypeEntry : original
        .getInstanceSetTypes().entrySet()) {
      newInstanceSetTypesMap.put(paramInstances.get(instSetTypeEntry.getKey()),
          setTypes.get(instSetTypeEntry.getValue()));
    }
    copy.setInstanceSetTypes(newInstanceSetTypesMap);
  }

  /**
   * Copies a parameter instance.
   * 
   * @param paramInstance
   *          the original parameter instance
   * @return the copy
   */
  private ParameterInstance copyParameterInstance(
      ParameterInstance paramInstance) {
    Parameter param = paramInstance.getParameter();
    return new ParameterInstance(paramInstance.getComparisonIndex(),
        new Parameter(param.getID(), param.isGenerationDependent(),
            param.getName(), param.getValueHeight(), param.getValueWidth(),
            param.getPopulation()), paramInstance.getGeneration());
  }

  /**
   * Copies a set.
   * 
   * @param set
   *          the original set
   * @param paramInstances
   *          the mapping from old to new parameter instances
   * @return the copy
   */
  private Set copySet(Set set,
      Map<ParameterInstance, ParameterInstance> paramInstances) {
    Set copy = new Set();
    copy.setName(set.getName());
    copy.setDescription(set.getDescription());
    copy.setProbability(set.getProbability());

    Map<ParameterInstance, ParameterAssignmentSet> copyOfSetData = new HashMap<>();
    for (Entry<ParameterInstance, ParameterAssignmentSet> setDataEntry : set
        .getSetData().entrySet()) {
      copyOfSetData.put(paramInstances.get(setDataEntry.getKey()),
          copyParamAssignmentSet(setDataEntry.getValue(), paramInstances));
    }

    copy.setSetData(copyOfSetData);
    return copy;
  }

  /**
   * Copies a parameter assignment set.
   * 
   * @param paramAssignmentSet
   *          the original parameter assignment set
   * @param paramInstances
   *          the map from old to new parameter instances
   * @return the copy
   */
  private ParameterAssignmentSet copyParamAssignmentSet(
      ParameterAssignmentSet paramAssignmentSet,
      Map<ParameterInstance, ParameterInstance> paramInstances) {
    ParameterAssignmentSet copy = new ParameterAssignmentSet();
    for (ParameterAssignment assignment : paramAssignmentSet.getAssignments()) {
      copy.add(new ParameterAssignment(paramInstances.get(assignment
          .getParamInstance()), assignment.getName(), assignment
          .getDescription(), assignment.getProbability(), assignment
          .getDeviation(), assignment.getMatrix().copy()));
    }
    return copy;
  }

  /**
   * Copies a set type.
   * 
   * @param setType
   *          the original set type
   * @param paramInstances
   *          the mapping from old to new parameter instances
   * @param sets
   *          the mapping from old to new sets
   * @return the copy
   */
  private SetType copySetType(SetType setType,
      Map<ParameterInstance, ParameterInstance> paramInstances,
      Map<Set, Set> sets) {
    SetType copy = new SetType(setType.getName(), setType.getDescription());

    List<ParameterInstance> copyDefinedParameters = new ArrayList<>();
    for (ParameterInstance paramInstance : setType.getDefinedParameters())
      copyDefinedParameters.add(paramInstances.get(paramInstance));
    List<Set> copySets = new ArrayList<>();
    for (Set set : setType.getSets())
      copySets.add(sets.get(set));

    copy.setDefinedParameters(copyDefinedParameters);
    copy.setSets(copySets);

    return copy;
  }

  /**
   * Loads a projection into the database. In some sense, this method is the
   * inverse of @link {@link Serializer#copyProjection(ProjectionModel)}, as it
   * makes sure that the newly loaded projection model is properly managed by
   * hibernate. The easiest way to do so is by storing it as a new projection.
   * 
   * @param absolutePath
   *          the absolute path
   * @param database
   *          the database
   * @return the projection model
   * @throws ClassNotFoundException
   *           the class not found exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public ProjectionModel loadProjection(String absolutePath,
      IP3MDatabase database) throws ClassNotFoundException, IOException,
      LoadedProjectionFormatException {
    List<String> warnings = new ArrayList<>();
    ProjectionModel loadedProjection = (ProjectionModel) load(absolutePath);

    ProjectionModel newProjection = new ProjectionModel();
    copySimpleFields(loadedProjection, newProjection);
    database.newProjection(newProjection);

    Map<ParameterInstance, ParameterInstance> paramInstances = matchParameterInstances(
        loadedProjection, newProjection, warnings);
    Map<SetType, SetType> setTypes = saveSetTypes(loadedProjection,
        newProjection, paramInstances);
    database.saveProjection(newProjection);
    saveSets(loadedProjection, newProjection, paramInstances, setTypes,
        database);

    // TODO: Make this visible to user
    for (String warning : warnings)
      SimSystem.report(Level.WARNING, warning);

    database.saveProjection(newProjection);
    return newProjection;
  }

  /**
   * Matches loaded parameter instances to those stored in the database.
   * 
   * @param loadedProjection
   *          the loaded projection
   * @param newProjection
   *          the new projection
   * @param warnings
   *          the list of warnings to be handed over to the user
   * @return the map
   * @throws LoadedProjectionFormatException
   *           in case no exact one-to-one matching could be found
   */
  private Map<ParameterInstance, ParameterInstance> matchParameterInstances(
      ProjectionModel loadedProjection, ProjectionModel newProjection,
      List<String> warnings) throws LoadedProjectionFormatException {

    Map<ParameterInstance, ParameterInstance> matching = new HashMap<>();
    List<ParameterInstance> oldInstances = new ArrayList<>(
        loadedProjection.getAllParameterInstances());

    for (final ParameterInstance newInstance : newProjection
        .getAllParameterInstances()) {

      List<ParameterInstance> matchCandidates = new ArrayList<>();

      for (ParameterInstance oldInstance : oldInstances)
        if (newInstance.getComparisonIndex() == oldInstance
            .getComparisonIndex()
            && newInstance.getGeneration() == oldInstance.getGeneration()
            && newInstance.getValueHeight() == oldInstance.getValueHeight()
            && newInstance.getValueWidth() == oldInstance.getValueWidth()
            && newInstance.getParameter().getPopulation() == oldInstance
                .getParameter().getPopulation()
            && newInstance.getParameter().isGenerationDependent() == oldInstance
                .getParameter().isGenerationDependent()) {
          matchCandidates.add(oldInstance);
        }

      if (matchCandidates.isEmpty())
        throw new LoadedProjectionFormatException(
            "No match found for parameter instance " + newInstance);

      // Sort potential matches by smallest Levenshtein distance to parameter
      // name
      ParameterInstance bestMatch = Collections.min(matchCandidates,
          new Comparator<ParameterInstance>() {
            final String targetName = newInstance.getParameter().getName();

            @Override
            public int compare(ParameterInstance inst1, ParameterInstance inst2) {
              return Integer.compare(Strings.getLevenshteinDistance(inst1
                  .getParameter().getName(), targetName), Strings
                  .getLevenshteinDistance(inst2.getParameter().getName(),
                      targetName));
            }
          });

      matching.put(bestMatch, newInstance);
      System.err.println("Matched '" + bestMatch + "' to '" + newInstance
          + "'.");
      if (!bestMatch.getParameter().getName()
          .equals(newInstance.getParameter().getName()))
        warnings.add("Could not find perfect match for parameter '"
            + newInstance.getParameter() + "', using best match '"
            + bestMatch.getParameter());

      oldInstances.remove(bestMatch);
    }

    return matching;
  }

  private Map<SetType, SetType> saveSetTypes(ProjectionModel loadedProjection,
      ProjectionModel newProjection,
      Map<ParameterInstance, ParameterInstance> paramInstances) {

    Map<SetType, SetType> setTypes = new HashMap<>();

    setTypes.put(loadedProjection.getDefaultSetType(),
        newProjection.getDefaultSetType());

    for (SetType setType : loadedProjection.getUserDefinedTypes()) {
      SetType newSetType = newProjection.createSetType(setType.getName(),
          setType.getDescription());
      setTypes.put(setType, newSetType);
      for (ParameterInstance paramInst : setType.getDefinedParameters()) {
        ParameterInstance newParamInst = paramInstances.get(paramInst);
        newSetType.addInstance(newParamInst);
        newProjection.assignParameterInstance(newParamInst, newSetType, false);
      }
    }

    return setTypes;
  }

  private void saveSets(ProjectionModel loadedProjection,
      ProjectionModel newProjection,
      Map<ParameterInstance, ParameterInstance> paramInstances,
      Map<SetType, SetType> setTypes, IP3MDatabase database) {

    for (SetType setType : loadedProjection.getAllSetTypes()) {
      SetType newSetType = setTypes.get(setType);
      for (Set set : setType.getSets()) {
        Set newSet = set != loadedProjection.getDefaultSet() ? newSetType
            .createSet(set.getName(), set.getDescription(),
                set.getProbability()) : newProjection.getDefaultSet();
        for (ParameterInstance paramInst : setType.getDefinedParameters()) {
          ParameterAssignmentSet paramAssignSet = set
              .getParameterAssignments(paramInst);
          for (ParameterAssignment paramAssign : paramAssignSet
              .getAssignments()) {
            ParameterAssignment newParamAssign = database
                .newParameterAssignment(paramInstances.get(paramInst),
                    paramAssign.getName(), paramAssign.getDescription(),
                    paramAssign.getProbability(), paramAssign.getDeviation(),
                    paramAssign.getMatrixValue());
            newSet.addParameterAssignment(newParamAssign);
          }
        }
      }
    }

  }

}