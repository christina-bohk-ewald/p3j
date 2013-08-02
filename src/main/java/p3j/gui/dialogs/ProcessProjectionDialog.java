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

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jamesii.gui.utils.BasicUtilities;

import p3j.misc.gui.GUI;
import p3j.pppm.ProjectionModel;

/**
 * Super class for dialogs letting the user process the {@link ProjectionModel}
 * in some way.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public abstract class ProcessProjectionDialog extends ProjectionDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6055434273943848669L;

	/** The projection model of which the age shall be adjusted. */
	private final ProjectionModel projectionModel;

	/**
	 * Instantiates a new process projection dialog.
	 * 
	 * @param projection
	 *          the projection
	 */
	public ProcessProjectionDialog(ProjectionModel projection) {
		projectionModel = projection;
	}

	/**
	 * Reinitializes user interface for age class adjustment.
	 * 
	 * @return the reference to the status message label
	 */
	protected JLabel reinitializeUIForAdjustment() {
		getContentPane().removeAll();
		JPanel content = new JPanel(new BorderLayout());
		content.add(GUI.getLabelToWait(), BorderLayout.CENTER);
		final JLabel status = new JLabel("Initializing...");
		content.add(status, BorderLayout.SOUTH);
		getContentPane().add(content);
		BasicUtilities.repaintOnEDT(this);
		return status;
	}

	public ProjectionModel getProjectionModel() {
	  return projectionModel;
  }

}