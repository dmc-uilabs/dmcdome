// DomeServer.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.conference;

import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.client.conference.ConferenceClientConnection;
import mit.cadlab.dome3.network.server.functions.ConferenceServerFunctions;
import org.apache.xmlrpc.XmlRpcException;

import java.sql.SQLException;
import java.util.*;

/**
 * The ConferenceServer class consists of all the functionality required in the Chat Server.
 * This is a restructuring of the original implementation
 * todo: support databases on different machine than ConferenceServer
 */
public class ConferenceServer
{
	private static HashMap conferenceRoomInfo = new HashMap();
	private static HashMap clientSessionNameMap = new HashMap();    // maps user names to session ids
	private static HashMap clientConferenceMap = new HashMap();     // maps names to playspace lists

	/**
	 * Chat server constructor
	 * The server URL is "http://localhost:port/RPC2"
	 * @param serverPort The port number to be used by clients to talk to the server
	 */

//	Method for joining a playspace conference

	public static boolean joinConference(String loginName, String sessionId,
	                                     String playspaceId, ConferenceClientConnection clientConn)
	{
		ConferenceRoom confRoom = null;

		// create the conference or retrieve an existing one
		if (!conferenceRoomInfo.containsKey(playspaceId)) {
			confRoom = new ConferenceRoom();
			conferenceRoomInfo.put(playspaceId, confRoom);
		} else
			confRoom = (ConferenceRoom) conferenceRoomInfo.get(playspaceId);

		// deal with error
		if (confRoom == null) {
			System.err.println("ERROR: could not add member.  ConferencePanel Room does not exist");
			return false;
		}

        //check if this person is already in that conference room
       // if(confRoom.getConferenceMembers().contains(loginName))
        //{
         //   System.out.println("already in conference");
         //   return false;
       // }
		// add member to conference
		confRoom.addConferenceMember(loginName, clientConn);
		// maintain a map of user sessions to conferences
		addToClientConferenceMap(loginName, sessionId, playspaceId);
		// tell the other conference members someone has joined
		informConferenceOfNewMember(confRoom, loginName);

		return true;
	}


	/**
	 * Store a mapping between an client session id and a list of playspaces. This will allow us
	 * when a client logs off to tell the other conference members about it.
	 * @param name User name
	 * @param sessionId Session id for the user
	 * @param playspaceId Playspace id
	 */
	private static void addToClientConferenceMap(String name, String sessionId, String playspaceId)
	{
		// map session ids to user names
		clientSessionNameMap.put(sessionId, name);

		ArrayList playspaceList = (ArrayList) clientConferenceMap.get(name);
		if (playspaceList == null) {
			// create the playspace list
			playspaceList = new ArrayList();
			clientConferenceMap.put(name, playspaceList);
		}

		// add the playspace id to the playspace list
		if (!playspaceList.contains(playspaceId))
			playspaceList.add(playspaceId);
	}


	/**
	 * Called when a user leaves a conference in order to remove the user from the map
	 * that associates users with playspaces.
	 * @param name
	 * @param playspaceId
	 */
	private static void removeFromClientConferenceMap(String name, String playspaceId)
	{
		ArrayList playspaceList = (ArrayList) clientConferenceMap.get(name);
		if (playspaceList != null && playspaceList.contains(playspaceId))
			playspaceList.remove(playspaceId);
	}


	/**
	 * This is called from DomeServer to tell us that a client has logged out. This
	 * will remove the client from all of its playspace conferences.
	 * @param sessionId Session id
	 */
	public static void clientLogout(String sessionId)
	{
		String name = (String) clientSessionNameMap.get(sessionId);

		ArrayList list = (ArrayList) clientConferenceMap.get(name);
		if (list != null) {
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				String playspaceId = (String) iter.next();
				leaveConference(name, playspaceId);
			}
		}
		clientConferenceMap.remove(name);
		clientSessionNameMap.remove(sessionId);
	}


// Method for getting current members of the playspace conference
	public static Vector whoIsInTheConferenceRoom(String playspaceId)
	{
		Vector conferenceMembers = new Vector();
		if (conferenceRoomInfo.containsKey(playspaceId)) {
			ConferenceRoom room = ((ConferenceRoom) conferenceRoomInfo.get(playspaceId));
			List allMembers = new ArrayList(room.getConferenceMembers());
			ListIterator iterator = allMembers.listIterator();
			while (iterator.hasNext()) {
				String name = (String) iterator.next();
				conferenceMembers.add(name);
			}
		} else {
			System.out.println("ERROR: Invalid playspaceId - playspace does not exist");
		}

		return conferenceMembers;
	}

