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
package p3j.misc.math;

import java.io.Serializable;

/**
 * Matrix object that contains its value as a {@link Matrix2D}. It also provides
 * an ID and hash code calculation routines, so that similar matrices can be
 * found easily by the storage system.
 * 
 * Created: August 18, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class Matrix implements Serializable {

	/** Serialization ID. */
	private static final long serialVersionUID = 5456211627452741990L;

	/** ID of this matrix. */
	private int id = -1;

	/** Hash, for quick lookup in database. */
	private long hash;

	/** Value of this matrix. */
	private Matrix2D value;

	/**
	 * Default constructor.
	 * 
	 * @param val
	 *          the associated value
	 */
	public Matrix(Matrix2D val) {
		storeValueAndHash(val);
	}

	/**
	 * Instantiates a new matrix (only required for beans compliance, do not use
	 * manually).
	 */
	public Matrix() {
	}

	/**
	 * Creates (deep) copy of this matrix, apart from the ID. The ID of the copy
	 * is set to -1.
	 * 
	 * @return copy of this matrix
	 */
	public Matrix copy() {
		return new Matrix(value.copy());
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public long getHash() {
		return hash;
	}

	public void setHash(long hash) {
		this.hash = hash;
	}

	public Matrix2D getValue() {
		return value;
	}

	/**
	 * Sets the value of the matrix. Also updates the hash code of the object.
	 * 
	 * @param value
	 *          the new value
	 */
	public void setValue(Matrix2D value) {
		storeValueAndHash(value);
	}

	/**
	 * Sets the matrix value and updates the hash code of the matrix.
	 * 
	 * @param value
	 *          the new value
	 */
	private void storeValueAndHash(Matrix2D value) {
		this.value = value;
		hash = value.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Matrix)) {
			return false;
		}
		Matrix2D val = ((Matrix) o).getValue();
		return val.equals(value);
	}

	@Override
	public int hashCode() {
		return (int) hash;
	}
}
