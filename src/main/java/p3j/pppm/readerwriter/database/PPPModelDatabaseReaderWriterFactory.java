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
package p3j.pppm.readerwriter.database;

import james.core.data.model.IModelReader;
import james.core.data.model.read.plugintype.IMIMEType;
import james.core.data.model.read.plugintype.ModelReaderFactory;
import james.core.model.IModel;
import james.core.model.symbolic.ISymbolicModel;
import james.core.parameters.ParameterBlock;

import java.net.URI;

import p3j.pppm.ProjectionModel;
import p3j.pppm.SymbolicProjectionModel;

/**
 * Factory for reader/writer of PPPM files.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class PPPModelDatabaseReaderWriterFactory extends ModelReaderFactory {

	/** Serialization ID. */
	private static final long serialVersionUID = 2423298767414579313L;

	@Override
	public IModelReader create(ParameterBlock params) {
		return new PPPMDatabaseReader();
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
	public boolean supportsURI(URI uri) {
		return uri.getScheme().equals("db-p3j");
	}

	@Override
	public boolean supportsMIMEType(IMIMEType mime) {
		return false;
	}

}
