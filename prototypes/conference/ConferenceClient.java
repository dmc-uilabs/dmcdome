// DomeServer.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package conference;

import mit.cadlab.dome.network.client.DomeRuntimeClient;
import mit.cadlab.dome.network.server.Debug;
import mit.cadlab.dome.network.NetworkUtils;
import mit.cadlab.dome.server.db.DbConstants;
import org.apache.xmlrpc.WebServer;

import javax.swing.JFrame;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

/**
 * The ConferenceServer class consists of all the functionality required in the Chat Server.
 * This is a restructuring of the original implementation
 * todo: support databases on different machine than ConferenceServer
 */
public class ConferenceClient
{
	private static HashMap _conferenceMap = new HashMap();

	private String hostName;  //name or IP address of the host
	private int _clientPort;
	private int _serverPort;
	private WebServer webserver;
	private static String defaultClientPort = null;
	private static HashMap _conferenceRoomInfo = new HashMap();
	private Conference _conferenceGUI;
	private ConferenceServerConnection _cfu;

	private String _clientName, _playspaceName;

	public static String getDefaultClientPort ()
	{
		if (defaultClientPort == null)
		{
			InetAddress addr = null;
			try {
				addr = InetAddress.getLocalHost();
				defaultClientPort = addr.getCanonicalHostName();
			}
			catch (UnknownHostException e) {
				defaultClientPort = "localhost";
			}
			defaultClientPort += ":9000";
		}
		return defaultClientPort;
	}


	/**
	 * Chat server constructor
	 * The server URL is "http://localhost:port/RPC2"
	 * @param serverPort The port number to be used by clients to talk to the server
	 */
	public ConferenceClient(int clientPort, int serverPort)
	{
		Debug.setDebugLevel(Debug.ALL); // trace method calls through server
		try {
            InetAddress addr = InetAddress.getLocalHost();
			hostName = addr.getHostName();
		}
		catch (UnknownHostException e) {
			hostName = "localhost";
		}
		this._clientPort = clientPort;
		try {

			webserver = new WebServer(this._serverPort);
			webserver.addHandler(DbConstants.FUNC_TYPE_CONFERENCE_CLIENT, new ConferenceClientHandler());
			Debug.trace(Debug.STATUS, hostName + ": ConferenceClient started on port " + this._clientPort + " at " + new Date());
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
		return this._serverPort;
	}
	public ConferenceServerConnection getConferenceServerConnection()
	{
		return this._cfu;
	}
	public void createConferenceGUI(String clientPort, String serverPort, String clientName, String playspaceName)
	{
		this._clientName = clientName;
		this._playspaceName = playspaceName;
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.addWindowListener(new WindowListener()
		{
			public void windowClosed(WindowEvent e){}
			public void windowOpened(WindowEvent e){}
			public void windowClosing(WindowEvent e)
			{
				ConferenceClientFunctions.leaveConference(ConferenceClient.this._cfu, ConferenceClient.this._clientName, ConferenceClient.this._playspaceName);
				((JFrame) e.getSource()).dispose();
			}
			public void windowActivated(WindowEvent e){}
			public void windowDeiconified(WindowEvent e){}
			public void windowDeactivated(WindowEvent e){}
			public void windowIconified(WindowEvent e){}
		});
		this._cfu = new ConferenceServerConnection(NetworkUtils.getHostName()+":"+serverPort, NetworkUtils.getHostName() + ":" + clientPort, clientName);
		this._conferenceGUI = new Conference(f, playspaceName, clientName, this._cfu);
		ConferenceClient._conferenceMap.put(clientName, this._conferenceGUI);
		if (ConferenceClientFunctions.joinConference(this._cfu, this._cfu.getClientUrl(), this._cfu.getLoginName(), playspaceName).booleanValue())
		{
			//todo in the future there must be a call made to the playspace database to retrieve the members that belong to a
			//todo particular playspace

			Vector conferenceMembers = new Vector();
			conferenceMembers.addElement("Jacob");
			conferenceMembers.addElement("David");
			conferenceMembers.addElement("Elaine");
			Vector currentOnLineMembers = ConferenceClientFunctions.whoIsInTheConferenceRoom(this._cfu, clientName, playspaceName);
			this._conferenceGUI.initializeConferenceGUI(conferenceMembers, currentOnLineMembers);
		}
		f.getContentPane().add(this._conferenceGUI);
		f.show();

	}
//	Method for joining a playspace chat

	public static Boolean informConferenceOfNewMember(String newMember, String ownerGUI)
	{
		// update the conference gui online members jscrollpane
		Conference memberGUI = (Conference) ConferenceClient._conferenceMap.get(ownerGUI);
		memberGUI.updateOnLinePane(newMember);
		memberGUI.setTranscriptText("", newMember+" has joined this conference",Conference.ENTER);
		return new Boolean(true);
	}
	public static Boolean informConferenceOfOldMember(String oldMember, String ownerGUI)
	{
		Conference memberGUI = (Conference) ConferenceClient._conferenceMap.get(ownerGUI);
		memberGUI.removeMember(oldMember);
		memberGUI.setTranscriptText("", oldMember+" has left this conference", Conference.LEAVE);
		return new Boolean(true);
	}

// Method for receiving messages from server

	public static Boolean sendMessageToConferenceMembers(String msgAuthor, String message)
	{

		List conferenceGUIs = new ArrayList(ConferenceClient._conferenceMap.values());
		ListIterator iterator = conferenceGUIs.listIterator();
		while(iterator.hasNext())
		{
			Conference gui = (Conference)iterator.next();
			gui.setTranscriptText(msgAuthor, message,Conference.MSG);
		}
		System.out.println(msgAuthor + " wrote: " + message);
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
		int clientPort = 9000;
		int serverPort = 8080;
		if (args.length > 0)
		{
			try
			{
				clientPort = Integer.parseInt(args[0]);
				serverPort = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException e)
			{
				usage();
				System.exit(0);
			}
		}
		try
		{
			new ConferenceClient(clientPort, serverPort);
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
		        "      default - ConferenceServer 9000");
	}

}
