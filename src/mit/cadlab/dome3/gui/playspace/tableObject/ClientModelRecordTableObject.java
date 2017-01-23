// FileSystemObjectTableObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.playspace.tableObject;

import mit.cadlab.dome3.swing.table.AbstractTableObject;
import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.network.client.objectrecord.ClientModelRecord;
import mit.cadlab.dome3.objectmodel.model.ClientModelRuntime;
import mit.cadlab.dome3.network.client.objectrecord.ClientProjectRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientObjectRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientAnalysisToolRecord;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class ClientModelRecordTableObject extends AbstractTableObject
{
	protected ClientObjectRecord fObj;
	protected TableCellRenderer statusRenderer = new StatusTableCellRenderer();
	private String modelStatus = "";
	public static final String STATUS = "status: ";
	public static final String EMPTYSTRING = "";

	public ClientModelRecordTableObject(ClientModelRecord fObj)
	{
		super(fObj);
		this.fObj = fObj;
		ClientModelRuntime m = fObj.getModel();
		m.addPropertyChangeListener(new ModelStatusListener());
	}

	public ClientModelRecordTableObject(ClientProjectRecord fObj)
	{
		super(fObj);
		this.fObj = fObj;
		//ClientProjectRuntime m = this.fObj.getModel();
		//m.addPropertyChangeListener(new ModelStatusListener());
	}

    public ClientModelRecordTableObject(ClientAnalysisToolRecord fObj)
    {
        super(fObj);
        this.fObj = fObj;
    }

	public Object getValueAt(int column)
	{
		if (column == 0) {
			return ""; // doesn't matter if in treetable, will return tree value
		} else if (column == 2) {
			return modelStatus;
		} else if (column == 3) {
			return fObj.getDescription();
		}
		return null;
	}

	public TableCellRenderer getCellRenderer(int row, int column)
	{
		return new DefaultTableCellRenderer();
	}

	public Class getClassAt(int column)
	{
		if (column == 1) {
			return String.class;
		} else if (column == 2) {
			return String.class;
		} else
			return null;

	}

	public TableCellRenderer getRendererAt(int column)
	{
		if (column == 2) { //status column
			return statusRenderer;
		} else
			return null;
	}

	public static class StatusTableCellRenderer implements TableCellRenderer
	{
		private JLabel statusLabel = Templates.makeLabel("");

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus,
		                                               int rowIndex, int vColIndex)
		{
			// Configure the component with the specified value
			statusLabel.setText(value.toString());
			return statusLabel;
		}

		// The following methods override the defaults for performance reasons
		public void validate()
		{
		}

		public void revalidate()
		{
		}

		protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
		{
		}

		public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue)
		{
		}
	}

	public class ModelStatusListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String propName = e.getPropertyName();
			if (propName.equals(ClientModelRuntime.MODELSTATUS)) {
				String newStatus = (String) e.getNewValue();
				if (newStatus.equals(EMPTYSTRING)) {
					modelStatus = newStatus;
				} else {
					modelStatus = STATUS + newStatus;
				}
			}
			ClientModelRecordTableObject.this.fireTableObjectChanged();
		}
	}

}
