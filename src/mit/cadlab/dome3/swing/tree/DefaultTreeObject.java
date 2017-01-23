// DefaultTreeObject.java
package mit.cadlab.dome3.swing.tree;

import mit.cadlab.dome3.util.DArrayList;
import mit.cadlab.dome3.util.DListEvent;
import mit.cadlab.dome3.util.DListListener;

import java.util.Collections;
import java.util.List;

public class DefaultTreeObject extends AbstractTreeObject
{

	protected Object data;
	protected boolean allowsChildren;

	public DefaultTreeObject(Object data)
	{ // leaf
		this.data = data;
		this.allowsChildren = false;
	}

	public DefaultTreeObject(Object data, boolean allowsChildren)
	{
		this.data = data;
		this.allowsChildren = allowsChildren;
	}

	public DefaultTreeObject(Object data, DArrayList children)
	{
		this.data = data;
		this.allowsChildren = true;
		children.addDListListener(new TreeObjectDListListener());
	}

	// Tree support for ObjectTreeNode
	public boolean allowsChildren()
	{
		return allowsChildren;
	}

	public String getTreeValue()
	{
		return (data == null) ? "" : data.toString();
	}

	public void setTreeValue(String value)
	{
	}

	// override with application specific implementation
	public List getChildren()
	{
		return Collections.EMPTY_LIST;
	}

	public List getChildren(String view)
	{
		return Collections.EMPTY_LIST;
	}

	protected class TreeObjectDListListener implements DListListener
	{

		public TreeObjectDListListener()
		{
		}

		public void intervalChanged(DListEvent e)
		{
			//System.out.println("TreeObjectDListListener: "+e);
			fireChildrenChanged(makeIntArray(e.getFirstIndex(), e.getLastIndex()));
		}

		public void intervalAdded(DListEvent e)
		{
			//System.out.println("TreeObjectDListListener: "+e);
			fireChildrenAdded(makeIntArray(e.getFirstIndex(), e.getLastIndex()));
		}

		public void intervalRemoved(DListEvent e)
		{
			//System.out.println("TreeObjectDListListener: "+e);
			fireChildrenRemoved(makeIntArray(e.getFirstIndex(), e.getLastIndex()));
		}

		public void itemsRemoved(DListEvent e)
		{
			//System.out.println("TreeObjectDListListener: "+e);
			fireChildrenRemoved(e.getIndices());
		}

		public void itemsReplaced(DListEvent e)
		{
			//System.out.println("TreeObjectDListListener: "+e);
			fireChildrenChanged(e.getIndices());
		}

		protected int[] makeIntArray(int i1, int i2)
		{
			int[] a = new int[i2 - i1 + 1];
			for (int i = 0; i < a.length; ++i)
				a[i] = i + i1;
			return a;
		}

	} // end TreeObjectDListListener

}
