// IntegrationProject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.project;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import mit.cadlab.dome3.objectmodel.modelinterface.manager.ModelInterfaceManager;

import java.util.List;

/**
 * An integration project allows one to specify a set of resources
 * to tie together via subscriptions in integration models.
 * On the surface, an IntegrationProject looks like a model.
 */
public interface IntegrationProject extends Model
{

	public static TypeInfo TYPE_INFO = new TypeInfo("Project", "project");
	public static String XML_TAG = TYPE_INFO.getXmlType(); // needed for backwards compatibility with old implementation
	public static final String PROPERTY_CLOSED = "ProjectClosed";

	public List getResourceModels();

	public List getIntegrationModels();

	public List getInterfaces(); // todo: move to Model interface?

	public ModelInterfaceManager getProjectInterfacesManager();

	public ProjectResourceInfo getResource(String id);

	public ProjectIntegrationModelInfo getIntegrationModel(String id);

	public ConnectionMappingManager getMappingManager();

}
