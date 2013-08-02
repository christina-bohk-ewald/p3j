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
package p3j.gui.panels.matrices;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import net.sf.jeppers.grid.JGrid;
import net.sf.jeppers.grid.SelectionModel;

import org.jamesii.SimSystem;

/**
 * An adapter to integrate Copy/Paste-Behaviour to {@link JGrid}. Implements
 * 'fast' navigation and selection by pressing CTRL + Arrow or CTRL + SHIFT +
 * Arrow. Implements clipboard operations to deliver a user-friendly UI.
 * 
 * Inspired by code from Nils O. Sel(ao)sdal (see
 * http://groups.google.com/group/comp
 * .lang.java.gui/browse_frm/thread/2289d2f55aaed5ad
 * /3665b8ac63e4656a?tvc=1&q=copy+paste+excel+nach+jtable#3665b8ac63e4656a)
 * 
 * Created on January 10, 2007
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 */
public class GridBehaviourAdapter implements ActionListener {

	/** The action command for 'right'. */
	public static final String ACTION_COMMAND_RIGHT = "Right";

	/** The action command for 'left' */
	public static final String ACTION_COMMAND_LEFT = "Left";

	/** The action command for 'up'. */
	public static final String ACTION_COMMAND_UP = "Up";

	/** The action command for 'down'. */
	public static final String ACTION_COMMAND_DOWN = "Down";

	/** The prefix for 'fast' action commands. */
	public static final String FAST_PREFIX = "fast";

	/** The prefix for 'fast' action commands on the selection. */
	public static final String FAST_SELECTION_CMD_PREFIX = "fastSel";

	/** Reference to grid. */
	private JGrid grid;

	/** Selection model of JGrid. */
	private SelectionModel selModel;

	/** Reference to system clipboard. */
	private Clipboard clipBoard;

	// Constants to indicate the directions

	/** Upwards. */
	public static final int DIR_UP = 0;

	/** Right direction. */
	public static final int DIR_RIGHT = 1;

	/** Downwards. */
	public static final int DIR_DOWN = 2;

	/** Left direction. */
	public static final int DIR_LEFT = 3;

