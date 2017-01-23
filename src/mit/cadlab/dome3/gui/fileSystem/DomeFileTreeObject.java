// FileTreeObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem;

import mit.cadlab.dome3.icons.DomeIcons;

import javax.swing.Icon;
import java.util.List;

public class DomeFileTreeObject extends FileSystemObjectTreeObject
{
	protected DomeFile file;

	public DomeFileTreeObject(DomeFile file)
	{
		super(file, file.getContent());
		this.file = file;
	}

	protected DomeFileTreeObject(DomeFile file, boolean allowsChildren)
	{
		super(file, allowsChildren);
		this.file = file;
	}

	// Support for custom icons for each object
	// Default implementation returns look and feel icons.
	public Icon getIcon(int itemState)
	{
		if (file.getFileType().equals(DomeFile.MODEL_TYPE))
			return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.MODEL_OPEN : DomeIcons.MODEL);
		else if (file.getFileType().equals(DomeFile.PLAYSPACE_TYPE))
			return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.PLAYSPACE_OPEN : DomeIcons.PLAYSPACE);
		else if (file.getFileType().equals(DomeFile.INTERFACE_TYPE))
			return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.INTERFACE_OPEN : DomeIcons.INTERFACE);
		else if (file.getFileType().equals(DomeFile.PROJECT_TYPE))
			return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.PROJECT_OPEN : DomeIcons.PROJECT);
        else if (file.getFileType().equals(DomeFile.ANALYSIS_TOOL_TYPE))
            return DomeIcons.getIcon(itemState == OPEN_ICON ? DomeIcons.ANALYSIS_TOOL_OPEN : DomeIcons.ANALYSIS_TOOL);
		else // Folder type or unknown type; return default system icons
			return super.getIcon(itemState);
		//TODO set the other types of icons: user, group, etc
	}

	// override with application specific implementation
	public List getChildren()
	{
		return file.getContent();
	}
}
