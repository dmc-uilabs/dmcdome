// DomeFileTableObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.deploy;

import mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTableObject;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;

public class DeployDomeFileTableObject extends FileSystemObjectTableObject
{
	protected DomeFile domeFile;

	public DeployDomeFileTableObject(DomeFile domeFile)
	{
		super(domeFile);
		this.domeFile = domeFile;
	}

	public Object getValueAt(int column)
	{
		if (column == 1) {
			return new String("ver " + (new Double(domeFile.getVersion())).intValue());
		} else if (column == 2) {
			return domeFile.getDescription();
		} else if (column == 3)
			return domeFile.getModified();
		return super.getValueAt(column);
	}

}
