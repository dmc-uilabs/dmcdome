package mit.cadlab.dome3.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A <code>DList</code> is an extension of the <code>ArrayList</code> class
 * which supports defining before and after hooks to the add/remove methods and
 * supports notification of changes to the List.
 *
 * @version 1.0
 */
public class DArrayList extends AbstractList implements List, Cloneable,
        java.io.Serializable
{

	/**
	 * An object representing nothing moved in or out of the list.
	 * Used as a "null" return value. Distinct from null, though.
	 */
	private static Object NO_OBJECT_REMOVED = new Object();

	/**
	 * The array buffer into which the elements of the ArrayList are stored.
	 * The capacity of the ArrayList is the length of this array buffer.
	 */
	private transient Object elementData[];

	/**
	 * The size of the ArrayList (the number of elements it contains).
	 *
	 * @serial
	 */
	private int size = 0;

	/**
	 * List of items that are watching the membership of the list.
	 */
	private List listeners;


	public DArrayList()
	{
		this(10);
	}

	public DArrayList(int initialCapacity)
	{
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: " +
			                                   initialCapacity);
		this.elementData = new Object[initialCapacity];
	}

	public DArrayList(Collection objs)
	{
		this((objs.size() * 110) / 100); // Allow 10% room for growth
		addAll(objs);
	}

	public DArrayList(Object[] objs)
	{
		this((objs.length * 110) / 100); // Allow 10% room for growth
		addAll(objs);
	}

	/**
	 * Checks if result from remove/set operation indicates that
	 * object was removed (different from removing a null object).
	 *
	 * @param obj result from remove/set operation
	 * @return true if no object removed/set; false otherwise
	 */
	public boolean wasObjectRemoved(Object obj)
	{
		return NO_OBJECT_REMOVED != obj;
	}

	public synchronized void trimToSize()
	{
		modCount++;
		int oldCapacity = elementData.length;
		if (size < oldCapacity) {
			Object oldData[] = elementData;
			elementData = new Object[size];
			System.arraycopy(oldData, 0, elementData, 0, size);
            cleanArray(oldData);
		}
	}


    private void cleanArray(Object[] oArray){
        for (int i = 0; i < oArray.length; i++) {
            oArray[i]=null;
        }
    }

	public synchronized void ensureCapacity(int minCapacity)
	{
		modCount++;
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
			Object oldData[] = elementData;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			elementData = new Object[newCapacity];
			System.arraycopy(oldData, 0, elementData, 0, size);
            cleanArray(oldData);
		}
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return  the number of elements in this list.
	 */
	public int size()
	{
		return size;
	}

	/**
	 * Tests if this list has no elements.
	 *
	 * @return  <tt>true</tt> if this list has no elements;
	 *          <tt>false</tt> otherwise.
	 */
	public boolean isEmpty()
	{
		return size == 0;
	}

	/**
	 * Returns <tt>true</tt> if this list contains the specified element.
	 *
	 * @param elem element whose presence in this List is to be tested.
	 */
	public boolean contains(Object elem)
	{
		return indexOf(elem) >= 0;
	}

	/**
	 * Searches for the first occurence of the given argument, testing
	 * for equality using the <tt>equals</tt> method.
	 *
	 * @param   elem   an object.
	 * @return  the index of the first occurrence of the argument in this
	 *          list; returns <tt>-1</tt> if the object is not found.
	 * @see     Object#equals(Object)
	 */
	public int indexOf(Object elem)
	{
		return indexOf(elem, 0);
	}

	public int indexOf(Object elem, int startIndex)
	{
		if (elem == null) {
			for (int i = startIndex; i < size; i++)
				if (elementData[i] == null)
					return i;
		} else {
			for (int i = startIndex; i < size; i++)
				if (elem.equals(elementData[i]))
					return i;
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified object in
	 * this list.
	 *
	 * @param   elem   the desired element.
	 * @return  the index of the last occurrence of the specified object in
	 *          this list; returns -1 if the object is not found.
	 */
	public int lastIndexOf(Object elem)
	{
		if (elem == null) {
			for (int i = size - 1; i >= 0; i--)
				if (elementData[i] == null)
					return i;
		} else {
			for (int i = size - 1; i >= 0; i--)
				if (elem.equals(elementData[i]))
					return i;
		}
		return -1;
	}

	/**
	 * Returns a shallow copy of this <tt>ArrayList</tt> instance.  (The
	 * elements themselves are not copied.)
	 *
	 * @return  a clone of this <tt>ArrayList</tt> instance.
	 */
	public Object clone()
	{
		try {
			DArrayList v = (DArrayList) super.clone();
			v.elementData = new Object[size];
			System.arraycopy(elementData, 0, v.elementData, 0, size);
			v.modCount = 0;
			return v;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * Returns an array containing all of the elements in this list
	 * in the correct order.
	 *
	 * @return an array containing all of the elements in this list
	 * 	       in the correct order.
	 */
	public Object[] toArray()
	{
		Object[] result = new Object[size];
		System.arraycopy(elementData, 0, result, 0, size);
		return result;
	}

	/**
	 * Returns an array containing all of the elements in this list in the
	 * correct order.  The runtime type of the returned array is that of the
	 * specified array.  If the list fits in the specified array, it is
	 * returned therein.  Otherwise, a new array is allocated with the runtime
	 * type of the specified array and the size of this list.<p>
	 *
	 * If the list fits in the specified array with room to spare (i.e., the
	 * array has more elements than the list), the element in the array
	 * immediately following the end of the collection is set to
	 * <tt>null</tt>.  This is useful in determining the length of the list
	 * <i>only</i> if the caller knows that the list does not contain any
	 * <tt>null</tt> elements.
	 *
	 * @param a the array into which the elements of the list are to
	 *		be stored, if it is big enough; otherwise, a new array of the
	 * 		same runtime type is allocated for this purpose.
	 * @return an array containing the elements of the list.
	 * @throws ArrayStoreException if the runtime type of a is not a supertype
	 *         of the runtime type of every element in this list.
	 */
	public Object[] toArray(Object a[])
	{
		if (a.length < size)
			a = (Object[]) java.lang.reflect.Array.newInstance(
			        a.getClass().getComponentType(), size);

		System.arraycopy(elementData, 0, a, 0, size);

		if (a.length > size)
			a[size] = null;

		return a;
	}

	// Hooks into add/remove operations on List
	// assumption: list is not changed by add/remove Hooks!
	// hooks for doing something before adding/removing objects from List
	// returns true if add/remove should proceed, false otherwise
	protected boolean addHookBefore(Object obj)
	{
		return true;
	};
	protected boolean removeHookBefore(Object obj)
	{
		return true;
	};

	// hooks for doing something after adding/removing objects from List
	protected void addHookAfter(Object obj)
	{
	};
	protected void removeHookAfter(Object obj)
	{
	};

	// Positional Access Operations

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param  index index of element to return.
	 * @return the element at the specified position in this list.
	 * @throws    IndexOutOfBoundsException if index is out of range <tt>(index
	 * 		  &lt; 0 || index &gt;= size())</tt>.
	 */
	public Object get(int index)
	{
		checkValidIndex(index);
		return elementData[index];
	}

	public synchronized boolean add(Object obj)
	{
		try {
			ensureCapacity(size + 1);  // Increments modCount!!
			if (_add(obj)) {
				int newIndex = size - 1;
				fireIntervalAdded(newIndex, newIndex, new DArrayList(new Object[]{obj}));
				return true;
			}
			return false;
		} catch (RuntimeException ex) {
			if (ex instanceof AfterHookException) {
				int newIndex = size - 1;
				fireIntervalAdded(newIndex, newIndex, new DArrayList(new Object[]{obj}));
			}
			throw ex; // re-throw
		}
	}

	public synchronized void add(int index, Object obj)
	{
		checkValidInsertionIndex(index);
		try {
			ensureCapacity(size + 1);  // Increments modCount!!
			if (_add(index, obj)) {
				fireIntervalAdded(index, index, new DArrayList(new Object[]{obj}));
			}
		} catch (RuntimeException ex) {
			if (ex instanceof AfterHookException)
				fireIntervalAdded(index, index, new DArrayList(new Object[]{obj}));
			throw ex; // re-throw
		}
	}

	public synchronized boolean addAll(Collection objs)
	{
		if (objs == null || objs.size() == 0) return false;
		ensureCapacity(size + objs.size()); // increments modCount
		List errors = new ArrayList();
		List itemsAdded = new ArrayList();
		int beginIndex = size;
		Iterator it = objs.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			try {
				if (_add(obj))
					itemsAdded.add(obj);
			} catch (RuntimeException ex) {
				errors.add(ex);
				if (ex instanceof AfterHookException)
					itemsAdded.add(obj);
			}
		}
		int numNew = itemsAdded.size();
		if (numNew > 0)
			fireIntervalAdded(beginIndex, beginIndex + numNew - 1,
			                  itemsAdded);
		if (errors.size() == 1)
			throw (RuntimeException) errors.get(0);
		else if (errors.size() > 1)
			throw new MultipleErrorsException("DList.addAll", errors);
		return (numNew > 0);
	}

	public synchronized boolean addAll(Object[] objs)
	{
		if (objs == null || objs.length == 0) return false;
		int numNew = objs.length;
		ensureCapacity(size + numNew); // increments modCount
		List errors = new ArrayList();
		List itemsAdded = new ArrayList();
		int beginIndex = size;
		for (int i = 0; i < numNew; ++i) {
			Object obj = objs[i];
			try {
				if (_add(obj))
					itemsAdded.add(obj);
			} catch (RuntimeException ex) {
				errors.add(ex);
				if (ex instanceof AfterHookException)
					itemsAdded.add(obj);
			}
		}
		numNew = itemsAdded.size();
		if (numNew > 0)
			fireIntervalAdded(beginIndex, beginIndex + numNew - 1,
			                  itemsAdded);
		if (errors.size() == 1)
			throw (RuntimeException) errors.get(0);
		else if (errors.size() > 1)
			throw new MultipleErrorsException("DList.addAll", errors);
		return (numNew > 0);
	}

	public synchronized boolean addAll(int index, Collection objs)
	{
		checkValidInsertionIndex(index);
		if (objs == null || objs.size() == 0) return false;
		ensureCapacity(size + objs.size()); // increments modCount
		List errors = new ArrayList();
		List itemsAdded = new ArrayList();
		Iterator it = objs.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			try {
				if (_add(index + itemsAdded.size(), obj))
					itemsAdded.add(obj);
			} catch (RuntimeException ex) {
				errors.add(ex);
				if (ex instanceof AfterHookException)
					itemsAdded.add(obj);
			}
		}
		int numNew = itemsAdded.size();
		if (numNew > 0)
			fireIntervalAdded(index, index + numNew - 1,
			                  itemsAdded);
		if (errors.size() == 1)
			throw (RuntimeException) errors.get(0);
		else if (errors.size() > 1)
			throw new MultipleErrorsException("DList.addAll", errors);
		return (numNew > 0);
	}

	public synchronized boolean addAll(int index, Object[] objs)
	{
		checkValidInsertionIndex(index);
		if (objs == null || objs.length == 0) return false;
		int numNew = objs.length;
		ensureCapacity(size + numNew); // increments modCount
		List errors = new ArrayList();
		List itemsAdded = new ArrayList();
		for (int i = 0; i < numNew; ++i) {
			Object obj = objs[i];
			try {
				if (_add(index + itemsAdded.size(), obj))
					itemsAdded.add(obj);
			} catch (RuntimeException ex) {
				errors.add(ex);
				if (ex instanceof AfterHookException)
					itemsAdded.add(obj);
			}
		}
		numNew = itemsAdded.size();
		if (numNew > 0)
			fireIntervalAdded(index, index + numNew - 1,
			                  itemsAdded);
		if (errors.size() == 1)
			throw (RuntimeException) errors.get(0);
		else if (errors.size() > 1)
			throw new MultipleErrorsException("DList.addAll", errors);
		return (numNew > 0);
	}

	public synchronized void clear()
	{
		if (!isEmpty())
			removeRange(0, size);
	}

	public synchronized boolean remove(Object obj)
	{
		try {
			int index = _remove(obj);
			if (index != -1) {
				fireIntervalRemoved(index, index, new DArrayList(new Object[]{obj}));
				return true;
			}
			return false;
		} catch (RuntimeException ex) {
			if (ex instanceof AfterHookException) {
				int index = ((Integer) ((AfterHookException) ex).object).intValue();
				fireIntervalRemoved(index, index, new DArrayList(new Object[]{obj}));
				((AfterHookException) ex).object = obj; // replace with object
			}
			throw ex; // re-throw
		}
	}

	public synchronized Object remove(int index)
	{
		checkValidIndex(index);
		try {
			Object obj = _remove(index);
			if (obj != NO_OBJECT_REMOVED) {
				fireIntervalRemoved(index, index, new DArrayList(new Object[]{obj}));
				return obj;
			}
			return NO_OBJECT_REMOVED;
		} catch (RuntimeException ex) {
			if (ex instanceof AfterHookException) {
				fireIntervalRemoved(index, index, new DArrayList(new Object[]{((AfterHookException) ex).object}));
			}
			throw ex; // re-throw
		}
	}

	public synchronized boolean removeAll(Collection objs)
	{
		if (objs == null || objs.size() == 0) return false;
		List indicesList = new ArrayList();
		// find location of collection objects in this list
		Iterator it = objs.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			int objIndex = indexOf(obj);
			while (objIndex != -1) {
				Integer objIndexInteger = new Integer(objIndex);
				if (indicesList.contains(objIndexInteger)) {
					// find next occurence
					objIndex = indexOf(obj, objIndex + 1);
				} else {
					indicesList.add(objIndexInteger);
					break;
				}
			}
		}
		int[] indices = Converters.toIntArray(indicesList);
		return removeIndices(indices);
	}

	public synchronized boolean removeAll(Object[] objs)
	{
		if (objs == null || objs.length == 0) return false;
		List indicesList = new ArrayList();
		// find location of array objects in this list
		for (int i = 0; i < objs.length; ++i) {
			Object obj = objs[i];
			int objIndex = indexOf(obj);
			while (objIndex != -1) {
				Integer objIndexInteger = new Integer(objIndex);
				if (indicesList.contains(objIndexInteger)) {
					// find next occurence
					objIndex = indexOf(obj, objIndex + 1);
				} else {
					indicesList.add(objIndexInteger);
					break;
				}
			}
		}
		int[] indices = Converters.toIntArray(indicesList);
		return removeIndices(indices);
	}

	public synchronized boolean removeAll(int[] indices)
	{
		if (indices == null || indices.length == 0) return false;
		// check indices are all valid; remove duplicates
		List l = new ArrayList();
		for (int i = 0; i < indices.length; ++i) {
			checkValidIndex(indices[i]);
			Integer index = new Integer(indices[i]);
			if (!l.contains(index))
				l.add(index);
		}
		return removeIndices(Converters.toIntArray(l));
	}

	private boolean removeIndices(int[] indices)
	{
		// indices are known to be good and distinct
		// indices are sorted into ascending order in place
		Arrays.sort(indices); // ascending order
		ArrayList removedIndices = new ArrayList();
		ArrayList itemsRemoved = new ArrayList();
		ArrayList errors = new ArrayList();
		for (int i = indices.length - 1; i >= 0; --i) { // remove in descending order
			int index = indices[i];
			try {
				Object obj = _remove(index);
				if (obj != NO_OBJECT_REMOVED) {
					removedIndices.add(0, new Integer(index));
					itemsRemoved.add(0, obj);
				}
			} catch (RuntimeException ex) {
				errors.add(ex);
				if (ex instanceof AfterHookException) {
					removedIndices.add(0, new Integer(index));
					itemsRemoved.add(0, ((AfterHookException) ex).object);
				}
			}
		}
		if (itemsRemoved.size() > 0)
			fireItemsRemoved(Converters.toIntArray(removedIndices), itemsRemoved);
		if (errors.size() == 1)
			throw (RuntimeException) errors.get(0);
		else if (errors.size() > 1)
			throw new MultipleErrorsException("DList.removeAll", errors);
		return itemsRemoved.size() > 0;
	}

	/**
	 * Removes range of items in range specified.
	 * smaller number is lower bound; larger number is upper bound
	 * lower bound inclusive; upper bound exclusive
	 *
	 * @param index1 lower bound inclusive
	 * @param index2 upper bound exclusive
	 */
	public synchronized void removeRange(int index1, int index2)
	{
		if (index1 > index2) { // swap order
			int temp = index1;
			index1 = index2;
			index2 = temp;
		}
		checkValidIndex(index1);
		checkValidInsertionIndex(index2);

		ArrayList removedIndices = new ArrayList();
		ArrayList itemsRemoved = new ArrayList();
		ArrayList errors = new ArrayList();
		for (int i = index2 - 1; i >= index1; --i) { // remove in descending order
			try {
				Object obj = _remove(i);
				if (obj != NO_OBJECT_REMOVED) {
					removedIndices.add(0, new Integer(i));
					itemsRemoved.add(0, obj);
				}
			} catch (RuntimeException ex) {
				errors.add(ex);
				if (ex instanceof AfterHookException) {
					removedIndices.add(0, new Integer(i));
					itemsRemoved.add(0, ((AfterHookException) ex).object);
				}
			}
		}
		if (itemsRemoved.size() == (index2 - index1)) // all removed
			fireIntervalRemoved(index1, index2 - 1, itemsRemoved);
		else if (itemsRemoved.size() > 0)
			fireItemsRemoved(Converters.toIntArray(removedIndices), itemsRemoved);
		if (errors.size() == 1)
			throw (RuntimeException) errors.get(0);
		else if (errors.size() > 1)
			throw new MultipleErrorsException("DList.removeRange", errors);
	}

	public synchronized boolean retainAll(Collection objs)
	{
		throw new UnsupportedOperationException();
	}

	public synchronized boolean retainAll(Object[] objs)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Replace item at index with given object.
	 * Set fails/is cancelled if object can not be added to Vector.
	 *
	 * @param index an <code>int</code> value
	 * @param obj an <code>Object</code> value
	 * @return an <code>Object</code> value
	 */
	public synchronized Object set(int index, Object obj)
	{
		checkValidIndex(index);
		// check that item can be added
		boolean addOk;
		try {
			addOk = addHookBefore(obj);
		} catch (RuntimeException ex) {
			throw new BeforeHookException(this, ex, obj);
		}
		if (!addOk)
			return NO_OBJECT_REMOVED;
		Object removedObj = elementData[index];
		boolean removeOk;
		try {
			removeOk = removeHookBefore(removedObj);
		} catch (RuntimeException ex) {
			throw new BeforeHookException(this, ex, removedObj);
		}
		if (!removeOk)
			return NO_OBJECT_REMOVED;
		// perform set
		modCount++;
		elementData[index] = obj;
		ArrayList errors = new ArrayList();
		// cleanup remove
		try {
			removeHookAfter(removedObj);
		} catch (RuntimeException ex) {
			errors.add(new AfterHookException(this, ex, removedObj));
		}
		// cleanup add
		try {
			addHookAfter(obj);
		} catch (RuntimeException ex) {
			errors.add(new AfterHookException(this, ex, obj));
		}
		fireItemsReplaced(new int[]{index}, new DArrayList(new Object[]{removedObj, obj}));
		if (errors.size() == 1)
			throw (RuntimeException) errors.get(0);
		else if (errors.size() > 1)
			throw new MultipleErrorsException("DList.set", errors);
		return removedObj;
	}

	public synchronized void shiftLeft(int index)
	{
		checkValidIndex(index);
		if (_shiftLeft(index))
			fireIntervalChanged(index - 1, index);
	}

	public synchronized void shiftRight(int index)
	{
		checkValidIndex(index);
		if (_shiftRight(index))
			fireIntervalChanged(index, index + 1);
	}

	public synchronized void shiftLeft(int[] indices)
	{
		// assume no repeats
		Arrays.sort(indices); // ascending order
		checkValidIndex(indices[0]); // check first element
		checkValidIndex(indices[indices.length - 1]); // check last element
		// all indices are valid
		List errors = new ArrayList();
		int beginIndex = -1;
		int endIndex = -1;
		for (int i = 0; i < indices.length; ++i) {
			try {
				int index = indices[i];
				if (_shiftLeft(index)) {
					if (beginIndex == -1)
						beginIndex = index - 1;
					endIndex = index;
				}
			} catch (RuntimeException ex) {
				errors.add(ex);
			}
		}
		if (beginIndex != -1)
			fireIntervalChanged(beginIndex, endIndex);
		if (errors.size() == 1)
			throw (RuntimeException) errors.get(0);
		else if (errors.size() > 1)
			throw new MultipleErrorsException("DList.shiftLeft", errors);
	}

	public synchronized void shiftRight(int[] indices)
	{
		// assume no repeats
		Arrays.sort(indices); // ascending order
		checkValidIndex(indices[0]); // check first element
		checkValidIndex(indices[indices.length - 1]); // check last element
		// all indices are valid
		List errors = new ArrayList();
		int beginIndex = -1;
		int endIndex = -1;
		for (int i = indices.length - 1; i >= 0; --i) {
			try {
				int index = indices[i];
				if (_shiftRight(index)) {
					if (endIndex == -1)
						endIndex = index + 1;
					beginIndex = index;
				}
			} catch (RuntimeException ex) {
				errors.add(ex);
			}
		}
		if (endIndex != -1)
			fireIntervalChanged(beginIndex, endIndex);
		if (errors.size() == 1)
			throw (RuntimeException) errors.get(0);
		else if (errors.size() > 1)
			throw new MultipleErrorsException("DList.shiftRight", errors);
	}

	// internal methods to manipulate contents of list
	// error check & ensureCapacity has already been called
	// before these calls!

	/**
	 * Internal method for adding object to Vector
	 *
	 * @param obj an <code>Object</code> to be appended to List
	 * @return true if element added to List; false otherwise
	 */
	private boolean _add(Object obj)
	{
		boolean addOk;
		try {
			addOk = addHookBefore(obj);
		} catch (RuntimeException ex) {
			throw new BeforeHookException(this, ex, obj);
		}
		if (addOk) {
			elementData[size++] = obj;
			try {
				addHookAfter(obj);
			} catch (RuntimeException ex) {
				throw new AfterHookException(this, ex, obj);
			}
			return true;
		}
		return false;
	}

	/**
	 * Internal method for adding objects to List at specific index.
	 *
	 * @param index an index to place the new object
	 * @param obj an <code>Object</code> to be added to List
	 * @return a <code>boolean</code> value indicating if object added
	 */
	private boolean _add(int index, Object obj)
	{
		// assumes index is already checked for validity
		boolean addOk;
		try {
			addOk = addHookBefore(obj);
		} catch (RuntimeException ex) {
			throw new BeforeHookException(this, ex, obj);
		}
		if (addOk) {
			if (size != index)
				System.arraycopy(elementData, index, elementData, index + 1,
				                 size - index);
			elementData[index] = obj;
			size++;
			try {
				addHookAfter(obj);
			} catch (RuntimeException ex) {
				throw new AfterHookException(this, ex, obj);
			}
			return true;
		}
		return false;
	}

	/**
	 * Internal method to remove object from List.
	 *
	 * @param obj an <code>Object</code> to remove from List
	 * @return the index the object was at; -1 if no object removed
	 */
	private int _remove(Object obj)
	{
		int index = indexOf(obj);
		if (index == -1) {
			return -1;
		}
		boolean removeOk;
		try {
			removeOk = removeHookBefore(obj);
		} catch (RuntimeException ex) {
			throw new BeforeHookException(this, ex, obj);
		}
		if (removeOk) {
			__remove(index);
			try {
				removeHookAfter(obj);
			} catch (RuntimeException ex) {
				throw new AfterHookException(this, ex, new Integer(index)); // temporary use of object space
			}
			return index;
		}
		return -1;
	}

	private Object _remove(int index)
	{
		// assume index checked
		Object obj = elementData[index];
		boolean removeOk;
		try {
			removeOk = removeHookBefore(obj);
		} catch (RuntimeException ex) {
			throw new BeforeHookException(this, ex, obj);
		}
		if (removeOk) {
			__remove(index);
			try {
				removeHookAfter(obj);
			} catch (RuntimeException ex) {
				throw new AfterHookException(this, ex, obj);
			}
			return obj;
		}
		return NO_OBJECT_REMOVED;
	}

	private void __remove(int index)
	{
		int numMoved = size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(elementData, index + 1,
			                 elementData, index, numMoved);
		}
		elementData[--size] = null; // let gc do its work
	}

	private void _swap(int index1, int index2)
	{
		// assume indices are valid
		Object temp = elementData[index1];
		elementData[index1] = elementData[index2];
		elementData[index2] = temp;
	}

	private boolean _shiftLeft(int index)
	{
		if (index == 0) return false;
		_swap(index, index - 1);
		return true;
	}

	private boolean _shiftRight(int index)
	{
		if (index == (size - 1)) // last item can not be moved down
			return false;
		_swap(index, index + 1);
		return true;
	}

	/**
	 * Check if the given index is in range.  If not, throw an appropriate
	 * runtime exception.
	 */
	private void checkValidIndex(int index)
	{
		if (index >= size || index < 0)
			throw new IndexOutOfBoundsException("ListIndex: " + index +
			                                    ", Size: " + size);
	}

	private void checkValidInsertionIndex(int index)
	{
		if (index > size || index < 0)
			throw new IndexOutOfBoundsException("InsertionIndex: " + index +
			                                    ", Size: " + size);
	}

	/**
	 * Save the state of the <tt>ArrayList</tt> instance to a stream (that
	 * is, serialize it).
	 *
	 * @serialData The length of the array backing the <tt>ArrayList</tt>
	 *             instance is emitted (int), followed by all of its elements
	 *             (each an <tt>Object</tt>) in the proper order.
	 */
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	        throws java.io.IOException
	{
		// Write out element count, and any hidden stuff
		s.defaultWriteObject();

		// Write out array length
		s.writeInt(elementData.length);

		// Write out all elements in the proper order.
		for (int i = 0; i < size; i++)
			s.writeObject(elementData[i]);
	}

	/**
	 * Reconstitute the <tt>ArrayList</tt> instance from a stream (that is,
	 * deserialize it).
	 */
	private synchronized void readObject(java.io.ObjectInputStream s)
	        throws java.io.IOException, ClassNotFoundException
	{
		// Read in size, and any hidden stuff
		s.defaultReadObject();

		// Read in array length and allocate array
		int arrayLength = s.readInt();
		elementData = new Object[arrayLength];

		// Read in all elements in the proper order.
		for (int i = 0; i < size; i++)
			elementData[i] = s.readObject();
	}

	// Listener support
	public void addDListListener(DListListener l)
	{
		if (l == null) return;
		if (listeners == null)
			listeners = new ArrayList();
		if (!listeners.contains(l)) {
			listeners.add(l);
		}
	}

	public void removeDListListener(DListListener l)
	{
		listeners.remove(l);
		if (listeners.size() == 0)
			listeners = null;
	}

	// order of indices does not matter since DListEvent sorts them
	protected void fireIntervalChanged(int index0, int index1)
	{
		if (listeners == null) return;
		fireListEvent(DListEvent.createIntervalChangedEvent(this, index0, index1)); // range of change
	}

	protected void fireIntervalAdded(int index0, int index1, List itemsAdded)
	{
		if (listeners == null) return;
		fireListEvent(DListEvent.createIntervalAddedEvent(this, index0, index1, itemsAdded));
	}

	protected void fireIntervalRemoved(int index0, int index1, List itemsRemoved)
	{
		if (listeners == null) return;
		fireListEvent(DListEvent.createIntervalRemovedEvent(this, index0, index1, itemsRemoved));
	}

	protected void fireItemsRemoved(int[] indices, List itemsRemoved)
	{
		if (listeners == null) return;
		fireListEvent(DListEvent.createItemsRemovedEvent(this, indices, itemsRemoved));
	}

	protected void fireItemsReplaced(int[] indices, List itemsOldAndNew)
	{
		if (listeners == null) return;
		fireListEvent(DListEvent.createItemsReplacedEvent(this, indices, itemsOldAndNew));
	}

	protected void fireListEvent(DListEvent event)
	{
		List errors = new ArrayList();
		synchronized (listeners) {
			Iterator it = listeners.iterator();
			while (it.hasNext()) {
				DListListener listener = (DListListener) it.next();
				try {
					switch (event.getType()) {
						case DListEvent.INTERVAL_CHANGED:
							listener.intervalChanged(event);
							break;
						case DListEvent.INTERVAL_ADDED:
							listener.intervalAdded(event);
							break;
						case DListEvent.INTERVAL_REMOVED:
							listener.intervalRemoved(event);
							break;
						case DListEvent.ITEMS_REMOVED:
							listener.itemsRemoved(event);
							break;
						case DListEvent.ITEMS_REPLACED:
							listener.itemsReplaced(event);
							break;
						default:
							System.err.println("fireListEvent unknown event type: " + event);
					}
				} catch (RuntimeException ex) {
					ex.printStackTrace();
					errors.add(new FireListEventException(ex, listener));
				}
			}
		}
		if (errors.size() == 1)
			throw (RuntimeException) errors.get(0);
		else if (errors.size() > 1)
			throw new MultipleErrorsException(event.toString(), errors);
		//if (errors.size() > 0)
		//System.out.println(new MultipleErrorsException(event.toString(),errors));
		//exceptionHandler.handleExceptions("fireIntervalChanged",errors);
	}

	/**
	 * This class is wrapped around exceptions thrown during a
	 * beforeHook method.
	 */
	public static class BeforeHookException extends RuntimeException
	{
		public DArrayList list;
		public Exception exception;
		public Object object;

		/**
		 * @param ex exception thrown during beforeHook method
		 * @param obj the object which caused the exception
		 */
		public BeforeHookException(DArrayList list, RuntimeException ex, Object obj)
		{
			super(ex.toString());
			this.list = list;
			exception = ex;
			object = obj;
		}

		public String toString()
		{
			return "DList.BeforeHookException for " + ClassUtils.getClassName(list) + ": " + getMessage();
		}

	}

	/**
	 * This class is wrapped around exceptions thrown during an
	 * afterHook method.
	 */
	public static class AfterHookException extends RuntimeException
	{
		public DArrayList list;
		public Exception exception;
		public Object object;

		/**
		 * @param ex exception thrown during afterHook method
		 * @param obj the object which caused the exception
		 */
		public AfterHookException(DArrayList list, RuntimeException ex, Object obj)
		{
			super(ex.toString());
			this.list = list;
			exception = ex;
			object = obj;
		}

		public String toString()
		{
			return "DList.AfterHookException for " + ClassUtils.getClassName(list) + ": " + getMessage();
		}
	}

	public static class FireListEventException extends RuntimeException
	{
		public Exception exception;
		public Object object;

		/**
		 * @param ex exception thrown during fireListEvent method
		 * @param obj the object which caused the exception
		 */
		public FireListEventException(RuntimeException ex, Object obj)
		{
			super(ex.toString());
			exception = ex;
			object = obj;
		}

		public String toString()
		{
			return "DList.FireListEventException from " + object + "\n\t" + getMessage();
		}

	}

}
