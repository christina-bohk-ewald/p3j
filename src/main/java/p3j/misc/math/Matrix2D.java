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

import james.SimSystem;

import java.util.List;
import java.util.logging.Level;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * Wrapper class for matrices.
 * 
 * Created on July 04, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class Matrix2D extends DenseDoubleMatrix2D {

  /** The step-size of the function to compute a hash value for a matrix. */
  private static final int MATRIX_HASH_STEPSIZE = 5;

  /** Serialization ID. */
  private static final long serialVersionUID = 8686379106612322945L;

  /** Offset for the number of rows. */
  static final long OFFSET_ROW = 1000L;

  /** Offset for the check-sum of the matrix. */
  static final long OFFSET_HASH = 1000000L;

  /** Part of error messages. */
  static final String ERR_MSG_COL_STORE = "Matrix operation error: Can't store to column with index ";

  /** Part of error messages. */
  static final String ERR_MSG_SOURCE_MATRIX = "Matrix operation error: source matrix has ";

  /** Part of error messages. */
  static final String ERR_MSG_CANNOT_READ = " cannot be read.";

  /** Error message for column errors. */
  static final String ERR_MSG_COL = ", so column with index ";

  /** Label for rows. */
  private String rowLabel = "";

  /** Label for column. */
  private String columnLabel = "";

  /**
   * Standard constructor.
   * 
   * @param values
   *          matrix values
   * @param rLabel
   *          the row label
   * @param cLabel
   *          the column label
   */
  public Matrix2D(double[][] values, String rLabel, String cLabel) {
    super(values);
    this.rowLabel = rLabel;
    this.columnLabel = cLabel;
  }

  /**
   * Constructor for bean compatibility.
   */
  public Matrix2D() {
    super(new double[0][0]);
  }

  /**
   * Alternative constructor.
   * 
   * @param rows
   *          number of rows
   * @param columns
   *          number of columns
   */
  public Matrix2D(int rows, int columns) {
    super(rows, columns);
  }

  /**
   * Assigns column values.
   * 
   * @param index
   *          index of the column to be filled
   * @param values
   *          values to be filled in
   */
  public void assignColumn(int index, double[] values) {

    if (index >= this.columns) {
      throw new IllegalArgumentException(ERR_MSG_COL_STORE + index
          + " - matrix only has " + this.columns + " columns");
    }

    if (values.length != this.rows) {
      throw new IllegalArgumentException("Matrix operation error: Can't store "
          + values.length + " - value vector to matrix with " + this.rows
          + " rows");
    }

    for (int i = 0; i < this.rows; i++) {
      this.setQuick(i, index, values[i]);
    }
  }

  /**
   * Copies content from source column of source matrix to target column of this
   * matrix.
   * 
   * @param indexSourceColumn
   *          index of the column in the source matrix
   * @param source
   *          the source matrix
   * @param indexTargetColumn
   *          index of the column in the target matrix
   */
  public void assignColumn(int indexSourceColumn, Matrix2D source,
      int indexTargetColumn) {

    if (source.rows != this.rows) {
      throw new IllegalArgumentException(ERR_MSG_SOURCE_MATRIX + source.rows
          + "rows, while this matrix has " + this.rows + ".");
    }

    if (indexSourceColumn >= source.columns) {
      throw new IllegalArgumentException(ERR_MSG_SOURCE_MATRIX + source.columns
          + ERR_MSG_COL + indexSourceColumn + ERR_MSG_CANNOT_READ);
    }

    if (indexTargetColumn >= this.columns) {
      throw new IllegalArgumentException(
          "Matrix operation error: target matrix has " + this.columns
              + ERR_MSG_COL + indexTargetColumn + ERR_MSG_CANNOT_READ);
    }

    for (int i = 0; i < this.rows; i++) {
      this.setQuick(i, indexTargetColumn, source.getQuick(i, indexSourceColumn));
    }
  }

  @Override
  public Matrix2D copy() {
    return new Matrix2D(this.getContent().toArray(), this.getRowLabel(),
        this.getColumnLabel());
  }

  /**
   * Calculates hash code of the value {@link Matrix2D}.
   * 
   * @param val
   *          the matrix for which the hash code shall be computed
   * @return the hash code of the matrix
   */
  public static long calculateHashCode(Matrix2D val) {
    long result = 0;
    result += OFFSET_ROW * val.rows() + val.columns();
    for (int x = 1; x < val.rows(); x += MATRIX_HASH_STEPSIZE)
      for (int y = 1; y < val.columns(); y += MATRIX_HASH_STEPSIZE)
        result += (Double.doubleToLongBits(val.getQuick(x, y)) ^ Double
            .doubleToLongBits(val.getQuick(x - 1, y - 1)));
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Matrix2D)) {
      return false;
    }

    Matrix2D val = (Matrix2D) o;
    if (!rowLabel.equals(val.getRowLabel())
        && columnLabel.equals(val.getColumnLabel())) {
      return false;
    }

    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return (int) calculateHashCode(this);
  }

  /**
   * Copies as much as possible from the source to the target.
   * 
   * @param source
   *          the source matrix
   * @param target
   *          the target matrix
   */
  public static void subMatrix(Matrix2D source, Matrix2D target) {
    for (int i = 0; i < Math.min(source.rows, target.rows); i++) {
      for (int j = 0; j < Math.min(source.columns, target.columns); j++) {
        target.setQuick(i, j, source.get(i, j));
      }
    }
  }

  public String getColumnLabel() {
    return columnLabel;
  }

  public void setColumnLabel(String columnLabel) {
    this.columnLabel = columnLabel;
  }

  public String getRowLabel() {
    return rowLabel;
  }

  public void setRowLabel(String rowLabel) {
    this.rowLabel = rowLabel;
  }

  /**
   * Adds a number of given matrices. All have to have the same dimensions
   * 
   * @param addList
   *          the list of given matrices m_1, ..., m_n
   * @return resulting matrix m_1 + ... + m_n
   */
  public static Matrix2D add(List<Matrix2D> addList) {
    Matrix2D[] list = addList.toArray(new Matrix2D[0]);
    Matrix2D result = list[0].copy();
    for (int i = 0; i < result.rows(); i++) {
      for (int j = 0; j < result.columns(); j++) {
        double res = result.getQuick(i, j);
        for (int k = 1; k < list.length; k++) {
          res += list[k].getQuick(i, j);
        }
        result.setQuick(i, j, res);
      }
    }
    return result;
  }

  /**
   * Sums up all rows of a matrix column-wise.
   * 
   * @param mat
   *          the matrix whose columns should be summed up
   * @return result matrix 1 x columns
   */
  public static Matrix2D sumRows(Matrix2D mat) {
    return sumRows(mat, null, null);
  }

  /**
   * Sums up some rows of a matrix column-wise. Set null to lower/upper border
   * to set it to default value (0/rows(), respectively).
   * 
   * @param mat
   *          the matrix whose columns should be summed up
   * @param lowerBorder
   *          the lower border
   * @param upperBorder
   *          the upper border
   * 
   * @return result matrix 1 x columns
   */
  public static Matrix2D sumRows(Matrix2D mat, Integer lowerBorder,
      Integer upperBorder) {
    Matrix2D result = new Matrix2D(1, mat.columns);
    int lowB = lowerBorder == null ? 0 : lowerBorder;
    int upB = upperBorder == null ? mat.rows : upperBorder;
    for (int i = 0; i < mat.columns(); i++) {
      double res = 0.0;
      for (int j = lowB; j < upB; j++) {
        res += mat.getQuick(j, i);
      }
      result.setQuick(0, i, res);
    }
    return result;
  }

  /**
   * Subtract list of matrices, element-wise.
   * 
   * @param subList
   *          the list of matrices to be subtracted
   * 
   * @return the result matrix
   */
  public Matrix2D sub(List<Matrix2D> subList) {
    Matrix2D result = copy();
    for (int i = 0; i < result.rows(); i++) {
      for (int j = 0; j < result.columns(); j++) {
        double sub = 0.0;
        for (Matrix2D subMat : subList) {
          sub += subMat.getQuick(i, j);
        }
        result.setQuick(i, j, result.getQuick(i, j) - sub);
      }
    }
    return result;
  }

  /**
   * Create a matrix with differing dimensions. Unknown values are filled with
   * zeros.
   * 
   * @param newRows
   *          the new number of rows
   * @param newCols
   *          the new number of columns
   * 
   * @return a new matrix of the appropriate sizes
   */
  public Matrix2D getResizedMatrix(int newRows, int newCols) {
    if (newRows <= 0 || newCols <= 0) {
      SimSystem.report(Level.WARNING, "Resize to " + newRows + " x " + newCols
          + " matrix not supported - matrix must not be empty.");
      return copy();
    }
    Matrix2D result = new Matrix2D(newRows, newCols);
    result.setRowLabel(rowLabel);
    result.setColumnLabel(columnLabel);
    int minRows = Math.min(rows, newRows);
    int minCols = Math.min(columns, newCols);

    for (int row = 0; row < minRows; row++) {
      for (int col = 0; col < minCols; col++) {
        result.setQuick(row, col, getQuick(row, col));
      }
    }
    return result;
  }

}
