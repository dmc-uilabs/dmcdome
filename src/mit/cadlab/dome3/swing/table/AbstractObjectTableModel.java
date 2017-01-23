// AbstractObjectTableModel.java
package mit.cadlab.dome3.swing.table;

import javax.swing.table.AbstractTableModel;

/**
 * Partial implementation of ObjectTableModel
 * with methods that manage column names and number.
 */
public abstract class AbstractObjectTableModel extends AbstractTableModel
        implements ObjectTableModel
{

	/* to be implemented by subclasses
	 * TableModel interface
	 * public int getRowCount();
	 * public boolean isCellEditable(int rowIndex, int columnIndex);
	 * public Object getValueAt(int rowIndex, int columnIndex);
	 * public void setValueAt(Object aValue, int rowIndex, int columnIndex);
	 * ObjectTableModel interface
	 * public Class getColumnClass(int row, int column);
	 * public TableCellRenderer getCellRenderer(int row, int column);
	 * public TableCellEditor getCellEditor(int row, int column);
	 */

	protected int columnCount = 0;
	protected String[] columnNames = null;
	protected Class[] columnClasses = null;

	public AbstractObjectTableModel(int numberColumns)
	{
		if (numberColumns < 0)
			throw new IllegalArgumentException("number columns can not be negative");
		else
			columnCount = numberColumns;
	}

	public AbstractObjectTableModel(int numberColumns, Object[] columnNames)
	{
		this(numberColumns);
		setColumnNames(columnNames);
	}

	public void setColumnNames(Object[] newColumnNames)
	{
		if (newColumnNames == null) {
			columnNames = new String[]{};
		} else {
			columnNames = new String[newColumnNames.length];
			for (int i = 0; i < columnNames.length; ++i) {
				Object name = newColumnNames[i];
				columnNames[i] = ((name == null) ? "" : name.toString());
			}
		}
		fireTableStructureChanged();
	}

	public void setColumnClasses(Class[] classes)
	{
		if (classes == null) {
			columnClasses = new Class[]{};
		} else {
			columnClasses = new Class[classes.length];
			for (int i = 0; i < columnClasses.length; ++i)
				columnClasses[i] = classes[i];
		}
		fireTableStructureChanged();
	}

	// TableModel interface
	public Class getColumnClass(int column)
	{
		assertValidColumnIndex(column);
		if (columnClasses != null && column < columnClasses.length) {
			Class cClass = columnClasses[column];
			return (cClass == null) ? Object.class : cClass;
		}
		return Object.class;
	}

	public int getColumnCount()
	{
		return columnCount;
	}

	public String getColumnName(int column)
	{
		assertValidColumnIndex(column);
		if (columnNames != null && column < columnNames.length) {
			String cName = columnNames[column];
			return (cName == null) ? super.getColumnName(column) : cName;
		}
		return super.getColumnName(column);
	}

	protected void assertValidColumnIndex(int column)
	{
		if (column < 0 || column >= columnCount)
			throw new IndexOutOfBoundsException("invalid table column index: " + column);
	}

}
