package mit.cadlab.dome3.network.client.functions;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.db.DbConstants;

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 31, 2003
 * Time: 9:04:36 AM
 * To change this template use Options | File Templates.
 */

public class ConferenceClientFunctions
{
	public static boolean joinConference(ServerConnection svrConn, String playspaceId)
	{
		Boolean result = (Boolean) svrConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_SERVER + "."
		                                           + DbConstants.JOIN_CONFERENCE,
		                                           Vectors.create(svrConn.getConnectionId(), playspaceId));
		return result.booleanValue();
	}

	public static Vector whoIsInTheConferenceRoom(ServerConnection svrConn, String playspaceId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_SERVER + "."
		                                + DbConstants.WHO_IS_IN_THE_CONFERENCE_ROOM,
		                                Vectors.create(svrConn.getConnectionId(), playspaceId));
	}

	public static boolean sendMessageToEveryone(ServerConnection svrConn, String message, String playspaceId)
	{
		Boolean result = (Boolean) svrConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_SERVER + "."
		                                           + DbConstants.SEND_MESSAGE_TO_EVERYONE,
		                                           Vectors.create(svrConn.getConnectionId(), message, playspaceId));
		return result.booleanValue();
	}

	public static boolean sendMessageToMember(ServerConnection svrConn, String msgTo,
	                                          String message, String playspaceId)
	{
		Boolean result = (Boolean) svrConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_SERVER + "."
		                                           + DbConstants.SEND_MESSAGE_TO_MEMBER,
		                                           Vectors.create(svrConn.getConnectionId(), msgTo, message, playspaceId));
		return result.booleanValue();
	}

	public static boolean leaveConference(ServerConnection svrConn, String playspaceId)
	{
		Boolean result = (Boolean) svrConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_SERVER + "."
		                                           + DbConstants.LEAVE_CONFERENCE,
		                                           Vectors.create(svrConn.getConnectionId(), playspaceId));
		return result.booleanValue();
	}
}
