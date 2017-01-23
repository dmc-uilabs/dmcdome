// TreeObjectEvent.java
package mit.cadlab.dome3.swing.tree;

import java.util.Arrays;
import java.util.EventObject;

public class TreeObjectEvent extends EventObject
{

	public static final int VALUE_CHANGED = 201;
	public static final int STRUCTURE_CHANGED = 202;
	public static final int CHILDREN_CHANGED = 203;
	public static final int CHILDREN_ADDED = 204;
	public static final int CHILDREN_REMOVED = 205;

	protected int type;
	protected int[] indices;

	public TreeObjectEvent(Object source, int type)
	{
		super(source);
		this.type = type;
	}

	public TreeObjectEvent(Object source, int type, int[] indices)
	{
		super(source);
		this.type = type;
		this.indices = new int[indices.length];
		for (int i = 0; i < indices.length; ++i)
			this.indices[i] = indices[i];
		Arrays.sort(this.indices);
	}

	public int getType()
	{
		return type;
	}

	public int[] getIndices()
	{
		return indices;
	}

	public String toString()
	{
		String str = "TreeObjectEvent: ";
		switch (type) {
			case VALUE_CHANGED:
				str += "VALUE_CHANGED";
				break;
			case STRUCTURE_CHANGED:
				str += "STRUCTURE_CHANGED";
				break;
			case CHILDREN_CHANGED:
				str += "CHILDREN_CHANGED";
				str += "\tindices: " + indicesString();
				break;
			case CHILDREN_ADDED:
				str += "CHILDREN_ADDED";
				str += "\tindices: " + indicesString();
				break;
			case CHILDREN_REMOVED:
				str += "CHILDREN_REMOVED";
				str += "\tindices: " + indicesString();
				break;
			default:
				str += "UNKNOWN TYPE " + type;
				str += "\tindices: " + indicesString();
		}
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
