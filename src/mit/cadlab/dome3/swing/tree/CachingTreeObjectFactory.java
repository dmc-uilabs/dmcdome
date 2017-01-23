// CachingTreeObjectFactory.java
package mit.cadlab.dome3.swing.tree;

import mit.cadlab.dome3.swing.treetable.CachingObjectFactory;

/**
 * This TreeObjectFactory only creates one TreeObject for each unique object
 * handed to it. Created objects are cached and returned for subsequent
 * requests for TreeObjects.
 */
public class CachingTreeObjectFactory implements TreeObjectFactory
{

	protected CachingObjectFactory factory;

	/**
	 * Creates CachingTreeObjectFactory.
	 * @param name The name of the factory is used in error messages.
	 */
	public CachingTreeObjectFactory(String name)
	{
		factory = new CachingObjectFactory(name);
	}

	/**
	 * Creates CachingTreeObjectFactory.
	 * @param name The name of the factory is used in error messages.
	 * @param defaultTreeObjectClassName The default class to be used.
	 *        must have constructor which takes Object instance
	 */
	public CachingTreeObjectFactory(String name,
	                                String defaultTreeObjectClassName)
	{
		factory = new CachingObjectFactory(name, defaultTreeObjectClassName);
	}

	/**
	 * Registers tree object information for a tree object which should
	 * be created for a particular class where the constructor takes the
	 * type of the class as the parameter type.
	 * @param objClassName class name for an object (classes and abstract classes only; no interfaces)
	 * @param treeObjectClassName class name of the TreeObject class
	 */
	public void registerTreeObjectInfo(String objClassName,
	                                   String treeObjectClassName)
	{
		factory.registerObjectInfo(objClassName, treeObjectClassName);
	}

	/**
	 * Registers tree object information for a tree object which should
	 * be created for a particular class where the constructor takes a
	 * superclass or interface of the object specified.
	 * @param objClassName class name for an object (classes and abstract classes only; no interfaces)
	 * @param treeObjectClassName class name of the TreeObject class
	 * @param treeObjectParamClassName class name for the TreeObject constructor parameter
	 */
	public void registerTreeObjectInfo(String objClassName,
	                                   String treeObjectClassName,
	                                   String treeObjectParamClassName)
	{
		factory.registerObjectInfo(objClassName, treeObjectClassName,
		                           treeObjectParamClassName);
	}

	/**
	 * Registers tree object information for objects which use the same
	 * TreeObject class as another object which has already been registered.
	 * @param objClassName class name for a new object in the table
	 * @param keyClassName class name for the object whose TreeObject information should
	 * be shared with this object.
	 */
	public void registerTreeObjectKeyLink(String objClassName,
	                                      String keyClassName)
	{
		factory.registerObjectKeyLink(objClassName, keyClassName);
	}

	/**
	 * Creates/returns the TreeObject for the given object.
	 * Some implementations may create a new TreeObject each time or cache instances of
	 * the tree object.
	 * @param obj the object to return a TreeObject for
	 * @return a TreeObject for the object specified.
	 */
	public TreeObject getTreeObject(Object obj)
	{
		return (TreeObject) factory.getObject(obj);
	}

}
