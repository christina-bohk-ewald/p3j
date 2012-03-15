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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import p3j.gui.panels.projections.ParameterInstanceNode;
import p3j.misc.Misc;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterAssignmentComparator;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.parameters.ParameterInstanceComparator;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * Auxiliary class to encode assumption mappings and describe the encoding in
 * human- and machine-readable form. Both
 * {@link ParameterAssumptionEncoder#orderedParameterInstances} and
 * {@link ParameterAssumptionEncoder#encoding} are necessary: the first item
 * gives the order in which the assumptions allocated to the parameter instances
 * are given, the second defines which index belongs to which assumption. For
 * example, the encoded assumption sequence
 * 
 * [0 2 3]
 * 
 * can be used to read out a specific allocation as follows:
 * 
 * 1. The parameter instance in question is the last item in
 * {@link ParameterAssumptionEncoder#orderedParameterInstances}, so the selected
 * assumption here is '3'
 * 
 * 2. For this parameter instance, the index '3' refers to a specific parameter
 * assignment.
 * 
 * This mapping is summarised in both human- and machine-readable form.
 * 
 * The order of the instances is defined by {@link ParameterInstanceComparator}.
 * The order of assignments is defined by their IDs.
 * 
 * @see ParameterAssignment
 * @see ParameterInstance
 * @see ParameterInstanceComparator
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ParameterAssumptionEncoder {

	/** The given projection. */
	private final ProjectionModel projection;

	/** The ordered sequence of parameter instances. */
	private final List<ParameterInstance> orderedParameterInstances;

	/** The encoding parameter instance ID => (parameter assignment ID => index). */
	private final Map<Integer, Map<Integer, Integer>> encoding;

	/** Auxiliary data structure: ID => parameter assignment. */
	private final Map<Integer, ParameterAssignment> assignments = new HashMap<Integer, ParameterAssignment>();

	/**
	 * Instantiates a new parameter assumption encoder.
	 * 
	 * @param projectionModel
	 *          the projection model
	 */
	public ParameterAssumptionEncoder(ProjectionModel projectionModel) {
		projection = projectionModel;
		orderedParameterInstances = setUpOrderedInstanceList();
		encoding = constructEncoding();
	}

	/**
	 * Sets the up ordered instance list.
	 * 
	 * @return the list< parameter instance>
	 */
	private List<ParameterInstance> setUpOrderedInstanceList() {
		List<ParameterInstance> paramInstances = new ArrayList<ParameterInstance>(
		    projection.getAllParameterInstances());
		Collections.sort(paramInstances, new ParameterInstanceComparator());
		return Collections.unmodifiableList(paramInstances);
	}

	/**
	 * Constructs the encoding.
	 * 
	 * @return the map containing the encoding
	 */
	private Map<Integer, Map<Integer, Integer>> constructEncoding() {
		Map<Integer, Map<Integer, Integer>> paramEncoding = new HashMap<Integer, Map<Integer, Integer>>();

		Map<ParameterInstance, SetType> instSetTypeMap = projection
		    .getInstanceSetTypes();
		for (ParameterInstance instance : orderedParameterInstances) {
			paramEncoding.put(instance.getID(),
			    getSubEncoding(instSetTypeMap, instance));
		}

		return Collections.unmodifiableMap(paramEncoding);
	}

	/**
	 * Constructs the encoding for a certain parameter instance.
	 * 
	 * @param instSetTypeMap
	 *          the map from parameter instances to Settypes
	 * @param instance
	 *          the given parameter instance
	 * 
	 * @return the sub-encoding
	 */
	private Map<Integer, Integer> getSubEncoding(
	    Map<ParameterInstance, SetType> instSetTypeMap, ParameterInstance instance) {

		List<ParameterAssignment> assignmentList = getAllAssignmentsForInstance(
		    instSetTypeMap, instance);

		Collections.sort(assignmentList, new ParameterAssignmentComparator());

		Map<Integer, Integer> subEncoding = new HashMap<Integer, Integer>();
		int assignmentCounter = 0;
		for (ParameterAssignment assignment : assignmentList) {
			assignments.put(assignment.getID(), assignment);
			subEncoding.put(assignment.getID(), assignmentCounter++);
		}

		return Collections.unmodifiableMap(subEncoding);
	}

	/**
	 * Gets the all assignments for a parameter instance.
	 * 
	 * @param instSetTypeMap
	 *          the instance to Settype map
	 * @param instance
	 *          the parameter instance
	 * 
	 * @return the all assignments for the instance
	 */
	private List<ParameterAssignment> getAllAssignmentsForInstance(
	    Map<ParameterInstance, SetType> instSetTypeMap, ParameterInstance instance) {
		SetType setType = instSetTypeMap.get(instance);
		List<ParameterAssignment> assignmentList = new ArrayList<ParameterAssignment>();
		for (Set set : setType.getSets()) {
			assignmentList.addAll(set.getParameterAssignments(instance)
			    .getAssignments());
		}
		return assignmentList;
	}

	/**
	 * Writes a mapping summary. Basically the contents of
	 * {@link ParameterAssumptionEncoder#encoding}, ordered by
	 * {@link ParameterAssumptionEncoder#orderedParameterInstances}.
	 * 
	 * @param dataDirectory
	 *          the data directory
	 * 
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public void writeMappingSummary(File dataDirectory) throws IOException {

		StringBuffer strBuf = new StringBuffer();
		for (ParameterInstance instance : orderedParameterInstances) {
			strBuf.append(ParameterInstanceNode.getDisplayName(instance) + "  ["
			    + instance.getID() + "]:\n");
			Map<Integer, Integer> assignmentMap = encoding.get(instance.getID());
			Map<Integer, Integer> inverted = new HashMap<Integer, Integer>();
			Misc.invertMap(assignmentMap, inverted);
			for (int i = 0; i < inverted.size(); i++) {
				int id = inverted.get(i);
				ParameterAssignment assignment = assignments.get(id);
				strBuf.append("\t" + i + ": " + assignment.getName() + " [" + id
				    + "]\n");
			}
			strBuf.append("\n\n");
		}

		FileWriter fw = new FileWriter(dataDirectory.getAbsolutePath()
		    + File.separatorChar + "assumption_mapping.txt");
		fw.append(strBuf);
		fw.close();

	}

	/**
	 * Encode a given instance -> assignment mapping to array.
	 * 
	 * @param assignment
	 *          the assignment mapping
	 * 
	 * @return the encoded mapping
	 */
	public int[] encode(Map<ParameterInstance, ParameterAssignment> assignment) {
		int[] result = new int[orderedParameterInstances.size()];
		Map<Integer, Integer> idMapping = convertToIDMapping(assignment);
		for (int i = 0; i < result.length; i++) {
			ParameterInstance instance = orderedParameterInstances.get(i);
			result[i] = encoding.get(instance.getID()).get(
			    idMapping.get(instance.getID()));
		}
		return result;
	}

	/**
	 * Converts given assignment to an instance id => assignment id mapping.
	 * 
	 * @param assignment
	 *          the assignment
	 * 
	 * @return the corresponding mapping among IDs
	 */
	private Map<Integer, Integer> convertToIDMapping(
	    Map<ParameterInstance, ParameterAssignment> assignment) {
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (Entry<ParameterInstance, ParameterAssignment> aEntry : assignment
		    .entrySet()) {
			result.put(aEntry.getKey().getID(), aEntry.getValue().getID());
		}
		return result;
	}

	/**
	 * Returns a verbose encoding of a given assignment.
	 * 
	 * @param assignment
	 *          the assignment
	 * 
	 * @return the verbose encoding
	 */
	public String verboseEncoding(
	    Map<ParameterInstance, ParameterAssignment> assignment) {
		StringBuffer strBuf = new StringBuffer();
		Map<Integer, Integer> idMapping = convertToIDMapping(assignment);
		for (int i = 0; i < orderedParameterInstances.size(); i++) {
			ParameterInstance instance = orderedParameterInstances.get(i);
			int assignmentID = idMapping.get(instance.getID());
			strBuf.append(ParameterInstanceNode.getDisplayName(instance) + ":\n\t"
			    + assignments.get(assignmentID).getName() + " [option:"
			    + encoding.get(instance.getID()).get(assignmentID) + "]\n");
		}
		return strBuf.toString();
	}
}
