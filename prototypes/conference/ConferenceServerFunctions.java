package conference;

import mit.cadlab.dome.server.db.DbConstants;
import mit.cadlab.dome.network.client.functions.Vectors;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Apr 2, 2003
 * Time: 3:12:40 AM
 * To change this template use Options | File Templates.
 */
public class ConferenceServerFunctions
{
	public static Boolean informMembersOfNewMember(ConferenceClientConnection clientConn, String newMember, String ownerGUI)
	{
		return (Boolean)clientConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_CLIENT+"."+DbConstants.INFORM_CONFERENCE_OF_NEW_MEMBER, Vectors.create(newMember, ownerGUI));
	}
	public static Boolean sendMessageToConferenceMember(ConferenceClientConnection clientConn, String msgAuthor, String message)
	{
		return (Boolean) clientConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_CLIENT+"."+DbConstants.SEND_MESSAGE_TO_CONFERENCE_MEMBER, Vectors.create(msgAuthor, message));
	}
	public static Boolean informMembersOfDeparture(ConferenceClientConnection clientConn, String oldMember, String ownerGUI)
	{
		return (Boolean) clientConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_CLIENT+"."+DbConstants.INFORM_CONFERENCE_OF_OLD_MEMBER, Vectors.create(oldMember, ownerGUI));
	}

}
