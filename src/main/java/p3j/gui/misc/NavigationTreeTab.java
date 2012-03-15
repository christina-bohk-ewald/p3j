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

/**
 * The available tabs for the navigation tree on the left-hand side of the GUI.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public enum NavigationTreeTab {

	/** The overview on all projections in the database. */
	DB_OVERVIEW,

	/** The overview on the elements of the projection. */
	PROJECTION_OVERVIEW,

	/** The overview on the results associated with the projection. */
	RESULTS_OVERVIEW;

	/**
	 * Gets the tab index.
	 * 
	 * @return the tab index
	 */
	public int getTabIndex() {
		switch (this) {
		case DB_OVERVIEW:
			return 0;
		case PROJECTION_OVERVIEW:
			return 1;
		case RESULTS_OVERVIEW:
			return 2;
		default:
			throw new IllegalArgumentException();
		}
	}
}
