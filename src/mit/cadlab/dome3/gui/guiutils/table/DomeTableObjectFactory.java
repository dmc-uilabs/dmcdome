// DomeTableObjectFactory.java
package mit.cadlab.dome3.gui.guiutils.table;

import mit.cadlab.dome3.gui.guiutils.treetable.CachingObjectFactory;
import mit.cadlab.dome3.swing.table.TableObject;
import mit.cadlab.dome3.swing.table.TableObjectFactory;

public class DomeTableObjectFactory implements TableObjectFactory
{

	protected CachingObjectFactory factory;

	public DomeTableObjectFactory(String name)
	{
		factory = new CachingObjectFactory(name);
	}

	public DomeTableObjectFactory(String name,
	                              String defaultTableObjectClassName)
	{
		factory = new CachingObjectFactory(name, defaultTableObjectClassName);
	}

	public void registerTableObjectInfo(String objClassName,
	                                    String tableObjectClassName)
	{
		factory.registerObjectInfo(objClassName, tableObjectClassName);
	}

	public void registerTableObjectInfo(String objClassName,
	                                    String tableObjectClassName,
	                                    String tableObjectParamClassName)
	{
		factory.registerObjectInfo(objClassName, tableObjectClassName,
		                           tableObjectParamClassName);
	}

	public void registerTableObjectKeyLink(String objClassName,
	                                       String keyClassName)
	{
		factory.registerObjectKeyLink(objClassName, keyClassName);
	}

	public TableObject getTableObject(Object obj)
	{
		return (TableObject) factory.getObject(obj);
	}

    /**
     * caching object factory is holding concrete parameters even after they are deleted
     * locate factory object using this method and then remove concrete parameter from the cache.
     * before removing concrete parameter we should also call delete() for data object associated with that concrete parameter.
     * @return
     */
    public CachingObjectFactory getCachingObjectFactory()
	{
		return factory;
	}
}
