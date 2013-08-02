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
package p3j.pppm.readerwriter.file;

import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.data.model.ModelFileReaderFactory;
import org.jamesii.core.data.model.read.plugintype.IMIMEType;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;

import p3j.pppm.ProjectionModel;
import p3j.pppm.SymbolicProjectionModel;

/**
 * Factory for reader/writer of PPPM files.
 * 
 * TODO: Finish this, use {@link p3j.misc.Serializer}.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class PPPModelFileReaderWriterFactory extends ModelFileReaderFactory {

	/** Serialization ID. */
	private static final long serialVersionUID = -7324702277171597907L;

	@Override
	public String getDescription() {
		return "Probabilistic Population Projections";
	}

	@Override
	public String getFileEnding() {
		return "p3j";
	}

	@Override
	public IModelReader create(ParameterBlock params) {
		return null;
	}

	@Override
	public boolean supportsModel(IModel model) {
		return model instanceof ProjectionModel;
	}

	@Override
	public boolean supportsModel(ISymbolicModel<?> model) {
		return model instanceof SymbolicProjectionModel;
	}

	@Override
	public boolean supportsMIMEType(IMIMEType mime) {
		return false;
	}

}
