// TreeHistoryEvent.java
package mit.cadlab.dome3.swing.tree;

import java.util.EventObject;

/**
 * Notification of element added/removed from tree history
 * and if the history queue changes from empty to non-empty
 * and vice versa.
 */
public class TreeHistoryEvent extends EventObject
{

	public static final int EVENT_ADDED = 0;
	public static final int EVENT_REMOVED = 1;
	public static final int EVENT_QUEUE_NONEMPTY = 2;
	public static final int EVENT_QUEUE_EMPTY = 3;

	protected int type;

	public TreeHistoryEvent(Object source, int type)
	{
		super(source);
		this.type = type;
	}

	public int getType()
	{
		return type;
	}

	protected String typeToString()
	{
		switch (type) {
			case EVENT_ADDED:
				return "EVENT_ADDED";
			case EVENT_REMOVED:
				return "EVENT_REMOVED";
			case EVENT_QUEUE_NONEMPTY:
				return "EVENT_QUEUE_NONEMPTY";
			case EVENT_QUEUE_EMPTY:
				return "EVENT_QUEUE_EMPTY";
			default:
				return "UNKNOWN_TYPE: " + type;
		}
	}

	public String toString()
	{
		return "TreeHistoryEvent: " + typeToString();
	}

}
