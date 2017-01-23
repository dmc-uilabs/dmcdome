// DomeServer.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package conference;

import mit.cadlab.dome.network.server.Debug;
import mit.cadlab.dome.server.db.DbConstants;
import org.apache.xmlrpc.WebServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

/**
 * The ConferenceServer class consists of all the functionality required in the Chat Server.
 * This is a restructuring of the original implementation
 * todo: support databases on different machine than ConferenceServer
 */
public class ConferenceServer
{

	private String hostName;  //name or IP address of the host
	private int port;
	private WebServer webserver;
	private static String defaultServerPort = null;
	private static HashMap _conferenceRoomInfo = new HashMap();

	public static String getDefaultServerPort ()
	{
		if (defaultServerPort == null)
		{
			InetAddress addr = null;
			try {
				addr = InetAddress.getLocalHost();
				defaultServerPort = addr.getCanonicalHostName();
			}
			catch (UnknownHostException e) {
				defaultServerPort = "localhost";
			}
			defaultServerPort += ":8082";

		}
		return defaultServerPort;
	}


	/**
	 * Chat server constructor
	 * The server URL is "http://localhost:port/RPC2"
	 * @param serverPort The port number to be used by clients to talk to the server
	 */
	public ConferenceServer(int serverPort)
	{
		Debug.setDebugLevel(Debug.ALL); // trace method calls through server
		try {
            InetAddress addr = InetAddress.getLocalHost();
			hostName = addr.getHostName();
		}
		catch (UnknownHostException e) {
			hostName = "localhost";
		}

		this.port = serverPort;
		try {

			webserver = new WebServer(port);
			webserver.addHandler(DbConstants.FUNC_TYPE_CONFERENCE_SERVER, new ConferenceServerHandler());
			Debug.trace(Debug.STATUS, hostName + ": ConferenceServer started on port " + port + " at " + new Date());
		}
		catch (IOException ie) {
			throw new RuntimeException("Could not instantiate ConferenceServer because of I/O exception");
		}
	}

	public String getHostName()
	{
		return hostName;
	}

	public int getPort()
	{
		return port;
	}

//	Method for joining a playspace conference

	public static Boolean joinConference(String loginName, Object playspaceId, ConferenceClientConnection clientConn)
	{
		ConferenceRoom confRoom = null;
		if(!ConferenceServer._conferenceRoomInfo.containsKey(playspaceId))
		{
			confRoom = new ConferenceRoom();
			ConferenceServer._conferenceRoomInfo.put(playspaceId, confRoom);
		}
		else
			confRoom = (ConferenceRoom)ConferenceServer._conferenceRoomInfo.get(playspaceId);
		if(confRoom != null)
			confRoom.addConferenceMember(loginName, clientConn);
		else
		{
			System.out.println("ERROR: could not add member.  Conference Room does not exist");
			return new Boolean(false);
		}
		ConferenceServer.informConferenceOfNewMember(confRoom, loginName);
		return new Boolean(true);
	}

// Method for getting current members of the playspace conference

