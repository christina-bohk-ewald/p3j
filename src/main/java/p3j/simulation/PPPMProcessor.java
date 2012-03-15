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

import james.SimSystem;
import james.core.processor.RunnableProcessor;
import james.core.util.misc.Pair;

import java.util.List;
import java.util.logging.Level;

import p3j.database.DatabaseFactory;
import p3j.database.IP3MDatabase;
import p3j.experiment.results.ExecutionSummary;
import p3j.misc.errors.GeneratorError;
import p3j.pppm.IProjectionModel;
import p3j.simulation.assignments.plugintype.IParamAssignmentGenerator;

/**
 * Encapsulates the execution of the PPPM. It calls the chosen
 * {@link IParamAssignmentGenerator} to generate a valid assignment of PPPM
 * variables, i.e. a mapping {@link p3j.pppm.parameters.ParameterInstance} ->
 * {@link p3j.pppm.parameters.ParameterAssignment} that is defined for all
 * {@link p3j.pppm.parameters.ParameterInstance} objects required for
 * calculation (this is dependent on the number of generations, etc.). It also
 * contains the functions that map the {@link p3j.pppm.parameters.Parameter}
 * constants from {@link p3j.pppm.parameters.Parameters} to their destination
 * fields in
 * {@link p3j.simulation.calculation.deterministic.parameters.BasicParameters}.
 * This has be done for every kind of population.
 * 
 * Created on February 18, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class PPPMProcessor extends RunnableProcessor {

	/** Serialization ID. */
	private static final long serialVersionUID = 6432515166274911748L;

	/** The {@link IParamAssignmentGenerator} to be used. */
	private final transient IParamAssignmentGenerator generator;

	/** The model to be simulated. */
	private final IProjectionModel model;

	/**
	 * The pseudo-time, e.g. how many times the deterministic model had been
	 * calculated.
	 */
	private int calcCount;

	/** The database, to store results. */
	private transient IP3MDatabase dataBase;

	/**
	 * Default constructor.
	 * 
	 * @param mod
	 *          the model to be simulated
	 * @param gen
	 *          the assignment generator to be used
	 */
	public PPPMProcessor(IProjectionModel mod, IParamAssignmentGenerator gen) {
		super(mod);
		model = mod;
		generator = gen;
		generator.init(model);
		dataBase = DatabaseFactory.createDatabase();
	}

	@Override
	public Double getTime() {
		return new Double(calcCount);
	}

	/**
	 * Calculates the outcome of *one* deterministic calculation.
	 */
	@Override
	protected void nextStep() {

		// If the generator says we are done, i.e. we cannot generate more samples,
		// then we are done
		if (generator.assignmentsLeft() == 0) {
			calcCount = Integer.MAX_VALUE;
			return;
		}

		// Select assignment, set everything up
		SingleExecution execution = new SingleExecution(model, dataBase);
		Pair<ExecutionSummary, List<GeneratorError>> runResults = execution
		    .execute(generator);

		for (GeneratorError e : runResults.getSecondValue()) {
			SimSystem.report(Level.WARNING, e.getErrorMessage());
		}

		calcCount++;
		changed(new Pair<PPPMProcessor, ExecutionSummary>(this,
		    runResults.getFirstValue()));
	}
}
