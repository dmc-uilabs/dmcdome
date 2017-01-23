// DomeVectorTableModel.java
// ver 0.2  05/15/02
// ver history: add in JavaBean support, listening to dataObject change, then decide the table change
//


import javax.swing.table.AbstractTableModel;
import mit.cadlab.dome.util.JavaBeanSupport;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class DomeVectorTableModel extends AbstractTableModel {
    protected DomeVectorData data;
    protected PropertyChangeListener propertyListener;

    public DomeVectorTableModel(DomeVectorData vector) {
	if(vector==null)
	    throw new IllegalArgumentException("DomeVectorTableModel - null DomeVector Data");
	this.data = vector;
	propertyListener = getPropertyListener();
	data.addPropertyChangeListener(propertyListener);
    }

    protected PropertyChangeListener getPropertyListener() {
	return new VectorTablePropertyChangeListener();
    }

    public int getRowCount() {
	if (data.isRowVector()) {
	    return 1;
	} else { // columnVector
	    return data.getSize();
	}
    }

    public int getColumnCount() {
	if (!data.isRowVector()) {
	    return 1;
	} else {
	    return data.getSize();
	}
    }

    public Object getValueAt(int row, int column) {
	if (data.isRowVector() && row==0) {
	    return data.getItem(column);
	} else if (!data.isRowVector() && column==0) {
	    return data.getItem(row);
	}
	else {
	    return null;
	}
    
    }
  
    public boolean isFixedSize(){
	return data.isFixedSize();
    }

    public void setFixedSize(boolean isFixed){
	data.setFixedSize(isFixed);
    }

    public void setValueAt(Object value, int row, int column) {
    
	if(!(value instanceof Number)) {
	    //see if that is from the table editor input
	    if(value instanceof String) {
		Double digitValue;
		try{//see if it can be translate into digits
		    digitValue=new Double(Double.parseDouble((String)value));}
		catch(Exception e){
		    debug(e.getMessage());
		    return; 
		}
		if (data.isRowVector() && row==0) {
		    data.setItem(column,(Number)digitValue);
		    //fireTableCellUpdated(row, column);
		} else if (!data.isRowVector() && column==0) {
		    data.setItem(row,(Number)digitValue);
		    //fireTableCellUpdated(row, column);
		} 
		else {
		    return;
		}

		return;
	    }
	    else{
		debug("warning!-- value is not a Number, not set");
		return;
	    }
	}
	//seting value by calling directlly
	if (data.isRowVector() && row==0) {
	    data.setItem(column,(Number)value);
	    //fireTableCellUpdated(row, column);
	} 
	else if (!data.isRowVector() && column==0) {
	    data.setItem(row,(Number)value);
	    //fireTableCellUpdated(row, column);
	} 
	else {
	    return;
	}

    }

    public void addRowsOrColumns(int index, int howMany, Number initialValue) {
	data.addItems(index,howMany,initialValue);
	
    }

    public void removeRowsOrColumns(int[] indices) {
	data.removeItems(indices);
	//fireTableStructureChanged();
    }

  
    public void setIsRowVector(boolean isRowVector) {
	data.setRowVector(isRowVector);
	//fireTableStructureChanged();
    }

    public void fillItems(int[] indices, Number n) {
	data.fillItems(indices,n);
	//fireTableDataChanged();
    }
    public boolean isRowVector(){
	return data.isRowVector();
    }
  
    public void setSize(int newSize){
	if(newSize==data.getSize()) return;
	else if(newSize>data.getSize()){
	    addRowsOrColumns(data.getSize(),newSize-data.getSize(),data.initialValue);
	}
	else if(newSize<data.getSize()){
	    data.removeRange(newSize,data.getSize());
	    //fireTableStructureChanged();
	}

    }

    public boolean isCellEditable(int rowIndex,int columnIndex)
    {
	return true;
    }

    public void setValueType(String type){
	if(type.equals(data.getValueType())) return;
	data.setValueType(type);
	
    }
 
    public String getColumnName(int column) {
	/** the default ones
	    String result = "";
	    for (; column >= 0; column = column / 26 - 1) {
	    result = (char)((char)(column%26)+'A') + result;
	    }
	    return result;*/
	return String.valueOf(column);
    }
    private void debug(String msg){
	boolean debug=true;
	if(debug)
	    System.out.println("DomeVectorTableModel: "+msg);
    }

    protected class VectorTablePropertyChangeListener implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent e) {
	    String property = e.getPropertyName();
	    Object newValue = e.getNewValue();
	    if (property.equals(DomeVector.DATA)) {//repaint whole table
		fireTableDataChanged();
	    }
	    else if (property.equals(DomeVector.ROWVECTOR)) {//repaint whole table
		 fireTableStructureChanged();
		
	    }
	    else if (property.equals(DomeVector.ITEM)) {//repaint that cell
		position p=(position)(newValue);
		int row=p.row;
		int column=p.col;
		
		fireTableCellUpdated(row, column);
	    }
	     else if (property.equals(DomeVector.ITEMS)) {//repaint those cells
		positions ps=(positions)(newValue);
		
		if(ps.getChangeType().equals(positions.ADD))
		    {
			if(ps.getStartIndex()!=-1&&!data.isRowVector()) 
			    fireTableRowsInserted(ps.getStartIndex(),ps.getEndIndex()-1);
			else
			   fireTableStructureChanged();  
			
		    }
		else if(ps.getChangeType().equals(positions.DEL))
		    {
			if(ps.getStartIndex()!=-1&&!data.isRowVector())    
			    fireTableRowsDeleted(ps.getStartIndex(),ps.getEndIndex()-1);
			else
			    fireTableStructureChanged();
		    }
		else if(ps.getChangeType().equals(positions.UPDATE))
		    {
			fireTableDataChanged();
		    }
		
	    }
	}
  }
}
