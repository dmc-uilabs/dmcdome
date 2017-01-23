// AbstractBatchModelRuntime.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.plugin.batch;

import mit.cadlab.dome3.plugin.PluginModelRuntime;
import mit.cadlab.dome3.network.CompoundId;
import org.dom4j.Element;

import java.util.List;

public abstract class AbstractBatchModelRuntime extends PluginModelRuntime
{
	public AbstractBatchModelRuntime(CompoundId parentId, Element xml, boolean isProjectResource)
	{
		super(parentId, xml, isProjectResource);
	}

	protected void executeNativePlugin(List affectedOutputParams) {
		if (writeInputParameters()) {
		    if (runModel()) {
			    readOutputParameters(affectedOutputParams);
			}
		}
	}

	// todo: perhaps change the following methods so they throw exceptions instead?
	protected abstract boolean writeInputParameters();

	protected abstract boolean runModel();

	protected abstract boolean readOutputParameters(List affectedOutputParams);

	protected abstract void loadModelDefinition();

}
