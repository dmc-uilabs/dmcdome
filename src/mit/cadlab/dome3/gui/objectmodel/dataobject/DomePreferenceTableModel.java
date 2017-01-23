// DomePreferenceTableModel.java
// ver
// ver history:


package mit.cadlab.dome3.gui.objectmodel.dataobject;


import mit.cadlab.dome3.objectmodel.dataobject.DomePreferenceData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomePreference;
import javax.swing.table.AbstractTableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;


public class DomePreferenceTableModel extends AbstractTableModel
{
	protected DomePreferenceData data;
	protected PropertyChangeListener propertyListener;

	public DomePreferenceTableModel(DomePreferenceData vector)
	{
		if (vector == null)
			throw new IllegalArgumentException("DomePreferenceTableModel - null DomeVector Data");
		this.data = vector;
		propertyListener = getPropertyListener();
		data.addPropertyChangeListener(propertyListener);
	}

    /**
     * sangmok : a new method to fix memory leakage problem
     * DomePreferenceTableModel have reference to DataObject.
     * It should be released when table model is no more used.
     * Now that DomePreferenceTableModel is not automatically released even when the Table GUI window closes.
     * This method is an alternative but not perfect way to make sure the data object released.
     * To be perfect, we must fix all ConcreteParameter and DataObjectPanel released when the window closed.
     * If those instances are released, this TableModel will be released automatically, and there is no need to invoke this method().
     */
    public void releaseReferenceToDataObject() {
        data = null;
    }

    protected void setData(DomePreferenceData data) {
        this.data = data;
    }


    /**
     * make this table model no more listen to the data changes
     * this method should be invoked before DomeMatrixTableModel is replace by a new instance of DomeMatrixTableModel
     * for example, when user change the data type of matrix from real to integer, this should be called before assign new DomeMatrixTableModel
     */
    public void removePropertyChangeListener() {
        data.removePropertyChangeListener(propertyListener);
    }

	protected PropertyChangeListener getPropertyListener()
	{
		return new PreferenceTablePropertyChangeListener();
	}

	public int getRowCount()
	{
		return data.getRowCount();
	}

	public int getColumnCount()
	{
		return data.getColumnCount();
	}

	public Object getValueAt(int row, int column)
	{
		return data.getItem(row, column);
	}

	public boolean isFixedSize()
	{
		return data.isFixedSize();
	}


	public void setValueAt(Object value, int row, int column)
	{
		if (!(value instanceof Number)) {
			//see if that is from the table editor input
			if (value instanceof String) {
				Double digitValue;
				try {//see if it can be translateDefaultSubscriptions into digits
					digitValue = new Double(Double.parseDouble((String) value));
				} catch (Exception e) {
					debug(e.getMessage());
					return;
				}

				data.setItem(row, column, (Number) digitValue);
				//fireTableChanged(new TableModelEvent(this, row, row, column));
				return;
			} else {
				debug("warning!-- value is not a Number, not set");
				return;
			}
		}
		//seting value by calling directlly
		data.setItem(row, column, (Number) value);
		//fireTableChanged(new TableModelEvent(this, row, row, column));
	}

	// public void addRows(int index, int howMany, Number initialValue) {
	//  if (data.addRowItems(index,howMany,initialValue))
	//    { // added rows
	//	fireTableRowsInserted(index, index+howMany-1);
	//   }

	//}

	// public void addColumns(int index, int howMany, Number initialValue) {
	//   if (data.addColumnItems(index,howMany,initialValue))
	//    {
	// added columns
	//	fireTableStructureChanged();
	//    }
	// }

	// public void removeRows(int[] indices) {
	//  if(data.removeRowItems(indices)){
	// Generate notification
	//   for(int i=0;i<indices.length;i++)
	//  fireTableRowsDeleted(indices[i], indices[i]);
	//  }
	// }

	// public void removeColumns(int[] indices) {
	//  if(data.removeColumnItems(indices))
	//  fireTableStructureChanged();
	// }

	// public void removeRange(int startIndex,int endIndex,boolean isForRow){

	//   if(data.removeRange(startIndex,endIndex,isForRow)){
	//  if(isForRow)    // Generate notification
	//     fireTableRowsDeleted(startIndex, endIndex-1);
	//  else
	//    fireTableStructureChanged();

	//   }
	// }

	// public void fillItems(Vector selectedPoints, Number n) {
	//  if(data.fillItems(selectedPoints,n))
	//  fireTableDataChanged();
	// }






	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return true;
	}

	public void setValueType(String type)
	{
		if (type.equals(data.getValueType())) return;
		data.setValueType(type);
		// fireTableDataChanged();
	}

	public String getColumnName(int column)
	{
		/** the default ones
		 String result = "";
		 for (; column >= 0; column = column / 26 - 1) {
		 result = (char)((char)(column%26)+'A') + result;
		 }
		 return result;*/
		return String.valueOf(column);
	}



	// public void setRowCount(int newSize) {
	//   int oldNumRows = getRowCount();


	//  if (newSize <= oldNumRows) {
	//    data.setRowCount(newSize);
	// Generate notification
	//     fireTableRowsDeleted(getRowCount(), oldNumRows-1);
	//  }
	//  else {
	// Generate notification
	//    data.setRowCount(newSize);
	//   fireTableRowsInserted(oldNumRows, getRowCount()-1);
	//  }
	// }

	// public void setColumnCount(int newSize){
	//   if(data.isFixedSize()) {
	//    debug("Warning! data size is fixed, can't change column count");
	//    return;
	//  }
	//  if(newSize==getColumnCount()) return;
	//  data.setColumnCount(newSize);
	//   fireTableStructureChanged();

	// }



	private void debug(String msg)
	{
		boolean debug = false;
		if (debug)
			System.out.println("DomePreferenceTableModel: " + msg);
	}


	protected class PreferenceTablePropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(DomePreference.DATA)) {//repaint whole table
				fireTableDataChanged();
			} else if (property.equals(DomePreference.VALUETYPE)) {//repaint that cell
				fireTableDataChanged();
			} else if (property.equals(DomePreference.ITEM)) {//repaint that cell
				java.awt.Point p = (java.awt.Point) (newValue);
				int row = p.x;
				int column = p.y;

				fireTableCellUpdated(row, column);
			} else if (property.equals(DomePreference.ITEMS)) {//repaint those cells
				Vector info = (Vector) newValue;
				//the first item are description
				String des = (String) info.get(0);

				if (des.equals("addrow")) {
					for (int i = 1; i < info.size(); i++) {
						int row = ((Integer) (info.get(i))).intValue();
						fireTableRowsInserted(row, row);
					}
				} else if (des.equals("delrow")) {


					for (int i = 1; i < info.size(); i++) {
						int row = ((Integer) (info.get(i))).intValue();
						fireTableRowsDeleted(row, row);
					}

					//this is for delete all the rows
					if (getRowCount() == 0)
						fireTableStructureChanged();

				} else if (des.equals("addcol")) {
					fireTableStructureChanged();
				} else if (des.equals("delcol")) {
					fireTableStructureChanged();
				}
			}
		}
	}
}
