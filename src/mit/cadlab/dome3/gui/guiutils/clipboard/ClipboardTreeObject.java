// ClipboardTreeObject.java
package mit.cadlab.dome3.gui.guiutils.clipboard;

import mit.cadlab.dome3.swing.tree.DefaultTreeObject;

import java.util.List;

public class ClipboardTreeObject extends DefaultTreeObject
{

	public ClipboardTreeObject(Clipboard cb)
	{
		super(cb, true);
		if (cb == null) {
			throw new IllegalArgumentException("ClipboardTreeObject - null Clipboard");
		}
		((Clipboard) cb).addClipboardListener(new TreeObjectDListListener());
	}

	// Tree support for ObjectTreeNode
	public String getTreeValue()
	{
		return "Clipboard";
	}

	public void setTreeValue(String value)
	{
		// do nothing
	}

	public List getChildren()
	{
		return ((Clipboard) data).getSelections();
	}

}
