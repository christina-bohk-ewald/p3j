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
package p3j.pppm;

import james.core.data.model.read.plugintype.IMIMEType;
import james.core.model.Model;
import james.core.model.symbolic.ISymbolicModel;
import james.core.model.symbolic.convert.IDocument;

/**
 * Implementation of {@link ISymbolicModel} that wraps {@link ProjectionModel}.
 * To be extended in the future.
 * 
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class SymbolicProjectionModel extends Model implements
    ISymbolicModel<IProjectionModel> {

	/** Serialization ID. */
	private static final long serialVersionUID = -3240252207880062231L;

	/** The actual {@link IProjectionModel}. */
	private IProjectionModel projModel;

	/**
	 * Instantiates a new symbolic projection model.
	 * 
	 * @param pModel
	 *          the actual projection model
	 */
	public SymbolicProjectionModel(ProjectionModel pModel) {
		projModel = pModel;
	}

	@Override
	public IProjectionModel getAsDataStructure() {
		return projModel;
	}

	@Override
	public boolean setFromDataStructure(IProjectionModel model) {
		projModel = model;
		return true;
	}

	@Override
	public IDocument<?> getAsDocument(Class<? extends IDocument<?>> targetFormat) {
		return null;
	}

	@Override
	public boolean setFromDocument(IDocument<?> model) {
		return false;
	}

	@Override
	public void removeSource() {
	}

	@Override
	public void setSource(String src, IMIMEType mime) {
	}

	@Override
	public boolean isSourceAvailable() {
		return false;
	}

	@Override
	public String getSource() {
		return null;
	}

	@Override
	public IMIMEType getSourceMimeType() {
		return null;
	}
}
