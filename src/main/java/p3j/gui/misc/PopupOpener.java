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
package p3j.gui.misc;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

/**
 * Mouse Adapted that opens a pop-up menu.
 * 
 * Created on March 4, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
public class PopupOpener extends MouseAdapter {

  /**
   * Reference to pop-up menu.
   */
  private final JPopupMenu popupMenu;

  /**
   * Default constructor.
   * 
   * @param puMenu
   *          the pop-up menu to be opened
   */
  public PopupOpener(JPopupMenu puMenu) {
    this.popupMenu = puMenu;
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (e.isPopupTrigger()) {
      popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }
  }
}
