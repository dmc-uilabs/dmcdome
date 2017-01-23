package mit.cadlab.dome3.objectmodel.model;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.objectrecord.ClientInterfaceRecord;
import mit.cadlab.dome3.network.server.RuntimeObjectInfo;
import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspaceRuntime;
import mit.cadlab.dome3.objectmodel.ClientRuntimeScope;
import mit.cadlab.dome3.objectmodel.modelinterface.ModelInterfaceRuntimeClient;
import mit.cadlab.dome3.util.DomeJavaBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Name: ClientModelRuntime
 * User: thorek
 * Date: Mar 19, 2003
 * Time: 12:29:05 PM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class ClientModelRuntime extends DomeJavaBean implements ClientRuntimeScope
{
	private String name;
	private String description;
	private String url;
	private String status = "";
	private CompoundId runtimeId;
	private ServerConnection serverConn;
	private List interfaces;
	public static final String MODELSTATUS = "modelStatus";

	/**
	 *
	 * @param modelId Runtime model id
	 * @param name Model name
	 * @param description Model description
	 * @param ifaceInfo Interface static info structure
	 * @param serverConn Server connection
	 * @param playspace Playspace runtime object. This is stored in the client interface
	 * records because the interfaces in the playspace gui needs easy access to the
	 * playspace object. If this playspace is not needed this variable can be null.
	 */
	public ClientModelRuntime(CompoundId modelId, String name, String description,
	                          RuntimeObjectInfo ifaceInfo, ServerConnection serverConn,
	                          ClientPlayspaceRuntime playspace)
	{
		this.runtimeId = new CompoundId(modelId);
		runtimeId.setInterfaceStaticId(null);
		this.name = name;
		this.description = description;
		this.serverConn = serverConn;
		this.url = serverConn.getServerPort();

		interfaces = new ArrayList();
		for (Iterator ifaceIter = ifaceInfo.getAllStaticIds().iterator(); ifaceIter.hasNext();) {
			String id = (String) ifaceIter.next();
			name = ifaceInfo.getName(id);
			description = ifaceInfo.getDescription(id);
			String url = serverConn.getServerPort();
			CompoundId interfaceId = new CompoundId (runtimeId);
			interfaceId.setInterfaceStaticId(id);
			ClientInterfaceRecord record;
			record = new ClientInterfaceRecord(serverConn, interfaceId, name, description, url, playspace);
			interfaces.add(record);
		}
	}


	public ClientModelRuntime(CompoundId modelId, String name,
	                          String description, String url)
	{
		this.runtimeId = new CompoundId(modelId);
		runtimeId.setInterfaceStaticId(null);
		this.name = name;
		this.description = description;
		this.url = url;
	}

	public String getUrl()
	{
		return url;
	}

	public ServerConnection getServerConnection()
	{
		return serverConn;
	}

	public void createInterfaceRecords(ServerConnection con,
	                                   RuntimeObjectInfo ifaceInfo,
	                                   ClientPlayspaceRuntime playspace)
	{
		this.serverConn = con;

		interfaces = new ArrayList();
		for (Iterator ifaceIter = ifaceInfo.getAllStaticIds().iterator(); ifaceIter.hasNext();) {
			String id = (String) ifaceIter.next();
			name = ifaceInfo.getName(id);
			description = ifaceInfo.getDescription(id);
			String url = serverConn.getServerPort();
			CompoundId interfaceId = new CompoundId (runtimeId);
			interfaceId.setInterfaceStaticId(id);
			ClientInterfaceRecord record;
			record = new ClientInterfaceRecord(con, interfaceId, name, description, url, playspace);
			interfaces.add(record);
		}
	}

	public void setStatus(String status)
	{
		String oldStatus = this.status;
		this.status = status;
		firePropertyChange(MODELSTATUS, oldStatus, status);
	}

	public String getStatus()
	{
		return status;
	}

	public String getDescription()
	{
		return description;
	}

	public String getName()
	{
		return name;
	}

	public String getId()
	{
		return runtimeId.getModelStaticId();
	}

	public CompoundId getCompoundId ()
	{
		return runtimeId;
	}


	/**
	 * @return List of ClientInterfaceRecord objects
	 */
	public List getInterfaceStaticInfo()
	{
		if (interfaces == null) {
			return Collections.EMPTY_LIST;
		}
		return Collections.unmodifiableList(interfaces);
	}

	/** Creates corresponding server side interface */
	public void instantiateAllInterfaces() {
		for (Iterator iterator = interfaces.iterator(); iterator.hasNext();) {
			ClientInterfaceRecord clientInterfaceRecord = (ClientInterfaceRecord) iterator.next();
			ModelInterfaceRuntimeClient iface = clientInterfaceRecord.getInterface();
//			iface.incrementReferenceCount();
		}
	}
}
