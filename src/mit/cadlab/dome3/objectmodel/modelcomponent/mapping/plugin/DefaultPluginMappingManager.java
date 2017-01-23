/**
 * Created by IntelliJ IDEA.
 * User: Cadlab
 * Date: Mar 19, 2003
 * Time: 3:28:40 AM
 * To change this template use Options | File Templates.
 */
package mit.cadlab.dome3.objectmodel.modelcomponent.mapping.plugin;

import mit.cadlab.dome3.objectmodel.AbstractDomeObject;
import mit.cadlab.dome3.objectmodel.DeletionEvent;
import mit.cadlab.dome3.objectmodel.DeletionListener;
import mit.cadlab.dome3.objectmodel.ModelObjectScope;
import mit.cadlab.dome3.objectmodel.modelinterface.dome.DomeModelInterface;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.plugin.PluginModel;
import mit.cadlab.dome3.util.xml.XMLUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DefaultPluginMappingManager implements PluginMappingManager
{

	protected HashMap mapping;
	protected PluginModel model;
	protected DeletionListener parameterDeletionListener;

	public DefaultPluginMappingManager(PluginModel model)
	{
		this.model = model;
		mapping = new HashMap();
		parameterDeletionListener = new ParameterDeletionListener();
	}

	public Object getMappingObjectForParameter(Parameter p)
	{
		return mapping.get(p);
	}

	public void addMapping(Parameter p, Object mappingObject)
	{
		if (mappingObject == null || mappingObject.toString().trim().equals(""))
			removeAllMappings(p);
		else {
			if (!mapping.containsKey(p))
				p.addDeletionListener(parameterDeletionListener);
			mapping.put(p, mappingObject);
		}
	}

	public void removeAllMappings(Parameter p)
	{
		mapping.remove(p);
	}

	/**
	 * Create an XML representation of the mapping manager
	 * @return dom4j XML element
	 */
	public Element toXmlElement(ModelObjectScope scope, String elementName)
	{
		Element xmlElement = DocumentHelper.createElement(elementName);

		// traverse the list of mappings
		Collection keys = Collections.unmodifiableSet(mapping.keySet());
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			// add mapped parameter heading
			Parameter p = (Parameter) iter.next();
			if(p.getScope().equals(scope)) {
				Element mappedParam = p.toXmlMappedRef();

				// add mapped parameters
				String map = (String) mapping.get(p);
				mappedParam.addText(map);

				// finally, add the new mappings to the xml structure
				xmlElement.add(mappedParam);
			}
		}

		return (xmlElement.elements().isEmpty() ? null : xmlElement);
	}

	public void addMappings(Element xmlElement)
	{
		addMappings(model, xmlElement);
	}

	public void addMappings(ModelObjectScope scope, Element xmlElement)
	{
		Id paramId;
		Parameter p1;
		String reference;
		Element paramElement;

		XMLUtils.makeRootElement(xmlElement); // necessary to use XPath locally
		List params = xmlElement.selectNodes("/" + xmlElement.getQName().getName()
		                                     + "/" + Parameter.XML_MAPPED_TAG);

		for (Iterator iter = params.iterator(); iter.hasNext();) {
			// get the mapping element
			paramElement = (Element) iter.next();
			if (paramElement == null) {
				throw new IllegalArgumentException("no mapping parameter");
			}


			paramId = AbstractDomeObject.parseXmlRef(paramElement);
			if (paramId == null) {
				throw new IllegalArgumentException("no parameter Id in mapping");
			}
			p1 = (Parameter) scope.getModelObjectById(paramId);
			if (p1 == null) {
				if (scope instanceof DomeModelInterface &&
				        ((DomeModelInterface) scope).isDefaultInterface()) {
					p1 = (Parameter) ((DomeModelInterface) scope).getTempModelObjectById(paramId);
					if (p1 == null) {
						throw new IllegalArgumentException("mapping parameter '" + paramId + "' not found in scope"
						                                   + "'" + scope.getName() + "'");
					}
				} else {
					throw new IllegalArgumentException("mapping parameter '" + paramId + "' not found in scope "
					                                   + "'" + scope.getName() + "'");
				}
			}

			// get the mapped parameters and establish the mappings
			reference = paramElement.getText();
			if (reference == null) {
				throw new IllegalArgumentException("no target references in mapping");
			}
			// create mapping
			addMapping(p1, reference);
		}
	}

	class ParameterDeletionListener implements DeletionListener
	{
		public void objectDeleted(DeletionEvent e)
		{
			removeAllMappings((Parameter) e.getSource());
		}
	}

}
