// Vectors.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.client.functions;

import java.util.Vector;
import java.util.Arrays;

/**
 * This class has utility functions for creating Vectors
 * via a static create function which has been overloaded for 1-10 items
 */
public class Vectors
{

	public static Vector create(Object obj)
	{
		return new Vector(Arrays.asList(new Object[]{obj}));
	}

	public static Vector create(Object obj1, Object obj2)
	{
		return new Vector(Arrays.asList(new Object[]{obj1, obj2}));
	}

	public static Vector create(Object obj1, Object obj2, Object obj3)
	{
		return new Vector(Arrays.asList(new Object[]{obj1, obj2, obj3}));
	}

	public static Vector create(Object obj1, Object obj2, Object obj3, Object obj4)
	{
		return new Vector(Arrays.asList(new Object[]{obj1, obj2, obj3, obj4}));
	}

	public static Vector create(Object obj1, Object obj2, Object obj3, Object obj4, Object obj5)
	{
		return new Vector(Arrays.asList(new Object[]{obj1, obj2, obj3, obj4, obj5}));
	}

	public static Vector create(Object obj1, Object obj2, Object obj3, Object obj4, Object obj5,
	                            Object obj6)
	{
		return new Vector(Arrays.asList(new Object[]{obj1, obj2, obj3, obj4, obj5,
		                                             obj6}));
	}

	public static Vector create(Object obj1, Object obj2, Object obj3, Object obj4, Object obj5,
	                            Object obj6, Object obj7)
	{
		return new Vector(Arrays.asList(new Object[]{obj1, obj2, obj3, obj4, obj5,
		                                             obj6, obj7}));
	}

	public static Vector create(Object obj1, Object obj2, Object obj3, Object obj4, Object obj5,
	                            Object obj6, Object obj7, Object obj8)
	{
		return new Vector(Arrays.asList(new Object[]{obj1, obj2, obj3, obj4, obj5,
		                                             obj6, obj7, obj8}));
	}

	public static Vector create(Object obj1, Object obj2, Object obj3, Object obj4, Object obj5,
	                            Object obj6, Object obj7, Object obj8, Object obj9)
	{
		return new Vector(Arrays.asList(new Object[]{obj1, obj2, obj3, obj4, obj5,
		                                             obj6, obj7, obj8, obj9}));
	}

	public static Vector create(Object obj1, Object obj2, Object obj3, Object obj4, Object obj5,
	                            Object obj6, Object obj7, Object obj8, Object obj9, Object obj10)
	{
		return new Vector(Arrays.asList(new Object[]{obj1, obj2, obj3, obj4, obj5,
		                                             obj6, obj7, obj8, obj9, obj10}));
	}

}
