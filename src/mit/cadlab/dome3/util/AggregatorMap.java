// AggregatorMap.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

/**
 * This type of map aggregates distinct values for a key into a set.
 * The value for each key is a list containing every value which has been set for the key.
 */
public class AggregatorMap implements Map
{
	private HashMap data = new HashMap();
	private DBag valueSet = new DBag();
	private ValueListListener vl = new ValueListListener();

	public AggregatorMap()
	{
	}

	public AggregatorMap(Map initialValues) {
		putAll(initialValues);
	}

	public void clear()
	{
		data.clear();
	}

	public boolean containsKey(Object key)
	{
		return data.containsKey(key);
	}

	/**
	 * Returns true if value is in a value list of this map.
	 * @param value
	 * @return
	 */
	public boolean containsValue(Object value)
	{
		Iterator values = data.values().iterator();
		List list;
		while (values.hasNext()) {
			list = (List) values.next();
			if (list.contains(value))
				return true;
		}
		return false;
	}

	public Set entrySet()
	{
		return data.entrySet();
	}

	/**
	 * Returns value list for the key; or null if key is not in map
	 * @param key
	 * @return
	 */
	public Object get(Object key)
	{
		if (data.containsKey(key))
			return Collections.unmodifiableList((List) data.get(key));
		return null;
	}

	public boolean isEmpty()
	{
		return data.isEmpty();
	}

	public Set keySet()
	{
		return data.keySet();
	}

	/**
	 * adds value to valueList of key
	 * @param key
	 * @param value
	 * @return new value list
	 */
	public Object put(Object key, Object value)
	{
		DSet valueList = (DSet) data.get(key);
		if (valueList == null) {
			valueList = new DSet(); // value list only stores set of values
			valueList.addDListListener(vl);
			data.put(key, valueList);
		}
		valueList.add(value);
		return Collections.unmodifiableList(valueList);
	}

	/**
	 * adds values to valueList of key
	 * @param key
	 * @param values
	 * @return new value list
	 */
	public Object putValues(Object key, Collection values)
	{
		List valueList = (List) data.get(key);
		if (valueList == null) {
			valueList = new DSet(); // value list only stores set of values
			data.put(key, valueList);
		}
		valueList.addAll(values);
		return Collections.unmodifiableList(valueList);
	}

	public void putAll(Map t)
	{
		Iterator entries = t.entrySet().iterator();
		Map.Entry entry;
		if (t instanceof AggregatorMap) {
			while (entries.hasNext()) {
				entry = (Map.Entry) entries.next();
				putValues(entry.getKey(), (DSet) entry.getValue());
			}
		}
		else {
			while (entries.hasNext()) {
				entry = (Map.Entry) entries.next();
				put(entry.getKey(), entry.getValue());
			}
		}
	}

	public Object remove(Object key)
	{
		DSet valueList = (DSet) data.remove(key);
		if (valueList == null)
			return null;
		valueList.removeDListListener(vl);
		valueSet.removeAll(valueList);
		return valueList;
	}

	public boolean remove(Object key, Object value)
	{
		DSet valueList = (DSet) data.get(key);
		if (valueList == null)
			return false;
		boolean result = valueList.remove(value);
		if (valueList.isEmpty())
			this.remove(key);
		return result;
	}

	public int size()
	{
		return data.size();
	}

	/**
	 * Returns an unmodifiable list of items in the value lists of this aggregator map.
	 * This is equivalent to a union of all the value lists.
	 * @return
	 */
	public Collection values()
	{
		return valueSet.getMembers();
	}

	class ValueListListener implements DListListener
	{
		public void intervalAdded(DListEvent e)
		{
			valueSet.addAll(e.getItems());
		}

		public void intervalChanged(DListEvent e)
		{
		}

		public void intervalRemoved(DListEvent e)
		{
			valueSet.removeAll(e.getItems());
		}

		public void itemsRemoved(DListEvent e)
		{
			valueSet.removeAll(e.getItems());
		}

		public void itemsReplaced(DListEvent e)
		{
		}
	}

}
