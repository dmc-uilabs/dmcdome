// CachingObjectFactory.java
package mit.cadlab.dome3.gui.guiutils.treetable;

import mit.cadlab.dome3.util.ClassObjectMap;
import mit.cadlab.dome3.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * Stores mappings of key strings to constructor methods.
 * Constructor methods used have exactly one parameter.
 */
public class CachingObjectFactory
{

	protected ClassObjectMap constructors;
	protected HashMap objects = new HashMap();
	protected String name = "";

	/**
	 * name is used to identify this object factory during errors
	 */
	public CachingObjectFactory(String name)
	{
		this.name = name;
		constructors = new ClassObjectMap();
	}

	/**
	 * defaultTgtClassName is used as default constructor;
	 * must have constructor which takes Object instance
	 */
	public CachingObjectFactory(String name, String defaultTgtClassName)
	{
		this.name = name;
		try {
			constructors = new ClassObjectMap(getConstructor(defaultTgtClassName,
			                                                 "java.lang.Object"));
		} catch (Exception ex) {
			handleException(ex, name + " constructor");
			constructors = new ClassObjectMap();
		}
	}

	/**
	 * objClassName is key and parameter
	 * tgtClassName is object to be created
	 */
	public void registerObjectInfo(String objClassName,
	                               String tgtClassName)
	{
		registerObjectInfo(objClassName, tgtClassName, objClassName);
	}

	/**
	 * objClassName is key
	 * tgtClassName is object to be created
	 * tgtParamClassName is parameter class for object to be created
	 * tgtParamClassName is most likely superclass of objClassName
	 */
	public void registerObjectInfo(String objClassName,
	                               String tgtClassName,
	                               String tgtParamClassName)
	{
		try {
			Class objClass = Class.forName(objClassName);
			if (constructors.containsKeyClass(objClass)) {
				System.out.println("CachingObjectFactory.registerObjectInfo - " +
				                   "duplicate class ignored: " + objClassName);
				return;
			}
			Constructor objectCtr = getConstructor(tgtClassName,
			                                       tgtParamClassName);
			if (objectCtr != null)
				constructors.addClassObjectPair(objClass, objectCtr);
		} catch (Exception ex) {
			handleException(ex, "registerObjectInfo(" + objClassName + ", " + tgtClassName + ", " + tgtParamClassName + ")");
		}
	}

	public void registerObjectKeyLink(String objClassName,
	                                  String keyClassName)
	{
		try {
			Class objClass = Class.forName(objClassName);
			Class keyClass = Class.forName(keyClassName);
			constructors.addKeyClass(objClass, keyClass);
		} catch (Exception ex) {
			handleException(ex, "registerObjectKeyLink(" + objClassName + ", " + keyClassName + ")");
		}
	}

	private Constructor getConstructor(String className, String parameterClassName)
	{
		try {
			Class objClass = Class.forName(className);
			Class paramClass = Class.forName(parameterClassName);
			return objClass.getConstructor(new Class[]{paramClass});
		} catch (Exception ex) {
			handleException(ex, "getConstructor(" + className + ", " + parameterClassName + ")");
			return null;
		}
	}

	/**
	 * factory caches objects which have been made
	 * returns target object if one already made
	 * otherwise, returns new object and saves in cache
	 */
	public Object getObject(Object obj)
	{
		try {
			// find from cache first
			Object tgtObj = objects.get(obj);
			if (tgtObj == null) { // create new object, store in cache
				Constructor ctr = (Constructor) constructors.getObjectForClass(obj.getClass());
				tgtObj = ctr.newInstance(new Object[]{obj});
				objects.put(obj, tgtObj);
			}
			return tgtObj;
		} catch (Exception ex) {
			handleException(ex, ClassUtils.getClassName(obj) + "\t" + obj);
			return null;
		}
	}

	public void removeCachedTreeObject(Object obj) {
		objects.remove(obj);
	}

	private void handleException(Exception ex, String msg)
	{
		System.err.println(name + ": " + ex + "\n\t" + msg);
	}

}
