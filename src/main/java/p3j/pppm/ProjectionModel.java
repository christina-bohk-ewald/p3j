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
package p3j.pppm;

import james.core.model.Model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import p3j.pppm.parameters.Parameter;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterAssignmentSet;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;
import p3j.simulation.calculation.deterministic.Constants;

/**
 * Stores a complete configuration for the PPPM, containing {@link Set} objects
 * associated with different {@link SetType} instances. Such a scenario can be
 * used for forecasting, it comprises all information necessary for basic
 * reproducibility of the Monte-Carlo-simulation. Each scenario holds a list of
 * all {@link ParameterInstance} objects that need an assignment. Disjunct
 * subsets of this list will be managed by {@link SetType}.
 * 
 * TODO: Store RNG seed to ensure exact reproducibility.
 * 
 * Created on January 22, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class ProjectionModel extends Model implements IProjectionModel {

  /** Serialization ID. */
  private static final long serialVersionUID = -1984182268566219449L;

  /** The ID of this scenario. */
  private int id;

  /** Name of the scenario. */
  private String name;

  /** Description of the scenario. */
  private String description;

  /**
   * Default Settype. This will always be there and contain all parameters that
   * do not belong to specific Settypes.
   */
  private SetType defaultType = new SetType("Default Settype",
      "This is the default Settype.");

  /** Default set. */
  private Set defaultSet = defaultType.createSet("Default Set",
      "This is the default set.", 1);

  /** List of user defined Settypes. */
  private List<SetType> userDefinedTypes = new ArrayList<SetType>();

  /** List of all parameter instances available in this scenario. */
  private List<ParameterInstance> allParameterInstances = new ArrayList<ParameterInstance>();

  /** Mapping {@link ParameterInstance} to {@link SetType} that manages it. */
  private Map<ParameterInstance, SetType> instanceSetTypes = new HashMap<ParameterInstance, SetType>();

  /** The sub-population model that is assumed. */
  private SubPopulationModel subPopulationModel = PPPModelFactory.DEFAULT_SUBPOPULATION_MODEL;

  /**
   * Number of generations to be taken into account. Is initially set to
   * {@link Constants#DEFAULT_NUM_GENERATIONS}.
   */
  private int generations = Constants.DEFAULT_NUM_GENERATIONS;

  /**
   * Number of years to be predicted. Is initially set to
   * {@link Constants#DEFAULT_NUM_YEARS}.
   */
  private int years = Constants.DEFAULT_NUM_YEARS;

  /**
   * Maximum age of individual. Is initially set to
   * {@link Constants#DEFAULT_MAXIMUM_AGE}.
   */
  private int maximumAge = Constants.DEFAULT_MAXIMUM_AGE;

  /** The jump-off year. */
  private int jumpOffYear = Calendar.getInstance().get(Calendar.YEAR);

  /**
   * Constructor for bean compatibility (do NOT use manually!).
   */
  public ProjectionModel() {
  }

  /**
   * Default constructor.
   * 
   * @param scenName
   *          name of the scenario
   * @param desc
   *          description of the scenario
   * @param numOfGenerations
   *          number of generations to be considered
   * @param predYears
   *          number of years to be predicted
   * @param maxAge
   *          maximum age to be considered
   * @param jumpOffYear
   *          the jump off year
   * @param subPopModel
   *          the model of sub-populations
   */
  public ProjectionModel(String scenName, String desc, int numOfGenerations,
      int predYears, int maxAge, int jumpOffYear, SubPopulationModel subPopModel) {
    this.name = scenName;
    this.description = desc;
    this.generations = numOfGenerations;
    this.years = predYears;
    this.maximumAge = maxAge;
    this.jumpOffYear = jumpOffYear;
    this.subPopulationModel = subPopModel;
  }

  /**
   * Initializes projection. Needs to be called after
   * {@link #allParameterInstances} has been initialized.
   */
  @Override
  public void init() {
    for (int i = 0; i < allParameterInstances.size(); i++) {
      instanceSetTypes.put(allParameterInstances.get(i), defaultType);
      defaultType.addInstance(allParameterInstances.get(i));
    }
  }

  /**
   * Defines a new Settype for this scenario.
   * 
   * @param stName
   *          name of the Settype
   * @param stDesc
   *          description of the Settype
   * 
   * @return newly defined Settype for this scenario
   */
  public SetType createSetType(String stName, String stDesc) {
    SetType setType = new SetType(stName, stDesc);
    userDefinedTypes.add(setType);
    return setType;
  }

  /**
   * Get all {@link ParameterInstance} objects that still belong to the default
   * {@link SetType}. These have not yet been assigned to any custom
   * {@link SetType}.
   * 
   * @return list of all parameter instances that are not yet assigned to user
   *         defined Settypes
   */
  public List<ParameterInstance> getUnassignedParameterInstances() {
    return this.defaultType.getDefinedParameters();
  }

  /**
   * Assign a {@link ParameterInstance} to a specific {@link SetType}.
   * 
   * @param instance
   *          the parameter instance to be assigned
   * @param type
   *          the {@link SetType} that shall manage the
   *          {@link ParameterInstance} from now on
   * @param migrate
   *          flag to switch migration of existing assumption to new Settype
   *          on/off
   * 
   * @return true, if parameter instance could be assigned (i.e., former set
   *         type is default Settype), otherwise false
   */
  public boolean assignParameterInstance(ParameterInstance instance,
      SetType type, boolean migrate) {

    if (type.equals(defaultType)) {
      removeParameterInstanceAssignment(instance);
    }

    SetType associatedSetType = instanceSetTypes.get(instance);

    if (!associatedSetType.equals(defaultType)) {
      return false;
    }

    type.addInstance(instance);
    if (migrate) {
      migrateAssignmentsFromDefaultType(instance, type);
    }
    defaultType.removeInstance(instance);
    instanceSetTypes.put(instance, type);
    return true;
  }

  /**
   * Migrate assignments from default type.
   * 
   * @param instance
   *          the parameter instance of the assignments to be migrated
   * @param newType
   *          the destination Settype
   */
  private void migrateAssignmentsFromDefaultType(ParameterInstance instance,
      SetType newType) {
    ParameterAssignmentSet paramAssignmentSet = getDefaultSet()
        .getParameterAssignments(instance);

    // Stop if there is nothing to migrate
    if (paramAssignmentSet.getAssignments().isEmpty())
      return;

    Set set = newType.getNumOfSets() == 0 ? newType.createSet(
        "Migrated parameter assignments",
        "Automatically created during Settype definition.", 0.0) : newType
        .getSets().get(0);

    for (ParameterAssignment pa : paramAssignmentSet.getAssignments()) {
      set.addParameterAssignment(pa);
    }
  }

  /**
   * Removes a {@link ParameterInstance} from a certain {@link SetType}.
   * 
   * @param instance
   *          the instance that should from now on be managed by the default
   *          {@link SetType}
   * 
   * @return true, if instance could be removed from its Settype (i.e., if set
   *         type is not default Settype)
   */
  public boolean removeParameterInstanceAssignment(ParameterInstance instance) {
    SetType associatedSetType = instanceSetTypes.get(instance);

    if (associatedSetType.equals(defaultType)) {
      return false;
    }

    associatedSetType.removeInstance(instance);
    defaultType.addInstance(instance);
    instanceSetTypes.put(instance, defaultType);
    return true;
  }

  /**
   * Retrieves a custom {@link SetType}.
   * 
   * @param index
   *          index of the Settype
   * 
   * @return Settype with given index
   */
  public SetType getSetType(int index) {
    return userDefinedTypes.get(index);
  }

  /**
   * Removes {@link SetType} with given index from scenario.
   * 
   * @param index
   *          index of the Settype
   * 
   * @return true, if operation could be executed
   */
  public boolean removeSetType(int index) {
    SetType removedSetType = userDefinedTypes.remove(index);

    if (removedSetType == null) {
      return false;
    }

    List<ParameterInstance> parameterInstances = new ArrayList<ParameterInstance>(
        removedSetType.getDefinedParameters());
    for (ParameterInstance instance : parameterInstances) {
      removeParameterInstanceAssignment(instance);
    }
    return true;
  }

  @Override
  public List<SetType> getAllSetTypes() {
    List<SetType> setTypes = new ArrayList<SetType>();
    setTypes.add(defaultType);
    setTypes.addAll(userDefinedTypes);
    return setTypes;
  }

  // Getters/Setter to ensure bean compatibility

  /**
   * Gets the default set.
   * 
   * @return the default set
   */
  public Set getDefaultSet() {
    return defaultSet;
  }

  /**
   * Sets the default set.
   * 
   * @param defaultSet
   *          the new default set
   */
  public void setDefaultSet(Set defaultSet) {
    this.defaultSet = defaultSet;
  }

  /**
   * Gets the default type.
   * 
   * @return the default type
   */
  public SetType getDefaultType() {
    return defaultType;
  }

  /**
   * Sets the default type.
   * 
   * @param defaultType
   *          the new default type
   */
  public void setDefaultType(SetType defaultType) {
    this.defaultType = defaultType;
  }

  /**
   * Gets the default Settype.
   * 
   * @return the default Settype
   */
  public SetType getDefaultSetType() {
    return defaultType;
  }

  @Override
  public int getGenerations() {
    return generations;
  }

  /**
   * Sets the generations.
   * 
   * @param generations
   *          the new generations
   */
  public void setGenerations(int generations) {
    // TODO: Remove obsolete generations if necessary.
    this.generations = generations;
  }

  @Override
  public int getMaximumAge() {
    return maximumAge;
  }

  /**
   * Sets the maximum age.
   * 
   * @param maximumAge
   *          the new maximum age
   */
  public void setMaximumAge(int maximumAge) {
    // TODO: Adjust matrix size if necessary.
    this.maximumAge = maximumAge;
  }

  @Override
  public int getYears() {
    return years;
  }

  /**
   * Sets the years.
   * 
   * @param years
   *          the new years
   */
  public void setYears(int years) {
    // TODO: Adjust matrix size if necessary.
    this.years = years;
  }

  /**
   * Gets the user defined types.
   * 
   * @return the user defined types
   */
  public List<SetType> getUserDefinedTypes() {
    return userDefinedTypes;
  }

  /**
   * Sets the user defined types.
   * 
   * @param userDefinedTypes
   *          the new user defined types
   */
  public void setUserDefinedTypes(List<SetType> userDefinedTypes) {
    this.userDefinedTypes = userDefinedTypes;
  }

  /**
   * Gets the all parameter instances.
   * 
   * @return the all parameter instances
   */
  public List<ParameterInstance> getAllParameterInstances() {
    return this.allParameterInstances;
  }

  /**
   * Sets the all parameter instances.
   * 
   * @param allParameterInstances
   *          the new all parameter instances
   */
  public void setAllParameterInstances(
      List<ParameterInstance> allParameterInstances) {
    this.allParameterInstances = allParameterInstances;
  }

  @Override
  public Map<ParameterInstance, SetType> getInstanceSetTypes() {
    return instanceSetTypes;
  }

  /**
   * Sets the instance Settypes.
   * 
   * @param instanceSetTypes
   *          the instance Settypes
   */
  public void setInstanceSetTypes(
      Map<ParameterInstance, SetType> instanceSetTypes) {
    this.instanceSetTypes = instanceSetTypes;
  }

  /**
   * Removes the Settype.
   * 
   * @param setType
   *          the Settype
   * 
   * @return true, if successful
   */
  public boolean removeSetType(SetType setType) {
    return removeSetType(userDefinedTypes.indexOf(setType));
  }

  /**
   * Gets the number of (user-defined) of Settypes.
   * 
   * @return the number of Settypes
   */
  public int getNumOfSetTypes() {
    return userDefinedTypes.size();
  }

  /**
   * Gets the id.
   * 
   * @return the id
   */
  public int getID() {
    return id;
  }

  /**
   * Sets the id.
   * 
   * @param uniqueID
   *          the new id
   */
  public void setID(int uniqueID) {
    id = uniqueID;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the description.
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   * 
   * @param description
   *          the new description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the number of age classes. Is calculated by adding 1 to
   * {@link ProjectionModel#maximumAge}.
   * 
   * @return the number of age classes
   */
  public int getNumberOfAgeClasses() {
    return maximumAge + 1;
  }

  /**
   * Gets the jump off year.
   * 
   * @return the jump off year
   */
  public int getJumpOffYear() {
    return jumpOffYear;
  }

  /**
   * Sets the jump off year.
   * 
   * @param jumpOffYear
   *          the new jump off year
   */
  public void setJumpOffYear(int jumpOffYear) {
    this.jumpOffYear = jumpOffYear;
  }

  /**
   * Gets the sub-population model.
   * 
   * @return the sub-population model
   */
  @Override
  public SubPopulationModel getSubPopulationModel() {
    // For compatibility with old projections without a sub-population model:
    if (subPopulationModel == null)
      subPopulationModel = PPPModelFactory.createDefaultSubPopulationModel();
    return subPopulationModel;
  }

  /**
   * Sets the sub-population model.
   * 
   * @param subPopulationModel
   *          the new sub-population model
   */
  public void setSubPopulationModel(SubPopulationModel subPopulationModel) {
    this.subPopulationModel = subPopulationModel;
  }

  /**
   * Counts the number of parameter assignments contained in this projection.
   * 
   * @return the number of parameter assignments
   */
  public int countNumberOfParameterAssignments() {
    int numOfAssignments = 0;
    for (SetType setType : getAllSetTypes())
      for (Set set : setType.getSets())
        for (ParameterAssignmentSet assignSet : set.getSetData().values())
          numOfAssignments += assignSet.size();
    return numOfAssignments;
  }

  /**
   * Filters all parameter instances from a given list by a given
   * sub-population. The parameter instances in the new list are sorted by
   * {@link Parameter#getSortingIndex()}.
   * 
   * @param originalInstances
   *          the original list of parameter instances
   * @param subPopulation
   *          the sub population
   * @return the parameter instances
   */
  public static List<ParameterInstance> filterParamInstancesBySubPopulation(
      List<ParameterInstance> originalInstances, SubPopulation subPopulation) {
    List<ParameterInstance> instances = new ArrayList<>();
    for (ParameterInstance instance : originalInstances)
      if (instance.getParameter().getName().startsWith(subPopulation.getName()))
        instances.add(instance);
    Collections.sort(instances, new Comparator<ParameterInstance>() {
      @Override
      public int compare(ParameterInstance p1, ParameterInstance p2) {
        return Integer.compare(p1.getParameter().getSortingIndex(), p2
            .getParameter().getSortingIndex());
      }
    });
    return instances;
  }
}
