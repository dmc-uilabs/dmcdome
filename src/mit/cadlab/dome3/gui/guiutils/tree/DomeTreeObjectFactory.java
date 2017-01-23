// DomeTreeObjectFactory.java
package mit.cadlab.dome3.gui.guiutils.tree;

import mit.cadlab.dome3.gui.guiutils.treetable.CachingObjectFactory;
import mit.cadlab.dome3.swing.tree.TreeObject;
import mit.cadlab.dome3.swing.tree.TreeObjectFactory;

public class DomeTreeObjectFactory implements TreeObjectFactory
{

	protected CachingObjectFactory factory;

	public DomeTreeObjectFactory(String name)
	{
		factory = new CachingObjectFactory(name);
	}

	public DomeTreeObjectFactory(String name,
	                             String defaultTreeObjectClassName)
	{
		factory = new CachingObjectFactory(name, defaultTreeObjectClassName);
	}

	public void registerTreeObjectInfo(String objClassName,
	                                   String treeObjectClassName)
	{
		factory.registerObjectInfo(objClassName, treeObjectClassName);
	}

	public void registerTreeObjectInfo(String objClassName,
	                                   String treeObjectClassName,
	                                   String treeObjectParamClassName)
	{
		factory.registerObjectInfo(objClassName, treeObjectClassName,
		                           treeObjectParamClassName);
	}

	public void registerTreeObjectKeyLink(String objClassName,
	                                      String keyClassName)
	{
		factory.registerObjectKeyLink(objClassName, keyClassName);
	}

	public TreeObject getTreeObject(Object obj)
	{
		return (TreeObject) factory.getObject(obj);
	}

	public void removeCachedTreeObject(Object obj)
	{
		factory.removeCachedTreeObject(obj);
	}
}
