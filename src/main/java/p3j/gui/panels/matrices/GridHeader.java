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
package p3j.gui.panels.matrices;

import javax.swing.SwingConstants;

import net.sf.jeppers.grid.JGrid;
import net.sf.jeppers.grid.JGridHeader;

/**
 * 
 * Header implementation for {@link JGrid}. Implements numbering of rows and
 * adds a prefix by initializing {@link GridHeaderModel} properly.
 * 
 * Created on January 14, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class GridHeader extends JGridHeader {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 8687133380633075120L;

  /**
   * Default constructor.
   * 
   * @param grid
   *          the {@link JGrid} component
   * @param pref
   *          the desired prefix
   * @param startWithOne
   *          flag that determines if row/column enumeration starts with one
   *          (true) or with zero (false)
   * @param column
   *          true iff this is a column header
   */
  public GridHeader(JGrid grid, String pref, boolean startWithOne,
      boolean column) {
    super(grid, column ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL);
    this.setGridModel(new GridHeaderModel(column ? grid.getColumnModel() : grid
        .getRowModel(), pref, startWithOne, column));
  }

}