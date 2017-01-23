// ServerAdministrationFunctions.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.client.functions;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.server.db.DbConstants;

import org.apache.xmlrpc.XmlRpcException;

import java.util.Vector;

public class ServerAdministrationFunctions
{

	public static void shutdown(ServerConnection svrConn)
	{
		try {
			svrConn.execute(DbConstants.FUNC_TYPE_SERVER_ADMIN + "." + DbConstants.SHUTDOWN);
		} catch (Exception e) {
			// ignore
		}
	}

}
