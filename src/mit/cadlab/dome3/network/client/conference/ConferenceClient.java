// DomeServer.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.client.conference;

import mit.cadlab.dome3.objectmodel.playspace.ClientPlayspace;
import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.network.client.functions.PermissionFunctions;
import mit.cadlab.dome3.network.client.functions.UserGroupFunctions;
import mit.cadlab.dome3.gui.conference.ConferencePanel;
import mit.cadlab.dome3.network.client.functions.ConferenceClientFunctions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;


/**
 * The ConferenceServer class consists of all the functionality required in the Chat Server.
 * This is a restructuring of the original implementation
 * todo: support databases on different machine than ConferenceServer
 */
public class ConferenceClient
{
	public static final Dimension DEFAULT_SIZE = new Dimension(400, 400);
	private static HashMap _conferenceMap = new HashMap();
	private ConferencePanel _conferenceGUI;
	private ServerConnection _svrConn;

	private String _playspaceId;
	private String _playspaceName;
	private JDialog d;

	/**
	 * Chat server constructor
	 * The server URL is "http://localhost:port/RPC2"
	 * @param svrConn
	 */
	public ConferenceClient(ServerConnection svrConn)
	{
		this._svrConn = svrConn;
	}

	public ServerConnection getConferenceServerConnection()
	{
		return this._svrConn;
	}

	public JDialog createConferenceGUI(Component parent, ClientPlayspace playspace)
	{
		_playspaceId = playspace.getCompoundId().getPlayspaceStaticId();
		_playspaceName = playspace.getName();
		_conferenceGUI = new ConferencePanel(_playspaceId, _svrConn);
		_conferenceMap.put(_svrConn.getLoginName(), _conferenceGUI);

		joinConference();

		d = new JDialog((JFrame) SwingUtilities.windowForComponent(parent),
		                "ConferencePanel for playspace: " + _playspaceName,
		                false);
		d.getContentPane().add(_conferenceGUI);
		d.setSize(DEFAULT_SIZE);
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				ConferenceClientFunctions.leaveConference(_svrConn, _playspaceId);
         	}
            public void windowClosed(WindowEvent e)
            {
                d=null;
            }
		});
		d.setLocationRelativeTo(parent);
		return d;
	}


	public void joinConference()
	{
      if (ConferenceClientFunctions.joinConference(_svrConn, _playspaceId)) {
			Vector memberIds = PermissionFunctions.getPlayspaceUserMembers(_svrConn, _playspaceId);
			Vector conferenceMembers = new Vector();
			for (Iterator i = memberIds.iterator(); i.hasNext();) {
				Integer id = (Integer) i.next();
				//Get detailed user/group info (id, type, name, description, status, CAN_SAVE_MODEL, CAN_SAVE_PLAYSPACE)
				Vector info = UserGroupFunctions.getUserGroupInfo(_svrConn, id.intValue());
				conferenceMembers.addElement(info.get(2));
			}
            Vector currentOnLineMembers = ConferenceClientFunctions.whoIsInTheConferenceRoom(_svrConn, _playspaceId);
			_conferenceGUI.initializeConferenceGUI(conferenceMembers, currentOnLineMembers);
		}
	}


//	Method for joining a playspace chat

	public static boolean informConferenceOfNewMember(String newMember, String ownerGUI)
	{
		// update the conference gui online members jscrollpane
		ConferencePanel memberGUI = (ConferencePanel) _conferenceMap.get(ownerGUI);
		memberGUI.updateOnLinePane(newMember);
		memberGUI.setTranscriptText("", newMember + " has joined this conference", ConferencePanel.ENTER);
		return true;
	}

	public static boolean informConferenceOfOldMember(String oldMember, String ownerGUI)
	{
		ConferencePanel memberGUI = (ConferencePanel) _conferenceMap.get(ownerGUI);
		memberGUI.removeMember(oldMember);
		memberGUI.setTranscriptText("", oldMember + " has left this conference", ConferencePanel.LEAVE);
		return true;
	}

// Method for receiving messages from server

	public static boolean sendMessageToConferenceMembers(String msgAuthor, String ownerGUI, String message)
	{

		/*List conferenceGUIs = new ArrayList(_conferenceMap.values());
		ListIterator iterator = conferenceGUIs.listIterator();
		while (iterator.hasNext()) {
			ConferencePanel gui = (ConferencePanel) iterator.next();
			gui.setTranscriptText(msgAuthor, message, ConferencePanel.MSG);
		}  */
        ConferencePanel memberGUI = (ConferencePanel) _conferenceMap.get(ownerGUI);
        memberGUI.setTranscriptText(msgAuthor, message, ConferencePanel.MSG);
		System.out.println(msgAuthor + " wrote: " + message);
		return true;
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
//	public static void main(String[] args)
//	{
//		int clientPort = 9000;
//		int serverPort = 8088;
//		if (args.length > 0)
//		{
//			try
//			{
//				clientPort = Integer.parseInt(args[0]);
//				serverPort = Integer.parseInt(args[1]);
//			}
//			catch (NumberFormatException e)
//			{
//				usage();
//				System.exit(0);
//			}
//		}
//		try
//		{
//			ServerConnection svrConn = LoginUtils.login(LoginUtils.USER,  "Jacob", RunMode.getClientUrl(), "localhost:8080", LoginUtils.encryptPassword("cadlab"));
//			new ConferenceClient(svrConn);
//		}
//		catch (Exception e)
//		{
//			System.err.println(e.getMessage());
//			System.exit(0);
//		}
//	}

	/**
	 * print the usage for the main method in the DomeServer class
	 */
	public static void usage()
	{
		System.out.println("usage: ConferenceServer [serverPort]\n" +
		                   "      default - ConferenceServer 9000");
	}

}
