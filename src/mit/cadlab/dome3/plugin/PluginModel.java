// PluginModel.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.plugin;

import mit.cadlab.dome3.objectmodel.model.Model;
import mit.cadlab.dome3.objectmodel.MultiViewSupport;
import mit.cadlab.dome3.objectmodel.modelcomponent.filter.Filterable;
import mit.cadlab.dome3.objectmodel.modelcomponent.mapping.plugin.PluginMappingManager;
import mit.cadlab.dome3.objectmodel.util.TypeInfo;
import mit.cadlab.dome3.objectmodel.util.causality.DependencyInfo;

public interface PluginModel extends Model, Filterable, MultiViewSupport
{

	public static final TypeInfo TYPE_INFO = new TypeInfo("Plugin Model", "PluginModel");

	public static final String DEPENDENCY_INFO = "dependencyInfo";

	public String getPluginTypeName();

	public String getPluginXmlType();

	public PluginMappingManager getPluginMappingManager();

	public DependencyInfo getDependencyInfo();

}
