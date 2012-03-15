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
import p3j.gui.dialogs.NewSetDialog;
import p3j.gui.misc.SubNodeSummary;
import p3j.gui.panels.PropertiesShowPanelFactory;
import p3j.misc.gui.GUI;
import p3j.pppm.ProjectionModel;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * Node in the projection tree.
 * 
 * Created: August 24, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class SetTypeNode extends ProjectionTreeNode<SetType> {

	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = -7749809167097727324L;

	/**
	 * Default constructor.
	 * 
	 * @param setType
	 *          the Settype represented by this node
	 */
	public SetTypeNode(SetType setType) {
		super(setType, setType.getName());
	}

	@Override
	public JPanel selected(TreePath selectionPath, final IProjectionTree projTree) {

		// Layout for info sheet on Settype

		final SetTypeNode node = this;
		final ProjectionNode projectionNode = (ProjectionNode) selectionPath
		    .getParentPath().getLastPathComponent();
		final JTextField name = new JTextField(getEntity().getName());
		final JTextArea description = new JTextArea(getEntity().getDescription());

		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GUI.printQuestion(P3J.getInstance(), "Really remove Settype?",
				    "Do you really want to remove the Settype '"
				        + getEntity().getName()
				        + "'? All associated data will be lost!")) {
					return;
				}
				ProjectionModel projection = projectionNode.getEntity();
				projection.removeSetType(getEntity());
				DatabaseFactory.getDatabaseSingleton().saveProjection(projection);
				projTree.totalRefresh();
			}
		});

		JButton createSetButton = new JButton("New Set");
		createSetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NewSetDialog dialog = new NewSetDialog(node, projTree);
				dialog.setVisible(true);
			}
		});

		JButton applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SetType setType = getEntity();
				setType.setName(name.getText());
				setType.setDescription(description.getText());
				setUserObject(setType.getName());
				DatabaseFactory.getDatabaseSingleton().saveSetType(setType);
				projTree.refreshNode(node);
			}
		});

		List<JButton> buttons = new ArrayList<JButton>();
		buttons.add(removeButton);
		buttons.add(createSetButton);
		buttons.add(applyButton);
		PropertiesShowPanelFactory pspf = new PropertiesShowPanelFactory(buttons, 1);
		pspf.sep("General Information");
		pspf.app("Name:", name);
		pspf.app("Description:", description, 2);
		pspf.app("Sets:", getEntity().getNumOfSets());
		pspf.app("Covered Parameters:", getEntity().getDefinedParameters().size());

		// Configure default set-type specifics (no set creation allowed)
		ProjectionModel projection = getProjectionEntity(ProjectionModel.class);
		boolean isDefault = (projection != null && getEntity() == projection
		    .getDefaultSetType());
		removeButton.setEnabled(!isDefault);
		createSetButton.setEnabled(!isDefault);
		pspf.app("Default type?", isDefault ? "Yes" : "No");
		pspf.appPreview(new SubNodeSummary<Set>(this, projTree, Set.class));

		return pspf.constructPanel();
	}
}
