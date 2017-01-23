package mit.cadlab.dome3.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class DSet extends DArrayList
{

	public DSet()
	{
		super();
	}

	public DSet(Collection objs)
	{
		super(objs);
	}

	public DSet(Object[] objs)
	{
		super(objs);
	}

	protected boolean addHookBefore(Object obj)
	{
		return !contains(obj);
	};

	// implement set operations here

	/**
	 * Create collections of items in items1 but not in items2
	 */
	public static Collection removeSet(Collection items1, Collection items2)
	{
		DSet result = new DSet(items1);
		Iterator it = result.iterator();
		while (it.hasNext()) {
			if (items2.contains(it.next()))
				it.remove();
		}
		return result;
	}

	public static Collection intersection(Collection items1, Collection items2)
	{
		DSet result = new DSet();
		if (items1.size() > items2.size()) { // force items1 to be smaller set
			Collection temp = items1;
			items1 = items2;
			items2 = temp;
		}
		Iterator it = items1.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (items2.contains(obj))
				result.add(obj);
		}
		return result;
	}

	public static boolean intersects(Collection c1, Collection c2)
	{
		Iterator it = c1.iterator();
		while (it.hasNext()) {
			if (c2.contains(it.next()))
				return true;
		}
		return false;
	}

    public static boolean intersects(cern.colt.list.AbstractIntList c1, cern.colt.list.AbstractIntList c2)
	{
        if(c1==null||c2==null) return false;

		int c1_size = c1.size();

        for(int i=0;i<c1_size;i++)
			if (c2.contains(c1.get(i)))  return true;

		return false;
	}

	public static Collection union(Collection items1, Collection items2)
	{
		DSet result = new DSet(items1);
		result.addAll(items2); // add all items one-by-one
		return result;
	}

	/**
	 * @param o
	 * @return true if sets have same items in any order
	 */
	public boolean equals(Object o)
	{
		if (o==null || !(o instanceof DSet))
			return false;
		DSet oSet = (DSet)o;
		if (this.size() != oSet.size())
			return false;
		Iterator items = this.iterator();
		Object item;
		while (items.hasNext()) {
			if (!oSet.contains(items.next()))
				return false;
		}
		return true;
	}

}
