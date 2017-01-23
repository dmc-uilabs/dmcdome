// OrderedSet.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class OrderedSet extends ArrayList implements Set
{

	public OrderedSet()
	{
		super();
	}

	public OrderedSet(Collection c)
	{
		super(c.size());
		addAll(c);
	}

	public OrderedSet(int initialCapacity)
	{
		super(initialCapacity);
	}

	public void add(int index, Object element)
	{
		_add(index, element);
	}

	private boolean _add(int index, Object element)
	{
		if (this.contains(element)) return false;
		super.add(index, element);
		return true;
	}

	public boolean add(Object o)
	{
		if (this.contains(o)) return false;
		return super.add(o);
	}

	public boolean addAll(Collection c)
	{
		int origSize = this.size();
		Iterator it = c.iterator();
		while (it.hasNext()) {
			add(it.next());
		}
		return this.size() == (origSize + c.size());
	}

	public boolean addAll(int index, Collection c)
	{
		int origSize = this.size();
		Iterator it = c.iterator();
		while (it.hasNext()) {
			if (_add(index, it.next())) {
				index++;
			}
		}
		return this.size() == (origSize + c.size());
	}

	public Object set(int index, Object element)
	{
		if (this.contains(element)) return null;
		return super.set(index, element);
	}
}
