// JavaDataObjectTableModel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.dsm;

import mit.cadlab.dome3.swing.table.AbstractObjectTableModel;
import mit.cadlab.dome3.swing.VTextIcon;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import java.awt.Color;

public class DsmDataObjectTableModel extends AbstractObjectTableModel
{
	Object[][] data;
	protected Color backGroundColor = null;

	public DsmDataObjectTableModel(Object[][] data, int numberColumns, Color useColor)
	{
		super(numberColumns);
		this.data = data;
		backGroundColor = useColor;
	}

	public DsmDataObjectTableModel(Object[][] data, int numberColumns, Object[] columnNames)
	{
		super(numberColumns, columnNames);
		this.data = data;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	public int getRowCount()
	{
		return 2;
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return data[rowIndex][columnIndex];
	}

	public Class getColumnClass(int row, int column)
	{
		return String.class;
	}

	// return null, use per type of class
	public TableCellRenderer getCellRenderer(int row, int column)
	{
		if (row == 0) {
			return new VerticalTextTableCellRenderer(backGroundColor);
		} else if (row == 1) {
			return new NumberColumnRenderer(backGroundColor);
		} else
			return null;
	}

	public TableCellEditor getCellEditor(int row, int column)
	{
		return null;
	}

	public static class VerticalTextTableCellRenderer extends DefaultTableCellRenderer
	{
		protected Color headerColor;
		protected boolean drawSeparator = false;

		public VerticalTextTableCellRenderer(Color columnTableColor)
		{
			setVerticalAlignment(SwingConstants.BOTTOM);
			headerColor = columnTableColor;
			setBorder(BorderFactory.createLineBorder(Color.GRAY));
			setBackground(headerColor);
			setOpaque(true);
		}

		protected void setValue(Object value)
		{
			VTextIcon textIcon = new VTextIcon(this, (value == null) ? "" : value.toString(), VTextIcon.ROTATE_LEFT);
			setIcon(textIcon);
		}
	}

	public static class NumberColumnRenderer extends DefaultTableCellRenderer
	{
		protected Color headerColor;

		public NumberColumnRenderer(Color columnTableColor)
		{
			setHorizontalAlignment(SwingConstants.CENTER);
			headerColor = columnTableColor;
			setBorder(BorderFactory.createLineBorder(Color.GRAY));
			setBackground(headerColor);
			setOpaque(true);
		}
	}
}
