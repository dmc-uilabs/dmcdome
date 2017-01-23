package conference;

import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.network.client.functions.Vectors;
import mit.cadlab.dome.server.db.DbConstants;

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
	public static Boolean joinConference(ConferenceServerConnection svrConn, String clientUrl, String clientId, Object playspaceId)
	{
		return (Boolean) svrConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_SERVER+"."+DbConstants.JOIN_CONFERENCE,Vectors.create(clientUrl, clientId, playspaceId));
	}
	public static Vector whoIsInTheConferenceRoom(ConferenceServerConnection svrConn, String clientId, Object playspaceId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_SERVER+"."+DbConstants.WHO_IS_IN_THE_CONFERENCE_ROOM, Vectors.create(clientId, playspaceId));
	}
	public static Boolean sendMessageToEveryone(ConferenceServerConnection svrConn, String authorId, String message, Object playspaceId)
	{
		return (Boolean) svrConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_SERVER+"."+DbConstants.SEND_MESSAGE_TO_EVERYONE, Vectors.create(authorId, message, playspaceId));
	}
	public static Boolean sendMessageToMember(ConferenceServerConnection svrConn, String authorId, String msgTo, String message, Object playspaceId)
	{
		return (Boolean) svrConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_SERVER+"."+DbConstants.SEND_MESSAGE_TO_MEMBER, Vectors.create(authorId, msgTo, message, playspaceId));
	}
	public static Boolean leaveConference(ConferenceServerConnection svrConn, String clientId, Object playspaceId)
	{
		return (Boolean) svrConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_SERVER+"."+DbConstants.LEAVE_CONFERENCE, Vectors.create(clientId, playspaceId));
	}
}
