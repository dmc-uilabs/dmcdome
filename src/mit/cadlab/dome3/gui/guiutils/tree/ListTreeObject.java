// ListTreeObject.java
package mit.cadlab.dome3.gui.guiutils.tree;

import mit.cadlab.dome3.swing.tree.DefaultTreeObject;
import mit.cadlab.dome3.util.DArrayList;

import java.util.List;

/**
 * Tree object for lists of Dome Objects
 */
public class ListTreeObject extends DefaultTreeObject
{

	public ListTreeObject(List data)
	{
		super(data, true);
		if (data == null)
			throw new NullPointerException("ListTreeObject - null data");
		if (data instanceof DArrayList)
			((DArrayList) data).addDListListener(new TreeObjectDListListener());
	}

	// Tree support for ObjectTreeNode
	public String getTreeValue()
	{
		return "";
	}

	public List getChildren()
	{
		return (List) data;
	}

}
