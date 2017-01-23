// DCollections.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.AbstractList;
import java.util.NoSuchElementException;

/**
 * DCollections provides a seamless immutable Collection or List view of two Collections or Lists.
 */
public class DCollections
{

	public static Collection unmodifiableCollection(Collection c1, Collection c2)
	{
		return new UnmodifiableCollection(c1, c2);
	}

	public static List unmodifiableList(List l1, List l2)
	{
		return new UnmodifiableList(l1, l2);
	}

	public static class UnmodifiableCollection implements Collection, Serializable
	{
		Collection c1,c2;

		UnmodifiableCollection(Collection c1, Collection c2)
		{
			if (c1 == null || c2 == null)
				throw new NullPointerException();
			this.c1 = c1;
			this.c2 = c2;
		}

		public int size()
		{
			return c1.size() + c2.size();
		}

		public boolean isEmpty()
		{
			return c1.isEmpty() && c2.isEmpty();
		}

		public boolean contains(Object o)
		{
			return c1.contains(o) || c2.contains(o);
		}

		public Object[] toArray()
		{
			Object[] c1Array = c1.toArray();
			Object[] c2Array = c2.toArray();
			Object[] c1c2Array = new Object[size()];
			System.arraycopy(c1Array, 0, c1c2Array, 0, c1Array.length);
			System.arraycopy(c2Array, 0, c1c2Array, c1Array.length, c2Array.length);
			return c1c2Array;
		}

		public Object[] toArray(Object[] a)
		{
			int size = size();
			Object[] c1Array = c1.toArray();
			Object[] c2Array = c2.toArray();
			if (a.length < size)
				a = (Object[]) java.lang.reflect.Array.newInstance(
				        a.getClass().getComponentType(), size);
			System.arraycopy(c1Array, 0, a, 0, c1Array.length);
			System.arraycopy(c2Array, 0, a, c1Array.length, c2Array.length);
			if (a.length > size)
				a[size] = null;
			return a;
		}

		public String toString()
		{
			if (c1.isEmpty())
				return c2.toString();
			if (c2.isEmpty())
				return c1.toString();
			String c1String = c1.toString();
			if (c1String.endsWith("]"))
				c1String = c1String.substring(0, c1String.length() - 1);
			String c2String = c2.toString();
			if (c2String.startsWith("["))
				c2String = c2String.substring(1);
			return c1String + ", " + c2String;
		}

		public Iterator iterator()
		{
			return new CollectionIterator();
		}

		protected class CollectionIterator implements Iterator
		{
			Iterator it;
			boolean isC1iterator = true;

			public CollectionIterator()
			{
				if (c1.isEmpty()) {
					it = c2.iterator();
					isC1iterator = false;
				} else {
					it = c1.iterator();
				}
			}

			public boolean hasNext()
			{
				return it.hasNext();
			}

			public Object next()
			{
				Object obj = it.next();
				if (isC1iterator && !it.hasNext()) {
					it = c2.iterator();
					isC1iterator = false;
				}
				return obj;
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		}

		public boolean add(Object o)
		{
			throw new UnsupportedOperationException();
		}

		public boolean remove(Object o)
		{
			throw new UnsupportedOperationException();
		}

		public boolean containsAll(Collection coll)
		{
			Collection bothCollections = new ArrayList(c1);
			bothCollections.addAll(c2);
			return bothCollections.containsAll(coll);
		}

		public boolean addAll(Collection coll)
		{
			throw new UnsupportedOperationException();
		}

		public boolean removeAll(Collection coll)
		{
			throw new UnsupportedOperationException();
		}

		public boolean retainAll(Collection coll)
		{
			throw new UnsupportedOperationException();
		}

		public void clear()
		{
			throw new UnsupportedOperationException();
		}
	}

	public static class UnmodifiableList extends UnmodifiableCollection implements List
	{
		protected List l1, l2;

		UnmodifiableList(List l1, List l2)
		{
			super(l1, l2);
			this.l1 = l1;
			this.l2 = l2;
		}

		public boolean equals(Object o)
		{
			return false;
		}

		public int hashCode()
		{
			return l1.hashCode() + l2.hashCode();
		}

		public Object get(int index)
		{
			if (index < l1.size())
				return l1.get(index);
			else if (index < size())
				return l2.get(index - l1.size());
			else
				throw new ArrayIndexOutOfBoundsException(index + " >= " + size());
		}

		public Object set(int index, Object element)
		{
			throw new UnsupportedOperationException();
		}

		public void add(int index, Object element)
		{
			throw new UnsupportedOperationException();
		}

		public Object remove(int index)
		{
			throw new UnsupportedOperationException();
		}

		public int indexOf(Object o)
		{
			int index = l1.indexOf(o);
			if (index != -1) // found
				return index;
			index = l2.indexOf(o);
			if (index == -1) // not found
				return index;
			// add length of first list to index
			return index + l1.size();
		}

		public int lastIndexOf(Object o)
		{
			int index = l2.lastIndexOf(o);
			if (index != -1) // found
				return index + l1.size();
			return l1.lastIndexOf(o);
		}

		public boolean addAll(int index, Collection c)
		{
			throw new UnsupportedOperationException();
		}

		public ListIterator listIterator()
		{
			return new TwoListIterator(0);
		}

		public ListIterator listIterator(int index)
		{
			return new TwoListIterator(index);
		}

		public List subList(int fromIndex, int toIndex)
		{
			throw new UnsupportedOperationException();
		}

		protected class TwoListIterator implements ListIterator
		{

			protected int index;

			public TwoListIterator(int index)
			{
				if (index < 0 || index >= size())
					throw new IndexOutOfBoundsException("Index: " + index);
				this.index = index;
			}

			public boolean hasNext()
			{
				return index < size();
			}

			public Object next()
			{
				if (index >= size())
					throw new NoSuchElementException();
				return get(index++);
			}

			public boolean hasPrevious()
			{
				return index > 0;
			}

			public Object previous()
			{
				if (index <= 0)
					throw new NoSuchElementException();
				return get(--index);
			}

			public int nextIndex()
			{
				return index;
			}

			public int previousIndex()
			{
				return index - 1;
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}

			public void set(Object o)
			{
				throw new UnsupportedOperationException();
			}

			public void add(Object o)
			{
				throw new UnsupportedOperationException();
			}
		}

	}

}