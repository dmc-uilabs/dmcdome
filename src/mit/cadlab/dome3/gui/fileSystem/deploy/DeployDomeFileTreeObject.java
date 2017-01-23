// DeployDomeFileTreeObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem.deploy;

import mit.cadlab.dome3.gui.fileSystem.DomeFileTreeObject;
import mit.cadlab.dome3.gui.fileSystem.DomeFile;
import mit.cadlab.dome3.swing.tree.DefaultTreeObject;

public class DeployDomeFileTreeObject extends DomeFileTreeObject
{
	public DeployDomeFileTreeObject(DomeFile file)
	{
		super(file, !file.getFileType().equals(DomeFile.INTERFACE_TYPE)); // interfaces can not be expanded
		if (allowsChildren())
			file.getContent().addDListListener(new DefaultTreeObject.TreeObjectDListListener());
	}

}
