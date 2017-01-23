// OrderedHashMap.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util;

import java.util.*;

public class OrderedHashMap extends HashMap
{
	// to do, LRU map?
	private DSet keys = new DSet();

	public OrderedHashMap()
	{
		super();
	}

	public OrderedHashMap(int initialCapacity)
	{
		super(initialCapacity);
	}

	public OrderedHashMap(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}

	public OrderedHashMap(Map m)
	{
		super(m);
		keys = new DSet(m.keySet());
	}

	public void clear()
	{
		super.clear();
		keys.clear();
	}

	public Object clone()
	{
		return new OrderedHashMap(this);
	}

	public Object put(Object key, Object value)
	{
		Object oldObject = super.put(key, value);
		if (!keys.contains(key))
			keys.add(key);
		return oldObject;
	}

	// putAll calls put...

	public Object remove(Object key)
	{
		Object oldObject = super.remove(key);
		keys.remove(key);
		return oldObject;
	}

	public Object elementAt(int i) {
		if (i < 0 || i > keys.size())
			throw new IllegalArgumentException("i=" + i);
		return super.get(keys.get(i));
	}

	public Object remove(int i) {
		if (i<0 || i>keys.size())
			throw new IllegalArgumentException("i="+i);
		return remove(keys.get(i));
	}

	public Set keySet()
	{
		return new HashSet(keys);
	}

	public List keyList()
	{
		return Collections.unmodifiableList(keys);
	}

	public Collection values()
	{
		return new AbstractList()
		{
			public Iterator iterator()
			{
				return new Iterator()
				{
					Iterator keyIterator = keys.iterator();

					public boolean hasNext()
					{
						return keyIterator.hasNext();
					}

					public Object next()
					{
						return OrderedHashMap.this.get(keyIterator.next());
					}

					public void remove()
					{
						throw new UnsupportedOperationException();
					}
				};
			}

			public Object get(int index)
			{
				return OrderedHashMap.this.get(keys.get(index));
			}

			public int size()
			{
				return OrderedHashMap.this.size();
			}

			public boolean contains(Object o)
			{
				return containsValue(o);
			}

			public void clear()
			{
				OrderedHashMap.this.clear();
			}
		};
	}

	public Set entrySet()
	{
		throw new UnsupportedOperationException();
	}

	public String toString()
	{
		if (size() == 0) {
			return "{ }";
		} else if (size() == 1) {
			Object key = keys.get(0);
			return "{" + key + " -> " + get(key) + "}";
		} else { // more than one key
			Iterator it = keys.iterator();
			Object key = it.next();
			StringBuffer sb = new StringBuffer("{ " + key + " -> " + get(key));
			while (it.hasNext()) {
				key = it.next();
				sb.append(",\n  " + key + " -> " + get(key));
			}
			sb.append("\n}");
			return sb.toString();
		}
	}

	// Listener support
	public void addKeySetListener(DListListener l)
	{
		keys.addDListListener(l);
	}

	public void removeKeySetListener(DListListener l)
	{
		keys.removeDListListener(l);
	}
}
