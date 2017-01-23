// DTable.java
package mit.cadlab.dome3.swing;

import java.util.Vector;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Allow header changes without restructuring table.
 * Allow row changes without restructuring table.
 */
public class DTable extends JTable
{

	public DTable()
	{
		super();
	}

	public DTable(int numRows, int numColumns)
	{
		super(numRows, numColumns);
	}

	public DTable(Object[][] rowData, Object[] columnNames)
	{
		super(rowData, columnNames);
	}

	public DTable(TableModel dm)
	{
		super(dm);
		//System.out.println(getAutoCreateColumnsFromModel());
		//setAutoCreateColumnsFromModel(false);
	}

	public DTable(TableModel dm, TableColumnModel cm)
	{
		super(dm, cm);
	}

	public DTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm)
	{
		super(dm, cm, sm);
	}

	public DTable(Vector rowData, Vector columnNames)
	{
		super(rowData, columnNames);
	}

	public void tableChanged(TableModelEvent e)
	{
		printTableModelEvent(e);
		super.tableChanged(e);
	}
	/*
	  if (e == null || e.getFirstRow() == TableModelEvent.HEADER_ROW) {
	    // The whole thing changed
	    clearSelection();

	    if (getAutoCreateColumnsFromModel()) {
	  // This will effect invalidation of the JTable and JTableHeader.
	  createDefaultColumnsFromModel();
	  return;
	    }

	    resizeAndRepaint();
	    return;
	  }

	  // The totalRowHeight calculated below will be incorrect if
	  // there are variable height rows. Repaint the visible region,
	  // but don't return as a revalidate may be necessary as well.
	  if (rowModel != null) {
	    repaint();
	  }

	  if (e.getType() == TableModelEvent.INSERT) {
	    tableRowsInserted(e);
	    return;
	  }

	  if (e.getType() == TableModelEvent.DELETE) {
	    tableRowsDeleted(e);
	    return;
	  }

	  int modelColumn = e.getColumn();
	  int start = e.getFirstRow();
	  int end = e.getLastRow();

	  Rectangle dirtyRegion;
	  if (modelColumn == TableModelEvent.ALL_COLUMNS) {
	    // 1 or more rows changed
	    dirtyRegion = new Rectangle(0, start * getRowHeight(),
	                getColumnModel().getTotalColumnWidth(), 0);
	  }
	  else {
	    // A cell or column of cells has changed.
	    // Unlike the rest of the methods in the JTable, the TableModelEvent
	    // uses the coordinate system of the model instead of the view.
	    // This is the only place in the JTable where this "reverse mapping"
	    // is used.
	    int column = convertColumnIndexToView(modelColumn);
	    dirtyRegion = getCellRect(start, column, false);
	  }

	  // Now adjust the height of the dirty region according to the value of "end".
	  // Check for Integer.MAX_VALUE as this will cause an overflow.
	  if (end != Integer.MAX_VALUE) {
	    dirtyRegion.height = (end-start+1)*getRowHeight();
	    repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height);
	  }
	  // In fact, if the end is Integer.MAX_VALUE we need to revalidate anyway
	  // because the scrollbar may need repainting.
	  else {
	    clearSelection();
	    resizeAndRepaint();
	  }
	}
	*/

	public void printTableModelEvent(TableModelEvent e)
	{
		System.out.print(getEventType(e.getType()) + ": ");
		System.out.print("rows " + getIntRow(e.getFirstRow()) +
		                 " to " + getIntRow(e.getLastRow()));
		System.out.println("\tcolumns: " + getIntCol(e.getColumn()));
	}

	public String getIntRow(int row)
	{
		if (row == TableModelEvent.HEADER_ROW)
			return "HEADER_ROW";
		else
			return Integer.toString(row);
	}

	public String getIntCol(int col)
	{
		if (col == TableModelEvent.ALL_COLUMNS)
			return "ALL_COLUMNS";
		else
			return Integer.toString(col);
	}

	public String getEventType(int type)
	{
		switch (type) {
			case (TableModelEvent.INSERT):
				return "INSERT";
			case (TableModelEvent.UPDATE):
				return "UPDATE";
			case (TableModelEvent.DELETE):
				return "DELETE";
			default:
				return "UNKNOWN";
		}
	}
}
