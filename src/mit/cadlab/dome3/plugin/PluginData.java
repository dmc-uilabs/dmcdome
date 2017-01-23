package mit.cadlab.dome3.plugin;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Aug 28, 2003
 * Time: 1:38:54 PM
 * To change this template use Options | File Templates.
 */
public interface PluginData
{
	public void resetObjectPointer();

	public Parameter getParameter();
	
}
