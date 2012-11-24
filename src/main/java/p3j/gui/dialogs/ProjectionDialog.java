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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;

/**
 * Super class of all dialogs with a certain size and some default buttons.
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public abstract class ProjectionDialog extends JDialog {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4217198597804149925L;

  /** The height of the dialog. */
  protected static final int DIALOG_HEIGHT = 150;

  /** The width of the dialog. */
  protected static final int DIALOG_WIDTH = 600;

  /** The OK button. */
  private final JButton okButton = new JButton("OK");
  {
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        okAction();
      }
    });
  }

  /** The cancel button. */
  private final JButton cancelButton = new JButton("Cancel");
  {
    getCancelButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
  }
  /** The buttons. */
  private final List<JButton> buttons = new ArrayList<JButton>();
  {
    getButtons().add(getOkButton());
    getButtons().add(getCancelButton());
  }

  /**
   * Override to add behavior for the OK button.
   */
  protected abstract void okAction();

  public List<JButton> getButtons() {
    return buttons;
  }

  public JButton getOkButton() {
    return okButton;
  }

  public JButton getCancelButton() {
    return cancelButton;
  }
}