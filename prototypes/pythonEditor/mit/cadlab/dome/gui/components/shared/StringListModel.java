// StringListModel.java
package mit.cadlab.dome.gui.components.shared;

import javax.swing.DefaultListModel;
import javax.swing.event.*;
import javax.swing.*;
import java.util.*;

/*
 * This class implements the ListModel interface.
 */
public class StringListModel extends AbstractListModel {

    protected Vector delegate = new Vector();

    public StringListModel(){
	super();
    }

    public StringListModel(String[] choices){
	super();
	addAll(choices);
    }

    public int getSize() {
	return delegate.size();
    }

    // use get to get String data at index
    public Object getElementAt(int index) {
	return delegate.elementAt(index);
    }

    public void copyInto(String anArray[]) {
	delegate.copyInto(anArray);
    }

    public void trimToSize() {
	delegate.trimToSize();
    }

    public void ensureCapacity(int minCapacity) {
	delegate.ensureCapacity(minCapacity);
    }

    public void setSize(int newSize) {
	int oldSize = delegate.size();
	delegate.setSize(newSize);
	if (oldSize > newSize) {
	    fireIntervalRemoved(this, newSize, oldSize-1);
	}
	else if (oldSize < newSize) {
	    fireIntervalAdded(this, oldSize, newSize-1);
	}
    }

    public int capacity() {
	return delegate.capacity();
    }

    public int size() {
	return delegate.size();
    }

    public boolean isEmpty() {
	return delegate.isEmpty();
    }

    public Enumeration elements() {
	return delegate.elements();
    }

    public boolean contains(String elem) {
	return delegate.contains(elem);
    }

    public int indexOf(String elem) {
	return delegate.indexOf(elem);
    }

    public int indexOf(String elem, int index) {
	return delegate.indexOf(elem, index);
    }

    public int lastIndexOf(String elem) {
	return delegate.lastIndexOf(elem);
    }

    public int lastIndexOf(String elem, int index) {
	return delegate.lastIndexOf(elem, index);
    }

    public String elementAt(int index) {
	return (String)delegate.elementAt(index);
    }

    public String firstElement() {
	return (String)delegate.firstElement();
    }

    public String lastElement() {
	return (String)delegate.lastElement();
    }

    public void setElementAt(String obj, int index) {
	delegate.setElementAt(obj, index);
	fireContentsChanged(this, index, index);
    }

    public void removeElementAt(int index) {
	delegate.removeElementAt(index);
	fireIntervalRemoved(this, index, index);
    }

    public void insertElementAt(String obj, int index) {
	delegate.insertElementAt(obj, index);
	fireIntervalAdded(this, index, index);
    }

    public void addElement(String obj) {
	int index = delegate.size();
	delegate.addElement(obj);
	fireIntervalAdded(this, index, index);
    }

    public boolean removeElement(String obj) {
	int index = indexOf(obj);
	boolean rv = delegate.removeElement(obj);
	if (index >= 0) {
	    fireIntervalRemoved(this, index, index);
	}
	return rv;
    }

    public void removeAllElements() {
	int index1 = delegate.size()-1;
	delegate.removeAllElements();
	if (index1 >= 0) {
	    fireIntervalRemoved(this, 0, index1);
	}
    }

    public String toString() {
	return delegate.toString();
    }

    public String[] toArray() {
	String[] rv = new String[delegate.size()];
	delegate.copyInto(rv);
	return rv;
    }

    public String get(int index) {
	return (String)delegate.elementAt(index);
    }

    public String set(int index, String element) {
	String rv = (String)delegate.elementAt(index);
	delegate.setElementAt(element, index);
	fireContentsChanged(this, index, index);
	return rv;
    }

    public void add(int index, String element) {
	delegate.insertElementAt(element, index);
	fireIntervalAdded(this, index, index);
    }

    public String remove(int index) {
	String rv = (String)delegate.elementAt(index);
	delegate.removeElementAt(index);
	fireIntervalRemoved(this, index, index);
	return rv;
    }

    public void clear() {
	int index1 = delegate.size()-1;
	delegate.removeAllElements();
	if (index1 >= 0) {
	    fireIntervalRemoved(this, 0, index1);
	}
    }

    public void removeRange(int fromIndex, int toIndex) {
	for(int i = toIndex; i >= fromIndex; i--) {
	    delegate.removeElementAt(i);
	}
	fireIntervalRemoved(this, fromIndex, toIndex);
    }

    public void addAll(String[] objs) {
	int index = delegate.size();
	for (int i=0; i<objs.length; ++i){
	    delegate.addElement(objs[i]);
	}
	fireIntervalAdded(this, index, index+objs.length-1);
    }

    public void addAll(int index, String[] objs) {
	for (int i=0; i<objs.length; ++i){
	    delegate.insertElementAt(objs[i],index+i);
	}
	fireIntervalAdded(this, index, index+objs.length-1);
    }

    public void addAll(Collection objs) {
	int index = delegate.size();
	Iterator iterator = objs.iterator();
	while (iterator.hasNext()){
	    delegate.addElement((String)iterator.next());
	}
	fireIntervalAdded(this, index, index+objs.size()-1);
    }

    public void addAll(int index, Collection objs) {
	Iterator iterator = objs.iterator();
	int i=0;
	while (iterator.hasNext()){
	    delegate.insertElementAt((String)iterator.next(),index+(i++));
	}
	fireIntervalAdded(this, index, index+objs.size()-1);
    }

}
