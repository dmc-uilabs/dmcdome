// DomeFileTableObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.browse;

import mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTableObject;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;

public class BrowseDomeFileTableObject extends FileSystemObjectTableObject
{
	protected DomeFile domeFile;

	public BrowseDomeFileTableObject(DomeFile domeFile)
	{
		super(domeFile);
		this.domeFile = domeFile;
	}

	public Object getValueAt(int column)
	{
		if (domeFile.getType().equalsIgnoreCase("model")) {
			if (column == 1) {
				return domeFile.getModified();
			} else if (column == 2) {
				return new Integer((int) domeFile.getVersion());
				//return new Integer((new Double(domeFile.getVersion())).intValue());
			} else if (column == 3) {
				return domeFile.getDescription();
			}
		} else if (domeFile.getType().equalsIgnoreCase("playspace")) {

		} else {

		}
		return super.getValueAt(column);
	}

}
