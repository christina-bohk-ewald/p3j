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

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.builder.ButtonBarBuilder2;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * Panel to conveniently display the properties of a PPPM entity.
 * 
 * Created: August 25, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class PropertiesShowPanelFactory {

  /** The column at which the preview panel is inserted. */
  private static final int PREVIEW_COLUMN_INDEX = 1;

  /** The height of the preview panel (in rows). */
  private static final int PREVIEW_ROW_HEIGHT = 1;

  /** The width of the preview panel (in columns). */
  private static final int PREVIEW_COLUMN_WIDTH = 5;

  /** The height of the button bar (in rows). */
  private static final int BUTTON_BAR_ROW_HEIGHT = 1;

  /** The width of the button bar (in columns). */
  private static final int BUTTON_BAR_COLUMN_WIDTH = 5;

  /** The column at which the button bar is inserted. */
  private static final int BUTTON_BAR_COLUMN_INDEX = 1;

  /** The default key column width. */
  private static final int KEY_WIDTH = 100;

  /** The default value column width. */
  private static final int VALUE_WIDTH = 250;

  /** The cell constraints that are used. */
  private final CellConstraints cc = new CellConstraints();

  /** The default form builder (for general tasks). */
  private final DefaultFormBuilder builder;

  /** The builder for the button list. */
  private final ButtonBarBuilder2 bbBuilder;

  /** Flag to determine whether this panel has any buttons (= options). */
  private boolean hasButtons;

  /** Component containing preview. */
  private JComponent preview;

  /**
   * Instantiates a new properties show panel factory.
   * 
   * @param keyWidth
   *          the width of the key column
   */
  public PropertiesShowPanelFactory(int keyWidth) {
    this(keyWidth, VALUE_WIDTH);
  }

  /**
   * Instantiates a new properties show panel factory.
   * 
   * @param keyWidth
   *          the width of the key column
   * @param valueWidth
   *          the width of the value column
   */
  public PropertiesShowPanelFactory(int keyWidth, int valueWidth) {
    FormLayout layout = new FormLayout("right:" + keyWidth + "dlu, 6dlu, f:"
        + valueWidth + "dlu, 4dlu, right:default:grow, 40dlu", "15dlu");
    builder = new DefaultFormBuilder(layout);
    builder.setDefaultDialogBorder();
    bbBuilder = new ButtonBarBuilder2();
    bbBuilder.setLeftToRightButtonOrder(true);
    init(null, 0);
  }

  /**
   * Simple constructor. No buttons are added.
   */
  public PropertiesShowPanelFactory() {
    this(KEY_WIDTH, VALUE_WIDTH);
  }

  /**
   * Constructor with only one kind of buttons.
   * 
   * @param buttons
   *          list of buttons
   */
  public PropertiesShowPanelFactory(List<JButton> buttons) {
    this(KEY_WIDTH);
    init(buttons, 0);
  }

  /**
   * Constructor for a single button.
   * 
   * @param button
   *          the button
   */
  public PropertiesShowPanelFactory(JButton button) {
    this();
    List<JButton> buttons = new ArrayList<JButton>();
    buttons.add(button);
    init(buttons, 0);
  }

  /**
   * Default constructor.
   * 
   * @param keyWidth
   *          the width of the column containing the keys
   * @param buttons
   *          the list of buttons to be displayed
   * @param numOfGeneralButtons
   *          of general buttons (will be displayed separately), has to be in
   *          [0, size of buttons list]
   */
  public PropertiesShowPanelFactory(int keyWidth, List<JButton> buttons,
      int numOfGeneralButtons) {
    this(keyWidth);
    init(buttons, numOfGeneralButtons);
  }

  /**
   * Comprehensive constructor.
   * 
   * @param keyWidth
   *          the width of the column containing the keys
   * @param valueWidth
   *          the width of the column containing the values
   * @param buttons
   *          the list of buttons to be displayed
   * @param numOfGeneralButtons
   *          of general buttons (will be displayed separately), has to be in
   *          [0, size of buttons list]
   */
  public PropertiesShowPanelFactory(int keyWidth, int valueWidth,
      List<JButton> buttons, int numOfGeneralButtons) {
    this(keyWidth, valueWidth);
    init(buttons, numOfGeneralButtons);
  }

  /**
   * Default constructor.
   * 
   * @param buttons
   *          the list of buttons to be displayed
   * @param numOfGeneralButtons
   *          of general buttons (will be displayed separately), has to be in
   *          [0, size of buttons list]
   */
  public PropertiesShowPanelFactory(List<JButton> buttons,
      int numOfGeneralButtons) {
    this();
    init(buttons, numOfGeneralButtons);
  }

  /**
   * Initializes factory.
   * 
   * @param buttons
   *          the buttons to be added
   * @param numOfGeneralButtons
   *          the number of general buttons (to be separated from the rest)
   */
  private void init(List<JButton> buttons, int numOfGeneralButtons) {
    if (buttons == null) {
      return;
    }
    for (int i = 0; i < buttons.size(); i++) {
      JButton button = buttons.get(i);
      if (i == buttons.size() - numOfGeneralButtons) {
        bbBuilder.addUnrelatedGap();
      } else {
        bbBuilder.addRelatedGap();
      }
      bbBuilder.addButton(button);
    }
    if (buttons.size() > 0) {
      bbBuilder.getLayout().setColumnSpec(1, ColumnSpec.decode("right:d:grow"));
      hasButtons = true;
    }
  }

  /**
   * Wrapper for adding a separator.
   * 
   * @param separator
   *          name of the separator
   */
  public void sep(String separator) {
    builder.addSeparator(separator);
    builder.nextLine();
  }

  /**
   * Add label and component.
   * 
   * @param label
   *          the label to be added
   * @param component
   *          the associated (input) component
   */
  public void app(String label, Component component) {
    builder.append(label, component);
    builder.nextLine();
  }

  /**
   * Add label and information as a string.
   * 
   * @param label
   *          the label
   * @param object
   *          the associated information, {@link Object#toString()} will be used
   */
  public void app(String label, Object object) {
    builder.append(label, new JLabel(object.toString()));
    builder.nextLine();
  }

  /**
   * Add label and component that spans multiple rows. Default row height is 40
   * dlu.
   * 
   * @param label
   *          the label
   * @param component
   *          the associated component
   * @param rowSpan
   *          the number of rows to be spanned (>=1)
   */
  public void app(String label, Component component, int rowSpan) {
    app(label, component, rowSpan, "40dlu");
  }

  /**
   * Add label and component that spans multiple rows.
   * 
   * @param label
   *          the label
   * @param component
   *          the associated component
   * @param rowSpan
   *          the number of rows to be spanned (>=1)
   * @param rowHeight
   *          the row height
   */
  public void app(String label, Component component, int rowSpan,
      String rowHeight) {
    builder.append(label);
    for (int i = 0; i < rowSpan - 1; i++) {
      builder.appendRow(RowSpec.decode(rowHeight));
    }
    builder.add(new JScrollPane(component),
        cc.xywh(builder.getColumn(), builder.getRow(), 1, rowSpan));
    builder.nextLine(rowSpan);
  }

  /**
   * Constructs overall panel.
   * 
   * @return the desired panel
   */
  public JPanel constructPanel() {
    builder.appendSeparator(hasButtons ? "Options" : "");
    builder.nextLine();
    builder.appendRow(RowSpec.decode("top:20dlu"));
    builder.add(bbBuilder.getPanel(), cc.xywh(BUTTON_BAR_COLUMN_INDEX,
        builder.getRow(), BUTTON_BAR_COLUMN_WIDTH, BUTTON_BAR_ROW_HEIGHT));

    if (preview != null) {
      builder.nextLine();
      builder.appendRow(RowSpec.decode("10dlu"));
      sep("Summary:");
      builder.appendRow(RowSpec.decode("top:d:grow"));
      builder.add(preview, cc.xywh(PREVIEW_COLUMN_INDEX, builder.getRow(),
          PREVIEW_COLUMN_WIDTH, PREVIEW_ROW_HEIGHT));
    }
    return builder.getPanel();
  }

  /**
   * Add preview to properties panel.
   * 
   * @param component
   *          the component containing the preview
   */
  public void appPreview(JComponent component) {
    preview = component;
  }

}
