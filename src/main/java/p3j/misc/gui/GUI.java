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
package p3j.misc.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.List;
import java.util.logging.Level;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.jamesii.SimSystem;
import org.jamesii.gui.utils.BasicUtilities;

import p3j.gui.P3J;

import com.jgoodies.forms.layout.CellConstraints;

/**
 * Class to store static GUI helper functions.
 * 
 * Created on 4 February 4, 2007
 * 
 * @author Roland Ewald
 */
public final class GUI {

  /** Number of pixels in the gaps of the standard border layout. */
  public static final int STD_LAYOUT_GAP = 5;

  /** The font-size of the wait-message. */
  private static final int FONT_SIZE_WAIT_MSG = 25;

  /** The normal font-size. */
  private static final int FONT_SIZE_MEDIUM = 15;

  /**
   * The number of rows used for a single 'content' row in the layout (for
   * stand-alone dialogs).
   */
  public static final int ROW_SKIP_LAYOUT = 2;

  /**
   * The index of the column at which the keys are added to the content panel
   * (for stand-alone dialogs).
   */
  public static final int KEYS_COLUMN_INDEX = 2;

  /**
   * The index of the column at which the input elements (textfields etc.) are
   * added to the content panel (for stand-alone dialogs).
   */
  public static final int INPUT_COLUMN_INDEX = 4;

  /**
   * Flag to determine whether we are in head-less mode (no GUI, therefore no
   * need to display error messages in dialogs). This is particularly convenient
   * for GUI tests.
   */
  private static boolean headless = false;

  /**
   * This class should not be instantiated.
   */
  private GUI() {
  }

  /**
   * Get standard border layout. With 5 pixels gaps vertically/horizontally.
   * 
   * @return standard border layout
   */
  public static BorderLayout getStdBorderLayout() {
    return new BorderLayout(STD_LAYOUT_GAP, STD_LAYOUT_GAP);
  }

  /**
   * Centre a given window on the screen.
   * 
   * @param window
   *          a Window
   */
  public static void centerOnScreen(Window window) {
    int locationX = (int) Math.round((Toolkit.getDefaultToolkit()
        .getScreenSize().getWidth() - window.getWidth()) / 2);
    int locationY = (int) Math.round((Toolkit.getDefaultToolkit()
        .getScreenSize().getHeight() - window.getHeight()) / 2);
    window.setLocation(locationX, locationY);
  }

  /**
   * Sets the head-less mode.
   * 
   * @param headless
   *          the new head-less mode
   */
  public static void setHeadless(boolean headless) {
    GUI.headless = headless;
  }

  /**
   * Checks the headless mode.
   * 
   * @return true if currently running head-less
   */
  public static boolean isHeadless() {
    return GUI.headless;
  }

