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
package p3j.simulation.calculation.deterministic.parameters;

import p3j.misc.math.Matrix2D;

/**
 * Basic parameters of the model.
 * 
 * Created on July 22, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class BasicParameters {

	/** Number of years to be projected. */
	private int numOfYears;

	/** The maximal age that is considered individually. */
	private int maxAge;

	/** Year-by-year age-specific male survivors. */
	private Matrix2D mortXm;

	/** Year-by-year age-specific female survivors. */
	private Matrix2D mortXf;

	/**
	 * Year-by-year matrix with fraction of male babies that died in the first six
	 * months (in relation to all babies that died).
	 */
	private Matrix2D deathProbInfant1halfMale;

	/**
	 * Year-by-year matrix with fraction of female babies that died in the first
	 * six months (TODO: NOT YET USED!!!).
	 */
	private Matrix2D deathProbInfant1halfFemale;

	/** Year-by-year survival probability of males at ages over 100. */
	private Matrix2D surviveProbO100m;

	/** Year-by-year survival probability of females at ages over 100. */
	private Matrix2D surviveProbO100f;

	/** Year-by-year age-specific fertility. */
	private Matrix2D fertX;

	/** Year-by-year proportion of male live births. */
	private Matrix2D malePropLiveBirth;

	/** Year-by-year proportion of female live births. */
	private Matrix2D femalePropLiveBirth;

	/**
	 * Default constructor.
	 * 
	 * @param numYears
	 *          number of years to be projected
	 * @param maximumAge
	 *          the maximum age to be considered
	 */
	public BasicParameters(int numYears, int maximumAge) {
		this.numOfYears = numYears;
		this.maxAge = maximumAge;
	}

	/**
	 * Setting rate of male live birth also sets the female one (1 - male).
	 * 
	 * @param maleRateLiveBirth
	 *          rate of male live births
	 */
	public void setMaleRateLiveBirth(Matrix2D maleRateLiveBirth) {
		this.malePropLiveBirth = maleRateLiveBirth;
		int rows = maleRateLiveBirth.rows();
		int cols = maleRateLiveBirth.columns();
		this.femalePropLiveBirth = new Matrix2D(rows, cols);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				this.femalePropLiveBirth.setQuick(i, j,
				    1.0 - maleRateLiveBirth.getQuick(i, j));
			}
		}
	}

	public Matrix2D getFemalePropLiveBirth() {
		return femalePropLiveBirth;
	}

	public Matrix2D getMalePropLiveBirth() {
		return malePropLiveBirth;
	}

	public int getNumOfYears() {
		return numOfYears;
	}

	public void setNumOfYears(int numOfYears) {
		this.numOfYears = numOfYears;
	}

	public Matrix2D getMortXm() {
		return mortXm;
	}

	public void setMortXm(Matrix2D mortXm) {
		this.mortXm = mortXm;
	}

	public Matrix2D getMortXf() {
		return mortXf;
	}

	public void setMortXf(Matrix2D mortXf) {
		this.mortXf = mortXf;
	}

	public Matrix2D getDeathProbInfant1halfMale() {
		return deathProbInfant1halfMale;
	}

	public void setDeathProbInfant1halfMale(Matrix2D deathProbInfant1halfMale) {
		this.deathProbInfant1halfMale = deathProbInfant1halfMale;
	}

	public Matrix2D getDeathProbInfant1halfFemale() {
		return deathProbInfant1halfFemale;
	}

	public void setDeathProbInfant1halfFemale(Matrix2D deathProbInfant1halfFemale) {
		this.deathProbInfant1halfFemale = deathProbInfant1halfFemale;
	}

	public Matrix2D getSurviveProbO100m() {
		return surviveProbO100m;
	}

	public void setSurviveProbO100m(Matrix2D surviveProbO100m) {
		this.surviveProbO100m = surviveProbO100m;
	}

	public Matrix2D getSurviveProbO100f() {
		return surviveProbO100f;
	}

	public void setSurviveProbO100f(Matrix2D surviveProbO100f) {
		this.surviveProbO100f = surviveProbO100f;
	}

	public Matrix2D getFertX() {
		return fertX;
	}

	public void setFertX(Matrix2D fertX) {
		this.fertX = fertX;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

}