	public static Vector whoIsInTheConferenceRoom(String memberName, Object playspaceId)
	{
		Vector conferenceMembers = new Vector();
		if (ConferenceServer._conferenceRoomInfo.containsKey(playspaceId))
		{
			List allMembers = new ArrayList(((ConferenceRoom) ConferenceServer._conferenceRoomInfo.get(playspaceId)).getConferenceMembers());
			ListIterator iterator = allMembers.listIterator();
			while (iterator.hasNext())
			{
				String name = (String) iterator.next();
				conferenceMembers.add(name);
			}
		}
		else
		{
			System.out.println("ERROR: Invalid playspaceId - playspace does not exist");
		}
		return
		    conferenceMembers;
	}

// Method for receiving message and calling a method for sending messages to everyone in the playspace conference
	public static Boolean sendMessageToEveryone(String msgAuthor, String message, Object playspaceId)
	{
		ConferenceRoom currentConference = (ConferenceRoom)ConferenceServer._conferenceRoomInfo.get(playspaceId);
		List conferenceMembers = currentConference.getConferenceMembers();
		ListIterator iterator = conferenceMembers.listIterator();
		while(iterator.hasNext())
		{
			String memberName = (String)iterator.next();
			ConferenceServer.sendMessageToConferenceMember(currentConference, msgAuthor, memberName, message);
		}
		return new Boolean(true);
	}

// Method for receiving a message and calling a method for sending messages to a receipient in the playspace conference
	public static Boolean sendMessageToMember(String msgAuthor, String msgTo, String message, Object playspaceId)
	{
		if(msgAuthor.equals(msgTo))
			return new Boolean(false);
		ConferenceRoom currentConference = (ConferenceRoom)ConferenceServer._conferenceRoomInfo.get(playspaceId);
		if(currentConference.isAMember(msgTo))
			ConferenceServer.sendMessageToConferenceMember(currentConference, msgAuthor, msgTo, message);
		else
		{
			System.out.println("Person not a member of playspace conference");
			return new Boolean(false);
		}
		return new Boolean(true);
	}

// Method for leaving a playspace conference
	public static Boolean leaveConference(String memberId, Object playspaceId)
	{
		ConferenceRoom currentConference = (ConferenceRoom)ConferenceServer._conferenceRoomInfo.get(playspaceId);
		if(currentConference.getConferenceMembers().size() > 1)
		{
			if (currentConference.isAMember(memberId))
			{
				List conferenceMembers = currentConference.getConferenceMembers();
				for(int i=conferenceMembers.size()-1; i>=0; i--)
				{
					String memberName = (String)conferenceMembers.get(i);
					if(memberName.equals(memberId))
					{
						conferenceMembers.remove(i);
						break;
					}
				}
			}
			else
			{
				System.out.println("Person not a member of playspace conference");
				return new Boolean(false);
			}
			ConferenceServer.informConferenceOfMemberLeaving(currentConference, memberId);
		}
		return new Boolean(true);
	}

// Methods that communicate with Conference Client connections.

// Method for sending messages to playspace conference members
	public static Boolean sendMessageToConferenceMember(ConferenceRoom confRoom, String msgAuthor, String msgTo, String message)
	{
		return
			ConferenceServerFunctions.sendMessageToConferenceMember(confRoom.getClientConnection(msgTo), msgAuthor, message);

	}
	public static Boolean informConferenceOfNewMember(ConferenceRoom confRoom, String newMember)
	{
		List conferenceMembers = confRoom.getConferenceMembers();
		ListIterator iterator = conferenceMembers.listIterator();
		while(iterator.hasNext())
		{
			String memberName =  (String)iterator.next();
			if(!memberName.equals(newMember))
			{
				ConferenceServerFunctions.informMembersOfNewMember(confRoom.getClientConnection(memberName), newMember, memberName);
			}
		}
		return new Boolean(true);
	}
	public static Boolean informConferenceOfMemberLeaving(ConferenceRoom confRoom, String memberName)
	{
		List conferenceMembers = confRoom.getConferenceMembers();
		ListIterator iterator = conferenceMembers.listIterator();
		while(iterator.hasNext())
		{
			String member = (String)iterator.next();
			if(member.equals(memberName))
				continue;
			ConferenceServerFunctions.informMembersOfDeparture(confRoom.getClientConnection(member), memberName, member);
		}
		return new Boolean(true);
	}
	/**
	 * Method to start the DOME server from a command line
	 * @param args Command line arguments to the server
	 *  Arguments are optional. Three arguments are possible, the first being the
	 *  server port, the second is the database port, and the third is the database file.
	 *  The default server port is 8080 and the default database port os 9001 and the
	 *  default database files is specified in DbInit.getDbFileName().
	 *  Arguments beyond the first three will be ignored.
	 */
	public static void main(String[] args)
	{
		int svrPort = 8082;
		if (args.length > 0)
		{
			try
			{
				svrPort = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException e)
			{
				usage();
				System.exit(0);
			}
		}
		try
		{
			new ConferenceServer(svrPort);
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * print the usage for the main method in the DomeServer class
	 */
	public static void usage()
	{
		System.out.println("usage: ConferenceServer [serverPort]\n" +
		        "      default - ConferenceServer 8080");
	}

}
