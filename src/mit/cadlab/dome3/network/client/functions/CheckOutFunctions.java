// CheckOutFunctions.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.client.functions;

import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.client.connection.ServerConnection;

import java.util.Vector;

/**
 * set of functions for checking out files from the server
 */
public class CheckOutFunctions
{

	public static Vector checkoutModel(ServerConnection svrConn, String modelId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_CHECKOUT_FILES + "." + DbConstants.CHECKOUT_MODEL,
		                                Vectors.create(svrConn.getConnectionId(), modelId));
	}

	public static Vector checkoutPlayspace(ServerConnection svrConn, String playspaceId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_CHECKOUT_FILES + "." + DbConstants.CHECKOUT_PLAYSPACE,
		                                Vectors.create(svrConn.getConnectionId(), playspaceId));
	}

	public static Vector checkoutProject(ServerConnection svrConn, String projectId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_CHECKOUT_FILES + "." + DbConstants.CHECKOUT_PROJECT,
		                                Vectors.create(svrConn.getConnectionId(), projectId));
	}

    public static Vector checkoutAuxFileForModel(ServerConnection svrConn, String auxfileId,String modelId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_CHECKOUT_FILES + "." + DbConstants.CHECKOUT_AUXFILE_FOR_MODEL,
		                                Vectors.create(svrConn.getConnectionId(),auxfileId, modelId));
	}

}
