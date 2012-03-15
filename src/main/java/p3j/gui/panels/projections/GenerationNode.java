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

import java.util.List;

import javax.swing.JPanel;
import javax.swing.tree.TreePath;

import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.pppm.parameters.ParameterInstance;

/**
 * Represents a set of instances that belongs to the same generation.
 * 
 * Created: August 24, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class GenerationNode extends ProjectionTreeNode<List<ParameterInstance>> {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 6051811013784521035L;

  /**
   * Default constructor.
   * 
   * @param instances
   *          non-empty list of instances belonging to the same generation
   */
  public GenerationNode(List<ParameterInstance> instances) {
    super(instances, "Descendant Generation "
        + (instances.get(0).getGeneration()));
  }

  @Override
  public JPanel selected(TreePath selectionPath, final IProjectionTree projTree) {
    PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory();
    pspf.sep("General Information");
    pspf.app("Descendant Generation:", getEntity().get(0).getGeneration());
    pspf.app("Parameters:", getEntity().size());
    pspf.app("Populations:", this.getChildCount());
    return pspf.constructPanel();
  }

}
