// EditEnumerationTableModel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.dataobject;

import mit.cadlab.dome3.gui.guiutils.treetable.TextCellEditor;
import mit.cadlab.dome3.objectmodel.dataobject.EnumerationData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeEnumeration;
import mit.cadlab.dome3.swing.table.AbstractObjectTableModel;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 */
public class EditEnumerationTableModel extends AbstractObjectTableModel {
    EnumerationData dataEnum;
    static String[] colNames = new String[]{"name", "value", "type"};
    protected PropertyChangeListener propertyListener;

    public EditEnumerationTableModel(EnumerationData d) {
        super(3, colNames);
        dataEnum = d;
        propertyListener = getPropertyListener();
        dataEnum.addPropertyChangeListener(propertyListener);
    }

    public int getColumnCount() {
        return 3;
    }

    public void setDataModel(EnumerationData ed) {

        dataEnum.setEnumeration(ed);

    }

    protected PropertyChangeListener getPropertyListener() {
        return new EnumerationTablePropertyChangeListener();
    }

    public int getRowCount() {
        return dataEnum.getSize();
    }

    public Class getColumnClass(int row, int column) {
        return dataEnum.getElementValue(row).getClass();
    }

    public Object getValueAt(int row, int col) {
        if (col == 0) //name col
        {
            return dataEnum.getElementName(row);
        } else if (col == 1)
            return dataEnum.getElementValue(row);
        else if (col == 2)
            return dataEnum.getElementValue(row).getClass().getName();

        return null;
    }


    public String getColumnName(int column) {
        if (column == 0)
            return "name";
        else if (column == 1)
            return "value";
        else if (column == 2)
            return "type";
        else
            return null;
    }

    public void setValueAt(Object value, int row, int col) {

        if (col == 0) {
            dataEnum.setElementName(row, value.toString());

        } else if (col == 1) {
            if (value instanceof Boolean)
                dataEnum.setElementValue(row, value);
            else
            //if (value.getClass().getName().equals(dataEnum.getElementValue(row).getClass().getName()))//direct calling or string object only
            //{
            //	dataEnum.setElementValue(row, value);
            //	return;
            //} else if (value instanceof String) //from the editor, and must be only for integer,boolean or real
            {
                String s = dataEnum.getElementValue(row).getClass().getName().toLowerCase();
                if (s.equals("java.lang.integer")) {
                    Integer i;
                    try {
                        Double d = Double.valueOf((String) value);
                        i = new Integer(d.intValue());
                    } catch (Exception ee) {
                        System.out.println("invalid input");
                        return;
                    }
                    dataEnum.setElementValue(row, i);
                    return;
                } else if (s.equals("java.lang.double")) {
                    Double i;
                    try {
                        i = Double.valueOf((String) value);
                    } catch (Exception ee) {
                        System.out.println("invalid input");
                        return;
                    }
                    dataEnum.setElementValue(row, i);

                    return;
                } else if (s.equals("java.lang.string")) {

                    dataEnum.setElementValue(row, value);

                    return;
                }
                //else
                //	System.out.println("invalid input");
            }
        }
    }

    public boolean isCellEditable(int row, int col) {
        if (col == 0 || col == 1)
            return true;
        else
            return false;

    }

    public void removeRow(int index) {
        dataEnum.removeElementAt(index);

    }

    public void moveRow(int startIndex, int endIndex, int toIndex) {
        if ((startIndex < 0) || (startIndex >= getRowCount()))
            throw new ArrayIndexOutOfBoundsException(startIndex);
        if ((endIndex < 0) || (endIndex >= getRowCount()))
            throw new ArrayIndexOutOfBoundsException(endIndex);
        if (startIndex > endIndex)
            throw new ArrayIndexOutOfBoundsException();

        if ((startIndex <= toIndex) && (toIndex <= endIndex))
            return;                     // Nothing to move

        boolean shift = toIndex < startIndex;

        // Do the move by first removing the row, then reinserting it
        for (int i = startIndex; i <= endIndex; i++) {
            String tempName = dataEnum.getElementName(i);
            Object tempValue = dataEnum.getElementValue(i);
            dataEnum.removeElementAt(i);
            dataEnum.insertElementAt(toIndex, tempName, tempValue);

            if (shift)
                toIndex++;
        }

    }


    public void addRow(Object value) {

        dataEnum.addElement(value);
    }

    public void insertRow(int index, Object value) {

        dataEnum.insertElementAt(index, value);
    }

    // return null, use per type of class
    public TableCellRenderer getCellRenderer(int row, int column) {
        //if(column==1)
        //    return null;
        return new DefaultTableCellRenderer();
    }

    public TableCellEditor getCellEditor(int row, int column) {
        if (column == 1 && dataEnum.getElementValue(row).getClass() == Boolean.class)
            return null;
        return new TextCellEditor();
    }

    protected class EnumerationTablePropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String property = e.getPropertyName();
            Object newValue = e.getNewValue();
            if (property.equals(DomeEnumeration.SIZE)) {//repaint whole table
                fireTableStructureChanged();
            } else if (property.equals(DomeEnumeration.ELEMENTNAME)) {//repaint that cell
                int index = ((Integer) newValue).intValue();
                fireTableCellUpdated(index, 0);
            } else if (property.equals(DomeEnumeration.ELEMENTVALUE)) {//repaint that cell
                int index = ((Integer) newValue).intValue();
                fireTableCellUpdated(index, 1);
            } else if (property.equals(DomeEnumeration.ENUMERATION)) {//repaint the whole table
                fireTableStructureChanged();
            }
        }
    }
}

