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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import p3j.gui.panels.projections.IProjectionTree;
import p3j.gui.panels.projections.ProjectionTreeNode;
import p3j.misc.gui.GUI;

import com.jgoodies.forms.builder.ButtonBarBuilder2;

/**
 * 
 * Super class for all dialogs that need to modify an {@link IProjectionTree}.
 * 
 * Created: September 7, 2008
 * 
 * @param <E>
 *          type of the entity
 * @param <N>
 *          type of the node representing the entity
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public abstract class AbstractProjectionTreeDialog<E, N extends ProjectionTreeNode<E>>
    extends JDialog {

	/** Serialization ID. */
	private static final long serialVersionUID = -1735341315107050803L;

	/** The entity to be edited. */
	private final E entity;

	/** The node belonging to the edited entity. */
	private final N entityNode;

	/** The projection tree (needs to be notified). */
	private IProjectionTree projectionTree;

	/** Cancel Settype creation. */
	private final JButton cancelButton = new JButton("Cancel");
	{
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
	}

	/** Confirm Settype creation. */
	private final JButton okButton = new JButton("OK");
	{
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
	}

	/**
	 * Default constructor.
	 * 
	 * @param projNode
	 *          the node representing the {@link p3j.pppm.ProjectionModel} which
	 *          shall be modified
	 * @param projTree
	 *          the projection tree
	 * @param width
	 *          the width of the dialog
	 * @param height
	 *          the height of the dialog
	 * @param okButtonText
	 *          the text to be displayed on the OK button
	 */
	public AbstractProjectionTreeDialog(N projNode, IProjectionTree projTree,
	    int width, int height, String okButtonText) {
		entity = projNode.getEntity();
		entityNode = projNode;
		setProjectionTree(projTree);
		okButton.setText(okButtonText);
		setSize(width, height);
		GUI.centerOnScreen(this);
	}

	/**
	 * Creates a panel containing the cancel and the OK button.
	 * 
	 * @return a panel containing the cancel and the OK button
	 */
	protected JPanel getButtonPanel() {
		ButtonBarBuilder2 bbBuilder = new ButtonBarBuilder2();
		bbBuilder.addButton(okButton);
		bbBuilder.addRelatedGap();
		bbBuilder.addButton(cancelButton);
		return bbBuilder.getPanel();
	}

	/**
	 * Will be called when user pressed OK button.
	 */
	protected abstract void ok();

	/**
	 * Will be called when user cancels dialog.
	 */
	protected abstract void cancel();

	public N getEntityNode() {
		return entityNode;
	}

	public IProjectionTree getProjectionTree() {
		return projectionTree;
	}

	public final void setProjectionTree(IProjectionTree projectionTree) {
		this.projectionTree = projectionTree;
	}

	public E getEntity() {
		return entity;
	}
}
