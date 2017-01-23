// StringVector.java
package mit.cadlab.dome3.util;

import java.util.NoSuchElementException;
import java.util.Vector;

public class StringVector implements Cloneable
{

	protected Vector delegate;

	public StringVector()
	{
		delegate = new Vector();
	}

	public StringVector(String[] strings)
	{
		this();
		addAll(strings);
	}

	public StringVector(Object[] strings)
	{
		this();
		addAll(strings);
	}

	public StringVector(Vector strings)
	{
		this();
		addAll(strings);
	}

	protected StringVector(StringVector sv)
	{
		delegate = (Vector) sv.delegate.clone();
	}

	public void add(int index, String s)
	{
		delegate.add(index, s);
	}

	public boolean add(String s)
	{
		return delegate.add(s);
	}

	public void addAll(String[] strings)
	{
		for (int i = 0; i < strings.length; ++i) {
			delegate.add(strings[i]);
		}
	}

	public void addAll(Object[] strings)
	{
		for (int i = 0; i < strings.length; ++i) {
			delegate.add((strings[i] == null) ? null : strings[i].toString());
		}
	}

	public void addAll(Vector strings)
	{
		for (int i = 0; i < strings.size(); ++i) {
			Object str = strings.elementAt(i);
			delegate.add((str == null) ? null : str.toString());
		}
	}

	public void addElement(String s)
	{
		delegate.addElement(s);
	}

	public void clear()
	{
		delegate.clear();
	}

	public Object clone()
	{
		return new StringVector(this);
	}

	public boolean contains(String s)
	{
		return delegate.contains(s);
	}

	public String elementAt(int index)
	{
		return (String) delegate.elementAt(index);
	}

	public StringEnumeration elements()
	{
		return new StringEnumeration()
		{
			int count = 0;

			public boolean hasMoreElements()
			{
				return count < delegate.size();
			}

			public Object nextElement()
			{
				synchronized (StringVector.this) {
					if (count < delegate.size()) {
						return delegate.elementAt(count++);
					}
				}
				throw new NoSuchElementException("StringVector Enumeration");
			}

			public int countStrings()
			{
				int stringsLeft = delegate.size() - count;
				return (stringsLeft > 0) ? stringsLeft : 0;
			}

			public String nextString()
			{
				synchronized (StringVector.this) {
					if (count < delegate.size()) {
						return (String) delegate.elementAt(count++);
					}
				}
				throw new NoSuchElementException("StringVector Enumeration");
			}
		};
	}

	public boolean equals(Object o)
	{
		return (o instanceof StringVector) && (this.delegate.equals(((StringVector) o).delegate));
	}

	public String firstElement()
	{
		return (String) delegate.firstElement();
	}

	public String get(int index)
	{
		return (String) delegate.get(index);
	}

	public int hashCode()
	{
		return delegate.hashCode();
	}

	public int indexOf(String s)
	{
		return delegate.indexOf(s);
	}

	public int indexOf(String s, int index)
	{
		return delegate.indexOf(s, index);
	}

	public void insertElementAt(String s, int index)
	{
		delegate.insertElementAt(s, index);
	}

	public boolean isEmpty()
	{
		return delegate.isEmpty();
	}

	public String lastElement()
	{
		return (String) delegate.lastElement();
	}

	public int lastIndexOf(String s)
	{
		return delegate.lastIndexOf(s);
	}

	public int lastIndexOf(String s, int index)
	{
		return delegate.lastIndexOf(s, index);
	}

	public String remove(int index)
	{
		return (String) delegate.remove(index);
	}

	public boolean remove(String s)
	{
		return delegate.remove(s);
	}

	public void removeAllElements()
	{
		delegate.removeAllElements();
	}

	public boolean removeElement(String s)
	{
		return delegate.removeElement(s);
	}

	public void removeElementAt(int index)
	{
		delegate.removeElementAt(index);
	}

	public String set(int index, String s)
	{
		return (String) delegate.set(index, s);
	}

	public void setElementAt(String s, int index)
	{
		delegate.setElementAt(s, index);
	}

	public int size()
	{
		return delegate.size();
	}

	public String[] toArray()
	{
		String[] a = new String[delegate.size()];
		delegate.copyInto(a);
		return a;
	}

	public Vector toVector()
	{
		return (Vector) delegate.clone();
	}

	public String toString()
	{
		return delegate.toString();
	}

}
