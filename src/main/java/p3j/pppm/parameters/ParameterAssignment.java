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

import james.SimSystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;

import p3j.misc.Misc;
import p3j.misc.math.Matrix;
import p3j.misc.math.Matrix2D;
import p3j.pppm.IStochasticOccurrence;

/**
 * One assignment of a parameter instance. This is a matrix with a concrete
 * name, a description, and a specific content (stored in
 * {@link ParameterAssignment#matrix}). This is, in other words, the basic input
 * for the PPPM. Each object is associated with a specific
 * {@link ParameterInstance} and therefore with a specific {@link Parameter}. It
 * also has a specific probability to occur, therefore
 * {@link IStochasticOccurrence} is implemented.
 * 
 * Example for an assignment: TFR is constant at 1.3 for native population in
 * the next 100 years.
 * 
 * Created on January 20, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ParameterAssignment implements Serializable, IStochasticOccurrence {

	/** Serialization ID. */
	private static final long serialVersionUID = 5672796759108189718L;

	/** ID of the assignment. */
	private int id;

	/** Reference to parameter instance. */
	private ParameterInstance paramInstance;

	/** Assigned value. */
	private Matrix matrix;

	/** Name of this alternative. */
	private String name = "";

	/** Description of this alternative. */
	private String description = "";

	/** Probability of this alternative. */
	private double probability;

	/**
	 * The deviation variable. It represents extra noise that should be included
	 * in the calculation. Default is zero, i.e. the same numbers that are put in
	 * are also returned.
	 */
	private double deviation;

	/**
	 * Default constructor.
	 * 
	 * @param instance
	 *          parameter instance to which this assignment belongs
	 */
	public ParameterAssignment(ParameterInstance instance) {
		paramInstance = instance;
	}

	/**
	 * Full constructor.
	 * 
	 * @param instance
	 *          the associated instance
	 * @param assignName
	 *          the name of the assignment
	 * @param desc
	 *          the description of the assignment
	 * @param prob
	 *          the probability of the assignment
	 * @param dev
	 *          the assumption-inherent deviation
	 * @param val
	 *          the assigned value
	 */
	public ParameterAssignment(ParameterInstance instance, String assignName,
	    String desc, double prob, double dev, Matrix val) {
		paramInstance = instance;
		name = assignName;
		description = desc;
		probability = prob;
		deviation = dev;
		matrix = val;
	}

	/**
	 * Constructor for bean compatibility.
	 */
	public ParameterAssignment() {

	}

	/**
	 * Sets matrix from a byte array (for deserialization).
	 * 
	 * @param byteArray
	 *          the byte array which contains the persistent matrix object
	 */
	public void setMatrixBinary(byte[] byteArray) {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			matrix = (Matrix) objectInputStream.readObject();
			objectInputStream.close();
		} catch (Exception ex) {
			SimSystem.report(Level.SEVERE, "Deserialization of matrix failed.", ex);
		}
	}

	/**
	 * Returns the matrix value as a byte array (for serialization).
	 * 
	 * @return byte representation of assignment value (a matrix object)
	 */
	public byte[] getMatrixBinary() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
			    outputStream);
			objectOutputStream.writeObject(matrix);
			objectOutputStream.close();
		} catch (IOException ex) {
			SimSystem.report(Level.SEVERE, "Serialization of matrix failed.", ex);
		}
		return outputStream.toByteArray();
	}

	/**
	 * Copies the assignment. This produces a deep copy, i.e., the value matrix is
	 * copied too.
	 * 
	 * @return a copy of the parameter assignment
	 */
	public ParameterAssignment getCopy() {

		ParameterAssignment returnAssignment = new ParameterAssignment(
		    getParamInstance());
		returnAssignment.description = description;
		returnAssignment.name = name;
		returnAssignment.probability = probability;
		returnAssignment.matrix = matrix.copy();

		return returnAssignment;
	}

	@Override
	public String toString() {
		return getName() + " (" + Misc.round(getProbability(), 2) + ")";
	}

	// Getter/Setter for bean compatibility

	@Override
	public double getProbability() {
		return probability;
	}

	@Override
	public void setProbability(double probability) {
		this.probability = probability;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}

	/**
	 * Sets the matrix value.
	 * 
	 * @param matrix2D
	 *          the new matrix value
	 */
	public void setMatrixValue(Matrix2D matrix2D) {
		if (matrix == null) {
			matrix = new Matrix(matrix2D);
		} else {
			matrix.setValue(matrix2D);
		}
	}

	/**
	 * This method returns the fixed matrix values, as entered by the user. It
	 * should NOT be called to get values for any calculations, only for editing.
	 * For calculations, call {@link
	 * ParameterAssignment#getMatrixValueForExecution(...)}.
	 * 
	 * @return the matrix values entered by the user
	 */
	public Matrix2D getMatrixValue() {
		return matrix.getValue();
	}

	public ParameterInstance getParamInstance() {
		return paramInstance;
	}

	public void setParamInstance(ParameterInstance paramInstance) {
		this.paramInstance = paramInstance;
	}

	public int getID() {
		return id;
	}

	public void setID(int uniqueID) {
		id = uniqueID;
	}

	public double getDeviation() {
		return deviation;
	}

	/**
	 * Sets deviation. Compatibility with older databases (which do not
	 * necessarily include this field) is ensured by taking a {@link Double} as a
	 * parameter, which can be null. This is checked, and the primitive is set
	 * accordingly.
	 * 
	 * @param newDeviation
	 *          the deviation
	 */
	public void setDeviation(Double newDeviation) {
		if (newDeviation == null || newDeviation < 0) {
			this.deviation = 0;
		} else {
			this.deviation = newDeviation;
		}
	}

}
