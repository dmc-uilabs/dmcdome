// DBagEvent.java
package mit.cadlab.dome3.util;

import java.util.EventObject;

public class DBagEvent extends EventObject
{

	public static boolean DEBUG = false;

	public static final int ITEM_ADDED = 0;
	public static final int ITEM_REMOVED = 1;
	public static final int ITEM_INCREMENTED = 2;
	public static final int ITEM_DECREMENTED = 3;
	public static final int BAG_EMPTY = 4;
	public static final int BAG_NONEMPTY = 5;

	protected DBag source;
	protected int type;
	protected Object item = null;
	protected int newItemCount = 0;

	public static DBagEvent createItemAddedEvent(Object source, Object obj, int count)
	{
		return new DBagEvent(source, ITEM_ADDED, obj, count);
	}

	public static DBagEvent createItemRemovedEvent(Object source, Object obj)
	{
		return new DBagEvent(source, ITEM_REMOVED, obj, 0);
	}

	public static DBagEvent createItemIncrementedEvent(Object source, Object obj, int count)
	{
		return new DBagEvent(source, ITEM_INCREMENTED, obj, count);
	}

	public static DBagEvent createItemDecrementedEvent(Object source, Object obj, int count)
	{
		return new DBagEvent(source, ITEM_DECREMENTED, obj, count);
	}

	public static DBagEvent createBagEmptyEvent(Object source)
	{
		return new DBagEvent(source, BAG_EMPTY);
	}

	public static DBagEvent createBagNonEmptyEvent(Object source)
	{
		return new DBagEvent(source, BAG_NONEMPTY);
	}

	protected DBagEvent(Object source, int type, Object item, int count)
	{
		super(source);
		checkType(type);
		this.type = type;
		this.item = item;
		this.newItemCount = count;
	}

	protected DBagEvent(Object source, int type)
	{
		super(source);
		checkType(type);
		this.type = type;
	}

	protected void checkType(int type)
	{
		switch (type) {
			case DBagEvent.ITEM_ADDED:
			case DBagEvent.ITEM_REMOVED:
			case DBagEvent.ITEM_INCREMENTED:
			case DBagEvent.ITEM_DECREMENTED:
			case DBagEvent.BAG_EMPTY:
			case DBagEvent.BAG_NONEMPTY:
				break;
			default:
				throw new IllegalArgumentException("DBagEvent type: " + type);
		}
	}

	public int getType()
	{
		return type;
	}

	public Object getItem()
	{
		return item;
	}

	public int getItemCount()
	{
		return newItemCount;
	}

	public DBag getBag()
	{
		return (DBag) getSource();
	}

	public String toString()
	{
		String str = "DBagEvent: ";
		switch (type) {
			case ITEM_ADDED:
				str += "ITEM_ADDED";
				str += "\t" + newItemCount + ": " + item;
				break;
			case ITEM_REMOVED:
				str += "ITEM_REMOVED";
				str += "\t" + newItemCount + ": " + item;
				break;
			case ITEM_INCREMENTED:
				str += "ITEM_INCREMENTED";
				str += "\t" + newItemCount + ": " + item;
				break;
			case ITEM_DECREMENTED:
				str += "ITEM_DECREMENTED";
				str += "\t" + newItemCount + ": " + item;
				break;
			case BAG_EMPTY:
				str += "BAG_EMPTY";
				break;
			case BAG_NONEMPTY:
				str += "BAG_NONEMPTY";
				break;
			default:
				str += "UNKNOWN TYPE " + type;
		}
		if (DEBUG)
			str += "\n" + getSource();
		return str;
	}

}
