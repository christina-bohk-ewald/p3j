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
package p3j.simulation.assignments.exhaustive;

import org.jamesii.core.parameters.ParameterBlock;

import p3j.simulation.assignments.plugintype.IParamAssignmentGenerator;
import p3j.simulation.assignments.plugintype.ParamAssignmentGenFactory;

/**
 * Creates an {@link ExhaustiveAssignmentGenerator}.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 * 
 */
public class ExhaustiveParamAssignmentGenFactory extends
    ParamAssignmentGenFactory {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -2894733238035338522L;

  /*
   * (non-Javadoc)
   * 
   * @see
   * p3j.simulation.assignments.plugintype.ParamAssignmentGenFactory#create(
   * james.core.simulation.parameters.ParameterBlock)
   */
  @Override
  public IParamAssignmentGenerator create(ParameterBlock params) {
    ExhaustiveAssignmentGenerator generator = new ExhaustiveAssignmentGenerator();
    ExhaustiveSimParameters parameters = new ExhaustiveSimParameters();
    generator.setParameters(parameters);
    return generator;
  }

}
