// BuildProjectIntegrationModelInfo.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.project.info;

import mit.cadlab.dome3.objectmodel.model.dome.DomeModel;
import org.dom4j.Element;

public class BuildProjectIntegrationModelInfo extends ProjectIntegrationModelInfo
{
	public BuildProjectIntegrationModelInfo(DomeModel model)
	{
		super(model);
	}

	public BuildProjectIntegrationModelInfo(BuildProjectIntegrationModelInfo modelinfo)
	{
		super(modelinfo);
	}

	public BuildProjectIntegrationModelInfo(Element xmlDescription)
	{
		super(xmlDescription);
	}

}
