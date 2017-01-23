// ServerConnection.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.client.connection;

import mit.cadlab.dome3.network.NetworkUtils;
import mit.cadlab.dome3.network.client.functions.Vectors;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.util.DomeJavaBean;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.AsyncCallback;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;

/**
 * represents a connection with a server
 */
public class ServerConnection extends DomeJavaBean
{
	public static final String LOGGED_OUT = "LOGGED OUT";

	private String loginType;
	private String loginName;
	private String serverPort;
	private String serverURL;
	private String clientURL;
	private XmlRpcClient server;
	private byte[] encryptedPwd;
	private String connectionId;
	private boolean isLoggedIn = false;
	private int referenceCount = 0;

	/**
	 * Creation of a connection with a server
	 *
	 * @param serverPort - server:port of the Dome Server
	 * used to convert to URL of http://serverPort/RPC2 (e.g. http://cadlab.mit.edu:8080/RPC2)
	 */
	public ServerConnection(String loginType, String serverPort)
	{
		this(loginType, serverPort, null);
	}

	/**
	 * Creation of a connection with a server
	 *
	 * @param serverPort - server:port of the Dome Server
	 * @param clientURL - the client URL for the server to send messages back to the client
	 * clientURL must be a correct XML-RPC URL (e.g. http://serverName:port/RPC2 for Apache XML-RPC)
	 * an empty/null client URL will result in the hostName being sent to the server
	 */
	public ServerConnection(String loginType, String serverPort, String clientURL)
	{
		this.loginType = loginType;
		this.serverPort = serverPort;
		this.serverURL = "http://" + serverPort + "/RPC2";
		try {
			server = new XmlRpcClient(this.serverURL);
			if (clientURL == null || clientURL.trim().length() == 0) {
				this.clientURL = NetworkUtils.getHostName();
			} else {
				this.clientURL = clientURL;
			}
		} catch (MalformedURLException me) {
			throw new UnreachableServerException("Server connection failed for " + serverURL);
		} catch (IOException ioe) {
			throw new UnreachableServerException(ioe.toString());
		}
	}

	/**
	 * executes a method which takes no parameters on the server
	 * @param methodName the method to be executed
	 * @return result of the method, if any
	 */
	public Object execute(String methodName)
	{
		return execute(methodName, DbConstants.EMPTY_VECTOR);
	}

	/**
	 * executes a method which takes parameters on the server
	 * @param methodName the method to be executed
	 * @param params the parameters of the method
	 * @return result of the method, if any
	 */
	public Object execute(String methodName, Vector params)
	        throws UnreachableServerException, ServerMethodException
	{
		return execute(false, methodName, params);
	}

	protected Object execute(boolean isLoginMethod, String methodName, Vector params)
	        throws UnreachableServerException, ServerMethodException
	{
		if (!isLoggedIn && !isLoginMethod)
			throw new RuntimeException(loginName + ":" + serverPort + " is logged off. Can not execute " + methodName);
		try {
			return server.execute(methodName, params);
		} catch (XmlRpcException re) {
			throw new ServerMethodException(re.code, re.getMessage().replaceAll("org.apache.xmlrpc.XmlRpcException: ", ""));
		} catch (IOException ioe) {
            throw new UnreachableServerException(ioe.toString());
		}
	}

	public void executeAsync(String methodName, Vector params)
	        throws UnreachableServerException, ServerMethodException
	{
		this.executeAsync(methodName, params, null);
	}

	public void executeAsync(String methodName, Vector params, AsyncCallback callBackFunction)
	        throws UnreachableServerException, ServerMethodException
	{
		server.executeAsync(methodName, params, callBackFunction);
	}

	public String getClientUrl()
	{
		return clientURL;
	}

	public String getConnectionId()
	{
		return connectionId;
	}

	public String getServerPort()
	{
		return serverPort;
	}

	public String getLoginType()
	{
		return loginType;
	}

	public String getLoginName()
	{
		return loginName;
	}

