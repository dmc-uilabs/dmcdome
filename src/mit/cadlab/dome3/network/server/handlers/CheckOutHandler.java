// CheckOutHandler.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.handlers;

import mit.cadlab.dome3.network.server.db.*;
import mit.cadlab.dome3.network.server.handlers.AbstractXmlRpcHandler;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.functions.CheckOutDbFunctions;
import mit.cadlab.dome3.network.server.functions.FileSystemDbFunctions;
import mit.cadlab.dome3.network.server.functions.FileEventDbFunction;

import org.apache.xmlrpc.XmlRpcException;

import java.util.Vector;

/**
 * The CheckOutHandler has methods for checking files out from the server
 */
public class CheckOutHandler extends AbstractXmlRpcHandler
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
		//Debug.trace(Debug.ALL, "CheckOutHandler.execute: " + methodName);
		try {
			if (methodName.equals(DbConstants.CHECKOUT_MODEL))
				return checkoutModel(params);
			else if (methodName.equals(DbConstants.CHECKOUT_PLAYSPACE))
				return checkoutPlayspace(params);
			else if (methodName.equals(DbConstants.CHECKOUT_PROJECT))
				return checkoutProject(params);
            else if (methodName.equals(DbConstants.CHECKOUT_AUXFILE_FOR_MODEL))
				return checkoutAuxFileForModel(params);
            throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_METHOD, methodName);
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XmlRpcException(0, e.getMessage());
		}
	}


	/**
	 * allows client to explicitly tell the server that it is terminating the connection
	 * @param params Argument list (sessionId)
	 */
	private Vector checkoutModel(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for checkoutModel(String connectionId, String modelId)");
		String sessionId = (String) params.get(0);
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);

		return CheckOutDbFunctions.checkoutModel(requestorId, (String) params.get(1), loginType);
	}


    private Vector checkoutAuxFileForModel(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR,PARAM_STR,PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for checkoutAuxFileForModel(ServerConnection svrConn, String auxfileId,String modelId)");
		String sessionId = (String) params.get(0);
        int fileEventId = FileEventDbFunction.createDownloadFileEvent(sessionId);

		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);

		return CheckOutDbFunctions.checkoutAuxFileForModel(requestorId,(String) params.get(1), (String) params.get(2), loginType,fileEventId);
	}

	private Vector checkoutPlayspace(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for checkoutPlayspace(String connectionId, String runtimeId)");
		String sessionId = (String) params.get(0);
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);
		return CheckOutDbFunctions.checkoutPlayspace(requestorId, (String) params.get(1), loginType);
	}

	private Vector checkoutProject(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for checkoutPlayspace(String connectionId, String runtimeId)");
		String sessionId = (String) params.get(0);
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		String loginType = FileSystemDbFunctions.getUserTypeFromSession(sessionId);
		return CheckOutDbFunctions.checkoutProject(requestorId, (String) params.get(1), loginType);
	}


}