  /**
   * Prints an error message.
   * 
   * @param parent
   *          the parent component for the dialog
   * @param title
   *          the title of the error dialog
   * @param message
   *          the message to be displayed
   */
  public static void printErrorMessage(Component parent, String title,
      Object message) {
    if (isHeadless()) {
      return;
    }
    JOptionPane.showMessageDialog(parent, message, title,
        JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Prints an error message and the stack trace of the corresponding exception.
   * 
   * @param parent
   *          the parent component for the dialog
   * @param title
   *          the title of the error dialog
   * @param message
   *          the message to be displayed
   * @param throwable
   *          the throwable (may provide additional info)
   */
  public static void printErrorMessage(Component parent, String title,
      Object message, Throwable throwable) {
    SimSystem.report(Level.SEVERE, title, throwable);
    throwable.printStackTrace();
    printErrorMessage(parent, title, message);
  }

  /**
   * Prints an error message with the P3J main window as parent instance. Uses
   * {@link P3J#getInstance()} to retrieve it.
   * 
   * @param title
   *          the title of the error dialog
   * @param throwable
   *          the throwable (may provide additional info)
   */
  public static void printErrorMessage(String title, Throwable throwable) {
    SimSystem.report(Level.SEVERE, title, throwable);
    printErrorMessage(P3J.getInstance(), title, throwable.getMessage(),
        throwable);
  }

  /**
   * Creates a button with an icon.
   * 
   * @param iconFileName
   *          the icon file name
   * @param defaultText
   *          the default text to be displayed when the icon cannot be loaded
   * @return the button
   */
  public static JButton createIconButton(String iconFileName, String defaultText) {
    return decorateButtonWithIconOrText(new JButton(),
        retrieveIcon(iconFileName), defaultText);
  }

  /**
   * Creates a radio button with an icon.
   * 
   * @param iconFileName
   *          the icon file name
   * @param defaultText
   *          the default text to be displayed when the icon cannot be loaded
   * @return the button
   */
  public static JRadioButton createIconRadioButton(String iconFileName,
      String defaultText) {
    return decorateButtonWithIconOrText(new JRadioButton(),
        retrieveIcon(iconFileName), defaultText);
  }

  /**
   * Decorate button with icon or text.
   * 
   * @param <B>
   *          the generic type of the button
   * @param button
   *          the button
   * @param icon
   *          the icon
   * @param defaultText
   *          the default text
   * @return the decorated button
   */
  public static <B extends AbstractButton> B decorateButtonWithIconOrText(
      B button, ImageIcon icon, String defaultText) {
    if (icon.getImage() != null) {
      button.setIcon(icon);
    } else {
      button.setText(defaultText);
    }
    return button;
  }

  /**
   * Retrieves icon.
   * 
   * @param iconFileName
   *          the icon file name
   * @return the image icon
   */
  public static ImageIcon retrieveIcon(String iconFileName) {
    return new ImageIcon(GUI.class.getResource("/p3j/icons/" + iconFileName));
  }

  /**
   * Prints a message.
   * 
   * @param parent
   *          the parent component for the dialog
   * @param title
   *          the title of the error dialog
   * @param message
   *          the message to be displayed
   */
  public static void printMessage(Component parent, String title, Object message) {
    if (isHeadless()) {
      return;
    }
    JOptionPane.showMessageDialog(parent, message, title,
        JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Prints a question message.
   * 
   * @param parent
   *          the parent component for the dialog
   * @param title
   *          the title of the dialog
   * @param message
   *          the question
   * 
   * @return true, if user chose yes, otherwise false
   */
  public static boolean printQuestion(Component parent, String title,
      Object message) {

    if (isHeadless()) {
      throw new UnsupportedOperationException(
          "Cannot print question while in head-less mode.");
    }

    int decision = JOptionPane.showConfirmDialog(parent, message, title,
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

    if (decision == JOptionPane.YES_OPTION) {
      return true;
    }

    return false;
  }

  /**
   * Replaces the contents o a default list model.
   * 
   * @param model
   *          the list model to be filled
   * @param newContent
   *          the list containing the content
   */
  public static void replaceListContents(DefaultListModel<Object> model,
      List<?> newContent) {
    model.clear();
    if (newContent != null) {
      for (Object element : newContent) {
        model.addElement(element);
      }
    }
  }

  /**
   * Shows modal dialog without blocking (by setting visible to true via a
   * separate runnable in the EDT).
   * 
   * @param dialogToShow
   *          the dialog to show
   */
  public static void showModalDialog(final JDialog dialogToShow) {
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        dialogToShow.setVisible(true);
        BasicUtilities.repaintOnEDT(dialogToShow);
      }
    });
  }

  /**
   * Gets a file chooser configured to select a directory.
   * 
   * @param dialogTitle
   *          the dialog title
   * 
   * @return the directory chooser
   */
  public static JFileChooser getDirectoryChooser(String dialogTitle) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setDialogTitle(dialogTitle);
    fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    return fileChooser;
  }

  /**
   * Gets the label to wait.
   * 
   * @return the label to wait
   */
  public static JLabel getLabelToWait() {
    JLabel msg = new JLabel("This may take a while. Please be patient...");
    msg.setFont(getDefaultFontLarge());
    return msg;
  }

  /**
   * Gets the large default font.
   * 
   * @return the large default font
   */
  public static Font getDefaultFontLarge() {
    return new Font("Sans", Font.BOLD, FONT_SIZE_WAIT_MSG);
  }

  /**
   * Gets the default bold font.
   * 
   * @return the default bold font
   */
  public static Font getDefaultFontBold() {
    return new Font("Sans", Font.BOLD, FONT_SIZE_MEDIUM);
  }

  /**
   * Adds a pair of key and input components to the given panel panel.
   * 
   * @param panel
   *          the panel
   * @param key
   *          the name of the label to be used
   * @param input
   *          the input component
   * @param currentRow
   *          the current row in the layout
   * @return the new row in the layout
   */
  public static int addRowToPanel(JPanel panel, String key, JComponent input,
      int currentRow) {
    CellConstraints c = new CellConstraints();
    panel.add(new JLabel(key), c.xy(KEYS_COLUMN_INDEX, currentRow));
    panel.add(input, c.xy(INPUT_COLUMN_INDEX, currentRow));
    return currentRow + ROW_SKIP_LAYOUT;
  }
}
