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
package p3j.experiment.results.filters;

import p3j.experiment.results.ResultsOfTrial;

/**
 * Makes sure that every result is included. This is the default filter in
 * {@link p3j.experiment.results.ResultExport}.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class IncludeAllResultFilter implements IResultFilter {

	@Override
	public boolean considerResult(ResultsOfTrial results) {
		return true;
	}

}
