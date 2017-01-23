// DBag.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

/**
 * This data structure is used to count the number of items in something
 */
public class DBag implements Collection, Cloneable, java.io.Serializable
{
	protected HashMap members = new HashMap();
	protected int memberCount = 0;

	/**
	 * List of items that are watching the membership of the bag.
	 */
	private List listeners;

	public DBag()
	{
		// default constructor
	}

	public DBag(Collection c)
	{
		this.addAll(c);
	}

	public DBag(Object[] objs)
	{
		this.addAll(objs);
	}

	public DBag(DBag bag)
	{
		Iterator it = bag.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			add(obj, bag.getCount(obj));
		}
	}

	protected Object clone() throws CloneNotSupportedException
	{
		return new DBag(this);
	}

	public int size()
	{
		return memberCount;
	}

	public int distinctMembers()
	{
		return members.size();
	}

	public boolean isEmpty()
	{
		return memberCount == 0;
	}

	public boolean contains(Object o)
	{
		return members.containsKey(o);
	}

	public int getCount(Object obj)
	{
		Integer objCount = (Integer) members.get(obj);
		if (objCount == null)
			return 0;
		else
			return objCount.intValue();
	}

	/**
	 * iterator for members in set
	 */
	public Iterator iterator()
	{
		return members.keySet().iterator();
	}

	public Collection getMembers() {
		return Collections.unmodifiableCollection(members.keySet());
	}

	/**
	 * members only
	 */
	public Object[] toArray()
	{
		return members.keySet().toArray();
	}

	public Object[] toArray(Object[] a)
	{
		return members.keySet().toArray(a);
	}

	private boolean incrementMemberCount(Object obj, int number)
	{
		if (number < 1)
			throw new IllegalArgumentException("can not add zero or negative number of elements: " + number);
		if (members.containsKey(obj)) {
			Integer objCount = (Integer) members.get(obj);
			Integer newObjCount = new Integer(objCount.intValue() + number);
			members.put(obj, newObjCount);
			this.fireItemIncremented(obj, newObjCount.intValue());
		} else { // add to hashmap
			members.put(obj, new Integer(number));
			this.fireItemAdded(obj, number);
		}
		boolean wasEmpty = (memberCount == 0);
		memberCount += number;
		if (wasEmpty)
			this.fireBagNonEmpty();
		return true;
	}

	private boolean decrementMemberCount(Object obj, int number)
	{
		if (number < 1)
			throw new IllegalArgumentException("can not remove zero or negative number of elements: " + number);
		if (members.containsKey(obj)) {
			Integer objCount = (Integer) members.get(obj);
			Integer newObjCount = new Integer(objCount.intValue() - number);
			if (newObjCount.intValue() < 0)
				throw new IllegalArgumentException("can not remove more elements than exists: " + number + " > " + objCount);
			if (newObjCount.intValue() == 0) { // none left, so remove it
				members.remove(obj);
				this.fireItemRemoved(obj);
			} else {
				members.put(obj, newObjCount);
				this.fireItemDecremented(obj, newObjCount.intValue());
			}
			memberCount -= number;
			if (memberCount == 0)
				this.fireBagEmpty();
			return true;
		} else {
			return false;
		}
	}

	public boolean add(Object o)
	{
		return add(o, 1);
	}

	public boolean add(Object o, int number)
	{
		return incrementMemberCount(o, number);
	}

	public boolean remove(Object o)
	{
		return remove(o, 1);
	}

	public boolean remove(Object o, int number)
	{
		return decrementMemberCount(o, number);
	}

	public boolean containsAll(Collection c)
	{
		Iterator it = c.iterator();
		while (it.hasNext()) {
			if (!members.containsKey(it.next()))
				return false;
		}
		return true;
	}

	public boolean addAll(Object[] objs)
	{
		for (int i = 0; i < objs.length; ++i)
			add(objs[i]);
		return true;
	}

	public boolean removeAll(Object[] objs)
	{
		boolean result = false;
		for (int i = 0; i < objs.length; ++i)
			result = remove(objs[i]) && result;
		return result;
	}

	public boolean addAll(Collection c)
	{
		Iterator it = c.iterator();
		while (it.hasNext())
			add(it.next());
		return true;
	}

	public boolean removeAll(Collection c)
	{
		boolean result = false;
		Iterator it = c.iterator();
		while (it.hasNext()) {
			result = remove(it.next()) && result;
		}
		return result;
	}

	public boolean retainAll(Collection c)
	{
		throw new UnsupportedOperationException();
	}

	public void clear()
	{
		// notify that all members going away
		members.clear();
		memberCount = 0;
	}

	public String toString()
	{
		return "size: " + memberCount + "\tmembers: " + members.size() + "\n" + members;
	}

	public String getMembersMapAsString() {
		return FormatUtils.mapToString(members);
	}
	
	// Listener support
	public void addDBagListener(DBagListener l)
	{
		if (l == null) return;
		if (listeners == null)
			listeners = new ArrayList();
		if (!listeners.contains(l)) {
			listeners.add(l);
		}
	}

	public void removeDBagListener(DBagListener l)
	{
		listeners.remove(l);
		if (listeners.size() == 0)
			listeners = null;
	}

	protected void fireItemAdded(Object obj, int count)
	{
		if (listeners == null) return;
		fireBagEvent(DBagEvent.createItemAddedEvent(this, obj, count));
	}

	protected void fireItemRemoved(Object obj)
	{
		if (listeners == null) return;
		fireBagEvent(DBagEvent.createItemRemovedEvent(this, obj));
	}

	protected void fireItemIncremented(Object obj, int count)
	{
		if (listeners == null) return;
		fireBagEvent(DBagEvent.createItemIncrementedEvent(this, obj, count));
	}

	protected void fireItemDecremented(Object obj, int count)
	{
		if (listeners == null) return;
		fireBagEvent(DBagEvent.createItemDecrementedEvent(this, obj, count));
	}

	protected void fireBagEmpty()
	{
		if (listeners == null) return;
		fireBagEvent(DBagEvent.createBagEmptyEvent(this));
	}

	protected void fireBagNonEmpty()
	{
		if (listeners == null) return;
		fireBagEvent(DBagEvent.createBagNonEmptyEvent(this));
	}

	protected void fireBagEvent(DBagEvent event)
	{
		List errors = new ArrayList();
		synchronized (listeners) {
			Iterator it = listeners.iterator();
			while (it.hasNext()) {
				try {
					switch (event.getType()) {
						case DBagEvent.ITEM_ADDED:
							((DBagListener) it.next()).itemAdded(event);
							break;
						case DBagEvent.ITEM_REMOVED:
							((DBagListener) it.next()).itemRemoved(event);
							break;
						case DBagEvent.ITEM_INCREMENTED:
							((DBagListener) it.next()).itemIncremented(event);
							break;
						case DBagEvent.ITEM_DECREMENTED:
							((DBagListener) it.next()).itemDecremented(event);
							break;
						case DBagEvent.BAG_EMPTY:
							((DBagListener) it.next()).bagEmpty(event);
							break;
						case DBagEvent.BAG_NONEMPTY:
							((DBagListener) it.next()).bagNonEmpty(event);
							break;
						default:
							System.err.println("fireBagEvent unknown event type: " + event);
					}
				} catch (Exception ex) {
					errors.add(ex);
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

}
