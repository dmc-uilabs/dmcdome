// DListDListModel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.swing;

import mit.cadlab.dome3.util.DListEvent;

import java.util.Iterator;
import javax.swing.AbstractListModel;

public class DListDListModel extends AbstractListModel
{

	protected mit.cadlab.dome3.util.DArrayList dataList;

	public DListDListModel(mit.cadlab.dome3.util.DArrayList list)
	{
		this.dataList = list;
		dataList.addDListListener(new DListDListListener());
	}

	// ListModel interface
	public int getSize()
	{
		return dataList.size();
	}

	public Object getElementAt(int index)
	{
		return dataList.get(index);
	}

	// convenience functions
	// list is modified by owner of list; just keep reference to it here

	public int size()
	{
		return dataList.size();
	}

	public boolean isEmpty()
	{
		return dataList.isEmpty();
	}

	public Iterator iterator()
	{
		return dataList.iterator();
	}

	public boolean contains(Object elem)
	{
		return dataList.contains(elem);
	}

	public int indexOf(Object elem)
	{
		return dataList.indexOf(elem);
	}

	public int indexOf(Object elem, int index)
	{
		return dataList.indexOf(elem, index);
	}

	public int lastIndexOf(Object elem)
	{
		return dataList.lastIndexOf(elem);
	}

	public Object get(int index)
	{
		return dataList.get(index);
	}

	public void shiftUp(int index)
	{
		dataList.shiftLeft(index);
	}

	public void shiftDown(int index)
	{
		dataList.shiftRight(index);
	}

	public Object[] toArray()
	{
		return dataList.toArray();
	}

	public Object[] toArray(Object[] a)
	{
		return dataList.toArray(a);
	}

	public String toString()
	{
		return dataList.toString();
	}

	class DListDListListener implements mit.cadlab.dome3.util.DListListener
	{
		public void intervalChanged(DListEvent e)
		{
			DListDListModel.this.fireContentsChanged(DListDListModel.this, e.getFirstIndex(), e.getLastIndex());
		}

		public void intervalAdded(DListEvent e)
		{
			DListDListModel.this.fireIntervalAdded(DListDListModel.this, e.getFirstIndex(), e.getLastIndex());
		}

		public void intervalRemoved(DListEvent e)
		{
			DListDListModel.this.fireIntervalRemoved(DListDListModel.this, e.getFirstIndex(), e.getLastIndex());
		}

		public void itemsRemoved(DListEvent e)
		{
			DListDListModel.this.fireContentsChanged(DListDListModel.this, e.getFirstIndex(), e.getLastIndex());
		}

		public void itemsReplaced(DListEvent e)
		{
			DListDListModel.this.fireContentsChanged(DListDListModel.this, e.getFirstIndex(), e.getLastIndex());
		}
	}
}
