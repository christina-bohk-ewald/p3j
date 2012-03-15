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
import java.util.List;
import java.util.Map;

import p3j.misc.math.Matrix2D;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.simulation.calculation.deterministic.parameters.MigChildParameters;
import p3j.simulation.calculation.deterministic.parameters.MigParameters;
import p3j.simulation.calculation.deterministic.parameters.NativeParameters;

/**
 * Summary of a single PPPM execution.
 * 
 * Created on February 20, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ExecutionSummary {

	/** The parameter assignments. */
	private final Map<ParameterInstance, ParameterAssignment> paramAssignments;

	/** Parameters for calculating native population. */
	private NativeParameters nativeParameters;

	/** Results of native population calculation. */
	private BasicResults nativeResults;

	/** Results of emigrant population calculation. */
	private List<BasicResults> emigrantResults = new ArrayList<BasicResults>();

	/** Parameters to calculate first emigrant generation. */
	private MigParameters firstEmigrantParameters;

	/** Parameters to calculate child generations of emigrants. */
	private List<MigChildParameters> emigrantParameters = new ArrayList<MigChildParameters>();

	/** Results of immigrant population calculation. */
	private List<BasicResults> immigrantResults = new ArrayList<BasicResults>();

	/** Parameters to calculate first immigrant generation. */
	private MigParameters firstImmigrantParameters;

	/** Parameters to calculate child generations of immigrants. */
	private List<MigChildParameters> immigrantParameters = new ArrayList<MigChildParameters>();

	/**
	 * Instantiates a new execution summary.
	 * 
	 * @param assignment
	 *          the overall assignment used: parameter instance -> assignment
	 */
	public ExecutionSummary(Map<ParameterInstance, ParameterAssignment> assignment) {
		paramAssignments = Collections.unmodifiableMap(assignment);
	}

	public List<MigChildParameters> getEmigrantParameters() {
		return emigrantParameters;
	}

	public void setEmigrantParameters(List<MigChildParameters> emigrantParameters) {
		this.emigrantParameters = emigrantParameters;
	}

	/**
	 * Adds the emigrant parameters.
	 * 
	 * @param parameters
	 *          the parameters
	 */
	public void addEmigrantParameters(MigChildParameters parameters) {
		this.emigrantParameters.add(parameters);
	}

	public MigParameters getFirstEmigrantParameters() {
		return firstEmigrantParameters;
	}

	public void setFirstEmigrantParameters(MigParameters firstEmigrantParameters) {
		this.firstEmigrantParameters = firstEmigrantParameters;
	}

	public MigParameters getFirstImmigrantParameters() {
		return firstImmigrantParameters;
	}

	public void setFirstImmigrantParameters(MigParameters firstImmigrantParameters) {
		this.firstImmigrantParameters = firstImmigrantParameters;
	}

	public List<MigChildParameters> getImmigrantParameters() {
		return immigrantParameters;
	}

	public void setImmigrantParameters(
	    List<MigChildParameters> immigrantParameters) {
		this.immigrantParameters = immigrantParameters;
	}

	/**
	 * Adds the immigrant parameter.
	 * 
	 * @param parameters
	 *          the parameters
	 */
	public void addImmigrantParameter(MigChildParameters parameters) {
		this.immigrantParameters.add(parameters);
	}

	public NativeParameters getNativeParameters() {
		return nativeParameters;
	}

	public void setNativeParameters(NativeParameters nativeParameters) {
		this.nativeParameters = nativeParameters;
	}

	public List<BasicResults> getEmigrantResults() {
		return emigrantResults;
	}

	public void setEmigrantResults(List<BasicResults> emigrantResults) {
		this.emigrantResults = emigrantResults;
	}

	/**
	 * Adds the results for emigrants.
	 * 
	 * @param emigrantResult
	 *          the emigrant results
	 */
	public void addEmigrantResult(BasicResults emigrantResult) {
		this.emigrantResults.add(emigrantResult);
	}

	public List<BasicResults> getImmigrantResults() {
		return immigrantResults;
	}

	public void setImmigrantResults(List<BasicResults> immigrantResults) {
		this.immigrantResults = immigrantResults;
	}

	/**
	 * Adds the results for immigrants.
	 * 
	 * @param immigrantResult
	 *          the immigrant results
	 */
	public void addImmigrantResult(BasicResults immigrantResult) {
		this.immigrantResults.add(immigrantResult);
	}

	public BasicResults getNativeResults() {
		return nativeResults;
	}

	public void setNativeResults(BasicResults nativeResults) {
		this.nativeResults = nativeResults;
	}

	/**
	 * Calculates total end population.
	 * 
	 * @return total end population
	 */
	public Matrix2D getTotalEndPopulation() {

		// Natives plus immigrants
		List<Matrix2D> addList = new ArrayList<Matrix2D>();
		addList.add(nativeResults.getEndXm());
		addList.add(nativeResults.getEndXf());
		for (BasicResults immResults : immigrantResults) {
			addList.add(immResults.getEndXm());
			addList.add(immResults.getEndXf());
		}

		// Minus all emigrants
		List<Matrix2D> subList = new ArrayList<Matrix2D>();
		for (BasicResults emResults : emigrantResults) {
			subList.add(emResults.getEndXm());
			subList.add(emResults.getEndXf());
		}

		return Matrix2D.add(addList).sub(subList);
	}

	public Map<ParameterInstance, ParameterAssignment> getParamAssignments() {
		return paramAssignments;
	}
}