	public byte[] getEncryptedPassword()
	{
		return encryptedPwd;
	}

	/**
	 * logs the user into this server
	 * @param name the name to login as
	 * @param encryptedPwd the encrypted password to send to the server
	 * @return true if successful; otherwise, returns false
	 */
	public boolean login(String name, byte[] encryptedPwd)
	        throws UnreachableServerException, ServerMethodException
	{
		if (loginType.equals(LoginUtils.ADMIN)) { // todo: remove this if branch when change to user login is permanent
			connectionId = (String) execute(true, DbConstants.FUNC_TYPE_CLIENT + "." + DbConstants.LOGIN_ADMIN,
			                                Vectors.create(name, encryptedPwd, clientURL));
			loginName = name;
		} else if (loginType.equals(LoginUtils.USER)) { // server determines if user is administrator or not
			Vector connIdAndLoginType = (Vector) execute(true, DbConstants.FUNC_TYPE_CLIENT + "." + DbConstants.LOGIN_USER,
			                                Vectors.create(name, encryptedPwd, clientURL));
			connectionId = (String)connIdAndLoginType.get(0);
			if (DbConstants.LOGIN_TYPE_ADMIN.equals(connIdAndLoginType.get(1))) // administrative user
				loginType = LoginUtils.ADMIN;
			loginName = name;
		} else if (loginType.equals(LoginUtils.GUEST)) {
			connectionId = (String) execute(true, DbConstants.FUNC_TYPE_CLIENT + "." + DbConstants.LOGIN_GUEST,
			                                Vectors.create(clientURL));
			loginName = "guest";
		} else
			throw new IllegalArgumentException("invalid login type: " + loginType);
		this.encryptedPwd = encryptedPwd;
		isLoggedIn = true;
		ServerConnectionCache.addServerConnection(this);
		return true;
	}


	/**
	 * logs the user out of the server; sets the connectionId to null
	 * not required, but nice to do
	 * @return true if successful; otherwise, returns false
	 */
	public boolean logout()
	{
		boolean success = false;
		try {
			execute(DbConstants.FUNC_TYPE_CLIENT + "." + DbConstants.LOGOUT,
			        Vectors.create(connectionId));
			success = true;
		} catch (Exception ex) { // ignore
		} finally {
			isLoggedIn = false;
		}
		ServerConnectionCache.removeServerConnection(this);
		firePropertyChange(LOGGED_OUT);
		return success;
	}

	/**
	 * shuts down the server and logs the user out of the server
	 * sets the connectionId to null - not required, but nice to do
	 * @return true if successful; otherwise, returns false
	 */
	public boolean shutdown()
	{
		if (!loginType.equals(LoginUtils.ADMIN)) {
			System.err.println("only administrators can shutdown the server");
			return false;
		}
			
		boolean success = false;
		try {
			execute(DbConstants.FUNC_TYPE_SERVER_ADMIN+ "." + DbConstants.SHUTDOWN,
			        Vectors.create(connectionId));
			success = true;
		}
		catch (Exception ex) { // ignore
		}
		finally {
			isLoggedIn = false;
		}
		ServerConnectionCache.removeServerConnection(this);
		firePropertyChange(LOGGED_OUT);
		return success;
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof ServerConnection) {
			ServerConnection svrConn = (ServerConnection) obj;
			if (svrConn.getLoginType().equalsIgnoreCase(loginType)
			        && svrConn.getLoginName().equalsIgnoreCase(loginName)
			        && svrConn.getServerPort().equalsIgnoreCase(serverPort)
			        && svrConn.getClientUrl().equalsIgnoreCase(clientURL)) {
				return true;
			}
		}

		return false;
	}

	public void addReference()
	{
		referenceCount++;
	}

	public void removeReference()
	{
		referenceCount--;
		if (referenceCount <= 0)
			logout();
	}

	public String toString()
	{
		return loginType + ":: " + loginName + "@" + serverPort + "\t" + clientURL + "-->" + connectionId;
	}

}
