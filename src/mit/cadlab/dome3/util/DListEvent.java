// DListEvent.java
package mit.cadlab.dome3.util;

import mit.cadlab.dome3.objectmodel.util.Names;

import java.util.Arrays;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

public class DListEvent extends EventObject
{

	public static boolean DEBUG = false;
	/**
	 * Order of items in interval of list have changed,
	 * but no items have been added or removed.
	 */
	public static final int INTERVAL_CHANGED = 0;
	public static final int INTERVAL_ADDED = 1;
	public static final int INTERVAL_REMOVED = 2;
	public static final int ITEMS_REMOVED = 3;
	public static final int ITEMS_REPLACED = 4;

	protected int type;
	protected int[] indices;
	protected List items; // added, removed, old/new

	public static DListEvent createIntervalChangedEvent(Object source, int index0, int index1)
	{
		return new DListEvent(source, INTERVAL_CHANGED, index0, index1, null);
	}

	public static DListEvent createIntervalAddedEvent(Object source, int index0, int index1,
	                                                  List itemsAdded)
	{
		return new DListEvent(source, INTERVAL_ADDED, index0, index1, itemsAdded);
	}

	public static DListEvent createIntervalRemovedEvent(Object source, int index0, int index1,
	                                                    List itemsRemoved)
	{
		return new DListEvent(source, INTERVAL_REMOVED, index0, index1, itemsRemoved);
	}

	public static DListEvent createItemsRemovedEvent(Object source, int[] indices,
	                                                 List itemsRemoved)
	{
		return new DListEvent(source, ITEMS_REMOVED, indices, itemsRemoved);
	}

	public static DListEvent createItemsReplacedEvent(Object source, int[] indices,
	                                                  List itemsOldAndNew)
	{
		return new DListEvent(source, ITEMS_REPLACED, indices, itemsOldAndNew);
	}

	protected DListEvent(Object source, int type, int index0, int index1, List l)
	{
		super(source);
		checkType(type);
		this.type = type;
		if (index0 == index1) {
			indices = new int[]{index0};
		} else {
			indices = new int[]{index0, index1};
			Arrays.sort(indices);
		}
		this.items = (l == null) ? null : Collections.unmodifiableList(l);
	}

	protected DListEvent(Object source, int type, int[] indices, List l)
	{
		super(source);
		checkType(type);
		this.type = type;
		this.indices = new int[indices.length];
		System.arraycopy(indices, 0, this.indices, 0, indices.length);
		Arrays.sort(this.indices);
		this.items = (l == null) ? null : Collections.unmodifiableList(l);
	}

	protected void checkType(int type)
	{
		switch (type) {
			case DListEvent.INTERVAL_CHANGED:
			case DListEvent.INTERVAL_ADDED:
			case DListEvent.INTERVAL_REMOVED:
			case DListEvent.ITEMS_REMOVED:
			case DListEvent.ITEMS_REPLACED:
				break;
			default:
				throw new IllegalArgumentException("DListEvent type: " + type);
		}
	}

	public int getType()
	{
		return type;
	}

	public int getFirstIndex()
	{
		return indices[0];
	}

	public int getLastIndex()
	{
		return indices[indices.length - 1];
	}

	public int[] getIndices()
	{
		return indices;
	}

	public List getItems()
	{
		return items;
	}

	public String toString()
	{
		String str = "DListEvent: ";
		switch (type) {
			case INTERVAL_CHANGED:
				str += "INTERVAL_CHANGED";
				str += "\tfrom " + getFirstIndex() + " to " + getLastIndex();
				break;
			case INTERVAL_ADDED:
				str += "INTERVAL_ADDED";
				str += "\tfrom " + getFirstIndex() + " to " + getLastIndex();
				break;
			case INTERVAL_REMOVED:
				str += "INTERVAL_REMOVED";
				str += "\tfrom " + getFirstIndex() + " to " + getLastIndex();
				break;
			case ITEMS_REMOVED:
				str += "ITEMS_REMOVED";
				str += "\tindices: " + indicesString();
				break;
			case ITEMS_REPLACED:
				str += "ITEMS_REPLACED";
				str += "\tindices: " + indicesString();
				break;
			default:
				str += "UNKNOWN TYPE " + type;
				str += "\tindices: " + indicesString();
		}
		if (DEBUG)
			str += "\n\tsource: " + Names.getNameId(getSource()) + "\n\titems: " + Names.getNameIds(items);
		return str;
	}

	protected String indicesString()
	{
		if (indices == null)
			return "null";
		else if (indices.length == 0)
			return "[ ]";
		else {
			StringBuffer sb = new StringBuffer("[" + indices[0]);
			for (int i = 1; i < indices.length; ++i)
				sb.append(" " + indices[i]);
			sb.append("]");
			return sb.toString();
		}
	}

}
