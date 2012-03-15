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
package p3j.pppm.parameters;

import java.io.Serializable;

import p3j.misc.MatrixDimension;
import p3j.misc.math.Matrix2D;

/**
 * Represents a parameter instance for the PPPM. This 'instantiates' a
 * {@link Parameter} (which might be generation dependent) for a certain
 * generation. Apart from that, this class also defines a comparison index,
 * which is mostly defined for interaction with the user. For example, all
 * parameter instances regarding the second generation of emigrants should be
 * shown in the GUI before the parameters of the third generation of emigrants,
 * and so on.
 * 
 * Created on August 7, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ParameterInstance implements Serializable {

	/** Serialization ID. */
	private static final long serialVersionUID = -1513086327051860243L;

	/** ID of this instance. */
	private int id = -1;

	/**
	 * Generation to which this parameter applies (-1 if it is a general
	 * parameter).
	 */
	private int generation = -1;

	/** Parameter that is instantiated. */
	private Parameter parameter;

	/**
	 * Overall index that is used for comparison. This is used for the GUI etc. to
	 * preserve the same order of parameter kinds (i.e., parameter instances).
	 * This facilitates finding a particular instance for the user.
	 */
	private int comparisonIndex;

	/**
	 * Default constructor.
	 * 
	 * @param cIndex
	 *          the comparison index
	 * @param param
	 *          the parameter that is instantiated
	 * @param gen
	 *          the generation this parameter belongs to
	 */
	public ParameterInstance(int cIndex, Parameter param, int gen) {
		this.comparisonIndex = cIndex;
		this.parameter = param;
		this.generation = param.isGenerationDependent() ? gen : -1;
	}

	/**
	 * Constructor for bean compatibility.
	 */
	public ParameterInstance() {
		this.comparisonIndex = -1;
		this.parameter = null;
		this.generation = -1;
	}

	/**
	 * Constructor for general parameters.
	 * 
	 * @param cIndex
	 *          the comparison index
	 * @param param
	 *          the parameter that is instantiated
	 */
	public ParameterInstance(int cIndex, Parameter param) {
		this.comparisonIndex = cIndex;
		this.generation = -1;
		this.parameter = param;
	}

	/**
	 * Creates empty matrix with the dimensions as specified by this parameter
	 * instance.
	 * 
	 * @return well-dimensioned empty matrix
	 */
	public Matrix2D createEmptyValue() {
		Matrix2D returnMatrix = new Matrix2D(getValueHeight().getDimension(),
		    getValueWidth().getDimension());
		returnMatrix.setRowLabel(getValueHeight().getLabel());
		returnMatrix.setColumnLabel(getValueWidth().getLabel());
		return returnMatrix;
	}

	@Override
	public String toString() {
		return parameter.getName()
		    + (generation == -1 ? "" : " [ " + this.generation + " ]");
	}

	/**
	 * Gets the value width.
	 * 
	 * @return the value width
	 */
	public MatrixDimension getValueWidth() {
		return parameter.getValueWidth();
	}

	/**
	 * Gets the value height.
	 * 
	 * @return the value height
	 */
	public MatrixDimension getValueHeight() {
		return parameter.getValueHeight();
	}

	public int getComparisonIndex() {
		return comparisonIndex;
	}

	public void setComparisonIndex(int comparisonIndex) {
		this.comparisonIndex = comparisonIndex;
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	public int getID() {
		return id;
	}

	public void setID(int uniqueID) {
		id = uniqueID;
	}
}
