// ClassUtils.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.util;

/**
 * Utility functions for working with classes.
 */
public class ClassUtils
{

	public static String getClassName(Object obj)
	{
		if (obj==null)
			return "<null>";
		Class objClass = (obj instanceof Class) ? (Class) obj : obj.getClass();
		String fullClassName = objClass.getName();
		return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
	}

}
