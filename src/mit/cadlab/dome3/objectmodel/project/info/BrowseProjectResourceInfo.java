package mit.cadlab.dome3.objectmodel.project.info;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.objectmodel.project.info.ProjectResourceInfo;
import org.dom4j.Element;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: sittha
 * Date: Apr 12, 2003
 * Time: 5:40:52 PM
 * To change this template use Options | File Templates.
 */
public class BrowseProjectResourceInfo extends ProjectResourceInfo
{

	protected ServerConnection svrConn;

	public BrowseProjectResourceInfo(Element xmlDescription)
	{
		super(xmlDescription);
	}


	public void loadResource()
	{
		if (resourceLoaded)
			return;
		Vector resourceInfo = null;
		if (MODEL_RESOURCE.equals(type))
			resourceInfo = FileSystemFunctions.getModelInfo(svrConn, resourceDeployId);
		else
			resourceInfo = FileSystemFunctions.getProjectInfo(svrConn, resourceDeployId);

		resourceName = (String) resourceInfo.get(1);
		resourceDescription = (String) resourceInfo.get(2);
		resourceVersion = resourceInfo.get(3).toString();

		//view.add(locationInfo);
		Vector info = loadResource(svrConn, DbConstants.FILESYSTEM_SUBSCRIBE);
		resourceLoaded = true;
	}
}
