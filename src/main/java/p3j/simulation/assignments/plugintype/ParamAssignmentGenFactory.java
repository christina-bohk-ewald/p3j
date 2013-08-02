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
package p3j.simulation.assignments.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base class for {@link IParamAssignmentGenerator} factories.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public abstract class ParamAssignmentGenFactory extends
    Factory<IParamAssignmentGenerator> {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -3123446562383771745L;

  /**
   * Creates an {@link IParamAssignmentGenerator} instance.
   * 
   * @param params
   *          parameters for creating the instance
   * @return a new generator
   */
  @Override
  public abstract IParamAssignmentGenerator create(ParameterBlock params);

}
