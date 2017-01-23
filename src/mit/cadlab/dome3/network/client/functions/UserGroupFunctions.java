// UserGroupFunctions.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.client.functions;

import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.client.connection.ServerConnection;

import java.util.Vector;

/**
 * This class contains functions which allow users to interact with the server
 * and manipulate users and groups.
 */
public class UserGroupFunctions
{

	/**
	 * Get an abbreviated version of the user list. The list is a vector of vectors containing
	 * the user name and id.
	 * @return Vector containing the list of user record vectors.
	 */
	public static Vector getSimpleActiveUsersAndGroupsList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.GET_SIMPLE_ACTIVE_USERS_AND_GROUPS_LIST);
	}

	/**
	 * Get an abbreviated version of the user list. The list is a vector of vectors containing
	 * the user name and id.
	 * @return Vector containing the list of user record vectors.
	 */
	public static Vector getSimpleUserList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.GET_SIMPLE_USER_LIST);
	}

	/**
	 * Get an abbreviated version of the group list. The list is a vector of vectors containing
	 * the group name and id.
	 * @return Vector containing the list of group record vectors.
	 */
	public static Vector getSimpleGroupList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.GET_SIMPLE_GROUP_LIST);
	}

	/**
	 * Get a detailed version of the user list. The list is a vector of vectors
	 * containing user name, id, description and status.
	 * @return Vector containing the list of user record vectors.
	 */
	public static Vector getDetailedUserList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.GET_DETAILED_USER_LIST);
	}

	/**
	 * Get a detailed version of the group list. The list is a vector of vectors
	 * containing group name, id, description and status.
	 * @return Vector containing the list of group record vectors.
	 */
	public static Vector getDetailedGroupList(ServerConnection svrConn)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.GET_DETAILED_GROUP_LIST);
	}


	/**
	 * Get detailed user/group info (id, type, name, description, status, CAN_SAVE_MODEL, CAN_SAVE_PLAYSPACE)
	 * @return Vector containing the user/group info.
	 */
	public static Vector getUserGroupInfo(ServerConnection svrConn, int userGroupId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.GET_USER_GROUP_INFO,
		                                Vectors.create(new Integer(userGroupId)));
	}


	/**
	 * Get user's groups info (<group id, group names, descr>)
	 * for a given user, where <> indicates a list of items.
	 * @return Vector containing the user's groups info.
	 */
	public static Vector getGroupsForUser(ServerConnection svrConn, int userId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.GET_GROUPS_FOR_USER,
		                                Vectors.create(new Integer(userId)));
	}


	/**
	 * Create a new user.
	 * @param name User name.
	 * @param descr User decription.
	 * @param pwd User password (encrypted).
	 * @param canSaveModels
	 * @param canSavePlayspaces
	 * @return new user id
	 */
	public static int createNewUser(ServerConnection svrConn, String name, String descr, byte[] pwd, boolean canSaveModels,
	                                boolean canSavePlayspaces)
	{
		return ((Integer) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.CREATE_NEW_USER,
		                                  Vectors.create(name, descr, pwd, new Boolean(canSaveModels), new Boolean(canSavePlayspaces)))).intValue();
	}

	/**
	 * Create a new group. Return success status.
	 * @param name Group name.
	 * @param descr Group decription.
	 * @param canSaveModels
	 * @param canSavePlayspaces
	 * @return new group id
	 */
	public static int createNewGroup(ServerConnection svrConn, String name, String descr, boolean canSaveModels,
	                                 boolean canSavePlayspaces)
	{
		return ((Integer) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.CREATE_NEW_GROUP,
		                                  Vectors.create(name, descr, new Boolean(canSaveModels), new Boolean(canSavePlayspaces)))).intValue();
	}

	/**
	 * Delete a user/group.
	 * @param id User/group Id.
	 */
	public static void deleteUserGroup(ServerConnection svrConn, int id)
	{
		svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.DELETE_USER_GROUP,
		                Vectors.create(new Integer(id)));
	}

	/**
	 * Edit user/group info.
	 * @param id User ID.
	 * @param newName New user name.
	 * @param newDescr New user decription.
	 * @param canSaveModels
	 * @param canSavePlayspaces
	 * @param status User status.
	 */
	public static void editUserGroupInfo(ServerConnection svrConn, int id, String newName, String newDescr,
	                                     boolean canSaveModels, boolean canSavePlayspaces, String status)
	{
		svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.EDIT_USER_GROUP_INFO,
		                Vectors.create(new Integer(id), newName, newDescr, new Boolean(canSaveModels),
		                               new Boolean(canSavePlayspaces), status));
	}

	/**
	 * Change user password
	 * @param userId User id.
	 * @param pwd User password (encrypted).
	 */
	public static void changeUserPassword(ServerConnection svrConn, int userId, byte[] pwd)
	{
		svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.CHANGE_USER_PASSWORD,
		                Vectors.create(new Integer(userId), pwd));
	}

	/**
	 * Change user password (by a user)
	 * @param oldPwd User old password (encrypted).
	 * @param newPwd User new password (encrypted).
	 */
	public static void changeUserPassword(ServerConnection svrConn, byte[] oldPwd, byte[] newPwd)
	{
		svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.CHANGE_USER_PASSWORD,
		                Vectors.create(svrConn.getConnectionId(), oldPwd, newPwd));
	}

	/**
	 * Edit user groups.
	 * @param userId User ID.
	 * @param groupIds
	 */
	public static void editGroupsForUser(ServerConnection svrConn, int userId, Vector groupIds)
	{
		svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.EDIT_GROUPS_FOR_USER,
		                Vectors.create(new Integer(userId), groupIds));
	}

	/**
	 * Get group's members info (<member type, member id, member names, descr>)
	 * for a given group, where <> indicates a list of items.
	 * @return Vector containing the group's members info.
	 */
	public static Vector getMembersForGroup(ServerConnection svrConn, int groupId)
	{
		return (Vector) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.GET_MEMBERS_FOR_GROUP,
		                                Vectors.create(new Integer(groupId)));
	}

	/**
	 * Edit group members
	 * @param groupId Group ID.
	 * @param memberIds
	 */
	public static void editMembersForGroup(ServerConnection svrConn, int groupId, Vector memberIds)
	{
		svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.EDIT_MEMBERS_FOR_GROUP,
		                Vectors.create(new Integer(groupId), memberIds));
	}

	/**
	 *
	 * @param svrConn
	 * @return
	 */
	public static Boolean userOrGroupHasSaveModelPermission(ServerConnection svrConn)
	{
		return (Boolean) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.SESSION_USER_HAS_SAVE_PERMISSION,
		                                 Vectors.create(svrConn.getConnectionId(), "MODEL"));
	}

	/**
	 *
	 * @param svrConn
	 * @return
	 */
	public static Boolean userOrGroupHasSaveModelPermission(ServerConnection svrConn, int id)
	{
		return (Boolean) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.USER_OR_GROUP_HAS_SAVE_PERMISSION,
		                                 Vectors.create(new Integer(id), "MODEL"));
	}

	/**
	 *
	 * @param svrConn
	 * @return
	 */
	public static Boolean userOrGroupHasSavePlayspacePermission(ServerConnection svrConn)
	{
		return (Boolean) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.SESSION_USER_HAS_SAVE_PERMISSION,
		                                 Vectors.create(svrConn.getConnectionId(), "PLAYSPACE"));
	}

	/**
	 *
	 * @param svrConn
	 * @param id
	 * @return
	 */
	public static Boolean userOrGroupHasSavePlayspacePermission(ServerConnection svrConn, int id)
	{
		return (Boolean) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.USER_OR_GROUP_HAS_SAVE_PERMISSION,
		                                 Vectors.create(new Integer(id), "PLAYSPACE"));
	}

	/**
	 *
	 * @param svrConn
	 * @return
	 */
	public static Boolean userInGroupWithSaveModelPermission(ServerConnection svrConn)
	{
		return (Boolean) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.USER_IN_GROUP_WITH_SAVE_PERMISSION,
		                                 Vectors.create(svrConn.getConnectionId(), "MODEL"));
	}

	/**
	 *
	 * @param svrConn
	 * @return
	 */
	public static Boolean userInGroupWithSavePlayspacePermission(ServerConnection svrConn)
	{
		return (Boolean) svrConn.execute(DbConstants.FUNC_TYPE_USER_GROUP + "." + DbConstants.USER_IN_GROUP_WITH_SAVE_PERMISSION,
		                                 Vectors.create(svrConn.getConnectionId(), "PLAYSPACE"));
	}

}
