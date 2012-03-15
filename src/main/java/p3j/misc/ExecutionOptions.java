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

/**
 * Options for the execution and handling of an experiment.
 * 
 * Created on March 04, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
@Deprecated
public class ExecutionOptions {

	// Storage options

	/**
	 * If true, all data will be stored in XML (using the built-in XML decoder) -
	 * binary format will be used otherwise.
	 */
	private boolean storeInXML;

	/** If true, files will be compressed. */
	private boolean useCompression = true;

	public boolean isStoreInXML() {
		return storeInXML;
	}

	public void setStoreInXML(boolean storeInXML) {
		this.storeInXML = storeInXML;
	}

	public boolean isUseCompression() {
		return useCompression;
	}

	public void setUseCompression(boolean useCompression) {
		this.useCompression = useCompression;
	}

}
