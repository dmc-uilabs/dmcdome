// FolderTreeObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem;

import mit.cadlab.dome3.icons.DomeIcons;

import javax.swing.Icon;
import java.util.List;

public class FolderTreeObject extends FileSystemObjectTreeObject
{
	protected Folder folder;

	public FolderTreeObject(Folder folder)
	{
		super(folder, folder.getContent());
		this.folder = folder;
	}

	// Support for custom icons for each object
	// Default implementation returns look and feel icons.
	public Icon getIcon(int itemState)
	{
		if (folder.getFolderType().equals(Folder.USER_HOME))
			return DomeIcons.getIcon(DomeIcons.USER_SMALL);
		else if (folder.getFolderType().equals(Folder.GROUP_HOME))
			return DomeIcons.getIcon(DomeIcons.GROUP_SMALL);
		else // Folder type or unknown type; return default system icons
			return super.getIcon(itemState);
	}

	public List getChildren()
	{
		return folder.getContent();
	}

}
