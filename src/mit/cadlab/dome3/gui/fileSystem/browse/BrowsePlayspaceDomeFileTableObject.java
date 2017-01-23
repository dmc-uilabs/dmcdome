// DomeFileTableObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.browse;

import mit.cadlab.dome3.gui.fileSystem.FileSystemObjectTableObject;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;

public class BrowsePlayspaceDomeFileTableObject extends FileSystemObjectTableObject
{
	protected DomeFile domeFile;

	public BrowsePlayspaceDomeFileTableObject(DomeFile domeFile)
	{
		super(domeFile);
		this.domeFile = domeFile;
	}

	public Object getValueAt(int column)
	{

		if (column == 2) {
			String desc = domeFile.getDescription();
			return desc;
		} else if (column == 1) {
			//playspace.getMembers
			//show number of acive members in the playspace
		}


		/*if(domeFile.getType().equalsIgnoreCase("model")){
			if (column == 2){
				return domeFile.getDescription();
			}
		}
		else if (domeFile.getType().equalsIgnoreCase("playspace")) {

			
			if (column == 2) {
				return domeFile.getDescription();
			}
		}
		else{

		}*/
		return super.getValueAt(column);
	}

}
