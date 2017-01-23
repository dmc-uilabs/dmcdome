/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package com.sun.java;

import mit.cadlab.dome3.gui.guiutils.table.TableSelection;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * BasicTableUI implementation
 *
 * @version 1.103 02/06/02
 * @author Philip Milne
 */
public class TableSelectionUI extends BasicTableUI
{

	protected MouseInputListener createMouseInputListener()
	{
		return new MyMouseInputHandler();
	}

	public class MyMouseInputHandler extends BasicTableUI.MouseInputHandler
	{

		private Component dispatchComponent;

		public void mouseClicked(MouseEvent e)
		{
			if (shouldIgnore(e)) {
				return;
			}

			repostEvent(e);

			Point p = e.getPoint();
			int row = table.rowAtPoint(p);
			int column = table.columnAtPoint(p);
			if (!e.isShiftDown())
				((TableSelection) table).setMySelectedPoint(row, column);
//      System.out.println("mouse click at "+row+","+column);
		}

		public void mouseDragged(MouseEvent e)
		{
			if (shouldIgnore(e)) {
				return;
			}

			repostEvent(e);

			CellEditor editor = table.getCellEditor();
			if (editor == null || editor.shouldSelectCell(e)) {
				Point p = e.getPoint();
				int row = table.rowAtPoint(p);
				int column = table.columnAtPoint(p);
				// The autoscroller can generate drag events outside the Table's range.
				if ((column == -1) || (row == -1)) {
					return;
				}
				((TableSelection) table).setMySelectedPointRect(row, column);

			}
		}

		public void mousePressed(MouseEvent e)
		{
			if (shouldIgnore(e)) {
				return;
			}

			Point p = e.getPoint();
			int row = table.rowAtPoint(p);
			int column = table.columnAtPoint(p);
			// The autoscroller can generate drag events outside the Table's range.
			if ((column == -1) || (row == -1)) {
				return;
			}

			if (table.editCellAt(row, column, e)) {
				setDispatchComponent(e);
				repostEvent(e);
			} else {
				table.requestFocus();
			}

			/*      CellEditor editor = table.getCellEditor();
			if (editor == null || editor.shouldSelectCell(e)) {
		 setValueIsAdjusting(true);
		 table.changeSelection(row, column, e.isControlDown(), e.isShiftDown());
		 }*/

			if ((!e.isControlDown()) && (!e.isShiftDown())) {
				((TableSelection) table).clearPoints();
			}

			if (!e.isShiftDown())
				((TableSelection) table).setMyStartPoint(row, column);

		}

		public void mouseReleased(MouseEvent e)
		{
			if (shouldIgnore(e)) {
				return;
			}

			repostEvent(e);
			dispatchComponent = null;
			//setValueIsAdjusting(false);
			Point p = e.getPoint();
			int row = table.rowAtPoint(p);
			int column = table.columnAtPoint(p);
			if (e.isShiftDown()) {
				((TableSelection) table).setMySelectedPointRect(row, column);
			}
		}

		private boolean shouldIgnore(MouseEvent e)
		{
			return !(SwingUtilities.isLeftMouseButton(e) && table.isEnabled());
		}

		private boolean repostEvent(MouseEvent e)
		{
			if (dispatchComponent == null) {
				return false;
			}
			MouseEvent e2 = SwingUtilities.convertMouseEvent(table, e, dispatchComponent);
			dispatchComponent.dispatchEvent(e2);
			return true;
		}

		private void setDispatchComponent(MouseEvent e)
		{
			Component editorComponent = table.getEditorComponent();
			Point p = e.getPoint();
			Point p2 = SwingUtilities.convertPoint(table, p, editorComponent);
			dispatchComponent = SwingUtilities.getDeepestComponentAt(editorComponent,
			                                                         p2.x, p2.y);
		}

	}

}

