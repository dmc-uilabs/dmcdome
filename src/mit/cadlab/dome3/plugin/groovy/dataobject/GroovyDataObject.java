package mit.cadlab.dome3.plugin.groovy.dataobject;

import mit.cadlab.dome3.plugin.AbstractPluginData;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DataObject;

/**
 * GroovyDataObject defines methods for data exchange between 'catalog plugin model object data' and 'dome model object data'
 * User: Sangmok Han
 * Date: 2005. 9. 5.
 */
public abstract class GroovyDataObject extends AbstractPluginData {

    abstract public boolean getIsResult();
    abstract public void loadNativeData();
    abstract public void loadJavaData();

    abstract public DataObject getData();
}
