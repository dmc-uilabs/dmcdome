package mit.cadlab.dome3.objectmodel.project.info;

import org.dom4j.Element;
import mit.cadlab.dome3.objectmodel.project.info.ProjectIntegrationModelInfo;

/**
 * Created by IntelliJ IDEA.
 * Name: ProjectIntegrationModelInfoRuntime
 * User: thorek
 * Date: Apr 19, 2003
 * Time: 12:17:32 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */

public class ProjectIntegrationModelInfoRuntime extends ProjectIntegrationModelInfo
{
	private Object resourceObject;


	public ProjectIntegrationModelInfoRuntime(Element xmlDescription)
	{
		super(xmlDescription);

		// create object
	}

	public void setObject(Object resourceObject)
	{
		this.resourceObject = resourceObject;
	}

	public Object getObject()
	{
		return resourceObject;
	}
}
