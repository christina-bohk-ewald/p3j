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

import javax.swing.AbstractListModel;

import p3j.pppm.ProjectionModel;

/**
 * Model of the list of Settypes.
 * 
 * Created on January 28, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class SetTypesListModel extends AbstractListModel {

	/** Serialization ID. */
	private static final long serialVersionUID = 8369935030761632549L;

	/** Reference to edited projection. */
	private ProjectionModel projection;

	/**
	 * Default constructor.
	 * 
	 * @param currentProjection
	 *          the projection to be edited
	 */
	public SetTypesListModel(ProjectionModel currentProjection) {
		this.projection = currentProjection;
	}

	@Override
	public Object getElementAt(int index) {
		return this.projection.getSetType(index);
	}

	@Override
	public int getSize() {
		return this.projection.getNumOfSetTypes();
	}

	/**
	 * Remove a Settype.
	 * 
	 * @param index
	 *          index of the Settype
	 */
	public void removeSetType(int index) {
		this.projection.removeSetType(index);
		this.fireContentsChanged(this, index, getSize() + 1);
	}

	/**
	 * Add a Settype.
	 * 
	 * @param name
	 *          name of the Settype
	 * @param desc
	 *          description of the Settype
	 */
	public void addSetType(String name, String desc) {
		int index = this.projection.getNumOfSetTypes();
		this.projection.createSetType(name, desc);
		this.fireContentsChanged(this, index, index + 1);
	}

	/**
	 * Propagates event of a changing Settype.
	 * 
	 * @param stIndex
	 *          index of Settype that was changed
	 */
	public void setTypeChanged(int stIndex) {
		this.fireContentsChanged(this, stIndex, stIndex);
	}

	public ProjectionModel getProjection() {
		return projection;
	}

	public void setProjection(ProjectionModel scenario) {
		this.projection = scenario;
	}

}
