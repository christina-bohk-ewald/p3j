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
package p3j.pppm.parameters;

import james.SimSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import p3j.database.DatabaseFactory;
import p3j.database.IP3MDatabase;
import p3j.misc.MatrixDimension;

/**
 * Central parameters directory. Contains all {@link Parameter} definitions
 * necessary for the PPPM.
 * 
 * Created on August 7, 2006
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public final class Parameters {

	/** Counter for the IDs. */
	private static int idCounter = 1;

	/**
	 * The survivors at age x. Since this string is later used to check whether a
	 * given parameter refers to mortality, it should be constant over all
	 * {@link Parameter} instances.
	 * 
	 * TODO: Add an enumeration to define the type of a {@link Parameter}.
	 */
	public static final String SURVIVORS_AGE_X = "Survivors at age x";

	/** List with all parameters. */
	private List<Parameter> params = new ArrayList<Parameter>();

	/** Mapping from id => parameter. */
	private final Map<Integer, Parameter> idToParameter = new HashMap<Integer, Parameter>();

	/** Natives: Survival probability of open-end age class (male). */
	public static final Parameter NAT_SURV_PROB_O100_M = new Parameter(nextID(),
	    false, "Natives: Survival probability of open-end age class (male)",
	    MatrixDimension.SINGLE, MatrixDimension.YEARS, Population.NATIVES);

	/** Natives: Survival probability of open-end age class (female). */
	public static final Parameter NAT_SURV_PROB_O100_F = new Parameter(nextID(),
	    false, "Natives: Survival probability of open-end age class (female)",
	    MatrixDimension.SINGLE, MatrixDimension.YEARS, Population.NATIVES);

	/** Natives: Proportion of infant deaths dying in the first 6 months (male). */
	public static final Parameter NAT_DEATHPROB_INFANT_1STHALF_M = new Parameter(
	    nextID(),
	    false,
	    "Natives: Proportion of infant deaths dying in the first 6 months (male)",
	    MatrixDimension.SINGLE, MatrixDimension.YEARS, Population.NATIVES);

	/** Natives: Proportion of infant deaths dying in the first 6 months (female). */
	public static final Parameter NAT_DEATHPROB_INFANT_1STHALF_F = new Parameter(
	    nextID(),
	    false,
	    "Natives: Proportion of infant deaths dying in the first 6 months (female)",
	    MatrixDimension.SINGLE, MatrixDimension.YEARS, Population.NATIVES);

	/** Natives: Proportion of male live births. */
	public static final Parameter NAT_PROP_LIVEBIRTH_M = new Parameter(nextID(),
	    false, "Natives: Proportion of male live births", MatrixDimension.SINGLE,
	    MatrixDimension.YEARS, Population.NATIVES);

	/** Natives: Mortality (male). */
	public static final Parameter NAT_MORT_X_M = new Parameter(nextID(), false,
	    "Natives: " + SURVIVORS_AGE_X + " (male)", MatrixDimension.AGES,
	    MatrixDimension.YEARS, Population.NATIVES);

	/** Natives: Mortality (female). */
	public static final Parameter NAT_MORT_X_F = new Parameter(nextID(), false,
	    "Natives: " + SURVIVORS_AGE_X + " (female)", MatrixDimension.AGES,
	    MatrixDimension.YEARS, Population.NATIVES);

	/** Natives: Fertility. */
	public static final Parameter NAT_FERT = new Parameter(nextID(), false,
	    "Natives: Fertility", MatrixDimension.AGES, MatrixDimension.YEARS,
	    Population.NATIVES);

	/** Natives: Jump-off population (male). */
	public static final Parameter NAT_P_END_SY_M = new Parameter(nextID(), false,
	    "Natives: Jump-off population (male)", MatrixDimension.AGES,
	    MatrixDimension.SINGLE, Population.NATIVES);

	/** Natives: Jump-off population (female). */
	public static final Parameter NAT_P_END_SY_F = new Parameter(nextID(), false,
	    "Natives: Jump-off population (female)", MatrixDimension.AGES,
	    MatrixDimension.SINGLE, Population.NATIVES);

	// EMIGRANTS

	/** Emigrants (male). */
	public static final Parameter EMIGRANTS_M = new Parameter(nextID(), false,
	    "Emigrants (male)", MatrixDimension.AGES, MatrixDimension.YEARS,
	    Population.EMIGRANTS);

	/** Emigrants (female). */
	public static final Parameter EMIGRANTS_F = new Parameter(nextID(), false,
	    "Emigrants (female)", MatrixDimension.AGES, MatrixDimension.YEARS,
	    Population.EMIGRANTS);

	/** Emigrants: Survival probability of open-end age class (male). */
	public static final Parameter EMIG_SURV_PROB_O100_M = new Parameter(nextID(),
	    true, "Emigrants: Survival probability of open-end age class (male)",
	    MatrixDimension.SINGLE, MatrixDimension.YEARS, Population.EMIGRANTS);

	/** Emigrants: Survival probability of open-end age class (female). */
	public static final Parameter EMIG_SURV_PROB_O100_F = new Parameter(nextID(),
	    true, "Emigrants: Survival probability of open-end age class (female)",
	    MatrixDimension.SINGLE, MatrixDimension.YEARS, Population.EMIGRANTS);

	/** Emigrants: Proportion of infant deaths dying in the first 6 months (male). */
	public static final Parameter EMIG_DEATHPROB_INFANT_1STHALF_M = new Parameter(
	    nextID(),
	    true,
	    "Emigrants: Proportion of infant deaths dying in the first 6 months (male)",
	    MatrixDimension.SINGLE, MatrixDimension.YEARS, Population.EMIGRANTS);

	/**
	 * Emigrants: Proportion of infant deaths dying in the first 6 months
	 * (female).
	 */
	public static final Parameter EMIG_DEATHPROB_INFANT_1STHALF_F = new Parameter(
	    nextID(),
	    true,
	    "Emigrants: Proportion of infant deaths dying in the first 6 months (female)",
	    MatrixDimension.SINGLE, MatrixDimension.YEARS, Population.EMIGRANTS);

	/** Emigrants: Proportion of male live births. */
	public static final Parameter EMIG_PROP_LIVEBIRTH_M = new Parameter(nextID(),
	    true, "Emigrants: Proportion of male live births",
	    MatrixDimension.SINGLE, MatrixDimension.YEARS, Population.EMIGRANTS);

	/** Emigrants: Mortality (male). */
	public static final Parameter EMIG_MORT_X_M = new Parameter(nextID(), true,
	    "Emigrants: " + SURVIVORS_AGE_X + " (male)", MatrixDimension.AGES,
	    MatrixDimension.YEARS, Population.EMIGRANTS);

	/** Emigrants: Mortality (female). */
	public static final Parameter EMIG_MORT_X_F = new Parameter(nextID(), true,
	    "Emigrants: " + SURVIVORS_AGE_X + " (female)", MatrixDimension.AGES,
	    MatrixDimension.YEARS, Population.EMIGRANTS);

	/** Emigrants: Fertility. */
	public static final Parameter EMIG_FERT = new Parameter(nextID(), true,
	    "Emigrants: Fertility", MatrixDimension.AGES, MatrixDimension.YEARS,
	    Population.EMIGRANTS);

	// IMMIGRANTS

	/** Immigrants (male). */
	public static final Parameter IMMIG_M = new Parameter(nextID(), false,
	    "Immigrants (male)", MatrixDimension.AGES, MatrixDimension.YEARS,
	    Population.IMMIGRANTS);

	/** Immigrants (female). */
	public static final Parameter IMMIG_F = new Parameter(nextID(), false,
	    "Immigrants (female)", MatrixDimension.AGES, MatrixDimension.YEARS,
	    Population.IMMIGRANTS);

	/** Immigrants: Survival probability of open-end age class (male). */
	public static final Parameter IMMIG_SURV_PROB_O100_M = new Parameter(
	    nextID(), true,
	    "Immigrants: Survival probability of open-end age class (male)",
	    MatrixDimension.SINGLE, MatrixDimension.YEARS, Population.IMMIGRANTS);

	/** Immigrants: Survival probability of open-end age class (female). */
	public static final Parameter IMMIG_SURV_PROB_O100_F = new Parameter(
	    nextID(), true,
	    "Immigrants: Survival probability of open-end age class (female)",
	    MatrixDimension.SINGLE, MatrixDimension.YEARS, Population.IMMIGRANTS);

	/**
	 * Immigrants: Proportion of infant deaths dying in the first 6 months (male).
	 */
	public static final Parameter IMMIG_DEATHPROB_INFANT_1STHALF_M = new Parameter(
	    nextID(),
	    true,
	    "Immigrants: Proportion of infant deaths dying in the first 6 months (male)",
	    MatrixDimension.SINGLE, MatrixDimension.YEARS, Population.IMMIGRANTS);

	/**
	 * Immigrants: Proportion of infant deaths dying in the first 6 months
	 * (female).
	 */
	public static final Parameter IMMIG_DEATHPROB_INFANT_1STHALF_F = new Parameter(
	    nextID(),
	    true,
	    "Immigrants: Proportion of infant deaths dying in the first 6 months (female)",
	    MatrixDimension.SINGLE, MatrixDimension.YEARS, Population.IMMIGRANTS);

	/** Immigrants: Proportion of male live births. */
	public static final Parameter IMMIG_PROP_LIVEBIRTH_M = new Parameter(
	    nextID(), true, "Immigrants: Proportion of male live births",
	    MatrixDimension.SINGLE, MatrixDimension.YEARS, Population.IMMIGRANTS);

	/** Immigrants: Mortality (male). */
	public static final Parameter IMMIG_MORT_X_M = new Parameter(nextID(), true,
	    "Immigrants: " + SURVIVORS_AGE_X + " (male)", MatrixDimension.AGES,
	    MatrixDimension.YEARS, Population.IMMIGRANTS);

	/** Immigrants: Mortality (female). */
	public static final Parameter IMMIG_MORT_X_F = new Parameter(nextID(), true,
	    "Immigrants: " + SURVIVORS_AGE_X + " (female)", MatrixDimension.AGES,
	    MatrixDimension.YEARS, Population.IMMIGRANTS);

	/** Immigrants: Fertility. */
	public static final Parameter IMMIG_FERT = new Parameter(nextID(), true,
	    "Immigrants: Fertility", MatrixDimension.AGES, MatrixDimension.YEARS,
	    Population.IMMIGRANTS);

	/** The singleton. */
	private static Parameters singleton;

	/**
	 * Returns singleton.
	 * 
	 * @return the parameters singleton
	 */
	public static Parameters getInstance() {
		if (singleton == null) {
			singleton = new Parameters();
		}
		return singleton;
	}

	/**
	 * Generates a unique id.
	 * 
	 * @return a unique id
	 */
	protected static int nextID() {
		return ++idCounter;
	}

	/**
	 * This class shall not be instantiated.
	 */
	private Parameters() {

		addNatives();
		addEmigrants();
		addImmigrants();

		// Is there a database
		IP3MDatabase db = DatabaseFactory.getDatabaseSingleton();

		List<Parameter> registeredParams = new ArrayList<Parameter>();

		// Set IDs
		for (Parameter p : params) {
			if (db != null) {
				Parameter registeredParameter = null;
				try {
					registeredParameter = db.newParameter(p.getName(),
					    p.isGenerationDependent(), p.getValueHeight(), p.getValueWidth(),
					    p.getPopulation());
				} catch (Exception ex) {
					SimSystem.report(ex);
				}
				if (registeredParameter != null) {
					registeredParams.add(registeredParameter);
				}
			} else {
				registeredParams.add(p);
			}
		}

		params = registeredParams;

		for (Parameter p : params) {
			idToParameter.put(p.getID(), p);
		}

	}

	/**
	 * Adds emigrant fields to the parameter mapping.
	 */
	private void addImmigrants() {
		params.add(IMMIG_SURV_PROB_O100_M);
		params.add(IMMIG_SURV_PROB_O100_F);
		params.add(IMMIG_DEATHPROB_INFANT_1STHALF_M);
		params.add(IMMIG_DEATHPROB_INFANT_1STHALF_F);
		params.add(IMMIG_PROP_LIVEBIRTH_M);
		params.add(IMMIG_MORT_X_M);
		params.add(IMMIG_MORT_X_F);
		params.add(IMMIG_FERT);
		params.add(IMMIG_M);
		params.add(IMMIG_F);
	}

	/**
	 * Adds the emigrant fields to parameter mapping.
	 */
	private void addEmigrants() {
		params.add(EMIG_SURV_PROB_O100_M);
		params.add(EMIG_SURV_PROB_O100_F);
		params.add(EMIG_DEATHPROB_INFANT_1STHALF_M);
		params.add(EMIG_DEATHPROB_INFANT_1STHALF_F);
		params.add(EMIG_PROP_LIVEBIRTH_M);
		params.add(EMIG_MORT_X_M);
		params.add(EMIG_MORT_X_F);
		params.add(EMIG_FERT);
		params.add(EMIGRANTS_M);
		params.add(EMIGRANTS_F);
	}

	/**
	 * Adds the native fields to parameter mapping.
	 */
	private void addNatives() {
		params.add(NAT_SURV_PROB_O100_M);
		params.add(NAT_SURV_PROB_O100_F);
		params.add(NAT_DEATHPROB_INFANT_1STHALF_M);
		params.add(NAT_DEATHPROB_INFANT_1STHALF_F);
		params.add(NAT_PROP_LIVEBIRTH_M);
		params.add(NAT_MORT_X_M);
		params.add(NAT_MORT_X_F);
		params.add(NAT_FERT);
		params.add(NAT_P_END_SY_M);
		params.add(NAT_P_END_SY_F);
	}

	/**
	 * Gets the params.
	 * 
	 * @return the params
	 */
	public List<Parameter> getParams() {
		return params;
	}

	/**
	 * Sets the params.
	 * 
	 * @param params
	 *          the new params
	 */
	public void setParams(List<Parameter> params) {
		this.params = params;
	}

}
