// UserGroupHandler.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.handlers;

import java.util.Vector;

import mit.cadlab.dome3.network.server.db.*;
import mit.cadlab.dome3.network.server.handlers.AbstractXmlRpcHandler;
import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.functions.FileSystemDbFunctions;
import mit.cadlab.dome3.network.server.functions.UserGroupDbFunctions;
import org.apache.xmlrpc.XmlRpcException;

/**
 * The XML-RPC handler for functions related to user and group management.
 */
public class UserGroupHandler extends AbstractXmlRpcHandler
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
		//Debug.trace(Debug.ALL, "UserGroupHandler.execute: " + methodName);
		try {
			if (methodName.equals(DbConstants.GET_SIMPLE_ACTIVE_USERS_AND_GROUPS_LIST))
				return UserGroupDbFunctions.getSimpleActiveUsersAndGroupsList();
			else if (methodName.equals(DbConstants.GET_SIMPLE_USER_LIST))
				return UserGroupDbFunctions.getSimpleUserList();
			else if (methodName.equals(DbConstants.SESSION_USER_HAS_SAVE_PERMISSION))
				return sessionUserHasSavePermission(params);
			else if (methodName.equals(DbConstants.USER_OR_GROUP_HAS_SAVE_PERMISSION))
				return userOrGroupHasSavePermission(params);
			else if (methodName.equals(DbConstants.USER_IN_GROUP_WITH_SAVE_PERMISSION))
				return userInGroupWithSavePermission(params);
			else if (methodName.equals(DbConstants.GET_DETAILED_USER_LIST))
				return UserGroupDbFunctions.getDetailedUserList();
			else if (methodName.equals(DbConstants.GET_USER_GROUP_INFO))
				return getUserGroupInfo(params);
			else if (methodName.equals(DbConstants.GET_GROUPS_FOR_USER))
				return getGroupsForUser(params);
			else if (methodName.equals(DbConstants.CREATE_NEW_USER))
				return createNewUser(params);
			else if (methodName.equals(DbConstants.DELETE_USER_GROUP)) {
				deleteUserGroup(params);
				return DbUtils.NO_VECTOR;
			} else if (methodName.equals(DbConstants.EDIT_USER_GROUP_INFO)) {
				editUserGroupInfo(params);
				return DbUtils.NO_VECTOR;
			} else if (methodName.equals(DbConstants.EDIT_GROUPS_FOR_USER)) {
				editGroupsForUser(params);
				return DbUtils.NO_VECTOR;
			} else if (methodName.equals(DbConstants.EDIT_MEMBERS_FOR_GROUP)) {
				editMembersForGroup(params);
				return DbUtils.NO_VECTOR;
			} else if (methodName.equals(DbConstants.CHANGE_USER_PASSWORD)) {
				changeUserPassword(params);
				return DbUtils.NO_VECTOR;
			} else if (methodName.equals(DbConstants.CREATE_NEW_GROUP))
				return createNewGroup(params);
			else if (methodName.equals(DbConstants.GET_SIMPLE_GROUP_LIST))
				return UserGroupDbFunctions.getSimpleGroupList();
			else if (methodName.equals(DbConstants.GET_DETAILED_GROUP_LIST))
				return UserGroupDbFunctions.getDetailedGroupList();
			else if (methodName.equals(DbConstants.GET_MEMBERS_FOR_GROUP))
				return getMembersForGroup(params);
			else
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_METHOD, methodName);
		} catch (XmlRpcException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XmlRpcException(0, e.getMessage());
		}
	}

	private Vector getUserGroupInfo(Vector params) throws XmlRpcException
	{
		if (params.size() != 1) {
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		}
		if (!validateParameterTypes(params, new Class[]{PARAM_INT}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getUserInfo(int userId)");
		return UserGroupDbFunctions.getUserGroupInfo(getInt(params.get(0)));
	}

	private Vector getGroupsForUser(Vector params) throws XmlRpcException
	{
		if (params.size() != 1) {
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		}
		if (!validateParameterTypes(params, new Class[]{PARAM_INT}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getUserGroups(int userId)");
		return UserGroupDbFunctions.getGroupsForUser(getInt(params.get(0)));
	}

	private Integer createNewUser(Vector params) throws XmlRpcException
	{
		if (params.size() != 5) {
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		}
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_BYTE_ARRAY, PARAM_BOOL, PARAM_BOOL}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for createNewUser(String name, String descr, byte[] pwd, boolean canSaveModels," +
			                          "boolean canSavePlayspaces)");
		return new Integer(UserGroupDbFunctions.createNewUser((String) params.get(0),
		                                                      (String) params.get(1),
		                                                      (byte[]) params.get(2),
		                                                      getBoolean(params.get(3)),
		                                                      getBoolean(params.get(4))));
	}

	private void deleteUserGroup(Vector params) throws XmlRpcException
	{
		if (params.size() != 1) {
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		} else if (!validateParameterTypes(params, new Class[]{PARAM_INT}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for deleteUserGroup(int id)");
		else
			UserGroupDbFunctions.deleteUserGroup(getInt(params.get(0)));
	}

	private void editUserGroupInfo(Vector params) throws XmlRpcException
	{
		if (params.size() != 6) {
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		}
		if (!validateParameterTypes(params, new Class[]{PARAM_INT, PARAM_STR, PARAM_STR, PARAM_BOOL, PARAM_BOOL, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for editUserGroupInfo(int userId, String newName, String newDescr," +
			                          "boolean canSaveModels, boolean canSavePlayspaces, String status)");
		UserGroupDbFunctions.editUserGroupInfo(getInt(params.get(0)),
		                                       (String) params.get(1),
		                                       (String) params.get(2),
		                                       getBoolean(params.get(3)),
		                                       getBoolean(params.get(4)),
		                                       (String) params.get(5));
	}

	private void editGroupsForUser(Vector params) throws XmlRpcException
	{
		if (params.size() != 2) {
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		}
		if (!validateParameterTypes(params, new Class[]{PARAM_INT, PARAM_VEC}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for editUserGroup(int userId, Vector groupIds)");
		UserGroupDbFunctions.editGroupsForUser(getInt(params.get(0)),
		                                       (Vector) params.get(1));
	}

	private void editMembersForGroup(Vector params) throws XmlRpcException
	{
		if (params.size() != 2) {
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		}
		if (!validateParameterTypes(params, new Class[]{PARAM_INT, PARAM_VEC}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for editMembersForGroup(int groupId, Vector memberIds)");
		UserGroupDbFunctions.editMembersForGroup(getInt(params.get(0)),
		                                         (Vector) params.get(1));
	}


	private void changeUserPassword(Vector params) throws XmlRpcException
	{
		if (!(params.size() == 2 || params.size() == 3)) {
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		}
		if (params.size() == 2) {
			if (!validateParameterTypes(params, new Class[]{PARAM_INT, PARAM_BYTE_ARRAY}))
				throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
				                          "invalid arguments for changeUserPassword(int userId, byte[] pwd)");
			UserGroupDbFunctions.changeUserPassword(getInt(params.get(0)),
			                                        (byte[]) params.get(1));
		} else if (params.size() == 3) {
			if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_BYTE_ARRAY, PARAM_BYTE_ARRAY}))
				throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
				                          "invalid arguments for changeUserPassword(String sessionId, byte[] pwd, byte[] pwd)");
			UserGroupDbFunctions.changeUserPassword((String) params.get(0),
			                                        (byte[]) params.get(1),
			                                        (byte[]) params.get(2));
		}

	}

	private Integer createNewGroup(Vector params) throws XmlRpcException
	{
		if (params.size() != 4) {
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		}
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR, PARAM_BOOL, PARAM_BOOL}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for createNewGroup(String name, String descr, boolean canSaveModels," +
			                          "boolean canSavePlayspaces)");
		return new Integer(UserGroupDbFunctions.createNewGroup((String) params.get(0),
		                                                       (String) params.get(1),
		                                                       getBoolean(params.get(2)),
		                                                       getBoolean(params.get(3))));
	}

	private Vector getMembersForGroup(Vector params) throws XmlRpcException
	{
		if (params.size() != 1) {

			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		}
		if (!validateParameterTypes(params, new Class[]{PARAM_INT}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for getMembersForGroup(int groupId)");
		return UserGroupDbFunctions.getMembersForGroup(getInt(params.get(0)));
	}

	private Boolean sessionUserHasSavePermission(Vector params) throws XmlRpcException
	{
		if (params.size() != 2) {
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		}
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for userOrGroupHasSavePermission(String sessionId, String type)");
		String sessionId = (String) params.get(0);
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		return UserGroupDbFunctions.userOrGroupHasSavePermission(requestorId, (String) (params.get(1)));
	}

	private Boolean userOrGroupHasSavePermission(Vector params) throws XmlRpcException
	{
		if (params.size() != 2) {
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		}
		if (!validateParameterTypes(params, new Class[]{PARAM_INT, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for userOrGroupHasSavePermission(int id, String type)");
		return UserGroupDbFunctions.userOrGroupHasSavePermission(getInt(params.get(0)), (String) (params.get(1)));
	}

	private Boolean userInGroupWithSavePermission(Vector params) throws XmlRpcException
	{
		if (params.size() != 2) {
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS,
			                          DbErrors.XMLRPC_INVALID_NUMBER_OF_ARGUMENTS_MSG);
		}
		if (!validateParameterTypes(params, new Class[]{PARAM_STR, PARAM_STR}))
			throw new XmlRpcException(DbErrors.XMLRPC_INVALID_ARGUMENT_LIST,
			                          "invalid arguments for userInGroupWithSavePermission(String sessionId, String type)");
		String sessionId = (String) params.get(0);
		int requestorId = FileSystemDbFunctions.getUserIdFromSession(sessionId).intValue();
		return UserGroupDbFunctions.userInGroupWithSavePermission(requestorId, (String) (params.get(1)));
	}
}
