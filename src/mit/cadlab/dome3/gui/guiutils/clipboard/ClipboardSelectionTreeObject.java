// ClipboardSelectionTreeObject.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.guiutils.clipboard;

import mit.cadlab.dome3.swing.tree.DefaultTreeObject;

import java.util.List;

public class ClipboardSelectionTreeObject extends DefaultTreeObject
{

	public ClipboardSelectionTreeObject(ClipboardSelection cs)
	{
		super(cs, true);
		if (cs == null) {
			throw new IllegalArgumentException("ClipboardSelectionTreeObject - null ClipboardSelection");
		}
		((ClipboardSelection) cs).addClipboardSelectionListener(new TreeObjectDListListener());
	}

	public ClipboardSelection getClipboardSelection()
	{
		return (ClipboardSelection) data;
	}

	// Tree support for ObjectTreeNode
	public String getTreeValue()
	{
		return ((ClipboardSelection) data).getName();
	}

	public void setTreeValue(String value)
	{
		((ClipboardSelection) data).setName(value);
	}

	public List getChildren()
	{
		return ((ClipboardSelection) data).getItems();
	}

}
