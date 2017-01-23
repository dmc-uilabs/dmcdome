// FileSystemObjectTreeObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.fileSystem;

import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.swing.tree.DefaultGuiTreeObject;
import mit.cadlab.dome3.util.DArrayList;

public class FileSystemObjectTreeObject extends DefaultGuiTreeObject
{
	protected FileSystemObject fObj;

	public FileSystemObjectTreeObject(FileSystemObject fObj)
	{
		super(fObj);
		this.fObj = fObj;
	}

	public FileSystemObjectTreeObject(FileSystemObject fObj, boolean allowsChildren)
	{
		super(fObj, allowsChildren);
		this.fObj = fObj;
	}

	public FileSystemObjectTreeObject(FileSystemObject fObj, DArrayList children)
	{
		super(fObj, children);
		this.fObj = fObj;
	}

	public String getTreeValue()
	{
		return fObj.getName();
	}

	protected void makeGui()
	{
		RunMode.open_in_browser();
	}

	public Object getData() {
		return fObj;
	}

}
