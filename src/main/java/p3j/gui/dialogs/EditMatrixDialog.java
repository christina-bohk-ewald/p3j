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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import p3j.gui.panels.matrices.EditMatrixPanel;
import p3j.misc.gui.GUI;
import p3j.pppm.parameters.ParameterAssignment;

import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.layout.ColumnSpec;

/**
 * Dialog for editing a matrix.
 * 
 * Created on January 07, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class EditMatrixDialog extends JDialog {

	/** Serialization ID. */
	private static final long serialVersionUID = 3054321149647892680L;

	/** Width of the dialog. */
	public static final int DIALOG_WIDTH = 600;

	/** Height of the dialog. */
	public static final int DIALOG_HEIGHT = 480;

	/** Central panel. */
	private EditMatrixPanel contentPanel;

	/**
	 * Default constructor.
	 * 
	 * @param owner
	 *          the owner of this dialog
	 * @param pAssign
	 *          the matrix to be edited
	 */
	public EditMatrixDialog(Frame owner, ParameterAssignment pAssign) {
		super(owner, "Edit Matrix:" + pAssign.getName(), true);
		setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		GUI.centerOnScreen(this);
		contentPanel = new EditMatrixPanel(pAssign);
		initialize();
	}

	/**
	 * This method initializes this dialog.
	 */
	private void initialize() {

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		JPanel panel = new JPanel(GUI.getStdBorderLayout());
		panel.add(contentPanel, BorderLayout.CENTER);

		ButtonBarBuilder2 bBuilder = new ButtonBarBuilder2();
		bBuilder.addButton(okButton);
		bBuilder.getLayout().setColumnSpec(1, ColumnSpec.decode("right:pref:grow"));
		panel.add(bBuilder.getPanel(), BorderLayout.SOUTH);
		this.setContentPane(panel);
	}
}
