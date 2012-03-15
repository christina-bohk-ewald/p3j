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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.tree.TreePath;

import p3j.database.DatabaseFactory;
import p3j.gui.P3J;
import p3j.gui.misc.SubNodeSummary;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.misc.Misc;
import p3j.misc.gui.GUI;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * A node in the projection tree that represents a {@link Set}.
 * 
 * Created: August 24, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class SetNode extends ProjectionTreeNode<Set> {

	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = -6441130209061752819L;

	/**
	 * Default constructor.
	 * 
	 * @param set
	 *          the set to be represented
	 */
	public SetNode(Set set) {
		super(set, set.getName());
	}

	@Override
	public JPanel selected(TreePath selectionPath, final IProjectionTree projTree) {

		// Layout for info sheet on set.

		final ProjectionTreeNode<?> node = this;
		final SetTypeNode stNode = (SetTypeNode) selectionPath.getParentPath()
		    .getLastPathComponent();
		final JTextField name = new JTextField(getEntity().getName());
		final JTextArea description = new JTextArea(getEntity().getDescription());
		final JTextField probability = new JTextField(getEntity().getProbability()
		    + "");

		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GUI.printQuestion(P3J.getInstance(), "Really remove set?",
				    "Do you really want to remove the set '" + getEntity().getName()
				        + "'? All associated data will be lost!")) {
					return;
				}
				SetType sType = stNode.getEntity();
				sType.removeSet(getEntity());
				DatabaseFactory.getDatabaseSingleton().saveSetType(sType);
				projTree.removeNode(node);
			}
		});

		JButton applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Set set = getEntity();
				set.setName(name.getText());
				set.setDescription(description.getText());
				set.setProbability(Double.parseDouble(probability.getText()));
				setUserObject(set.getName());
				DatabaseFactory.getDatabaseSingleton().saveSet(set);
				projTree.refreshNode(node);
			}
		});

		List<JButton> buttons = new ArrayList<JButton>();
		buttons.add(removeButton);
		buttons.add(applyButton);

		PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(buttons, 1);
		pspf.sep("General Information");
		pspf.app(Misc.GUI_LABEL_NAME, name);
		pspf.app(Misc.GUI_LABEL_DESCRIPTION, description, 2);
		pspf.app(Misc.GUI_LABEL_PROBABILITY, probability);

		// Set default Settype specifics - there will only be one set in the
		// default Settype, etc.
		SetType mySetType = getProjectionEntity(SetType.class);
		ProjectionModel projection = getProjectionEntity(ProjectionModel.class);
		boolean isDefault = (mySetType != null && projection != null && mySetType == projection
		    .getDefaultSetType());
		probability.setEditable(!isDefault);
		removeButton.setEnabled(!isDefault);
		pspf.app("Default set?", isDefault ? "Yes" : "No");
		pspf.appPreview(new SubNodeSummary<ParameterInstance>(this, projTree,
		    ParameterInstance.class));

		return pspf.constructPanel();
	}
}
