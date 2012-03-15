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
package p3j.experiment;

import java.util.Date;

import p3j.pppm.ProjectionModel;
import p3j.simulation.ISimulationParameters;
import p3j.simulation.assignments.plugintype.IParamAssignmentGenerator;

/**
 * Contains all information that are necessary to repeat an experiment.
 * 
 * Created: August 20, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class ExperimentDefinition {

	/** The ID of this experiment. */
	private int id;

	/** The random seed to be used. */
	private long randSeed;

	/** ID of the user who conducted the experiment. */
	private String user;

	/** Date of the experiment. */
	private Date date;

	/**
	 * Projection parameters that have been used. This encapsulates all input
	 * values.
	 */
	private ProjectionModel projection;

	/** The simulator that has been used. */
	private Class<? extends IParamAssignmentGenerator> simulator;

	/** The simulation parameters that have been used. */
	private ISimulationParameters simParams;

	/** All results of the experiment. */
	private ResultSet resultSet;

	/**
	 * Default constructor.
	 * 
	 * @param proj
	 *          the input used for the projection
	 * @param randS
	 *          the seed to be used for the random number generator (only relevant
	 *          for Monte-Carlo simulators)
	 * @param currentDate
	 *          current date
	 * @param userName
	 *          name of the user that issued this experiment
	 * @param sim
	 *          the class of the simulation algorithm
	 */
	public ExperimentDefinition(ProjectionModel proj, long randS,
	    Date currentDate, String userName,
	    Class<? extends IParamAssignmentGenerator> sim) {
		projection = proj;
		randSeed = randS;
		date = new Date(currentDate.getTime());
		user = userName;
		simulator = sim;
	}

	/**
	 * Constructor for bean compliance.
	 */
	public ExperimentDefinition() {
	}

	/**
	 * Gets the rand seed.
	 * 
	 * @return the rand seed
	 */
	public long getRandSeed() {
		return randSeed;
	}

	/**
	 * Sets the rand seed.
	 * 
	 * @param randSeed
	 *          the new rand seed
	 */
	public void setRandSeed(long randSeed) {
		this.randSeed = randSeed;
	}

	/**
	 * Gets the user.
	 * 
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 * 
	 * @param user
	 *          the new user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Gets the date.
	 * 
	 * @return the date
	 */
	public Date getDate() {
		return new Date(date.getTime());
	}

	/**
	 * Sets the date.
	 * 
	 * @param date
	 *          the new date
	 */
	public void setDate(Date date) {
		this.date.setTime(date.getTime());
	}

	/**
	 * Gets the projection.
	 * 
	 * @return the projection
	 */
	public ProjectionModel getProjection() {
		return projection;
	}

	/**
	 * Sets the projection.
	 * 
	 * @param projection
	 *          the new projection
	 */
	public void setProjection(ProjectionModel projection) {
		this.projection = projection;
	}

	/**
	 * Gets the simulator.
	 * 
	 * @return the simulator
	 */
	public Class<? extends IParamAssignmentGenerator> getSimulator() {
		return simulator;
	}

	/**
	 * Sets the simulator.
	 * 
	 * @param simulator
	 *          the new simulator
	 */
	public void setSimulator(Class<? extends IParamAssignmentGenerator> simulator) {
		this.simulator = simulator;
	}

	/**
	 * Gets the sim params.
	 * 
	 * @return the sim params
	 */
	public ISimulationParameters getSimParams() {
		return simParams;
	}

	/**
	 * Sets the sim params.
	 * 
	 * @param simParams
	 *          the new sim params
	 */
	public void setSimParams(ISimulationParameters simParams) {
		this.simParams = simParams;
	}

	/**
	 * Gets the result set.
	 * 
	 * @return the result set
	 */
	public ResultSet getResultSet() {
		return resultSet;
	}

	/**
	 * Sets the result set.
	 * 
	 * @param resultSet
	 *          the new result set
	 */
	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public int getID() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param uniqueID
	 *          the new id
	 */
	public void setID(int uniqueID) {
		id = uniqueID;
	}

}
