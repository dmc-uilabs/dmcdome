// PluginConfiguration.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.plugin;

import mit.cadlab.dome3.objectmodel.ModelObjectFactory;
import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeBoolean;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.plugin.DefaultPluginMappingManager;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.plugin.PluginMappingManager;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.ConcreteParameter;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents the parameters desired for a particular plugin's setup panel.
 */
public abstract class PluginConfiguration
{

	// setup attributes
	public static final String MAKE_CONSISTENT = "Make consistent when loaded";

	// subclasses should define a public TYPE_INFO instance
	// the TYPE_NAME is used in menu items referring to this type of plugin
	// the XML_TYPE of the TYPE_INFO is used as the pluginType in the XML
	// and as the model type in the database and as the model-specific file extension
	// The XML_TYPE can not have spaces.

	// subclasses must also define a String[] VALID_DATA_TYPES
	// and two constructors
	// PluginConfiguration(PluginModel model)
	// PluginConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)

	protected List setupParameters = new ArrayList();
	protected HashMap paramsByName = new HashMap();

	/**
	 * Subclasses should initialize setup parameters here.
	 */
	protected PluginConfiguration()
	{
    }


    protected PluginConfiguration(PluginModel model)
    {
    }

    /**
     }
	 * Subclasses should pass in model object factory from model here.
	 * @param moFactory
	 * @param xmlElement
	 */
	protected PluginConfiguration(PluginModel model, ModelObjectFactory moFactory, Element xmlElement)
	{
		List params = xmlElement.selectNodes("setup/" + Parameter.XML_TAG);
		Element element;
		Parameter param;
		for (int i = 0; i < params.size(); i++) {
			element = (Element) params.get(i);
			param = (Parameter) moFactory.newInstance(element, new Object[]{model, element});
			// get rid of old make consistent parameters
			if (!param.getName().equals(MAKE_CONSISTENT))
				addSetupParameter(param);
		}
	}

	public abstract TypeInfo getTypeInfo();

	public abstract String getMappingColumnName();

	public abstract int getMappingColumnSize();

    public abstract String[] getValidDataTypes();

	public String getTypeName()
	{
		return getTypeInfo().getTypeName();
	}

	public String getXmlType()
	{
		return getTypeInfo().getXmlType();
	}

	protected PluginMappingManager createPluginMappingManager(PluginModel m)
	{
		return new DefaultPluginMappingManager(m);
	}

	protected void addSetupParameter(Parameter param)
	{
		setupParameters.add(param);
		paramsByName.put(param.getName(), param);
	}

	public List getSetupParameters()
	{
		return Collections.unmodifiableList(setupParameters);
	}

	public int getSetupParameterCount()
	{
		return setupParameters.size();
	}

	public Parameter getSetupParameter(String name)
	{
		return (Parameter) paramsByName.get(name);
	}

	public Element toXmlElement()
	{
		Element setupElement = DocumentFactory.getInstance().createElement("setup");
		for (int i = 0; i < setupParameters.size(); i++) {
			Parameter p = (Parameter) setupParameters.get(i);
			setupElement.add(p.toXmlElement());
		}
        return setupElement;
	}

	/**
	 * override this to return true if the createParameter method should be called
	 * @return
	 */
	public boolean useCustomDatatype()
	{
		return false;
	}

	/**
	 *
	 * @param model
	 * @param id
	 * @param type
	 * @return null if the normal parameter creation should be used
	 * otherwise, return Object[] {Parameter, mapping string (optional)}
	 */
	public Object[] createParameter(PluginModel model, Id id, String type)
	{
		return null;
	}

}
