// ObjectTreeTable.java
package com.sun.java;

import mit.cadlab.dome3.swing.table.ObjectTable;
import mit.cadlab.dome3.swing.treetable.ObjectTreeTableCellRenderer;
import mit.cadlab.dome3.swing.treetable.ObjectTreeTableModel;
import mit.cadlab.dome3.swing.treetable.ObjectTreeTableCellEditor;
import mit.cadlab.dome3.swing.treetable.AbstractObjectTreeTableModel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

// changes to JTreeTable
// * changed references of JTreeTable to ObjectTreeTable
// * changed references of JTable to ObjectTable
// * changed references of TreeTableModel to ObjectTreeTableModel
// * added MouseListener to forward mouse clicks on JTable to tree
// * added MouseListener to forward mouse clicks on JViewport to Tree
// * added TreeSelectionListener to forward selection changes to table
//   for re-rendering
// * forwards table font to tree

public class ObjectTreeTable extends ObjectTable
{

	protected ObjectTreeTableCellRenderer ttcr;
	protected TableModelListener tableModelListener = new ValueUpdateTableModelListener();

	public ObjectTreeTable(ObjectTreeTableModel treeTableModel)
	{
		super(treeTableModel);
		ttcr = new ObjectTreeTableCellRenderer(getTree(), this);
		setDefaultRenderer(ObjectTreeTableModel.class, ttcr);
		setDefaultEditor(ObjectTreeTableModel.class, new ObjectTreeTableCellEditor(getTree(), this));
		setShowGrid(false); // No grid.
		setIntercellSpacing(new Dimension(0, 0)); // No intercell spacing
		int treeHeight = getTree().getRowHeight();
		if (treeHeight > 0)
			setRowHeight(treeHeight);
		else {
			int tableHeight = getRowHeight();
			if (tableHeight > 1) {
				getTree().setRowHeight(tableHeight);
			}
		}
		setCellSelectionEnabled(true);
		setColumnSelectionAllowed(true);
		addMouseListener(new TableToTreeMouseListener());
		getTree().addTreeSelectionListener(new TreeTableTreeSelectionListener());
		getTableHeader().setReorderingAllowed(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		treeTableModel.addTableModelListener(tableModelListener);
		addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				String property = event.getPropertyName();
				if (property.equals("model")) {
					((ObjectTreeTableModel) event.getOldValue()).removeTableModelListener(tableModelListener);
					((ObjectTreeTableModel) event.getNewValue()).addTableModelListener(tableModelListener);
				}
			}
		});
	}

	public void setFont(Font f)
	{
		super.setFont(f);
		if (getTree() != null)
			getTree().setFont(f);
	}

	public void stopEditing()
	{
		if (cellEditor != null) cellEditor.stopCellEditing();
	}

	protected void setInitialColumnWidths(int[] widths)
	{
		TableColumn column = null;
		int minColumns = Math.min(getModel().getColumnCount(), widths.length);
		for (int i = 0; i < minColumns; i++) {
			column = getColumnModel().getColumn(i);
			column.setPreferredWidth(widths[i]);
		}
	}

	public JTree getTree()
	{
		return ((ObjectTreeTableModel) getModel()).getTree();
	}

	public boolean isCellEditable(int row, int column)
	{
		// must always return true for tree column (0)
		int modelColumn = convertColumnIndexToModel(column);
		if (modelColumn == 0) return true;
		return getModel().isCellEditable(row, modelColumn);
	}

	public boolean isTreeEditable(int row)
	{
		return getModel().isCellEditable(row, 0);
	}

	/**
	 * Overridden to message super and forward the method to the tree.
	 * Since the tree is not actually in the component hieachy it will
	 * never receive this unless we forward it in this manner.
	 */
	public void updateUI()
	{
		super.updateUI();
		if (ttcr != null) {
			ttcr.updateUI();
			// Do this so that the editor is referencing the current renderer
			// from the tree. The renderer can potentially change each time
			// laf changes.
			setDefaultEditor(ObjectTreeTableModel.class, new ObjectTreeTableCellEditor(getTree(), this));
		}
		// Use the tree's default foreground and background colors in the
		// table.
		LookAndFeel.installColorsAndFont(this, "Tree.background",
		                                 "Tree.foreground", "Tree.font");
	}

	/**
	 * Workaround for BasicTableUI anomaly. Make sure the UI never tries to
	 * resize the editor. The UI currently uses different techniques to
	 * paint the renderers and editors and overriding setBounds() below
	 * is not the right thing to do for an editor. Returning -1 for the
	 * editing row in this case, ensures the editor is never painted.
	 */
	public int getEditingRow()
	{
		return (getColumnClass(editingColumn) == ObjectTreeTableModel.class) ? -1 :
		        editingRow;
	}

	/**
	 * Returns the actual row that is editing as <code>getEditingRow</code>
	 * will always return -1.
	 */
	private int realEditingRow()
	{
		return editingRow;
	}

	/**
	 * This is overriden to invoke supers implementation, and then,
	 * if the receiver is editing a Tree column, the editors bounds is
	 * reset. The reason we have to do this is because JTable doesn't
	 * think the table is being edited, as <code>getEditingRow</code> returns
	 * -1, and therefore doesn't automaticly resize the editor for us.
	 */
	public void sizeColumnsToFit(int resizingColumn)
	{
		super.sizeColumnsToFit(resizingColumn);
		if (getEditingColumn() != -1 && getColumnClass(editingColumn) ==
		        ObjectTreeTableModel.class) {
			Rectangle cellRect = getCellRect(realEditingRow(),
			                                 getEditingColumn(), false);
			Component component = getEditorComponent();
			component.setBounds(cellRect);
			component.validate();
		}
	}

	/**
	 * Overridden to pass the new rowHeight to the tree.
	 */
	public void setRowHeight(int rowHeight)
	{
		super.setRowHeight(rowHeight);
		if (ttcr != null && ttcr.getRowHeight() != rowHeight) {
			ttcr.setRowHeight(getRowHeight());
		}
	}

	/**
	 * Overriden to invoke repaint for the particular location if
	 * the column contains the tree. This is done as the tree editor does
	 * not fill the bounds of the cell, we need the renderer to paint
	 * the tree in the background, and then draw the editor over it.
	 */
	public boolean editCellAt(int row, int column, EventObject e)
	{
		boolean retValue = super.editCellAt(row, column, e);
		if (retValue && getColumnClass(column) == ObjectTreeTableModel.class) {
			repaint(getCellRect(row, column, false));
		}
		return retValue;
	}

	public void addNotify()
	{
		super.addNotify();
		if (getParent() instanceof JViewport) {
			// listen for mouse press in viewport, below table
			// so that tree and table will clear selection/focus
			getParent().addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent event)
				{
					if (cellEditor != null) cellEditor.stopCellEditing();
					clearSelection();
					getTree().clearSelection();
					getParent().requestFocus();
				}
			});
		}
	}

	/**
	 * Forwards mouse clicks and presses to tree.
	 */
	protected class TableToTreeMouseListener extends MouseAdapter
	{

		public void mouseClicked(MouseEvent event)
		{
			forwardEvent(event);
		}

		public void mousePressed(MouseEvent event)
		{
			forwardEvent(event);
		}

		protected void forwardEvent(MouseEvent event)
		{
			Point p = event.getPoint();
			int column = columnAtPoint(p);
			// find which column tree is in
			int counter = getColumnCount() - 1;
			for (; counter >= 0; counter--) {
				if (getColumnClass(counter) == ObjectTreeTableModel.class) {
					break;
				}
			}
			if (counter == -1) {
				System.err.println("set table.getColumnClass to return tree class");
				counter = 0; // set to first column if not found
			}
			if (column != counter) // not in column with tree
				return;
			MouseEvent treeEvent = new MouseEvent
			        (getTree(), event.getID(),
			         event.getWhen(), event.getModifiers(),
			         event.getX() - getCellRect(0, counter, true).x,
			         event.getY(), event.getClickCount(),
			         event.isPopupTrigger());
			getTree().dispatchEvent(treeEvent); // let tree handle event
		}
	}

	protected class TreeTableTreeSelectionListener implements TreeSelectionListener
	{
		public void valueChanged(TreeSelectionEvent event)
		{
			TreePath[] treePaths = event.getPaths();
			if (treePaths == null || treePaths.length == 0) return;
			JTree tree = getTree();
			AbstractObjectTreeTableModel tableModel = (AbstractObjectTreeTableModel) getModel();
			for (int i = 0; i < treePaths.length; ++i) {
				int row = tree.getRowForPath(treePaths[i]);
				tableModel.fireTableCellUpdated(row, 0); // assume tree in column 0
			}
		}
	}

	protected class ValueUpdateTableModelListener implements TableModelListener
	{
		// cancels editing of cell if cell contents change
		public void tableChanged(TableModelEvent e)
		{
            if (e.getType() == TableModelEvent.UPDATE) {
				int modelColumn = e.getColumn();
				int row = e.getFirstRow();
				int column = convertColumnIndexToView(modelColumn);
				if (cellEditor != null && getEditingColumn() == column && realEditingRow() == row) {
					cellEditor.cancelCellEditing();
				}
			}
		}
	}

}

// modification of JTreeTable
/**
 * This example shows how to create a simple JTreeTable component,
 * by using a JTree as a renderer (and editor) for the cells in a
 * particular column in the JTable.
 *
 * @version 1.2 10/27/98
 *
 * @author Philip Milne
 * @author Scott Violet
 */
