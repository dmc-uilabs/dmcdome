// ServerAdministrationHandler.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.handlers;

import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.handlers.AbstractXmlRpcHandler;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.DomeServer;
import mit.cadlab.dome3.network.server.functions.ClientDbFunctions;

import org.apache.xmlrpc.XmlRpcException;

import java.util.Vector;

public class ServerAdministrationHandler extends AbstractXmlRpcHandler
{

	private DomeServer server;

	public ServerAdministrationHandler(DomeServer server)
	{
		this.server = server;
	}

	public Object execute(String methodName, Vector params) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "ServerAdministrationHandler.execute: " + methodName);
		try {
			if (methodName.equals(DbConstants.SHUTDOWN)) {
				shutdown(params);
			} else
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_METHOD, methodName);
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XmlRpcException(0, e.getMessage());
		}
		return null;
	}

	/**
	 * allows client to explicitly tell the server to shutdown
	 * @param params Argument list (sessionId)
	 */
	private void shutdown(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for shutdown(String connectionId)");
		// todo: validate user is an administrator
		String connectionId = (String) params.get(0);
		ClientDbFunctions.logout(connectionId);
		DomeServer.clientLogout(connectionId);
		server.shutdown();
		System.exit(0);
	}

}

