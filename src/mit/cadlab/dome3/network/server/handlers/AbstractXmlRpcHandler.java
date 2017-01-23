// AbstractXmlRpcHandler.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.handlers;

import org.apache.xmlrpc.XmlRpcHandler;

import java.util.Vector;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Contains functions that may be useful to DOME XmlRpcHandlers
 */
public abstract class AbstractXmlRpcHandler implements XmlRpcHandler
{
	public static Class PARAM_INT = Integer.class;
	public static Class PARAM_REAL = Double.class;
	public static Class PARAM_STR = String.class;
	public static Class PARAM_HASHTABLE = Hashtable.class;
	public static Class PARAM_BYTE_ARRAY = byte[].class;
	public static Class PARAM_STR_ARRAY = String[].class;
	public static Class PARAM_BOOL = Boolean.class;
	public static Class PARAM_VEC = Vector.class;
	public static Class PARAM_OBJ = Object.class;

	public static boolean validateParameterTypes(Vector params, Class[] types)
	{
		Object obj;
		for (int i = 0; i < params.size(); i++) {
			obj = params.get(i);
			if (!types[i].isInstance(obj))
				return false;
		}
		return true;
	}

	public static boolean getBoolean(Object obj)
	{
		if (obj instanceof String)
			return Boolean.getBoolean((String) obj);
		return ((Boolean) obj).booleanValue();
	}

	public static int getInt(Object obj)
	{
		if (obj instanceof String)
			return Integer.parseInt((String) obj);
		return ((Number) obj).intValue();
	}

	public static double getDouble(Object obj)
	{
		if (obj instanceof String)
			return Double.parseDouble((String) obj);
		return ((Number) obj).doubleValue();
	}

	public static float getFloat(Object obj)
	{
		if (obj instanceof String)
			return Float.parseFloat((String) obj);
		return ((Number) obj).floatValue();
	}

	public static long getLong(Object obj)
	{
		if (obj instanceof String)
			return Long.parseLong((String) obj);
		return ((Number) obj).longValue();
	}

	public static short getShort(Object obj)
	{
		if (obj instanceof String)
			return Short.parseShort((String) obj);
		return ((Number) obj).shortValue();
	}

}
