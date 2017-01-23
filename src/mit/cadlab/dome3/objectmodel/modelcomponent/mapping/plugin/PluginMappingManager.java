/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Mar 19, 2003
 * Time: 3:24:58 AM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.objectmodel.modelcomponent.mapping.plugin;

import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import org.dom4j.Element;

public interface PluginMappingManager
{

	public Object getMappingObjectForParameter(Parameter p);

	public void addMapping(Parameter p, Object mappingObject);

	public void removeAllMappings(Parameter p);

	public Element toXmlElement(ModelObjectScope scope, String elementName);

	public void addMappings(Element xmlElement);
}
