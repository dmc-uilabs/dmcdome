// AbstractTableObject.java
package mit.cadlab.dome3.swing.table;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Implementation of TableObject that takes care of listeners
 * and provides basic implementation of most methods.
 */
public abstract class AbstractTableObject implements TableObject, TableData
{

	// to be implemented by subclasses
	// public Object getValueAt(int column)

	protected Object object;
	protected boolean[] isEditable = null;
	protected Hashtable listeners = new Hashtable();
	// supports the same table object in a table multiple times

	public AbstractTableObject(Object obj)
	{
		this.object = obj;
	}

	public AbstractTableObject(Object obj, boolean[] isEditable)
	{
		this(obj);
		this.isEditable = isEditable;
	}

	public void setEditable(boolean[] isEditable)
	{
		this.isEditable = isEditable;
		fireTableObjectChanged();
	}

	// TableData interface
	public TableObject getTableObject()
	{
		return this;
	}

	// TableObject interface
	public boolean isEditableAt(int column)
	{
		if (isEditable == null) return false;
		try {
			return isEditable[column];
		} catch (IndexOutOfBoundsException ex) {
			return false;
		}
	}

	public void setValueAt(Object value, int column)
	{
	}

	public Class getClassAt(int column)
	{
		return null;
	}

	public TableCellRenderer getRendererAt(int column)
	{
		return null;
	}

	public TableCellEditor getEditorAt(int column)
	{
		return null;
	}

	// TreeObject change notification support
	public void addTableObjectListener(TableObjectListener l)
	{
		if (listeners.containsKey(l)) { // already in table
			int count = ((Integer) listeners.get(l)).intValue();
			listeners.put(l, new Integer(count + 1)); // increment count
		} else { // add to table
			listeners.put(l, new Integer(1));
		}
	}

	public void removeTableObjectListener(TableObjectListener l)
	{
		if (listeners.containsKey(l)) { // in table
			int count = ((Integer) listeners.get(l)).intValue();
			if (count == 1) { // last one left, so remove
				listeners.remove(l);
			} else {
				listeners.put(l, new Integer(count - 1)); // decrement count
			}
		}
	}

	public void fireTableObjectChanged()
	{
		TableObjectEvent event = new TableObjectEvent(this);
		synchronized (listeners) {
			Enumeration e = listeners.keys();
			while (e.hasMoreElements()) {
				((TableObjectListener) e.nextElement()).tableObjectChanged(event);
			}
		}
	}

	public void fireTableObjectChanged(int column)
	{
		TableObjectEvent event = new TableObjectEvent(this, column);
		synchronized (listeners) {
			Enumeration e = listeners.keys();
			while (e.hasMoreElements()) {
				((TableObjectListener) e.nextElement()).tableObjectChanged(event);
			}
		}
	}

}