	/**
	 * Returns {@link KeyStroke} for copying to clipboard (CTRL + C).
	 * 
	 * @return key stroke for copy
	 */
	public static KeyStroke getCopyKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
	}

	/**
	 * Returns {@link KeyStroke} for cutting to clipboard (CTRL + X).
	 * 
	 * @return key stroke for cut
	 */
	public static KeyStroke getCutKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK, false);
	}

	/**
	 * Returns {@link KeyStroke} for pasting from clipboard (CTRL + V).
	 * 
	 * @return key stroke for paste
	 */
	public static KeyStroke getPasteKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
	}

	/**
	 * Default constructor.
	 * 
	 * @param target
	 *          the grid of which the behaviour shall be controlled
	 */
	public GridBehaviourAdapter(JGrid target) {

		this.grid = target;
		this.selModel = target.getSelectionModel();
		clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard();

		KeyStroke cut = getCutKeyStroke();
		KeyStroke copy = getCopyKeyStroke();
		KeyStroke paste = getPasteKeyStroke();

		KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);

		KeyStroke fastUp = KeyStroke.getKeyStroke(KeyEvent.VK_UP,
		    ActionEvent.CTRL_MASK, false);
		KeyStroke fastDown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
		    ActionEvent.CTRL_MASK, false);
		KeyStroke fastLeft = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
		    ActionEvent.CTRL_MASK, false);
		KeyStroke fastRight = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
		    ActionEvent.CTRL_MASK, false);

		KeyStroke fastSelUp = KeyStroke.getKeyStroke(KeyEvent.VK_UP,
		    ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK, false);
		KeyStroke fastSelDown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
		    ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK, false);
		KeyStroke fastSelLeft = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
		    ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK, false);
		KeyStroke fastSelRight = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
		    ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK, false);

		target.registerKeyboardAction(this, "Cut", cut, JComponent.WHEN_FOCUSED);
		target.registerKeyboardAction(this, "Copy", copy, JComponent.WHEN_FOCUSED);
		target
		    .registerKeyboardAction(this, "Paste", paste, JComponent.WHEN_FOCUSED);
		target.registerKeyboardAction(this, "Delete", delete,
		    JComponent.WHEN_FOCUSED);
		target.registerKeyboardAction(this, FAST_PREFIX + ACTION_COMMAND_UP,
		    fastUp, JComponent.WHEN_FOCUSED);
		target.registerKeyboardAction(this, FAST_PREFIX + ACTION_COMMAND_DOWN,
		    fastDown, JComponent.WHEN_FOCUSED);
		target.registerKeyboardAction(this, FAST_PREFIX + ACTION_COMMAND_LEFT,
		    fastLeft, JComponent.WHEN_FOCUSED);
		target.registerKeyboardAction(this, FAST_PREFIX + ACTION_COMMAND_RIGHT,
		    fastRight, JComponent.WHEN_FOCUSED);
		target.registerKeyboardAction(this, FAST_SELECTION_CMD_PREFIX
		    + ACTION_COMMAND_UP, fastSelUp, JComponent.WHEN_FOCUSED);
		target.registerKeyboardAction(this, FAST_SELECTION_CMD_PREFIX
		    + ACTION_COMMAND_DOWN, fastSelDown, JComponent.WHEN_FOCUSED);
		target.registerKeyboardAction(this, FAST_SELECTION_CMD_PREFIX
		    + ACTION_COMMAND_LEFT, fastSelLeft, JComponent.WHEN_FOCUSED);
		target.registerKeyboardAction(this, FAST_SELECTION_CMD_PREFIX
		    + ACTION_COMMAND_RIGHT, fastSelRight, JComponent.WHEN_FOCUSED);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("Cut")) {
			copyToClipBoard(true);
		} else if (actionCommand.equals("Copy")) {
			copyToClipBoard(false);
		} else if (actionCommand.equals("Paste")) {
			pasteFromClipboard();
		} else if (actionCommand.equals("Delete")) {
			deleteSelection();
		} else if (actionCommand.indexOf(FAST_SELECTION_CMD_PREFIX) == 0) {
			moveFast(Direction.getDirection(actionCommand
			    .substring(FAST_SELECTION_CMD_PREFIX.length())), true);
		} else if (actionCommand.indexOf(FAST_PREFIX) == 0) {
			moveFast(
			    Direction.getDirection(actionCommand.substring(FAST_PREFIX.length())),
			    false);
		}
	}

	/**
	 * Copies or cuts selected values to clipboard.
	 * 
	 * @param cut
	 *          if true, values will be cut out
	 */
	protected void copyToClipBoard(boolean cut) {
		StringBuffer buffer = new StringBuffer();
		int fromX = selModel.getFirstSelectedRow();
		int fromY = selModel.getFirstSelectedColumn();
		int toX = selModel.getLastSelectedRow();
		int toY = selModel.getLastSelectedColumn();

		for (int i = fromX; i <= toX; i++) {
			for (int j = fromY; j < toY; j++) {
				buffer.append(grid.getValueAt(i, j));
				buffer.append('\t');
				if (cut) {
					grid.setValueAt("", i, j);
				}
			}
			buffer.append(grid.getValueAt(i, toY));
			buffer.append('\n');
			if (cut) {
				grid.setValueAt("", i, toY);
			}
		}

		StringSelection strSelection = new StringSelection(buffer.toString());
		clipBoard.setContents(strSelection, strSelection);

		if (cut) {
			grid.repaint();
		}
	}

	/**
	 * Pastes from clipboard.
	 */
	protected void pasteFromClipboard() {

		String insertString = "";

		try {
			insertString = (String) (clipBoard.getContents(this)
			    .getTransferData(DataFlavor.stringFlavor));
		} catch (Exception ex) {
			SimSystem
			    .report(Level.WARNING, "Could not load data from clipboard.", ex);
		}

		if (insertString.length() == 0) {
			return;
		}

		String rowString;
		StringTokenizer tokenizer = new StringTokenizer(insertString, "\n");

		int startRow = selModel.getFirstSelectedRow();
		int startCol = selModel.getFirstSelectedColumn();
		int rowCount = grid.getRowCount();
		int colCount = grid.getColumnCount();

		for (int i = 0; tokenizer.hasMoreTokens(); i++) {

			rowString = tokenizer.nextToken();
			String[] values = rowString.split("\t");

			if (startRow + i >= rowCount) {
				break;
			}

			for (int j = 0; j < values.length; j++) {
				if (startCol + j >= colCount) {
					continue;
				}
				grid.setValueAt(values[j], startRow + i, startCol + j);
			}
		}

		grid.repaint();
	}

	/**
	 * Deletes selected values.
	 */
	private void deleteSelection() {
		SelectionModel sModel = grid.getSelectionModel();
		for (int i = sModel.getFirstSelectedRow(); i <= sModel.getLastSelectedRow(); i++) {
			for (int j = sModel.getFirstSelectedColumn(); j <= sModel
			    .getLastSelectedColumn(); j++) {
				grid.setValueAt("", i, j);
			}
		}
	}

	/**
	 * Emulates positioning when CTRIL is pressed. Used for fast selection and
	 * navigation in the {@link JGrid}. This methods walks in the given
	 * {@link Direction} until it encounters an empty field or the end of the
	 * matrix.
	 * 
	 * @param direction
	 *          the direction in which to go
	 * @param select
	 *          flag to determine if passed elements shall be selected (i.e., is
	 *          SHIFT also pressed?)
	 */
	protected void moveFast(Direction direction, boolean select) {

		if (direction == Direction.UNKNOWN) {
			return;
		}

		int fromX = selModel.getFirstSelectedRow();
		int toX = selModel.getLastSelectedRow();
		int fromY = selModel.getFirstSelectedColumn();
		int toY = selModel.getLastSelectedColumn();

		int rowCount = grid.getRowCount();
		int colCount = grid.getColumnCount();

		int currentX = fromX;
		int currentY = fromY;

		String value = getValue(direction.modifiesRows(), direction.getOffset(),
		    currentX, currentY, rowCount, colCount).toString();

		// Step into direction until empty grid element is reached
		while (!value.equals("")) {
			if (direction.modifiesRows()) {
				currentX += direction.getOffset();
			} else {
				currentY += direction.getOffset();
			}
			value = getValue(direction.modifiesRows(), direction.getOffset(),
			    currentX, currentY, rowCount, colCount).toString();
		}

		if (select) {
			selModel.setSelectionRange(Math.min(fromX, currentX),
			    Math.min(fromY, currentY), Math.max(toX, currentX),
			    Math.max(toY, currentY));
		} else {
			selModel.setSelectionRange(currentX, currentY, currentX, currentY);
		}

		grid.repaint();
	}

	/**
	 * Gets a value from the {@link JGrid} component. Checks bounds etc.
	 * 
	 * @param modifyX
	 *          flag to determine whether X is modified (true) or Y is modified
	 *          (false)
	 * @param offSet
	 *          either -1 or 1, depending on the direction and whether the
	 *          column/row index has to be incremented or decremented to go in
	 *          this direction, see {@link Direction#getOffset()}
	 * @param currentRow
	 *          current row number
	 * @param currentCol
	 *          current column number
	 * @param rowCount
	 *          number of rows
	 * @param colCount
	 *          number of columns
	 * 
	 * @return String representation of next value in path
	 */
	protected String getValue(boolean modifyX, int offSet, int currentRow,
	    int currentCol, int rowCount, int colCount) {
		boolean rowOutOfBounds = (currentRow + offSet < 0 || currentRow + offSet >= rowCount);
		boolean colOutOfBounds = (currentCol + offSet < 0 || currentCol + offSet >= colCount);
		if ((modifyX && rowOutOfBounds) || colOutOfBounds) {
			return "";
		}

		Object o = modifyX ? grid.getValueAt(currentRow + offSet, currentCol)
		    : grid.getValueAt(currentRow, currentCol + offSet);

		if (o == null) {
			return "";
		}
		return o.toString();
	}
}

