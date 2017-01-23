// LoginUtils.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.client.connection;

import mit.cadlab.dome3.gui.login.LoginInfo;
import mit.cadlab.dome3.gui.login.LoginPrompt;
import mit.cadlab.dome3.gui.login.LoginsCache;
import mit.cadlab.dome3.gui.mode.run.RunMode;
import mit.cadlab.dome3.network.NetworkUtils;

import java.awt.*;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LoginUtils
{

	public static String ADMIN = "ADMIN";
	public static String USER = "USER";
	public static String GUEST = "GUEST";

	public static String createClientURL(int port)
	{
		return "http://" + NetworkUtils.getHostName() + ":" + port + "/RPC2";
	}

	/**
	 * function to create a ServerConnection
	 * @param user User name
	 * @param svrPort server name and port number of server in format "server:port"
	 * @param encryptedPassword
	 * @return ServerConnection representing the successful connection
	 * @throws UnreachableServerException if server/port combination is unavailable
	 */
	public static ServerConnection login(String loginType, String user, String svrPort, byte[] encryptedPassword)
	{
		ServerConnection svrCon = new ServerConnection(loginType, svrPort);
		if (svrCon.login(user, encryptedPassword)) {
			return svrCon;
		}
		return null; // should never get here since unsuccessful logins should throw exception;
	}


	/**
	 * function to create a ServerConnection
	 * @param user User name
	 * @param url Client url
	 * @param svrPort server name and port number of server in format "server:port"
	 * @param encryptedPassword
	 * @return ServerConnection representing the successful connection
	 * @throws UnreachableServerException if server/port combination is unavailable
	 */
	public static ServerConnection login(String loginType, String user, String url, String svrPort, byte[] encryptedPassword)
	{
		ServerConnection svrCon = new ServerConnection(loginType, svrPort, url);
		if (svrCon.login(user, encryptedPassword)) {
			return svrCon;
		}
		return null; // should never get here since unsuccessful logins should throw exception;
	}


	/**
	 * function to encrypt passwords
	 * @param plainTextPassword the plaintext to be encrypted via SHA-1 algorithm
	 * @return encrypted password
	 * @throws RuntimeException if unable to encrypt password (algorithm not found)
	 */
	public static byte[] encryptPassword(String plainTextPassword)
	{
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			sha.update(plainTextPassword.getBytes());
			return sha.digest();
		} catch (java.security.NoSuchAlgorithmException e) {
			System.err.println(e.toString());
			throw new RuntimeException("unable to get SHA-1 algorithm to encrypt password");
		}
	}

	/**
	 * function to encrypt passwords
	 * @param charPassword the plaintext to be encrypted via SHA-1 algorithm
	 * @return encrypted password
	 * @throws RuntimeException if unable to encrypt password (algorithm not found)
	 */
	public static byte[] encryptPassword(char[] charPassword)
	{
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			sha.update(new String(charPassword).getBytes());
			return sha.digest();
		} catch (java.security.NoSuchAlgorithmException e) {
			System.err.println(e.toString());
			throw new RuntimeException("unable to get SHA-1 algorithm to encrypt password");
		}
	}

	public static ServerConnection compareServersAndGetConnection(ServerConnection svrConn, String newServerUrl)
	{
		if (newServerUrl == null) // avoid null pointer exception
			newServerUrl = svrConn.getServerPort();

		// get the server connection for this model
		if (areSameURLs(svrConn.getServerPort(), newServerUrl))
			return svrConn;

		return getServerConnection(null, newServerUrl, RunMode.getClientUrl(), true);
	}

	protected static boolean areSameURLs(String url1, String url2) {
		String[] urlInfo1 = NetworkUtils.parseServerPortInfo(url1);
		String[] urlInfo2 = NetworkUtils.parseServerPortInfo(url2);
		if (!urlInfo1[2].equals(urlInfo2[2])) // ports are not the same
			return false;
		if (urlInfo1[1].equals(urlInfo2[1])) // addresses are the same
			return true;
		try {
			InetAddress addr1 = InetAddress.getByName(urlInfo1[1]);
			InetAddress addr2 = InetAddress.getByName(urlInfo2[1]);
			if ((addr1.isLoopbackAddress() && addr2.isSiteLocalAddress()) ||
			        (addr1.isSiteLocalAddress() && addr2.isLoopbackAddress()))
				return true;
		}
		catch (UnknownHostException e) {
			// ignore
		}
		return false;
	}

	public static ServerConnection getServerConnection(Component parent, String serverPort)
	{
		return getServerConnection(parent, serverPort, NetworkUtils.getHostName(), false);
	}

	public static ServerConnection getServerConnection(Component parent, String serverPort, String clientUrl)
	{
		return getServerConnection(parent, serverPort, clientUrl, true);
	}

	private static ServerConnection getServerConnection(Component parent, String serverPort, String clientUrl,
	                                                    boolean desireTwoWayCommunication)
	{
		// find a cached server connection
		ServerConnection newSvrConn = ServerConnectionCache.getServerConnection(parent, serverPort, desireTwoWayCommunication);
		if (newSvrConn != null)
			return newSvrConn;

		// try to use name, password from cached login information
		List recentLogins = LoginsCache.getLogins();
		String loginType;
		for (Iterator loginIter = recentLogins.iterator(); loginIter.hasNext();) {
			LoginInfo login = (LoginInfo) loginIter.next();
			try {
				loginType = login.getLoginType().equals(LoginUtils.GUEST) ? LoginUtils.GUEST : LoginUtils.USER;
				newSvrConn = LoginUtils.login(loginType,
				                              login.getUsername(),
				                              clientUrl,
				                              serverPort,
				                              login.getEncryptedPwd());
			} catch (Exception use) {
			}
			if (newSvrConn != null)
				return newSvrConn;
		}

		// fresh login to a new server
		if (newSvrConn == null)
			newSvrConn = LoginPrompt.showDialog(parent, serverPort, clientUrl);
		return newSvrConn;
	}

}
