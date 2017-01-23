// RunProjectResourceInfo.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.project.info;

import mit.cadlab.dome3.gui.mode.run.RunFocusTracker;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.connection.LoginUtils;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.objectmodel.project.RunInterface;
import org.dom4j.Element;

import java.util.Vector;

public class RunProjectResourceInfo extends ProjectResourceInfo
{
	protected ServerConnection svrConn;

	public RunProjectResourceInfo(Element xmlDescription)
	{
		super(xmlDescription);
	}

	public ServerConnection getServerConnection()
	{
		if (svrConn == null) {
			svrConn = LoginUtils.getServerConnection(RunFocusTracker.getCurrentComponent(),
			                                         resourceHostName + ":" + resourcePort,
			                                         RunMode.getClientUrl());
			svrConn.addReference();
		}
		return svrConn;
	}

	public void setServerConnection(ServerConnection svrConn)
	{
		this.svrConn = svrConn;
	}

	public void releaseServerConnection()
	{
		if (svrConn != null) {
			svrConn.removeReference();
			svrConn = null;
		}
	}

	public void loadResource()
	{
		if (resourceLoaded)
			return;
		ServerConnection conn = getServerConnection();
		if (conn == null)
			return;

		Vector info = loadResource(svrConn, DbConstants.FILESYSTEM_BROWSE);
		if (MODEL_RESOURCE.equals(type)) {
			String ifaceName, ifaceId, ifaceDesc, ifaceDate;
			int ifaceVer;
			for (int i = 0; i < info.size(); i++) {
				Vector ifaceInfo = (Vector) info.elementAt(i);
				ifaceName = (String) ifaceInfo.get(0);
				ifaceId = (String) ifaceInfo.get(1);
				ifaceDesc = (String) ifaceInfo.get(2);
				ifaceVer = ((Integer) ifaceInfo.get(3)).intValue();
				ifaceDate = ifaceInfo.get(4).toString();
				view.add(new RunInterface(svrConn, null, this.getResourceUniqueId(), ifaceId, ifaceName, ifaceDesc));
			}
		}
		else { // project resource
			//create(projectContents, resourceInterfaces);
		}
		resourceLoaded = true;
	}

}
