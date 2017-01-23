// ObjectTable.java
package mit.cadlab.dome3.swing.table;

import mit.cadlab.dome3.gui.guiutils.treetable.Editors;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * An ObjectTable is used to represent a collection of objects in a table.
 * Each row represents one object and the value, renderer and editor
 * for each column in the table for that object is represented in
 * a TableObject.
 */
public class ObjectTable extends JTable
{

	protected ObjectTableModel objModel;

	public ObjectTable(ObjectTableModel model)
	{
		super(model);
	}

	protected ObjectTable()
	{
		super(new DefaultObjectTableModel(0, new Object[]{}, new Object[]{}));
	}

	public void setModel(TableModel model)
	{
		if (model instanceof ObjectTableModel) {
			objModel = (ObjectTableModel) model;
			super.setModel(model);
		} else {
			throw new IllegalArgumentException("ObjectTable.setModel requires ObjectTableModel");
		}
	}

	public TableCellRenderer getCellRenderer(int row, int column)
	{
		int columnIndex = convertColumnIndexToModel(column);
		// try to get for specific item
		TableCellRenderer renderer = null;
		TableModel tableModel = getModel();
		if (tableModel instanceof ObjectTableModel)
			renderer = ((ObjectTableModel) tableModel).getCellRenderer(row, columnIndex);
		if (renderer == null) { // try to get for column
			TableColumn tableColumn = getColumnModel().getColumn(column);
			renderer = tableColumn.getCellRenderer();
		}
		if (renderer == null) { // try to get default renderer for column class
			Class columnClass = null;
			if (tableModel instanceof ObjectTableModel)
				columnClass = ((ObjectTableModel) tableModel).getColumnClass(row, columnIndex);
			if (columnClass == null || columnClass == Object.class) // try again/more specific answer
				columnClass = getColumnClass(column);
			renderer = getDefaultRenderer(columnClass);
		}
		return renderer;
	}

	public TableCellEditor getCellEditor(int row, int column)
	{
		int columnIndex = convertColumnIndexToModel(column);
		// try to get for specific item
		TableCellEditor editor = null;
		TableModel tableModel = getModel();
		if (tableModel instanceof ObjectTableModel)
			editor = ((ObjectTableModel) tableModel).getCellEditor(row, columnIndex);
		if (editor == null) { // try to get for column
			TableColumn tableColumn = getColumnModel().getColumn(column);
			editor = tableColumn.getCellEditor();
		}
		if (editor == null) { // try to get default editor for column class
			Class columnClass = null;
			if (tableModel instanceof ObjectTableModel)
				columnClass = ((ObjectTableModel) tableModel).getColumnClass(row, columnIndex);
			if (columnClass == null || columnClass == Object.class) // try again/more specific answer
				columnClass = getColumnClass(column);
			editor = getDefaultEditor(columnClass);
		}

		// keep the Mapping Tool panel in front of the model build panel
		if (editor instanceof Editors.MappingCellEditor) {
			setFocusable(false);
		}

		return editor;
	}

}
