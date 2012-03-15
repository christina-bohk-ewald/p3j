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
package p3j.gui.dialogs;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import p3j.database.DatabaseFactory;
import p3j.gui.panels.projections.IProjectionTree;
import p3j.gui.panels.projections.SetNode;
import p3j.gui.panels.projections.SetTypeNode;
import p3j.misc.Misc;
import p3j.misc.gui.GUI;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Dialog to create a new {@link Set} for a given {@link SetType}.
 * 
 * Created: September 7, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class NewSetDialog extends
    AbstractProjectionTreeDialog<SetType, SetTypeNode> {

	/** Serialization ID. */
	private static final long serialVersionUID = -6901469269123228049L;

	/** Width of the dialog. */
	public static final int DIALOG_WIDTH = 480;

	/** Height of the dialog. */
	public static final int DIALOG_HEIGHT = 320;

	/** Text field for the name of the set. */
	private JTextField name = new JTextField("New Set");

	/** Text field to enter the probability. */
	private JTextField probability = new JTextField("0.0");

	/** Text area to enter a description of the set. */
	private JTextArea description = new JTextArea("");

	/**
	 * Default constructor.
	 * 
	 * @param stNode
	 *          the node of the current Settype
	 * @param projTree
	 *          the current projection tree
	 */
	public NewSetDialog(SetTypeNode stNode, IProjectionTree projTree) {
		super(stNode, projTree, DIALOG_WIDTH, DIALOG_HEIGHT, "Add Set");
		initUI();
	}

	/**
	 * Initializes user interface.
	 */
	private void initUI() {
		setTitle("Create New Set");

		FormLayout layout = new FormLayout("10dlu,right:60dlu,10dlu,d:grow,10dlu",
		    "10dlu,d,10dlu,d,10dlu,fill:80dlu:grow,10dlu,d,10dlu");
		JPanel contentPanel = new JPanel(layout);

		int currRow = GUI.ROW_SKIP_LAYOUT;
		currRow = GUI.addRowToPanel(contentPanel, Misc.GUI_LABEL_NAME, name,
		    currRow);
		currRow = GUI.addRowToPanel(contentPanel, Misc.GUI_LABEL_PROBABILITY,
		    probability, currRow);
		currRow = GUI.addRowToPanel(contentPanel, Misc.GUI_LABEL_DESCRIPTION,
		    description, currRow);

		contentPanel.add(getButtonPanel(),
		    new CellConstraints().xy(GUI.INPUT_COLUMN_INDEX, currRow));
		setContentPane(contentPanel);
	}

	@Override
	protected void cancel() {
		setVisible(false);
	}

	@Override
	protected void ok() {
		Set set = getEntity().createSet(name.getText(), description.getText(),
		    Double.parseDouble(probability.getText()));
		DatabaseFactory.getDatabaseSingleton().saveSetType(getEntity());

		SetNode newNode = getProjectionTree().createNewSetStructure(
		    getEntityNode(), set);
		getProjectionTree().refreshNodeSubStructure(getEntityNode());
		getProjectionTree().selectNode(newNode);
		setVisible(false);
	}
}
