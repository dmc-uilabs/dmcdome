// DomeFileTableObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem;

import mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTableObject;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;

public class PlayspaceRunDomeFileTableObject extends FileSystemObjectTableObject
{
	protected DomeFile domeFile;

	public PlayspaceRunDomeFileTableObject(DomeFile domeFile)
	{
		super(domeFile);
		this.domeFile = domeFile;
	}

	public Object getValueAt(int column)
	{
		if (domeFile.getType().equalsIgnoreCase("model")) {
			if (column == 2) {
				return domeFile.getStatus();
			}
		} else if (domeFile.getType().equalsIgnoreCase("playspace")) {
			if (column == 2) {
				return domeFile.getStatus();
			}
		} else if (domeFile.getType().equalsIgnoreCase("project")) {
			if (column == 2) {
				return domeFile.getStatus();
			}
		} else {

		}
		return super.getValueAt(column);
	}

}
