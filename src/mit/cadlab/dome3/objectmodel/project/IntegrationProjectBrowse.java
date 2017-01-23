package mit.cadlab.dome3.objectmodel.project;

import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.util.causality.CausalityManager;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager;
import org.dom4j.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 14, 2003
 * Time: 2:57:43 PM
 * To change this template use Options | File Templates.
 */
public class IntegrationProjectBrowse extends AbstractIntegrationProject
{

	public IntegrationProjectBrowse(Element xmlElement)
	{
		super(xmlElement);
	}

	// CausalitySupport interface
	protected CausalityManager getCausalityManager()
	{
		return null;
	}

	//from AbstractIntegrationProject
	protected ModelInterfaceManager createInterfacesManager()
	{
		return null;
	}

	public ConnectionMappingManager getMappingManager()
	{
		return null;
	}

}
