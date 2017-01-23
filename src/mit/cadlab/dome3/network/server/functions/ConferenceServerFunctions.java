package mit.cadlab.dome3.network.server.functions;

import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.client.conference.ConferenceClientConnection;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Apr 2, 2003
 * Time: 3:12:40 AM
 * To change this template use Options | File Templates.
 */
public class ConferenceServerFunctions
{
	public static boolean informMembersOfNewMember(ConferenceClientConnection clientConn, String newMember,
	                                               String ownerGUI)
	{
		Boolean result = (Boolean) clientConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_CLIENT + "."
		                                              + DbConstants.INFORM_CONFERENCE_OF_NEW_MEMBER,
		                                              Vectors.create(newMember, ownerGUI));
		return result.booleanValue();
	}

	public static boolean sendMessageToConferenceMember(ConferenceClientConnection clientConn,
	                                                    String msgAuthor, String msgTo,String message)
	{
		Boolean result = (Boolean) clientConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_CLIENT + "."
		                                              + DbConstants.SEND_MESSAGE_TO_CONFERENCE_MEMBER,
		                                              Vectors.create(msgAuthor,msgTo,message));
		return result.booleanValue();
	}

	public static boolean informMembersOfDeparture(ConferenceClientConnection clientConn, String oldMember,
	                                               String ownerGUI)
	{
		Boolean result = (Boolean) clientConn.execute(DbConstants.FUNC_TYPE_CONFERENCE_CLIENT + "."
		                                              + DbConstants.INFORM_CONFERENCE_OF_OLD_MEMBER,
		                                              Vectors.create(oldMember, ownerGUI));
		return result.booleanValue();
	}
}
