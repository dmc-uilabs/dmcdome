// DomeFileTableObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem;

public class DomeFileTableObject extends FileSystemObjectTableObject
{
	protected DomeFile domeFile;

	public DomeFileTableObject(DomeFile domeFile)
	{
		super(domeFile);
		this.domeFile = domeFile;
	}

	public Object getValueAt(int column)
	{
		if (column == 1)
			return domeFile.getModified();
		else if (column == 2)
			return new Double(domeFile.getVersion());
		else if (column == 3)
			return domeFile.getDescription();
		return super.getValueAt(column);
	}

}
