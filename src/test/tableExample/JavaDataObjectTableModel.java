// JavaDataObjectTableModel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package test.tableExample;

import mit.cadlab.dome3.swing.table.AbstractObjectTableModel;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;

public class JavaDataObjectTableModel extends AbstractObjectTableModel
{
	Object[][] data;

	public JavaDataObjectTableModel(Object[][] data, int numberColumns)
	{
		super(numberColumns);
		this.data = data;
	}

	public JavaDataObjectTableModel(Object[][] data, int numberColumns, Object[] columnNames)
	{
		super(numberColumns, columnNames);
		this.data = data;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return true;
	}

	public int getRowCount()
	{
		return data.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return data[rowIndex][columnIndex];
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		System.out.println(aValue+"\t"+aValue.getClass());
		data[rowIndex][columnIndex] = aValue;
	}

	public Class getColumnClass(int row, int column)
	{
		return data[row][column].getClass();
	}

	// return null, use per type of class
	public TableCellRenderer getCellRenderer(int row, int column)
	{
		return null;
	}

	public TableCellEditor getCellEditor(int row, int column)
	{
		return null;
	}
}
