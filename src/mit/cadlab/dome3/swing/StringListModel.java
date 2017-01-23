// StringListModel.java
package mit.cadlab.dome3.swing;

import mit.cadlab.dome3.util.StringVector;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

/*
 * This class implements the ListModel and ComboBoxModel interfaces.
 */

public class StringListModel extends AbstractListModel
        implements MutableComboBoxModel, java.io.Serializable, Cloneable
{

	protected StringVector delegate;
	protected String selectedString;

	public StringListModel()
	{
		delegate = new StringVector();
	}

	public StringListModel(String[] choices)
	{
		this();
		delegate.addAll(choices);
		if (delegate.size() > 0)
			selectedString = delegate.get(0);
	}

	public StringListModel(Object[] choices)
	{
		this();
		delegate.addAll(choices);
		if (delegate.size() > 0)
			selectedString = delegate.get(0);
	}

	public StringListModel(Vector choices)
	{
		this();
		delegate.addAll(choices);
		if (delegate.size() > 0)
			selectedString = delegate.get(0);
	}


	// ListModel interface
	public Object getElementAt(int index)
	{
		if (index >= 0 && index < delegate.size())
			return delegate.elementAt(index);
		else
			return null;
	}

	public int getSize()
	{
		return delegate.size();
	}


	// ComboBoxModel interface
	public Object getSelectedItem()
	{
		return selectedString;
	}

	public void setSelectedItem(Object obj)
	{
		if (obj != null &&
		        (selectedString == null ||
		        (selectedString != null && !selectedString.equals(obj)))) {
			selectedString = obj.toString();
			fireContentsChanged(this, -1, -1);
		}
	}

	public String getSelectedString()
	{
		return selectedString;
	}


	// MutableComboBoxModel
	public void addElement(Object obj)
	{
		delegate.addElement((obj == null) ? null : obj.toString());
		fireIntervalAdded(this, delegate.size() - 1, delegate.size() - 1);
		if (delegate.size() == 1 && selectedString == null && obj != null) {
			setSelectedItem(obj);
		}
	}

	public void insertElementAt(Object obj, int index)
	{
		delegate.insertElementAt((obj == null) ? null : obj.toString(), index);
		fireIntervalAdded(this, index, index);
	}

	public void removeElement(Object obj)
	{
		int index = delegate.indexOf((obj == null) ? null : obj.toString());
		if (index != -1) {
			removeElementAt(index);
		}
	}

	public void removeElementAt(int index)
	{
		if (getElementAt(index) == selectedString) {
			if (index == 0) {
				setSelectedItem(getSize() == 1 ? null : getElementAt(index + 1));
			} else {
				setSelectedItem(getElementAt(index - 1));
			}
		}
		delegate.removeElementAt(index);
		fireIntervalRemoved(this, index, index);
	}

	// StringVector interface
	public void addAll(String[] objs)
	{
		int index = delegate.size();
		delegate.addAll(objs);
		for (int i = 0; i < objs.length; ++i) {
			delegate.addElement(objs[i]);
		}
		fireIntervalAdded(this, index, index + objs.length - 1);
	}

	public void addAll(int index, String[] objs)
	{
		for (int i = 0; i < objs.length; ++i) {
			delegate.insertElementAt(objs[i], index + i);
		}
		fireIntervalAdded(this, index, index + objs.length - 1);
	}

	public void addAll(Collection objs)
	{
		int index = delegate.size();
		Iterator iterator = objs.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			delegate.addElement((obj == null) ? null : obj.toString());
		}
		fireIntervalAdded(this, index, index + objs.size() - 1);
	}

	public void addAll(int index, Collection objs)
	{
		Iterator iterator = objs.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			delegate.insertElementAt((obj == null) ? null : obj.toString(), index + (i++));
		}
		fireIntervalAdded(this, index, index + objs.size() - 1);
	}

	public void clear()
	{
		if (delegate.size() > 0) {
			int firstIndex = 0;
			int lastIndex = delegate.size() - 1;
			delegate.clear();
			selectedString = null;
			fireIntervalRemoved(this, firstIndex, lastIndex);
		}
	}

	public Object clone()
	{
		StringListModel clone = new StringListModel();
		clone.delegate = (StringVector) this.delegate.clone();
		clone.selectedString = selectedString;
		return clone;
	}

	public boolean contains(String s)
	{
		return delegate.contains(s) || (selectedString != null && selectedString.equals(s));
	}

	public String elementAt(int index)
	{
		return delegate.elementAt(index);
	}

	public boolean equals(Object o)
	{
		return (o instanceof StringListModel) &&
		        this.delegate.equals(((StringListModel) o).delegate) &&
		        this.selectedString.equals(((StringListModel) o).selectedString);
	}

	public String firstElement()
	{
		return delegate.firstElement();
	}

	public String get(int index)
	{
		if (index >= 0 && index < delegate.size())
			return delegate.get(index);
		else
			return null;
	}

	public int indexOf(String s)
	{
		return delegate.indexOf(s);
	}

	public int indexOf(String s, int index)
	{
		return delegate.indexOf(s, index);
	}

	public boolean isEmpty()
	{
		return delegate.isEmpty() && selectedString == null;
	}

	public String lastElement()
	{
		return delegate.lastElement();
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
		if (getElementAt(index) == selectedString) {
			if (index == 0) {
				setSelectedItem(getSize() == 1 ? null : getElementAt(index + 1));
			} else {
				setSelectedItem(getElementAt(index - 1));
			}
		}
		String removedObject = delegate.remove(index);
		fireIntervalRemoved(this, index, index);
		return removedObject;
	}

	public boolean remove(String s)
	{
		int index = delegate.indexOf(s);
		if (index != -1) {
			remove(index);
			return true;
		} else {
			return false;
		}
	}

	public void removeAllElements()
	{
		clear();
	}

	public void removeRange(int fromIndex, int toIndex)
	{
		int selectedIndex = indexOf(selectedString);
		for (int i = toIndex; i >= fromIndex; i--) {
			delegate.removeElementAt(i);
		}
		if (selectedIndex >= fromIndex && selectedIndex <= toIndex) {
			if (selectedIndex == 0) {
				setSelectedItem(getSize() == 0 ? null : getElementAt(0));
			} else {
				setSelectedItem(getElementAt(fromIndex - 1));
			}
		}
		fireIntervalRemoved(this, fromIndex, toIndex);
	}

	public String set(int index, String s)
	{
		int currentIndex = indexOf(selectedString);
		String replacedString = delegate.set(index, s);
		fireContentsChanged(this, index, index);
		if (index == currentIndex)
			setSelectedItem(s);
		return replacedString;
	}

	public void setElementAt(String s, int index)
	{
		int currentIndex = indexOf(selectedString);
		delegate.setElementAt(s, index);
		fireContentsChanged(this, index, index);
		if (index == currentIndex)
			setSelectedItem(s);
	}

	public int size()
	{
		return delegate.size();
	}

	public String[] toArray()
	{
		return delegate.toArray(); // does not include selected object
	}

	public StringVector toVector()
	{
		return (StringVector) delegate.clone();
	}

	public String toString()
	{
		return "selected: " + selectedString + "\n\t" + delegate.toString();
	}

}
