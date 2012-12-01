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
package p3j.gui.panels.projections;

import javax.swing.JPanel;
import javax.swing.tree.TreePath;

import p3j.gui.misc.SubNodeSummary;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.pppm.SubPopulation;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.parameters.Population;

/**
 * Node that represents a {@link Population}.
 * 
 * Created: August 24, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class SubPopulationNode extends ProjectionTreeNode<SubPopulation> {

  /** Serialization ID. */
  private static final long serialVersionUID = 6366143563278777563L;

  /**
   * Default constructor.
   * 
   * @param subPopulation
   *          the sub-population to be represented
   */
  public SubPopulationNode(SubPopulation subPopulation) {
    super(subPopulation, subPopulation.getName());
  }

  @Override
  public JPanel selected(TreePath selectionPath, final IProjectionTree projTree) {
    PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory();
    pspf.sep("General Information");
    pspf.app("Subpopulation:", getEntity());
    pspf.app("Parameters:", this.getChildCount());
    pspf.appPreview(new SubNodeSummary<ParameterInstance>(this, projTree,
        ParameterInstance.class));
    return pspf.constructPanel();
  }
}
