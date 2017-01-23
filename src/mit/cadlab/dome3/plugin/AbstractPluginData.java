// AbstractPluginData.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.plugin;

import mit.cadlab.dome3.objectmodel.modelobject.parameter.Parameter;

public abstract class AbstractPluginData implements PluginData
{
	protected Parameter parameter;

	public Parameter getParameter()
	{
		return parameter;
	}

}