/**
 * Auxiliary enumeration to define the direction of changing the element focus.
 * Also provides additional information, namely if row or column index is
 * modified by this direction, and if the modification is positive or negative.
 * 
 * Created: August 26, 2008
 * 
 * @author Christina Bohk
 * @author Roland Ewald
 * 
 */
enum Direction {

	/**
	 * Upwards.
	 */
	UP,

	/**
	 * Downwards.
	 */
	DOWN,

	/**
	 * Left.
	 */
	LEFT,

	/**
	 * Right.
	 */
	RIGHT,

	/**
	 * Default value, set when direction is unknown. See
	 * {@link Direction#getDirection(String)}.
	 */
	UNKNOWN;

	/**
	 * Gets direction from action command string.
	 * 
	 * @param str
	 *          the action command
	 * @return the associated direction
	 */
	static Direction getDirection(String str) {
		if (str.equals(GridBehaviourAdapter.ACTION_COMMAND_UP)) {
			return UP;
		}
		if (str.equals(GridBehaviourAdapter.ACTION_COMMAND_DOWN)) {
			return DOWN;
		}
		if (str.equals(GridBehaviourAdapter.ACTION_COMMAND_LEFT)) {
			return LEFT;
		}
		if (str.equals(GridBehaviourAdapter.ACTION_COMMAND_RIGHT)) {
			return RIGHT;
		}
		return UNKNOWN;
	}

	/**
	 * Tests whether following this direction modifies the row index.
	 * 
	 * @return true iff following this direction modifies the row index
	 */
	boolean modifiesRows() {
		return this == UP || this == DOWN;
	}

	/**
	 * Tests whether following this direction modifies the column index.
	 * 
	 * @return true iff following this direction modifies the column index
	 */
	boolean modifiesColumns() {
		return this == LEFT || this == RIGHT;
	}

	/**
	 * Get increment. {@link Direction#DOWN} and {@link Direction#RIGHT} increase
	 * the row/column index, so that 1 is returned. {@link Direction#UP} and
	 * {@link Direction#LEFT} decrease row/column index, so -1 is returned in that
	 * case.
	 * 
	 * @return the increment (1 or -1)
	 */
	int getOffset() {
		return (this == DOWN || this == RIGHT) ? 1 : -1;
	}
}