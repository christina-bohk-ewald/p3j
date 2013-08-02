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
package p3j.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ProcessorInformation;
import org.jamesii.core.processor.plugintype.ProcessorFactory;

import p3j.pppm.IProjectionModel;
import p3j.pppm.ProjectionModel;
import p3j.simulation.assignments.plugintype.AbstractParamAssignmentGenFactory;
import p3j.simulation.assignments.plugintype.ParamAssignmentGenFactory;

/**
 * Factory for a simple {@link PPPMProcessor}.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class PPPMProcessorFactory extends ProcessorFactory {

	/** Serialization ID. */
	private static final long serialVersionUID = -7552093170636960651L;

	@Override
	public IProcessor create(IModel model, IComputationTask computationTask,
	    Partition partition, ParameterBlock params) {
		if (!(model instanceof ProjectionModel)) {
			return null;
		}
		ParameterBlock pagfp = params.getSubBlock(ParamAssignmentGenFactory.class
		    .getName());
		ParamAssignmentGenFactory pagf = SimSystem.getRegistry().getFactory(
		    AbstractParamAssignmentGenFactory.class, pagfp);
		SimSystem.report(Level.INFO,
		    "Using parameter assignment generator:" + pagf.getClass());
		PPPMProcessor processor = new PPPMProcessor((ProjectionModel) model,
		    pagf.create(pagfp));
		processor.setComputationTask(computationTask);
		ProcessorInformation pi = new ProcessorInformation();
		pi.setLocal(processor);
		computationTask.setProcessorInfo(pi);
		return processor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see james.core.processor.plugintype.ProcessorFactory#getEfficencyIndex()
	 */
	@Override
	public double getEfficencyIndex() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * james.core.processor.plugintype.ProcessorFactory#getSupportedInterfaces()
	 */
	@Override
	public List<Class<?>> getSupportedInterfaces() {
		ArrayList<Class<?>> suppInterfaces = new ArrayList<Class<?>>();
		suppInterfaces.add(IProjectionModel.class);
		return suppInterfaces;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * james.core.processor.plugintype.ProcessorFactory#supportsSubPartitions()
	 */
	@Override
	public boolean supportsSubPartitions() {
		return false;
	}

}
