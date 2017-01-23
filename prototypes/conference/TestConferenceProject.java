package conference;

import mit.cadlab.dome.network.client.connection.ServerConnection;
import mit.cadlab.dome.network.client.connection.LoginUtils;
import mit.cadlab.dome.network.client.DomeRuntimeClient;

import javax.swing.JFrame;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

/**
 * Created by Jacob Wronski.
 * User: Jacob Wronski
 * Date: Mar 31, 2003
 * Time: 9:07:14 AM
 * To change this template use Options | File Templates.
 */
public class TestConferenceProject
{
	public static void main(String[] args)
	{
		if(args.length != 4)
		{
			System.out.println("Needs 3 arguments: client port, client name, conference room name");
			System.exit(0);
		}
		String clientPort = args[0];
		String serverPort = args[1];
		String clientName = args[2];
		String conferenceName = args[3];
		ConferenceClient clientWebServer = new ConferenceClient(Integer.parseInt(clientPort), Integer.parseInt(serverPort));
		clientWebServer.createConferenceGUI(clientPort, serverPort, clientName, conferenceName);
//			if(((Boolean)ConferenceClientFunctions.sendMessageToEveryone(cfu, clientName, "Hello", conferenceName)).booleanValue())
//				System.out.println("Everything is cool");
//			if(((Boolean)ConferenceClientFunctions.sendMessageToMember(cfu, clientName,"Elaine", "Are you there?", conferenceName)).booleanValue())
//				System.out.println("Message was send");
//			if(clientName.equals("David"))
//				ConferenceClientFunctions.leaveConference(cfu,clientName, conferenceName);
//		
	}
}
