// CachingTableObjectFactory.java
package mit.cadlab.dome3.swing.table;

import mit.cadlab.dome3.gui.guiutils.treetable.CachingObjectFactory;

/**
 * This TableObjectFactory only creates one TableObject for each unique object
 * handed to it. Created objects are cached and returned for subsequent
 * requests for TableObjects.
 */
public class CachingTableObjectFactory implements TableObjectFactory
{

	protected CachingObjectFactory factory;

	/**
	 * Creates CachingTableObjectFactory.
	 * @param name The name of the factory is used in error messages.
	 */
	public CachingTableObjectFactory(String name)
	{
		factory = new CachingObjectFactory(name);
	}

	/**
	 * Creates CachingTableObjectFactory.
	 * @param name The name of the factory is used in error messages.
	 * @param defaultTableObjectClassName The default class to be used.
	 *        must have constructor which takes Object instance
	 */
	public CachingTableObjectFactory(String name,
	                                 String defaultTableObjectClassName)
	{
		factory = new CachingObjectFactory(name, defaultTableObjectClassName);
	}

	/**
	 * Registers table object information for a table object which should
	 * be created for a particular class where the constructor takes the
	 * type of the class as the parameter type.
	 * @param objClassName class name for an object (classes and abstract classes only; no interfaces)
	 * @param tableObjectClassName class name of the TableObject class
	 */
	public void registerTableObjectInfo(String objClassName,
	                                    String tableObjectClassName)
	{
		factory.registerObjectInfo(objClassName, tableObjectClassName);
	}

	/**
	 * Registers table object information for a table object which should
	 * be created for a particular class where the constructor takes a
	 * superclass or interface of the object specified.
	 * @param objClassName class name for an object (classes and abstract classes only; no interfaces)
	 * @param tableObjectClassName class name of the TableObject class
	 * @param tableObjectParamClassName class name for the TableObject constructor parameter
	 */
	public void registerTableObjectInfo(String objClassName,
	                                    String tableObjectClassName,
	                                    String tableObjectParamClassName)
	{
		factory.registerObjectInfo(objClassName, tableObjectClassName,
		                           tableObjectParamClassName);
	}

	/**
	 * Registers table object information for objects which use the same
	 * TableObject class as another object which has already been registered.
	 * @param objClassName class name for a new object in the table
	 * @param keyClassName class name for the object whose TableObject information should
	 * be shared with this object.
	 */
	public void registerTableObjectKeyLink(String objClassName,
	                                       String keyClassName)
	{
		factory.registerObjectKeyLink(objClassName, keyClassName);
	}

	/**
	 * Creates/returns the TableObject for the given object.
	 * Some implementations may create a new TableObject each time or cache instances of
	 * the table object.
	 * @param obj the object to return a TableObject for
	 * @return a TableObject for the object specified.
	 */
	public TableObject getTableObject(Object obj)
	{
		return (TableObject) factory.getObject(obj);
	}

}
