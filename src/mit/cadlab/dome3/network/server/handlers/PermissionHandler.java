// PermissionHandler.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.handlers;

import mit.cadlab.dome3.network.server.db.*;
import mit.cadlab.dome3.network.server.handlers.AbstractXmlRpcHandler;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.functions.FileSystemDbFunctions;
import mit.cadlab.dome3.network.server.functions.PermissionDbFunctions;

import org.apache.xmlrpc.XmlRpcException;

import java.util.Vector;

/**
 * The XML-RPC handler for functions related to permissions management.
 * todo check that user actually has permission to do the functions requested
 */
public class PermissionHandler extends AbstractXmlRpcHandler
{

	/**
	 * This is the only method that an XMLRPC handler has to implement.
	 *
	 * @param methodName - name of the method on the sever that the client wants to invoke
	 * @param params - arguments to the method on the server
	 *
	 * @return results of the method execution on the server.
	 * @throws org.apache.xmlrpc.XmlRpcException wraps up any exceptions thrown by the method on the server or
	 * 					if a particular method is not found on the server.
	 */
	public Object execute(String methodName, Vector params) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "PermissionHandler.execute: " + methodName);
		try {
			if (methodName.equals(DbConstants.GET_PERMISSION_CATEGORY_INFO))
				return getPermissionCategoryInfo(params);
			else if (methodName.equals(DbConstants.GET_OBJECT_PERMISSION_INFO)) {
				return getObjectPermissionInfo(params);
			} else if (methodName.equals(DbConstants.SET_OBJECT_PERMISSIONS)) {
				return setObjectPermissions(params);
			} else if (methodName.equals(DbConstants.GET_PLAYSPACE_MEMBER)) {
				return getPlayspaceMembers(params);
			} else if (methodName.equals(DbConstants.GET_PLAYSPACE_USER_MEMBER)) {
				return getPlayspaceUserMembers(params);
			} else if (methodName.equals(DbConstants.USER_HAS_PERMISSION)) {
				return new Boolean(userHasPermission(params));
			} else if (methodName.equals(DbConstants.SESSION_USER_HAS_PERMISSION)) {
				return new Boolean(sessionUserHasPermission(params));
			} else
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_METHOD, methodName);
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XmlRpcException(0, e.getMessage());
		}
	}

	private Vector getPermissionCategoryInfo(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getPermissionCategoryInfo(String categoryName)");
		return PermissionDbFunctions.getPermissionCategoryInfo((String) params.get(0));
	}

	private Vector getObjectPermissionInfo(Vector params) throws XmlRpcException
	{
		if (params.size() != 2)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getObjectPermissionInfo(String objectId, String categoryName)");
		return PermissionDbFunctions.getObjectPermissionInfo((String) params.get(0), (String) params.get(1));
	}

	private Object setObjectPermissions(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_VEC}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for setObjectPermission(String objectId, String categoryName, Vector permissions)");
		PermissionDbFunctions.setObjectPermissions((String) params.get(0), (String) params.get(1), (Vector) params.get(2));
		return DbUtils.NO_VECTOR;
	}


	private boolean sessionUserHasPermission(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for sessionUserHasPermission(ServerConnection svrConn, String modelObjectId, String permissionName)");
		String sessionId = (String) params.get(0);
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();

		return PermissionDbFunctions.hasPermission(requestorId, ((String) params.get(1)), (String) params.get(2));

	}

	private boolean userHasPermission(Vector params) throws XmlRpcException
	{
		if (params.size() != 3)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_INT, PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for userHasPermission(ServerConnection svrConn, int userId,String modelObjectId,String permissionName)");
		return PermissionDbFunctions.hasPermission(((Integer) params.get(0)).intValue(), (String) params.get(1), (String) params.get(2));

	}

	private Vector getPlayspaceMembers(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getPlayspaceMembers(String playspaceId))");
		return PermissionDbFunctions.getPlayspaceMembers((String) params.get(0));

	}

	private Vector getPlayspaceUserMembers(Vector params) throws XmlRpcException
	{
		if (params.size() != 1)
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS, DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		if (!validateParameterTypes(params, new Class[]{PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getPlayspaceUserMembers(String playspaceId))");
		return PermissionDbFunctions.getPlayspaceUserMembers((String) params.get(0));

	}


}
