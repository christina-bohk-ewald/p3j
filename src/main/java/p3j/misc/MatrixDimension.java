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

import p3j.gui.P3J;

/**
 * Enumeration for all kinds of matrix dimensions used in the PPPM.
 * 
 * Created: August 17, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public enum MatrixDimension {

  /**
   * One line/row.
   */
  SINGLE,

  /**
   * As many lines/rows as years to be predicted.
   */
  YEARS,

  /**
   * As many lines/rows as ages to be considered.
   */
  AGES;

  /**
   * Return name of the dimension. This is used for the matrix editor. Single
   * lines/rows do not have a specific name.
   * 
   * @return proper label for dimension
   */
  public String getLabel() {
    switch (this) {
    case AGES:
      return "Age ";
    case YEARS:
      return "Year ";
    case SINGLE:
    default:
      return "";
    }
  }

  /**
   * Defines current numeric equivalent of the dimension.
   * 
   * @return a mount of entries in this dimension
   */
  public int getDimension() {
    switch (this) {
    case SINGLE:
      return 1;
    case AGES:
      return P3J.getCurrentProjection().getMaximumAge() + 1;
    case YEARS:
      return P3J.getCurrentProjection().getYears();
    default:
      return 0;
    }
  }
}
