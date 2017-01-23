// ClassObjectMap.java
package mit.cadlab.dome3.util;

import java.util.Hashtable;

/**
 * Maps supporting bindings of Classes to Objects.
 * Interfaces are not searched since many possibilities exist.
 * Instead, specify object, or associate classes with keyClasses.
 * null parameters not supported.
 */
public class ClassObjectMap
{

	protected Object defaultObject = null;
	protected Hashtable classObjectPairs = new Hashtable();
	protected Hashtable keyClassTable = new Hashtable();

	public ClassObjectMap()
	{
	}

	public ClassObjectMap(Object defaultObject)
	{
		this.defaultObject = defaultObject;
	}

	public Object getDefaultObject()
	{
		return defaultObject;
	}

	public void setDefaultObject(Object newDefaultObject)
	{
		defaultObject = newDefaultObject;
	}

	public boolean containsKeyClass(Class objClass)
	{
		return classObjectPairs.containsKey(objClass) ||
		        keyClassTable.containsKey(objClass);
	}

	public Object addClassObjectPair(Class objClass, Object obj)
	{
		return classObjectPairs.put(objClass, obj);
	}

	public Object removeClassObjectPair(Class objClass)
	{
		return classObjectPairs.remove(objClass);
	}

	public Class addKeyClass(Class objClass, Class keyClass)
	{
		return (Class) keyClassTable.put(objClass, keyClass);
	}

	public Class removeKeyClass(Class objClass)
	{
		return (Class) keyClassTable.remove(objClass);
	}

	public Object getObjectForClass(Class objClass)
	{
		Object result = null;
		// try specified class given, if any
		Object keyClass = keyClassTable.get(objClass);
		if (keyClass != null) {
			result = classObjectPairs.get(keyClass);
			if (result != null) return result;
		}
		// try class given
		result = classObjectPairs.get(objClass);
		if (result != null) return result;
		// try superclasses of class
		SuperclassEnumeration se = new SuperclassEnumeration(objClass);
		while (se.hasMoreSuperclasses() && result == null) {
			result = classObjectPairs.get(se.nextSuperclass());
		}
		if (result != null)
			return result;
		else // use default object
			return defaultObject;
	}

}
