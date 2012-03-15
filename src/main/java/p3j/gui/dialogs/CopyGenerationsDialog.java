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

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import p3j.misc.Misc;
import p3j.misc.gui.GUI;
import p3j.pppm.ProjectionModel;
import p3j.pppm.parameters.Parameter;
import p3j.pppm.parameters.ParameterAssignment;
import p3j.pppm.parameters.ParameterInstance;
import p3j.pppm.sets.Set;
import p3j.pppm.sets.SetType;

/**
 * Allows to copy structure from one generation to another.
 * 
 * Created on March 12, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class CopyGenerationsDialog extends JDialog {

	/** Serialization ID. */
	private static final long serialVersionUID = -7632325769179634802L;

	/** Width of the dialog. */
	public static final int DIALG_WIDTH = 600;

	/** Height of the dialog. */
	public static final int DIALG_HEIGHT = 600;

	/** Reference to current scenario. */
	private ProjectionModel projection;

	/** Text field to set source generation. */
	private JTextField fromGeneration = new JTextField(2);

	/** Text field to set target generation. */
	private JTextField toGeneration = new JTextField(2);

	/** Select Settype for operation. */
	private JComboBox selectSetType;

	/** Button to press if OK. */
	private final JButton okButton = new JButton("OK");
	{
		okButton.addActionListener(new CopyGenerations());
	}

	/**
	 * Default constructor.
	 * 
	 * @param proj
	 *          the current projection
	 * @param owner
	 *          the owner of this dialog
	 */
	public CopyGenerationsDialog(ProjectionModel proj, Window owner) {
		super(owner);
		this.projection = proj;
		setModal(true);
		setSize(DIALG_WIDTH, DIALG_HEIGHT);
		setTitle("Duplicate Generations");
		this.setContentPane(getContentPanel());
		GUI.centerOnScreen(this);
	}

	/**
	 * Constructs panel containing the user interface.
	 * 
	 * @return panel with UI
	 */
	protected final JPanel getContentPanel() {
		JPanel returnPanel = new JPanel();

		returnPanel.add(new JLabel("Duplicate all matrices from generation "));
		returnPanel.add(fromGeneration);
		returnPanel.add(new JLabel(" to generation "));
		returnPanel.add(toGeneration);
		returnPanel.add(new JLabel(" in Settype "));
		selectSetType = new JComboBox(projection.getUserDefinedTypes().toArray());
		returnPanel.add(selectSetType);
		returnPanel.add(okButton);
		return returnPanel;
	}

	/**
	 * Copies from one generation to the other.
	 */
	private class CopyGenerations implements ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			SetType setType = (SetType) selectSetType.getSelectedItem();

			int from = Misc.parseToInt(fromGeneration.getText());
			int to = Misc.parseToInt(toGeneration.getText());

			if (setType == null || from < 0 || to < 0) {
				return;
			}

			ArrayList<ParameterInstance> sources = new ArrayList<ParameterInstance>();
			ArrayList<ParameterInstance> targets = new ArrayList<ParameterInstance>();
			fillSourceAndTargetList(setType, from, to, sources, targets);

			Map<ParameterInstance, ParameterInstance> mapping = constructInstanceMapping(
			    sources, targets);
			copyParamAssignments(setType.getSets(), mapping);
			setVisible(false);
		}

		/**
		 * Fills source and target list.
		 * 
		 * @param setType
		 *          the Settype
		 * @param srcGen
		 *          the source generation
		 * @param targetGen
		 *          the target generation
		 * @param sources
		 *          the sources
		 * @param targets
		 *          the targets
		 */
		private void fillSourceAndTargetList(SetType setType, int srcGen,
		    int targetGen, List<ParameterInstance> sources,
		    List<ParameterInstance> targets) {
			List<ParameterInstance> parameterInstances = setType
			    .getDefinedParameters();
			for (int i = 0; i < parameterInstances.size(); i++) {
				if (!parameterInstances.get(i).getParameter().isGenerationDependent()) {
					continue;
				}
				if (parameterInstances.get(i).getGeneration() == srcGen) {
					sources.add(parameterInstances.get(i));
				}
				if (parameterInstances.get(i).getGeneration() == targetGen) {
					targets.add(parameterInstances.get(i));
				}
			}
		}

		/**
		 * Construct instance mapping. Go through source instances and finds out
		 * which one has a matching target.
		 * 
		 * @param sources
		 *          the source instances
		 * @param targets
		 *          the target instances
		 * 
		 * @return the map source parameter instance => target parameter instance
		 */
		private Map<ParameterInstance, ParameterInstance> constructInstanceMapping(
		    List<ParameterInstance> sources, List<ParameterInstance> targets) {
			Map<ParameterInstance, ParameterInstance> mapping = new HashMap<ParameterInstance, ParameterInstance>();
			for (int i = 0; i < sources.size(); i++) {
				ParameterInstance source = sources.get(i);
				Parameter param = source.getParameter();
				for (int j = 0; j < targets.size(); j++) {
					if (targets.get(j).getParameter().equals(param)) {
						mapping.put(source, targets.get(j));
						break;
					}
				}
			}
			return mapping;
		}

		/**
		 * Copies the parameter assignments.
		 * 
		 * @param sets
		 *          the list of new sets
		 * @param mapping
		 *          the mapping from each parameter instance to its copy
		 */
		private void copyParamAssignments(List<Set> sets,
		    Map<ParameterInstance, ParameterInstance> mapping) {
			java.util.Set<Entry<ParameterInstance, ParameterInstance>> mappingSet = mapping
			    .entrySet();

			for (Entry<ParameterInstance, ParameterInstance> mappingEntry : mappingSet) {
				ParameterInstance source = mappingEntry.getKey();
				ParameterInstance target = mappingEntry.getValue();

				for (int i = 0; i < sets.size(); i++) {
					Set set = sets.get(i);

					List<ParameterAssignment> assignments = new ArrayList<ParameterAssignment>(
					    set.getParameterAssignments(source).getAssignments());

					for (int j = 0; j < assignments.size(); j++) {
						ParameterAssignment srcAssign = assignments.get(i);
						ParameterAssignment cpyAssign = srcAssign.getCopy();
						cpyAssign.setParamInstance(target);
						set.addParameterAssignment(cpyAssign);
					}
				}
			}
		}
	}
}
