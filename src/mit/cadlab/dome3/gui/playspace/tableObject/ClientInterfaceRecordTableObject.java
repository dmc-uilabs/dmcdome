// FileSystemObjectTableObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.playspace.tableObject;

import mit.cadlab.dome3.swing.table.AbstractTableObject;
import mit.cadlab.dome3.gui.fileSystem.FileSystemObject;
import mit.cadlab.dome3.network.client.objectrecord.ClientModelRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientInterfaceRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientObjectRecord;
import mit.cadlab.dome3.network.client.objectrecord.ClientAnalysisToolInterfaceRecord;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

public class ClientInterfaceRecordTableObject extends AbstractTableObject
{
	protected ClientObjectRecord fObj;

	public ClientInterfaceRecordTableObject(ClientInterfaceRecord fObj)
	{
		super(fObj);
		this.fObj = fObj;
	}

    public ClientInterfaceRecordTableObject(ClientAnalysisToolInterfaceRecord fObj)
    {
        super(fObj);
        this.fObj = fObj;
    }

    public Object getValueAt(int column)
	{
		if (column == 0)
			return fObj.getName(); // doesn't matter if in treetable, will return tree value
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

}
