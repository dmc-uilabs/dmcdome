package mit.cadlab.dome3.objectmodel.dataobject;

import mit.cadlab.dome3.objectmodel.modelobject.ModelObject;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeCollection;

/**
 * Created by IntelliJ IDEA.
 * User: Renu
 * Date: May 22, 2003
 * Time: 4:39:01 PM
 * To change this template use Options | File Templates.
 */
public class DomeCollectionUtils
{
	public static boolean isCollectionParameter(ModelObject modelObject) {
		if((modelObject instanceof Parameter) &&
		        ((Parameter)modelObject).getContainerCollection() instanceof DomeCollection) {
			return true;
		}
		else return false;
	}
}
