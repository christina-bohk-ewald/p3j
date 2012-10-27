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
package p3j.gui.misc;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.SetType;

/**
 * List model for a list of parameters.
 * 
 * Created on February 4, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ParameterListModel extends AbstractListModel<ParameterInstance> {

  /** Serialization ID. */
  private static final long serialVersionUID = 5605809577849847910L;

  /** List of parameters. */
  private List<ParameterInstance> parameters;

  /** An empty list. */
  private static final List<ParameterInstance> EMPTY_LIST = new ArrayList<ParameterInstance>();

  @Override
  public ParameterInstance getElementAt(int index) {
    if (parameters == null) {
      return null;
    }
    return parameters.get(index);
  }

  @Override
  public int getSize() {
    if (parameters == null) {
      return 0;
    }
    return parameters.size();
  }

  /**
   * Updates with content from a new Settype.
   * 
   * @param newSetType
   *          the new Settype
   */
  public void updateSetType(SetType newSetType) {
    int oldIndex = this.parameters == null ? 0 : this.parameters.size();
    this.parameters = newSetType == null ? EMPTY_LIST : newSetType
        .getDefinedParameters();
    this.fireContentsChanged(this, 0, Math.max(oldIndex, getSize()));
  }

  /**
   * Refresh the list.
   */
  public void refresh() {
    this.fireContentsChanged(this, 0, getSize());
  }

}
