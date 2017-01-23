package mit.cadlab.dome3.network;

import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: thorek
 * Date: Mar 1, 2003
 * Time: 6:48:32 PM
 * To change this template use Options | File Templates.
 */
public class RuntimeUtils
{
	public static final Vector NO_VECTOR = new Vector();

	/*
	public static List vectorToList (Vector vector)
	{
		ArrayList list = new ArrayList();

		for (int i = 0; i < vector.size(); i++)
		{
			if (vector.get(i) instanceof Vector) {
				ArrayList row = new ArrayList();
				Vector resultListRow = (Vector) vector.get(i);
				for (int j = 0; j < resultListRow.size(); j++)
					row.add(resultListRow.get(i));
				list.add(row);
			}
			else
			if (vector.get(i) != null)
				list.add(vector.get(i));
		}

		return list;
	}
	*/

	public static Vector listToVector(List list)
	{
		Vector vector = new Vector();

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof List) {
				Vector row = new Vector();
				List resultListRow = (List) list.get(i);
				for (int j = 0; j < resultListRow.size(); j++)
					row.add(resultListRow.get(j));
				vector.add(row);
			} else if (list.get(i) != null)
				vector.add(list.get(i));
		}

		return vector;
	}


	// assumption: we don't want to preserve any vectors
	public static Vector objectToVector(Object obj)
	{
		Vector vector = new Vector();

		if (obj instanceof List)
			vector = RuntimeUtils.listToVector((List) obj);
		else if (obj instanceof Object[]) {
			for (int i = 0; i < ((Object[]) obj).length; i++)
				vector.add(((Object[]) obj)[i]);
		} else if (obj != null)
			vector.add(obj);

		return vector;
	}


	// assumption: we don't want to preserve any vectors
	public static Object vectorToObject(Vector vector)
	{
		Object obj;

		if (vector.size() == 0)
			obj = new Object();
		else if (vector.size() == 1)
			obj = vector.get(0);
		else {
			if (vector.get(0) instanceof Vector) {
				// looks like a list
				obj = new ArrayList();
				for (int i = 0; i < vector.size(); i++) {
					ArrayList row = new ArrayList();
					Vector resultListRow = (Vector) vector.get(i);
					for (int j = 0; j < resultListRow.size(); j++)
						row.add(vector.get(i));
					((ArrayList) obj).add(row);
				}
			} else {
				// looks like an array
				obj = new Object[vector.size()];
				for (int i = 0; i < vector.size(); i++)
					((Object[]) obj)[i] = vector.get(i);
			}
		}

		return obj;
	}
}
