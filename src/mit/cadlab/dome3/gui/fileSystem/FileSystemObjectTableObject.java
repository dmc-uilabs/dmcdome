// FileSystemObjectTableObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem;

import mit.cadlab.dome3.swing.table.AbstractTableObject;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

public class FileSystemObjectTableObject extends AbstractTableObject
{
	protected FileSystemObject fObj;

	public FileSystemObjectTableObject(FileSystemObject fObj)
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
