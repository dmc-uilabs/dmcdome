// BuildProjectIntegrationModelTreeObject.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.gui.objectmodel.project.build;

import mit.cadlab.dome3.gui.mode.build.BuildMode;
import mit.cadlab.dome3.gui.objectmodel.project.ProjectIntegrationModelTreeObject;
import mit.cadlab.dome3.objectmodel.model.dome.DomeModelBuilder;
import mit.cadlab.dome3.objectmodel.project.info.BuildProjectIntegrationModelInfo;

public class BuildProjectIntegrationModelTreeObject extends ProjectIntegrationModelTreeObject
{
	public BuildProjectIntegrationModelTreeObject(BuildProjectIntegrationModelInfo m)
	{
		super(m);
	}

	// override this method
	protected void makeGui()
	{
		gui = BuildMode.openIModel((DomeModelBuilder) info.getModel());
	}

}
