// DomeModel.java
// Copyright (c) 2002 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.model.dome;

import mit.cadlab.dome3.objectmodel.MultiViewSupport;
import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filter;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filterable;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.ConnectionMappingManager;
import mit.cadlab.dome3.objectmodel.modelobject.context.Context;
import mit.cadlab.dome3.objectmodel.project.IntegrationProject;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.id.Id;
import mit.cadlab.dome3.util.DListListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * DomeModel supports Relations
 */
public interface DomeModel extends Model, Filterable, MultiViewSupport
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Dome Model", "DomeModel");
	public static final String BUILD_VIEW = "Build View";
	public static final String OBJECT_TYPE_VIEW = "Object Type View";
	public static final String CAUSAL_VIEW = "Model Causality View";
	public static final List viewNames = Collections.unmodifiableList
	        (Arrays.asList(new String[]{BUILD_VIEW, OBJECT_TYPE_VIEW, CAUSAL_VIEW}));

	public static final String PARAMETERS_FILTER = "Parameters Filter";
	public static final String RELATIONS_FILTER = "Relations Filter";
	public static final String CONTEXTS_FILTER = "Contexts Filter";
	public static final String SUBSCRIPTIONS_FILTER = "Subscriptions Filter";

	public static final String SUBSCRIPTION_ADDED = "subscription added";
	public static final String SUBSCRIPTION_DELETED = "subscription deleted";

	public static final String RESOURCE_SUBSCRIBED = "resource subscribed";
	public static final String RESOURCE_UNSUBSCRIBED = "resource unsubscribed";
	public static final String IMODEL_DELETED = "imodel deleted";

	public static final String AUXFILES_MODIFIED = "auxfiles modified";

	public static final Id BUILD_CONTEXT_ID = new Id("BUILD_CXT");
	public static final Id FILE_CONTEXT_ID = new Id("FILE_CXT");
	public static final String FILES_CONTEXT = "Files";

	public Context getBuildContext();

	public Filter getFilter(String filterName);

	public ConnectionMappingManager getMappingManager();

	public IntegrationProject getIntegrationProject();

	public boolean isIntegrationModel();

	public Collection getModelInterfaces();

	public List getSubscriptions();

	public void addSubscriptionsListener(DListListener l);

	public void removeSubscriptionsListener(DListListener l);

}
