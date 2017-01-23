// DefaultObjectTableModel.java
package mit.cadlab.dome3.swing.table;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Default implementation of ObjectTableModel which requires
 * objects to be instances of TableData or TableObjects.
 * TableModel interface fulfilled by dispatching to TableObjects.
 * Data is stored in a vector which only holds TableObjects.
 */
public class DefaultObjectTableModel extends AbstractObjectTableModel
{

	protected Vector dataVector;
	protected DefaultTableObjectListener tObjListener = new DefaultTableObjectListener();

	DefaultObjectTableModel(int columnCount, Object[] columnNames, Object[] data)
	{
		super(columnCount, columnNames);
		setDataVector(data);
	}

	DefaultObjectTableModel(int columnCount, Object[] columnNames, Vector data)
	{
		super(columnCount, columnNames);
		setDataVector(data);
	}

	public void setDataVector(Object[] newData)
	{
		if (newData == null)
			throw new IllegalArgumentException("setDataVector() - Null parameter");
		dataVector = new Vector();
		for (int i = 0; i < newData.length; ++i) {
			TableObject tObj = getTableObjectFromObject(newData[i]);
			dataVector.add(tObj);
			tObj.addTableObjectListener(tObjListener);
		}
		fireTableChanged(new TableModelEvent(this, 0, getRowCount() - 1,
		                                     TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	public void setDataVector(Vector newData)
	{
		if (newData == null)
			throw new IllegalArgumentException("setDataVector() - Null parameter");
		dataVector = new Vector();
		Enumeration e = newData.elements();
		while (e.hasMoreElements()) {
			TableObject tObj = getTableObjectFromObject(e.nextElement());
			dataVector.add(tObj);
			tObj.addTableObjectListener(tObjListener);
		}
		fireTableChanged(new TableModelEvent(this, 0, getRowCount() - 1,
		                                     TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	protected TableObject getObjectAt(int row)
	{
		return (TableObject) dataVector.elementAt(row);
	}

	// TableModel interface

	public int getRowCount()
	{
		return dataVector.size();
	}

	public boolean isCellEditable(int row, int column)
	{
		return getObjectAt(row).isEditableAt(column);
	}

	public Object getValueAt(int row, int column)
	{
		return getObjectAt(row).getValueAt(column);
	}

	public void setValueAt(Object aValue, int row, int column)
	{
		getObjectAt(row).setValueAt(aValue, column);
		fireTableChanged(new TableModelEvent(this, row, row, column));
	}

	// ObjectTableModel interface
	public Class getColumnClass(int row, int column)
	{
		return getObjectAt(row).getClassAt(column);
	}

	public TableCellRenderer getCellRenderer(int row, int column)
	{
		return getObjectAt(row).getRendererAt(column);
	}

	public TableCellEditor getCellEditor(int row, int column)
	{
		return getObjectAt(row).getEditorAt(column);
	}

	public void fireTableCellUpdated(Object obj, int column)
	{
		int row = dataVector.indexOf(getTableObjectFromObject(obj));
		if (row != -1)
			fireTableCellUpdated(row, column);
	}

	// methods to support mutable table model
	public void addObject(Object data)
	{
		TableObject tObj = getTableObjectFromObject(data);
		dataVector.add(tObj);
		tObj.addTableObjectListener(tObjListener);
		fireTableChanged(new TableModelEvent(this, getRowCount() - 1, getRowCount() - 1,
		                                     TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	public void insertObject(int row, Object data)
	{
		TableObject tObj = getTableObjectFromObject(data);
		dataVector.insertElementAt(tObj, row);
		tObj.addTableObjectListener(tObjListener);
		fireTableChanged(new TableModelEvent(this, row, row,
		                                     TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	public void removeObject(int row)
	{
		((TableObject) dataVector.remove(row)).removeTableObjectListener(tObjListener);
		fireTableRowsDeleted(row, row);
	}

	public void removeObject(Object data)
	{
		int row = dataVector.indexOf(getTableObjectFromObject(data));
		if (row != -1) {
			removeObject(row);
		}
	}

	protected TableObject getTableObjectFromObject(Object obj)
	{
		if (obj == null) {
			throw new IllegalArgumentException("table data can not be null!");
		}
		if (obj instanceof TableData)
			return ((TableData) obj).getTableObject();
		else if (obj instanceof TableObject)
			return (TableObject) obj;
		else
			throw new IllegalArgumentException("Item is neither TableData nor TableObject instance: " + obj);
	}

	// supports the same table object in multiple rows of table
	protected int[] getRowsForObject(Object obj)
	{
		Vector result = new Vector();
		int i = dataVector.indexOf(obj);
		while (i != -1) {
			result.add(new Integer(i));
			i = dataVector.indexOf(obj, i + 1);
		}
		int[] rows = new int[result.size()];
		for (int r = 0; r < rows.length; ++r) {
			rows[r] = ((Integer) result.elementAt(r)).intValue();
		}
		return rows;
	}

	class DefaultTableObjectListener implements TableObjectListener
	{
		public void tableObjectChanged(TableObjectEvent event)
		{
			int[] rows = getRowsForObject(event.getSource());
			if (rows.length == 0) { // object not in table
				((TableObject) event.getSource()).removeTableObjectListener(this);
				return;
			}
			int column = event.getColumn();
			if (column == TableObjectEvent.ALL_COLUMNS)
				for (int i = 0; i < rows.length; ++i) {
					fireTableRowsUpdated(rows[i], rows[i]);
				}
			else
				for (int i = 0; i < rows.length; ++i) {
					fireTableCellUpdated(rows[i], column);
				}
		}
	}

}
