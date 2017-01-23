package mit.cadlab.dome3.objectmodel.modelcomponent.mapping.tool;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: jacob
 * Date: Jun 6, 2003
 * Time: 3:05:41 PM
 * To change this template use Options | File Templates.
 */
public interface ToolMappingManager
{
    public Object getMappingObjectForParameter(Parameter p);

	public void addMapping(Parameter p, Object mappingObject);

	public void removeAllMappings(Parameter p);

	public Element toXmlElement(ModelObjectScope scope, String elementName);

	public void addMappings(Element xmlElement);


}
