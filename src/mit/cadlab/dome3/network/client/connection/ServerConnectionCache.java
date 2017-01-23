// ServerConnectionCache.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.client.connection;

import mit.cadlab.dome3.gui.guiutils.SimpleChooser;
import mit.cadlab.dome3.network.NetworkUtils;
import mit.cadlab.dome3.swing.DListModel;
import mit.cadlab.dome3.util.DSet;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ListSelectionModel;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Class to cache connections to server which are used for getting information back from servers.
 */
public class ServerConnectionCache
{
	protected static Dimension DEFAULT_CHOOSER_SIZE = new Dimension(200, 400);

	// key=hostname(uppercase)/ipaddress, value=ServerConnection
	protected static HashMap twoWayConnectionMap = new HashMap(); // for connections with RPC client urls
	protected static HashMap oneWayConnectionMap = new HashMap(); // for connections with no/hostname only client urls

	public static ServerConnection getServerConnection(Component parent, String serverPort, boolean desireTwoWayCommunication)
	{
		String[] info = NetworkUtils.parseServerPortInfo(serverPort);
		DSet connections = new DSet();

		// try by addrPort
		String addrPort = info[1] + ":" + info[2];
		connections.addAll((DSet) twoWayConnectionMap.get(addrPort));
		if (!desireTwoWayCommunication)
			connections.addAll(0, (DSet) oneWayConnectionMap.get(addrPort));
		//if (connections.isEmpty()) { // check if address is loopback or localhost address
			String port = info[2];
			String alternateServerPort = null;
			try {
				InetAddress address = InetAddress.getByName(info[1]);
				if (address.isLoopbackAddress()) { // 127.0.0.1, so try local ip address
					InetAddress localAddress = InetAddress.getLocalHost();
					alternateServerPort = localAddress.getHostAddress() + ":" + port;
				} else if (address.isSiteLocalAddress()) { // the local ip address, so try loopback address
					alternateServerPort = "127.0.0.1:" + port;
				}
			}
			catch (UnknownHostException e) {
				// ignore
			}
			if (alternateServerPort != null) {
				connections.addAll((DSet) twoWayConnectionMap.get(alternateServerPort));
				if (!desireTwoWayCommunication)
					connections.addAll(0, (DSet) oneWayConnectionMap.get(alternateServerPort));
			}
		//}
		if (connections.isEmpty())
			return null;
		else if (connections.size() == 1)
			return (ServerConnection) connections.get(0);
		else {
			Object[] conns = SimpleChooser.showChooser(parent, "Select a connection:", "ok", "cancel",
			                                           new ServerConnectionListModel(connections), ListSelectionModel.SINGLE_SELECTION, DEFAULT_CHOOSER_SIZE);
			if (conns == null)
				return null;
			else
				return (ServerConnection) conns[0];
		}
	}

	public static void addServerConnection(ServerConnection conn)
	{
		HashMap map = isTwoWayConnection(conn) ? twoWayConnectionMap : oneWayConnectionMap;
		String[] info = NetworkUtils.parseServerPortInfo(conn.getServerPort());
		String hostname = info[0];
		String address = info[1];
		String port = info[2];
		if (hostname != null) { // add hostname:port if hostname exists
			String hostPort = hostname + ":" + port;
			DSet hostConnections = (DSet) map.get(hostPort);
			if (hostConnections == null) {
				hostConnections = new DSet();
				map.put(hostPort.toUpperCase(), hostConnections);
			}
			hostConnections.add(conn);
		}
		// add ipaddress:port
		String addrPort = address + ":" + port;
		DSet addrConnections = (DSet) map.get(addrPort);
		if (addrConnections == null) {
			addrConnections = new DSet();
			map.put(addrPort, addrConnections);
		}
		addrConnections.add(conn);
	}

	public static void removeServerConnection(ServerConnection conn)
	{
		HashMap map = isTwoWayConnection(conn) ? twoWayConnectionMap : oneWayConnectionMap;
		String[] info = NetworkUtils.parseServerPortInfo(conn.getServerPort());
		if (info[0] != null) { // remove hostname:port if hostname exists
			String hostPort = info[0] + ":" + info[2];
			DSet hostConnections = (DSet) map.get(hostPort);
			if (hostConnections != null) {
				hostConnections.remove(conn);
				if (hostConnections.isEmpty())
					map.remove(hostPort);
			}
		}
		// remove ipaddress:port
		String addrPort = info[1] + ":" + info[2];
		DSet addrConnections = (DSet) map.get(addrPort);
		if (addrConnections != null) {
			addrConnections.remove(conn);
			if (addrConnections.isEmpty())
				map.remove(addrPort);
		}
	}

	public static class ServerConnectionListModel extends DefaultListModel implements DListModel
	{
		public ServerConnectionListModel(DSet conns)
		{
			super();
			ensureCapacity(conns.size());
			Iterator it = conns.iterator();
			while (it.hasNext())
				addElement(it.next());
		}

		public Icon getIcon(int index)
		{
			return null;
		}

		public String getListText(int index)
		{
			Object obj = getElementAt(index);
			if (index == -1) {
				System.err.println("getListText for -1");
			}
			if (obj == null) return " ";
			ServerConnection conn = (ServerConnection) obj;
			return conn.getLoginType() + ":: " + conn.getLoginName() + "@" + conn.getServerPort() + (isTwoWayConnection(conn) ? "**" : "");
		}
	}

	public static boolean isTwoWayConnection(ServerConnection conn)
	{
		return conn.getClientUrl().endsWith("/RPC2");
	}

}