// Method for receiving message and calling a method for sending messages to everyone in the playspace conference
	public static boolean sendMessageToEveryone(String msgAuthor, String message, String playspaceId)
	{
		ConferenceRoom currentConference = (ConferenceRoom) conferenceRoomInfo.get(playspaceId);
		List conferenceMembers = currentConference.getConferenceMembers();
		ListIterator iterator = conferenceMembers.listIterator();
		while (iterator.hasNext()) {
			String memberName = (String) iterator.next();
            System.out.println("send to :"+memberName);
			sendMessageToConferenceMember(currentConference, msgAuthor, memberName, message);
		}
		return true;
	}

// Method for receiving a message and calling a method for sending messages to a receipient in the playspace conference
	public static boolean sendMessageToMember(String msgAuthor, String msgTo, String message, String playspaceId)
	{
		if (msgAuthor.equals(msgTo))
			return false;
		ConferenceRoom currentConference = (ConferenceRoom) conferenceRoomInfo.get(playspaceId);
		if (currentConference.isAMember(msgTo))
			sendMessageToConferenceMember(currentConference, msgAuthor, msgTo, message);
		else {
			System.out.println("Person not a member of playspace conference");
			return false;
		}
		return true;
	}

// Method for leaving a playspace conference
	public static boolean leaveConference(String name, String playspaceId)
	{
		ConferenceRoom currentConference = (ConferenceRoom) conferenceRoomInfo.get(playspaceId);
		//if (currentConference.getConferenceMembers().size() > 1) {
        if (currentConference.getConferenceMembers().size() >=1) {
			if (currentConference.isAMember(name)) {
				//List conferenceMembers = currentConference.getConferenceMembers();
				//for (int i = conferenceMembers.size() - 1; i >= 0; i--) {
				//	String memberName = (String) conferenceMembers.get(i);
				//	if (memberName.equals(name)) {
				//		conferenceMembers.remove(i);
				//		break;
				//	}
                //}
                currentConference.removeConferenceMember(name);
				removeFromClientConferenceMap(name, playspaceId);
			} else {
				System.out.println("Person not a member of playspace conference");
				return false;
			}
			informConferenceOfMemberLeaving(currentConference, name);
		}
		return true;
	}

// Methods that communicate with ConferencePanel Client connections.

// Method for sending messages to playspace conference members
	public static boolean sendMessageToConferenceMember(ConferenceRoom confRoom, String msgAuthor,
	                                                    String msgTo, String message)
	{
		return ConferenceServerFunctions.sendMessageToConferenceMember(confRoom.getClientConnection(msgTo),
		                                                               msgAuthor,
                                                                       msgTo,
		                                                               message);
	}

	public static boolean informConferenceOfNewMember(ConferenceRoom confRoom, String newMember)
	{
		List conferenceMembers = confRoom.getConferenceMembers();
		ListIterator iterator = conferenceMembers.listIterator();
		while (iterator.hasNext()) {
			String memberName = (String) iterator.next();
			if (!memberName.equals(newMember)) {
				ConferenceServerFunctions.informMembersOfNewMember(confRoom.getClientConnection(memberName),
				                                                   newMember,
				                                                   memberName);
			}
		}
		return true;
	}

	public static boolean informConferenceOfMemberLeaving(ConferenceRoom confRoom, String memberName)
	{
		List conferenceMembers = confRoom.getConferenceMembers();
		ListIterator iterator = conferenceMembers.listIterator();
		while (iterator.hasNext()) {
			String member = (String) iterator.next();
			if (member.equals(memberName))
				continue;
			ConferenceServerFunctions.informMembersOfDeparture(confRoom.getClientConnection(member),
			                                                   memberName,
			                                                   member);
		}
		return true;
	}

	public static Vector getUserInfoFromSession(String sessisonId) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getUserInfoFromSession");

		try {
			String query;
			query = "select USER_ID, NAME, URL from sessions, users_groups where USER_ID = users_groups.ID and SESSIONS.ID = '"
			        + sessisonId + "'";
			Vector v = DbUtils.executeQuery(query, true);
			if (v.isEmpty()) {
				throw new XmlRpcException(DbErrors.XMLRPC_BAD_SESSION_ID,
				                          DbErrors.XMLRPC_BAD_SESSION_ID_MSG);
			}
			return v;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}
}
