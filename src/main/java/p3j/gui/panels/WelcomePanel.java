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
package p3j.gui.panels;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jamesii.SimSystem;

import p3j.misc.gui.GUI;

/**
 * Panel to welcome the user. Loads file under html/index.html .
 * 
 * Created: August 23, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class WelcomePanel extends JPanel implements HyperlinkListener {

	/** Serialization ID. */
	private static final long serialVersionUID = -216475639669435678L;

	/** Path to content file. */
	protected static final String CONTENT_FILE = "doc/index.html";

	/** The html pane. */
	private JEditorPane htmlPane;

	/**
	 * Default constructor.
	 */
	public WelcomePanel() {
		super();
		setLayout(new BorderLayout());
		htmlPane = new JEditorPane();
		htmlPane.setContentType("text/html");
		htmlPane.addHyperlinkListener(this);
		htmlPane.setEditable(false);
		String content = getContent();
		htmlPane.setText(content);
		this.add(new JScrollPane(htmlPane));
	}

	/**
	 * Reads content from the HTML file specified in
	 * {@link WelcomePanel#CONTENT_FILE}.
	 * 
	 * @return the content of the file
	 */
	private String getContent() {

		StringBuffer content = new StringBuffer();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(CONTENT_FILE));
			String line = reader.readLine();
			while (line != null) {
				content.append(line);
				line = reader.readLine();
			}
		} catch (Exception ex) {
			SimSystem.report(ex);
			content.append("An error occurred:\n");
			content.append(ex);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					SimSystem.report(ex);
				}
			}
		}

		return content.toString();
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				htmlPane.setPage(event.getURL());
			} catch (IOException ioe) {
				GUI.printErrorMessage(this, "Could not open link.", ioe.getMessage(),
				    ioe);
			}
		}
	}
}
