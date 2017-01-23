// DomeVectorTableModel.java
// ver 0.2  05/15/02
// ver history: add in JavaBean support, listening to dataObject change, then decide the table change
//

package mit.cadlab.dome3.gui.objectmodel.dataobject;

import mit.cadlab.dome3.objectmodel.dataobject.DomeVectorData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector;
import mit.cadlab.dome3.objectmodel.util.Position;
import mit.cadlab.dome3.objectmodel.util.Positions;

import javax.swing.table.AbstractTableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class DomeVectorTableModel extends AbstractTableModel
{
	protected DomeVectorData data;
	protected PropertyChangeListener propertyListener;

	public DomeVectorTableModel(DomeVectorData vector)
	{
		if (vector == null)
			throw new IllegalArgumentException("DomeVectorTableModel - null DomeVector Data");
		this.data = vector;
		propertyListener = getPropertyListener();
		data.addPropertyChangeListener(propertyListener);
	}

    /**
     * sangmok : a new method to fix memory leakage problem
     * DomeMatrixTableModel have reference to DataObject.
     * It should be released when table model is no more used.
     * Now that DomeMatrixTableModel is not automatically released even when the Table GUI window closes.
     * This method is an alternative but not perfect way to make sure the data object released.
     * To be perfect, we must fix all ConcreteParameter and DataObjectPanel released when the window closed.
     * If those instances are released, this TableModel will be released automatically, and there is no need to invoke this method().
     */
    public void releaseReferenceToDataObject() {
        data = null;
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
		return new VectorTablePropertyChangeListener();
	}

	public DomeVectorData getData()
	{
		return data;
	}

	public void setData(DomeVectorData data)
	{
		this.data = data;
	}

	public int getRowCount()
	{
		if (data.isRowVector()) {
			return (data.getSize() == 0 ? 0 : 1);
		} else { // columnVector
			return data.getSize();
		}
	}

	public int getColumnCount()
	{
		if (!data.isRowVector()) {
			return (data.getSize() == 0 ? 0 : 1);
		} else {
			return data.getSize();
		}
	}

	public Object getValueAt(int row, int column)
	{
		if (data.isRowVector() && row == 0) {
			return data.getItem(column);
		} else if (!data.isRowVector() && column == 0) {
			return data.getItem(row);
		} else {
			return null;
		}

	}

	public boolean isFixedSize()
	{
		return data.isFixedSize();
	}

	public void setFixedSize(boolean isFixed)
	{
		data.setFixedSize(isFixed);
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
				if (data.isRowVector() && row == 0) {
					data.setItem(column, (Number) digitValue);
					//fireTableCellUpdated(row, column);
				} else if (!data.isRowVector() && column == 0) {
					data.setItem(row, (Number) digitValue);
					//fireTableCellUpdated(row, column);
				} else {
					return;
				}

				return;
			} else {
				debug("warning!-- value is not a Number, not set");
				return;
			}
		}
		//seting value by calling directlly
		if (data.isRowVector() && row == 0) {
			data.setItem(column, (Number) value);
			//fireTableCellUpdated(row, column);
		} else if (!data.isRowVector() && column == 0) {
			data.setItem(row, (Number) value);
			//fireTableCellUpdated(row, column);
		} else {
			return;
		}

	}

	public void addRowsOrColumns(int index, int howMany, Number initialValue)
	{
		data.addItems(index, howMany, initialValue);

	}

	public void removeRowsOrColumns(int[] indices)
	{
		data.removeItems(indices);
		//fireTableStructureChanged();
	}


	public void setIsRowVector(boolean isRowVector)
	{
		data.setRowVector(isRowVector);
		//fireTableStructureChanged();
	}

	public void fillItems(int[] indices, Number n)
	{
		data.fillItems(indices, n);
		//fireTableDataChanged();
	}

	public boolean isRowVector()
	{
		return data.isRowVector();
	}

	public void setSize(int newSize)
	{
		if (newSize == data.getSize())
			return;
		else if (newSize > data.getSize()) {
			addRowsOrColumns(data.getSize(), newSize - data.getSize(), data.getInitialValue());
		} else if (newSize < data.getSize()) {
			data.removeRange(newSize, data.getSize());
			//fireTableStructureChanged();
		}

	}

	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return true;
	}

	public void setValueType(String type)
	{
		if (type.equals(data.getValueType())) return;
		data.setValueType(type);

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

	private void debug(String msg)
	{
		boolean debug = false;
		if (debug)
			System.out.println("DomeVectorTableModel: " + msg);
	}

	protected class VectorTablePropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String property = e.getPropertyName();
			Object newValue = e.getNewValue();
			if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector.DATA)) {
				fireTableDataChanged();
			} else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector.ROWVECTOR) ||
			        property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector.SIZE))           //repaint whole table
			{
				fireTableStructureChanged();
			} else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector.ITEM)) {    //repaint that cell
				Position p = (Position) (newValue);
				int row = p.row;
				int column = p.col;

				fireTableCellUpdated(row, column);
			} else if (property.equals(mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeVector.ITEMS))//repaint those cells
			{
				Positions ps = (Positions) (newValue);

				if (ps.getChangeType().equals(Positions.ADD)) {
					if (ps.getStartIndex() != -1 && !data.isRowVector())
						fireTableRowsInserted(ps.getStartIndex(), ps.getEndIndex() - 1);
					else
						fireTableStructureChanged();

				} else if (ps.getChangeType().equals(Positions.DEL)) {
					if (ps.getStartIndex() != -1 && !data.isRowVector())
						fireTableRowsDeleted(ps.getStartIndex(), ps.getEndIndex() - 1);
					else
						fireTableStructureChanged();
				} else if (ps.getChangeType().equals(Positions.UPDATE)) {
					fireTableDataChanged();
				}
			}
		}
	}
}
