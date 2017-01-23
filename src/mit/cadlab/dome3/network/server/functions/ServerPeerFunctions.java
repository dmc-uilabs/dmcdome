package mit.cadlab.dome3.network.server.functions;

import mit.cadlab.dome3.network.CompoundId;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.ServerPeerConstants;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * Name: ServerPeerFunctions
 * User: thorek
 * Date: Apr 15, 2003
 * Time: 3:06:29 PM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class ServerPeerFunctions
{

	/**
	 * Delete a remote resource.
	 * @param svrConn Server connection
	 * @param runtimeId Resource runtime id
	 */
	public static void deleteRemoteResource(ServerConnection svrConn, CompoundId runtimeId, String type)
	{
		svrConn.executeAsync (ServerPeerConstants.FUNC_TYPE_SERVER_PEER + "."
		                      + ServerPeerConstants.DELETE_REMOTE_RESOURCE,
		                      Vectors.create(runtimeId.toString(), type));
	}


	/**
	 * Get a remote resource graph.
	 * @param svrConn Server connection
	 * @param resourceId Resource id
	 * @param interfaceIds Interface ids
	 * @return Resource graph
	 */
	public static Vector getResourceGraph(ServerConnection svrConn, CompoundId resourceId,
	                                      Vector interfaceIds)
	{
		Vector result;
		result = (Vector) svrConn.execute(ServerPeerConstants.FUNC_TYPE_SERVER_PEER + "."
		                                  + ServerPeerConstants.GET_RESOURCE_GRAPH,
		                                  Vectors.create(resourceId.toString(), interfaceIds));
		return result;
	}

    /**
     * Set the external graph of a remote resource.
     * @param svrConn Server connection
     * @param resourceId Resource id
     * @param extGraphXml External graph
     */
    public static void setResourceExternalGraph(ServerConnection svrConn, CompoundId resourceId,
                                                String extGraphXml) {
        svrConn.execute(ServerPeerConstants.FUNC_TYPE_SERVER_PEER + "." + ServerPeerConstants.SET_RESOURCE_EXTERNAL_GRAPH,
                        Vectors.create(resourceId.toString(), extGraphXml));
    }

	/**
	 * Tell resource that project run is complete
	 * @param svrConn Server connection
	 * @param resourceId Resource id
	 */
	public static void notifyProjectRunCompleted(ServerConnection svrConn, CompoundId resourceId)
	{
		svrConn.executeAsync(ServerPeerConstants.FUNC_TYPE_SERVER_PEER + "."
		                     + ServerPeerConstants.NOTIFY_PROJECT_RUN_COMPLETE,
		                     Vectors.create(resourceId.toString()));
	}

}
