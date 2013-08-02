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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jamesii.core.model.IModel;
import org.jamesii.core.model.formalism.Formalism;
import org.jamesii.core.model.plugintype.ModelFactory;
import org.jamesii.core.model.symbolic.ISymbolicModel;

/**
 * Creates {@link ProjectionModel} instances.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class PPPModelFactory extends ModelFactory {

  /** Serialization ID. */
  private static final long serialVersionUID = -8959309833233820296L;

  /** Default number of generations. */
  public static final int DEFAULT_GENERATIONS = 2;

  /** Default number of years to be predicted. */
  public static final int DEFAULT_YEARS = 10;

  /** Default maximum age. */
  public static final int DEFAULT_MAX_AGE = 100;

  /** The default calendar year. */
  public static final int DEFAULT_JUMP_OFF_YEAR = Calendar.getInstance().get(
      Calendar.YEAR);

  /** The default model of sub-populations. */
  public static final SubPopulationModel DEFAULT_SUBPOPULATION_MODEL = createDefaultSubPopulationModel();

  @Override
  public ISymbolicModel<?> create() {
    return new SymbolicProjectionModel(createDefaultModel());
  }

  @Override
  public Formalism getFormalism() {
    return new PPPMFormalism();
  }

  @Override
  public List<Class<? extends IModel>> getSupportedInterfaces() {
    ArrayList<Class<? extends IModel>> suppInterfaces = new ArrayList<Class<? extends IModel>>();
    suppInterfaces.add(IProjectionModel.class);
    return suppInterfaces;
  }

  /**
   * Creates a default {@link ProjectionModel}.
   * 
   * @return the projection model
   */
  public static ProjectionModel createDefaultModel() {
    return createModel("New Projection", "No description entered.",
        DEFAULT_GENERATIONS, DEFAULT_YEARS, DEFAULT_MAX_AGE,
        DEFAULT_JUMP_OFF_YEAR, DEFAULT_SUBPOPULATION_MODEL);
  }

  /**
   * Creates the default sub-population model (natives, immigrants, and
   * emigrants).
   * 
   * @return the sub-population model
   */
  public static SubPopulationModel createDefaultSubPopulationModel() {
    SubPopulationModel defaultModel = new SubPopulationModel();
    defaultModel.getSubPopulations().add(
        new SubPopulation("Natives", true, true, false));
    defaultModel.getSubPopulations().add(
        new SubPopulation("Immigrants", false, true, true));
    defaultModel.getSubPopulations().add(
        new SubPopulation("Emigrants", false, false, true));
    return defaultModel;
  }

  /**
   * Creates a new PPPModel object.
   * 
   * @param name
   *          the name
   * @param description
   *          the description
   * @param generations
   *          the number of generations
   * @param years
   *          the number of years
   * @param maxAge
   *          the maximum age
   * @param jumpOffYear
   *          the jump-off year
   * @param subPopModel
   *          the model of sub-populations
   * @return the projection model
   */
  public static ProjectionModel createModel(String name, String description,
      int generations, int years, int maxAge, int jumpOffYear,
      SubPopulationModel subPopModel) {
    return new ProjectionModel(name, description, generations, years, maxAge,
        jumpOffYear, subPopModel);
  }

}
