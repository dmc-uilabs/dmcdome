// DomeFileTableObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.browse;

import mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTableObject;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;

public class BrowseModelDomeFileTableObject extends FileSystemObjectTableObject
{
	protected DomeFile domeFile;

	public BrowseModelDomeFileTableObject(DomeFile domeFile)
	{
		super(domeFile);
		this.domeFile = domeFile;
	}

	public Object getValueAt(int column)
	{
		if (domeFile.getType().equalsIgnoreCase("model") ||
		        domeFile.getType().equalsIgnoreCase("interface") ||
		            domeFile.getType().equalsIgnoreCase("project") ||
                        domeFile.getType().equalsIgnoreCase(DomeFile.ANALYSIS_TOOL_TYPE))
        {
			if (column == 1) {
				return new String("ver " + new Integer((int) domeFile.getVersion()));
				//return domeFile.getModified();
			} else if (column == 2) {
				return domeFile.getDescription();
				//return new Double(domeFile.getVersion());
			} else if (column == 3) {
				//return domeFile.getDescription();
			}

		} else if (domeFile.getType().equalsIgnoreCase("playspace")) {
			System.out.println("BrowseModelDomeFileTableObject: Should not be here...");
		} else {

		}
		return super.getValueAt(column);
	}

}
