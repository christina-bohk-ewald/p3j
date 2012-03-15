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
package p3j.gui.dialogs.execstatus;

import james.core.experiments.instrumentation.computation.IComputationInstrumenter;
import james.core.experiments.instrumentation.computation.plugintype.ComputationInstrumenterFactory;
import james.core.parameters.ParameterBlock;

/**
 * Creates an {@link ExecutionProgressInstrumenter}.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class ExecProgressInstrFactory extends ComputationInstrumenterFactory {

	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 8122412991641145237L;

	/**
	 * The Constant NUM_OF_TRIALS. Type: {@link Integer}.
	 */
	public static final String NUM_OF_TRIALS = "NUMBER_OF_TRIALS";

	@Override
	public IComputationInstrumenter create(ParameterBlock parameter) {
		return new ExecutionProgressInstrumenter(parameter.getSubBlockValue(
		    NUM_OF_TRIALS, 1));
	}

	@Override
	public int supportsParameters(ParameterBlock parameters) {
		return 1;
	}

}
