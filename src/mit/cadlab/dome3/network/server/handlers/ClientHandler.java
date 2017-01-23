// ClientHandler.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.handlers;

import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.server.functions.ClientDbFunctions;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.handlers.AbstractXmlRpcHandler;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.network.client.functions.Vectors;

import org.apache.xmlrpc.XmlRpcException;

import java.util.Vector;

/**
 * The ClientHandler has methods that manage the interactions between a DOME Server
 * and DOME Client. These include authentication, session tracking, and disconnections.
 */
public class ClientHandler extends AbstractXmlRpcHandler
{

	/**
	 * This is the only method that an XMLRPC handler has to implement.
	 *
	 * @param methodName - name of the method on the sever that the client wants to invoke
	 * @param params - arguments to the method on the server
	 *
	 * @return results of the method execution on the server.
	 * @throws java.lang.Exception wraps up any exceptions thrown by the method on the server or
	 * 					if a particular method is not found on the server.
	 */
	public Object execute(String methodName, Vector params) throws Exception
	{
		//Debug.trace(Debug.ALL, "ClientHandler.execute: " + methodName);
		try {
			if (methodName.equals(DbConstants.LOGIN_ADMIN))
				return loginAdmin(params);
			else if (methodName.equals(DbConstants.LOGIN_USER))
				return loginUser(params);
			else if (methodName.equals(DbConstants.LOGIN_GUEST))
				return loginGuest(params);
			else if (methodName.equals(DbConstants.LOGOUT))
				return logout(params);
			else
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_METHOD, methodName);
		} catch (XmlRpcException e) {
			System.err.println(e);
			throw e;
		} catch (Exception e) {
			System.err.println();
			throw new XmlRpcException(0, e.getMessage());
		}
	}

	/**
	 * allows client to establish a connection with the server
	 * @param params Argument list (username, password, clientUrl)
	 * @return connection id to be used for all future connections
	 */
	private String loginAdmin(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_BYTE_ARRAY, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for loginAdmin(String userName, byte[] encryptedPassword, String clientUrl)");
		String sessionId = ClientDbFunctions.loginAdmin((String) params.get(0), (byte[]) params.get(1), (String) params.get(2));
		DomeServer.clientLogin(sessionId);
		return sessionId;
	}

	/**
	 * allows client to establish a connection with the server
	 * @param params Argument list (username, password, clientUrl)
	 * @return connection id to be used for all future connections and login type (user or admin)
	 */
	private Vector loginUser(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_BYTE_ARRAY, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for loginUser(String userName, byte[] encryptedPassword, String clientUrl)");
		String[] sessionIdAndLoginType = ClientDbFunctions.loginUser((String) params.get(0), (byte[]) params.get(1), (String) params.get(2));
		DomeServer.clientLogin(sessionIdAndLoginType[0]);
		return Vectors.create(sessionIdAndLoginType[0], sessionIdAndLoginType[1]);
	}

	/**
	 * allows client to establish a connection with the server
	 * @param params Argument list (username, password, clientUrl)
	 * @return connection id to be used for all future connections
	 */
	private String loginGuest(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for loginGuest(String clientUrl)");
		String sessionId = ClientDbFunctions.loginGuest((String) params.get(0));
		DomeServer.clientLogin(sessionId);
		return sessionId;
	}

	/**
	 * allows client to explicitly tell the server that it is terminating the connection
	 * @param params Argument list (sessionId)
	 */
	private Object logout(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for logout(String connectionId)");
		String connectionId = (String) params.get(0);
		ClientDbFunctions.logout(connectionId);
		DomeServer.clientLogout(connectionId);
		return DbUtils.NO_VECTOR; // can not return NULL to XML-RPC
	}

}