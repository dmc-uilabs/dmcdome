// Names.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.util;

import mit.cadlab.dome3.objectmodel.DomeObject;
import mit.cadlab.dome3.objectmodel.util.solving.NameIdNode;
import mit.cadlab.dome3.objectmodel.util.solving.MultiItemNode;
import mit.cadlab.dome3.util.ClassUtils;

import java.util.Collection;
import java.util.Iterator;

/**
 * functions for getting "names" of objects
 */
public class Names
{

	public static String getNameId(Object obj)
	{
		if (obj instanceof DomeObject)
			return ((DomeObject) obj).getNameIdString();
		else if (obj instanceof String)
			return (String) obj;
		else
			return ClassUtils.getClassName(obj);
	}

	public static String getName(Object obj)
	{
		if (obj instanceof DomeObject)
			return ((DomeObject)obj).getName();
		else if (obj instanceof NameIdNode)
			return ((NameIdNode)obj).getName();
		else if (obj instanceof MultiItemNode)
			return ((MultiItemNode)obj).getName();
		else
			return obj.toString();
	}

	public static String getNameIds(Collection objs)
	{
		if (objs == null || objs.isEmpty())
			return "";
		Iterator it = objs.iterator();
		if (objs.size() == 1)
			return "[" + getNameId(it.next()) + "]";
		StringBuffer sb = new StringBuffer("[" + getNameId(it.next()));
		while (it.hasNext())
			sb.append(", " + getNameId(it.next()));
		sb.append("]");
		return sb.toString();
	}

	public static String getNames(Collection objs)
	{
		return getNames(objs,true);
	}

	public static String getNames(Collection objs, boolean surroundWithBrackets)
	{
		if (objs == null || objs.isEmpty())
			return "";
		String prefix = surroundWithBrackets ? "[" : "";
		String suffix = surroundWithBrackets ? "]" : "";
		Iterator it = objs.iterator();
		if (objs.size() == 1)
			return prefix + getName(it.next()) + suffix;
		StringBuffer sb = new StringBuffer(prefix + getName(it.next()));
		while (it.hasNext())
			sb.append(", " + getName(it.next()));
		sb.append(suffix);
		return sb.toString();
	}

}
