// RunInterface.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.objectmodel.project;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.FileSystemFunctions;
import mit.cadlab.dome3.objectmodel.model.ClientModelRuntime;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.util.xml.XMLUtils;

public class RunInterface extends BrowseInterface
{

	public RunInterface(ServerConnection svrConn, ClientModelRuntime parent, String parentId, String ifaceDeployId, String name, String description)
	{
		super(svrConn, parent, parentId, ifaceDeployId, name, description);
	}

	public void loadInterface(CompoundId id)
	{
		if (iface == null && svrConn != null) {
			String ifaceXml = FileSystemFunctions.getInterfaceDescription(svrConn, ifaceDeployId);
			iface = new ModelInterfaceRuntimeClient(id, svrConn, (ClientModelRuntime) parentObj, XMLUtils.stringToXmlElement(ifaceXml));
			setView(CAUSAL_VIEW);
		}
	}

	public void setInterface(ModelInterfaceRuntimeClient iface)
	{
		this.iface = iface;
		setView(CAUSAL_VIEW);
	}

}
