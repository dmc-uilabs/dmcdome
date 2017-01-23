// LoopException.java
package mit.cadlab.dome3.objectmodel.util.causality;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.util.DomeException;

import java.util.Collections;
import java.util.List;

public class LoopException extends DomeException
{
	protected List objects;

	public LoopException(List objects)
	{
		super(getNamesFromList(objects));
		this.objects = Collections.unmodifiableList(objects);
	}

	protected static String getNamesFromList(List objects)
	{
		if (objects == null || objects.size() == 0) return "";
		if (objects.size() == 1) return getNameFromObject(objects.get(0));
		if (objects.size() == 2)
			return getNameFromObject(objects.get(0)) +
			        " and " + getNameFromObject(objects.get(1));
		// 3 or more objects
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < objects.size() - 1; ++i) {
			sb.append(getNameFromObject(objects.get(i)) + ", ");
		}
		sb.append("and " + getNameFromObject(objects.get(objects.size() - 1)));
		return sb.toString();
	}

	protected static String getNameFromObject(Object object)
	{
		if (object instanceof DomeObject)
			return ((DomeObject) object).getName();
		else
			return object.toString();
	}

	public List getObjects()
	{
		return objects;
	}

}
