// UserGroupDbFunctions.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.functions;

import org.apache.xmlrpc.XmlRpcException;

import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.functions.FileSystemDbFunctions;
import mit.cadlab.dome3.util.MultipleErrorsException;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.db.DbErrors;

/**
 * This class contains functions which allow users to interact with the database
 * and manipulate users and groups.
 */
public class UserGroupDbFunctions
{
	//todo:editUserGroupInfo should also modify the corresponding folders
	//changing user/group' name should change folder names
	//should add new folders if necessary

	private static String simpleActiveUsersAndGroupsListQuery = "select TYPE,ID,NAME from USERS_GROUPS where STATUS='"
	        + DbConstants.USER_GROUP_STATUS_ACTIVE + "' order by TYPE,NAME";
	private static String simpleUserGroupListQuery = "select ID,NAME from USERS_GROUPS where TYPE=? order by NAME";
	private static String detailedUserGroupListQuery = "select ID,NAME,DESCRIPTION,STATUS from USERS_GROUPS where TYPE=? order by NAME";
	private static String detailedSpecificUserQuery = "select ID,NAME,DESCRIPTION,STATUS from USERS_GROUPS where ID in (select USER_ID from SESSIONS where SESSIONS.ID=?)";
	private static String getUserGroupInfoQuery = "select ID,TYPE,NAME,DESCRIPTION,STATUS,CAN_SAVE_MODEL,CAN_SAVE_PLAYSPACE from USERS_GROUPS where ID=?";
	private static String getUserGroupsQuery = "select ID,NAME,DESCRIPTION from USERS_GROUPS where ID in (select GROUP_ID from GROUP_MEMBERSHIP where MEMBER_ID=?)";
	private static String getGroupMembersQuery = "select TYPE,ID,NAME,DESCRIPTION from USERS_GROUPS where ID in (select MEMBER_ID from GROUP_MEMBERSHIP where GROUP_ID=?)";
	private static String createNewUserGroupQuery = "insert into USERS_GROUPS (NAME,DESCRIPTION,PASSWORD,CAN_SAVE_MODEL,CAN_SAVE_PLAYSPACE,TYPE) values (?,?,?,?,?,?)";
	private static String deleteUserGroupQuery = "delete from USERS_GROUPS where ID=?";
	private static String changePasswordQuery = "update USERS_GROUPS set PASSWORD=? where ID=?";

	public static Vector getSimpleActiveUsersAndGroupsList() throws XmlRpcException
	{
		try {
			return DbUtils.executeQuery(simpleActiveUsersAndGroupsListQuery, false);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	private static Vector getUserGroupList(String type, String query) throws XmlRpcException
	{
		try {
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			stmt.setString(1, type);
			return DbUtils.executeQuery(stmt, false);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	private static Vector getSpecificUser(String sessionId, String query) throws XmlRpcException
	{
		try {
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			stmt.setString(1, sessionId);
			return DbUtils.executeQuery(stmt, false);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * Get an abbreviated version of the user list. The list is a vector of vectors containing
	 * the user name and id.
	 * @return Vector containing the list of user record vectors.
	 */
	public static Vector getSimpleUserList() throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "getSimpleUserList");
		return getUserGroupList(DbConstants.USER_TYPE, simpleUserGroupListQuery);
	}

	/**
	 * Get an abbreviated version of the group list. The list is a vector of vectors containing
	 * the group name and id.
	 * @return Vector containing the list of group record vectors.
	 */
	public static Vector getSimpleGroupList() throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "getSimpleGroupList");
		return getUserGroupList(DbConstants.GROUP_TYPE, simpleUserGroupListQuery);
	}

	/**
	 * Get a detailed version of the user list. The list is a vector of vectors
	 * containing user name, id, description and status.
	 * @return Vector containing the list of user record vectors.
	 */
	public static Vector getDetailedUserList() throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "getDetailedUserList");
		return getUserGroupList(DbConstants.USER_TYPE, detailedUserGroupListQuery);
	}


	/**
	 * Get a detailed version of the user list. The list is a vector of vectors
	 * containing user name, id, description and status.
	 * @return Vector containing the list of user record vectors.
	 */
	public static Vector getDetailedSpecificUser(String sessionId) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "getDetailedSpecificUser");
		return getSpecificUser(sessionId, detailedSpecificUserQuery);
	}


	/**
	 * Get a detailed version of the group list. The list is a vector of vectors
	 * containing group name, id, description and status.
	 * @return Vector containing the list of group record vectors.
	 */
	public static Vector getDetailedGroupList() throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "getDetailedGroupList");
		return getUserGroupList(DbConstants.GROUP_TYPE, detailedUserGroupListQuery);
	}


	/**
	 * Get detailed user/group info (id, type, name, description, status, CAN_SAVE_MODEL, CAN_SAVE_PLAYSPACE)
	 * @return Vector containing the user/group info.
	 */
	public static Vector getUserGroupInfo(int userGroupId) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "getUserGroupInfo");
		try {
			PreparedStatement stmt = DbUtils.getPreparedStatement(getUserGroupInfoQuery);
			stmt.setInt(1, userGroupId);
			Vector v = DbUtils.executeQuery(stmt, true);
			if (v.isEmpty()) {
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_USER_GROUP,
				                          DbErrors.XMLRPC_NO_SUCH_USER_GROUP_MSG);
			} else
				return v;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	/**
	 * Get user's groups info (<group id, group names, descr>)
	 * for a given user, where <> indicates a list of items.
	 * @return Vector containing the user's groups info.
	 */
	public static Vector getGroupsForUser(int userId) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "getGroupsForUser");
		try {
			PreparedStatement stmt = DbUtils.getPreparedStatement(getUserGroupsQuery);
			stmt.setInt(1, userId);
			return DbUtils.executeQuery(stmt, false);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

    /**
     * Get user's groups info (<group id, group names, descr>)
     * for a given user, where <> indicates a list of items.
     * @return Vector containing the user's groups info.
     */
    public static boolean isAdmin(int userId) throws XmlRpcException  {
        Vector groups = getGroupsForUser(userId);
        for (int i = 0; i < groups.size(); i++) {
            if (((Integer)((Vector) groups.get(i)).get(1)).intValue() ==DbConstants.ADMIN_GROUP_ID);
                return true;
        }
        return false;
    }


	/**
	 * Create a new user.
	 * @param name User name.
	 * @param descr User decription.
	 * @param pwd User password (encrypted).
	 * @param canSaveModels
	 * @param canSavePlayspaces
	 * @return new user id
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static int createNewUser(String name, String descr, byte[] pwd, boolean canSaveModels,
	                                boolean canSavePlayspaces) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "createNewUser");
		return createNewUserGroup(name, descr, pwd, canSaveModels, canSavePlayspaces, DbConstants.USER_TYPE);
	}

	/**
	 * Create a new group. Return success status.
	 * @param name Group name.
	 * @param descr Group decription.
	 * @param canSaveModels
	 * @param canSavePlayspaces
	 * @return new group id
	 */
	public static int createNewGroup(String name, String descr, boolean canSaveModels,
	                                 boolean canSavePlayspaces) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "createNewGroup");
		return createNewUserGroup(name, descr, null, canSaveModels, canSavePlayspaces, DbConstants.GROUP_TYPE);
	}

	public static int createNewUserGroup(String name, String descr, byte[] pwd, boolean canSaveModels,
	                                     boolean canSavePlayspaces, String type) throws XmlRpcException
	{
		try {
			PreparedStatement pStmt = DbUtils.getPreparedStatement(createNewUserGroupQuery);
			pStmt.setString(1, name);
			pStmt.setString(2, descr);
			pStmt.setBytes(3, pwd);
			pStmt.setBoolean(4, canSaveModels);
			pStmt.setBoolean(5, canSavePlayspaces);
			pStmt.setString(6, type);
			int id = DbUtils.executeInsert(pStmt, true);

			// reserve a row in USER_GROUP_FOLDERS table for a user
			if (canSaveModels || canSavePlayspaces) {
				String query = "insert into USER_GROUP_FOLDERS (USER_GROUP_ID) values ('" + id + "')";
				DbUtils.executeInsert(query, false);
			}
			// create 2 model folders (public and private)
			if (canSaveModels) {
				int publicModelFolderId = FileSystemDbFunctions.createFolder(DbConstants.NULL,
				                                                             id + "_public",
				                                                             DbConstants.MODEL_TYPE);
				int privateModelFolderId = FileSystemDbFunctions.createFolder(DbConstants.NULL,
				                                                              id + "_private",
				                                                              DbConstants.MODEL_TYPE);

				String query = "update USER_GROUP_FOLDERS set PUBLIC_MODEL_FOLDER_ID="
				        + publicModelFolderId + ", " +
				        "PRIVATE_MODEL_FOLDER_ID="
				        + privateModelFolderId + " where USER_GROUP_ID=" + id;
				DbUtils.executeUpdate(query);
			}

			// create a playspace folder
			if (canSavePlayspaces) {
				int publicPlayspaceFolderId = FileSystemDbFunctions.createFolder(DbConstants.NULL,
				                                                                 id + "_public",
				                                                                 DbConstants.PLAYSPACE_TYPE);
				int privatePlayspaceFolderId = FileSystemDbFunctions.createFolder(DbConstants.NULL,
				                                                                  id + "_private",
				                                                                  DbConstants.PLAYSPACE_TYPE);
				String query = "update USER_GROUP_FOLDERS set PUBLIC_PLAYSPACE_FOLDER_ID="
				        + publicPlayspaceFolderId + ", " +
				        "PRIVATE_PLAYSPACE_FOLDER_ID="
				        + privatePlayspaceFolderId + " where USER_GROUP_ID=" + id;
				DbUtils.executeUpdate(query);
			}
			return id;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	/**
	 * Delete a user/group.
	 * @param id User/group Id.
	 */
	public static void deleteUserGroup(int id) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "deleteUserGroup");
		try {
			PreparedStatement stmt = DbUtils.getPreparedStatement(deleteUserGroupQuery);
			stmt.setInt(1, id);
			int rowsDeleted = DbUtils.executeUpdate(stmt);
			if (rowsDeleted == 0)
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_USER_GROUP,
				                          DbErrors.XMLRPC_NO_SUCH_USER_GROUP_MSG);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * Edit user/group info.
	 * @param id user or group ID.
	 * @param newName New user name.
	 * @param newDescr New user decription.
	 * @param canSaveModels
	 * @param canSavePlayspaces
	 * @param status User status.
	 */
	public static void editUserGroupInfo(int id, String newName, String newDescr,
	                                     boolean canSaveModels, boolean canSavePlayspaces, String status)
	        throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "editUserGroupInfo");

		try {
			System.out.println("in editUserGroupInfo");
			boolean hasPublicModelFolder = false;
			boolean hasPrivateModelFolder = false;
			boolean hasPublicPlayspaceFolder = false;
			boolean hasPrivatePlayspaceFolder = false;
			int oldPublicModelID = DbConstants.NULL;
			int oldPrivateModelID = DbConstants.NULL;
			int oldPublicPlayspaceID = DbConstants.NULL;
			int oldPrivatePlayspaceID = DbConstants.NULL;

			// check previous canSaveModels and canSavePlayspaces
			String checkOldPermission = "select CAN_SAVE_MODEL,CAN_SAVE_PLAYSPACE from USERS_GROUPS where ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(checkOldPermission);
			stmt.setInt(1, id);
			Vector v = DbUtils.executeQuery(stmt, true);
			if (v.isEmpty())
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_USER_GROUP,
				                          DbErrors.XMLRPC_NO_SUCH_USER_GROUP_MSG);

			boolean oldCanSaveModel = ((Boolean) v.get(0)).booleanValue();
			boolean oldCanSavePlayspace = ((Boolean) v.get(1)).booleanValue();

			// if folder permission changes from true to false
			if (oldCanSaveModel && !canSaveModels) {
				String query = "update USER_GROUP_FOLDERS set PUBLIC_MODEL_FOLDER_ID='" + DbConstants.NULL + "', "
				        + "PRIVATE_MODEL_FOLDER_ID='" + DbConstants.NULL + "' "
				        + "where USER_GROUP_ID='" + id + "'";
				DbUtils.executeUpdate(query);
			}
			if (oldCanSavePlayspace && !canSavePlayspaces) {
				String query = "update USER_GROUP_FOLDERS set PUBLIC_PLAYSPACE_FOLDER_ID='" + DbConstants.NULL + "', "
				        + "PRIVATE_PLAYSPACE_FOLDER_ID='" + DbConstants.NULL + "' "
				        + "where USER_GROUP_ID='" + id + "'";
				DbUtils.executeUpdate(query);
			}
			if (!canSaveModels && !canSavePlayspaces) {
				String query = "delete from USER_GROUP_FOLDERS where USER_GROUP_ID='" + id + "'";
				DbUtils.executeUpdate(query);
			}

			// if folder permission changes from false to true
			if (!oldCanSaveModel && canSaveModels) {
				// for model folders
				//check if already have folder, and get ID
				String checkOldFolder = "select ID from MODEL_FOLDERS where NAME=?";
				PreparedStatement pStmt = DbUtils.getPreparedStatement(checkOldFolder);

				pStmt.setString(1, id + "_public");
				Vector oldPublicIDVector = DbUtils.executeQuery(pStmt, true);
				if (oldPublicIDVector.isEmpty()) {
					hasPublicModelFolder = false;
				} else {
					hasPublicModelFolder = true;
					oldPublicModelID = ((Integer) DbUtils.executeQuery(pStmt, true).get(0)).intValue();
				}

				pStmt.setString(1, id + "_private");
				Vector oldPrivateIDVector = DbUtils.executeQuery(pStmt, true);
				if (oldPrivateIDVector.isEmpty()) {
					hasPrivateModelFolder = false;
				} else {
					hasPrivateModelFolder = true;
					oldPrivateModelID = ((Integer) DbUtils.executeQuery(pStmt, true).get(0)).intValue();
				}

				// if so, add info to the user_group_folders table
				if (hasPublicModelFolder) {
					//if not already have a row in the user_group_folders table, insert a new row
					if (!hasRowInUserGroupFoldersTable(id)) {
						String query = "insert into USER_GROUP_FOLDERS (USER_GROUP_ID, PUBLIC_MODEL_FOLDER_ID, PRIVATE_MODEL_FOLDER_ID) " +
						        "values ('" + id + "','" + oldPublicModelID + "', '" + oldPrivateModelID + "')";
						DbUtils.executeInsert(query, false);
					} else { // if already have a row, update the info
						String query = "update USER_GROUP_FOLDERS set PUBLIC_MODEL_FOLDER_ID='" + oldPublicModelID + "', "
						        + "PRIVATE_MODEL_FOLDER_ID='" + oldPrivateModelID + "' "
						        + "where USER_GROUP_ID='" + id + "'";
						DbUtils.executeUpdate(query);
					}
				} else { //don't already have folder
					// create new folder
					int publicModelFolderId = FileSystemDbFunctions.createFolder(DbConstants.NULL, id + "_public", "MODEL");
					int privateModelFolderId = FileSystemDbFunctions.createFolder(DbConstants.NULL, id + "_private", "MODEL");
					//if not already have a row in the user_group_folders table, insert a new row
					if (!hasRowInUserGroupFoldersTable(id)) {
						String query = "insert into USER_GROUP_FOLDERS (USER_GROUP_ID, PUBLIC_MODEL_FOLDER_ID, PRIVATE_MODEL_FOLDER_ID) " +
						        "values ('" + id + "', '" + publicModelFolderId + "', '" + privateModelFolderId + "')";
						DbUtils.executeInsert(query, false);
					} else { // if already have a row, update the info
						String query = "update USER_GROUP_FOLDERS set PUBLIC_MODEL_FOLDER_ID='" + publicModelFolderId + "', "
						        + "PRIVATE_MODEL_FOLDER_ID='" + privateModelFolderId + "' "
						        + "where USER_GROUP_ID='" + id + "'";
						DbUtils.executeUpdate(query);
					}
				}
			}

			if (!oldCanSavePlayspace && canSavePlayspaces) {
				// for playspace folders
				//check if already have folder, and get ID
				String checkOldFolder = "select ID from PLAYSPACE_FOLDERS where NAME=?";
				PreparedStatement pStmt = DbUtils.getPreparedStatement(checkOldFolder);

				pStmt.setString(1, id + "_public");
				Vector oldPublicIDVector = DbUtils.executeQuery(pStmt, true);
				if (oldPublicIDVector.isEmpty()) {
					hasPublicPlayspaceFolder = false;
				} else {
					hasPublicPlayspaceFolder = true;
					oldPublicPlayspaceID = ((Integer) DbUtils.executeQuery(pStmt, true).get(0)).intValue();
				}

				pStmt.setString(1, id + "_private");
				Vector oldPrivateIDVector = DbUtils.executeQuery(pStmt, true);
				if (oldPrivateIDVector.isEmpty()) {
					hasPrivatePlayspaceFolder = false;
				} else {
					hasPrivatePlayspaceFolder = true;
					oldPrivatePlayspaceID = ((Integer) DbUtils.executeQuery(pStmt, true).get(0)).intValue();
				}

				// if so, add info to the user_group_folders table
				if (hasPublicPlayspaceFolder) {
					//if not already have a row in the user_group_folders table, insert a new row
					if (!hasRowInUserGroupFoldersTable(id)) {
						String query = "insert into USER_GROUP_FOLDERS (USER_GROUP_ID, PUBLIC_PLAYSPACE_FOLDER_ID, PRIVATE_PLAYSPACE_FOLDER_ID) " +
						        "values ('" + id + "', '" + oldPublicPlayspaceID + "', '" + oldPrivatePlayspaceID + "')";
						DbUtils.executeInsert(query, false);
					} else { // if already have a row, update the info
						String query = "update USER_GROUP_FOLDERS set PUBLIC_PLAYSPACE_FOLDER_ID='" + oldPublicPlayspaceID + "', "
						        + "PRIVATE_PLAYSPACE_FOLDER_ID='" + oldPrivatePlayspaceID + "' "
						        + "where USER_GROUP_ID='" + id + "'";
						DbUtils.executeUpdate(query);
					}
				} else { //don't already have folder
					// create new folder
					int publicPlayspaceFolderId = FileSystemDbFunctions.createFolder(DbConstants.NULL, id + "_public", "PLAYSPACE");
					int privatePlayspaceFolderId = FileSystemDbFunctions.createFolder(DbConstants.NULL, id + "_private", "PLAYSPACE");
					//if not already have a row in the user_group_folders table, insert a new row
					if (!hasRowInUserGroupFoldersTable(id)) {
						String query = "insert into USER_GROUP_FOLDERS (USER_GROUP_ID, PUBLIC_PLAYSPACE_FOLDER_ID, PRIVATE_PLAYSPACE_FOLDER_ID) " +
						        "values ('" + id + "', '" + publicPlayspaceFolderId + "', '" + privatePlayspaceFolderId + "')";
						DbUtils.executeInsert(query, false);
					} else { // if already have a row, update the info
						String query = "update USER_GROUP_FOLDERS set PUBLIC_PLAYSPACE_FOLDER_ID='" + publicPlayspaceFolderId + "', "
						        + "PRIVATE_PLAYSPACE_FOLDER_ID='" + privatePlayspaceFolderId + "' "
						        + "where USER_GROUP_ID='" + id + "'";
						DbUtils.executeUpdate(query);
					}
				}
			}

			// update user new info
			String query = "update USERS_GROUPS set NAME='" + newName + "', "
			        + "DESCRIPTION='" + newDescr + "', "
			        + "CAN_SAVE_MODEL='" + canSaveModels + "', "
			        + "CAN_SAVE_PLAYSPACE='" + canSavePlayspaces + "', "
			        + "STATUS='" + status + "' "
			        + "where ID='" + id + "'";
			DbUtils.executeUpdate(query);

		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	private static boolean hasRowInUserGroupFoldersTable(int userGroupId)
	        throws XmlRpcException
	{
		try {
			String checkRow = "select * from USER_GROUP_FOLDERS where USER_GROUP_ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(checkRow);
			stmt.setInt(1, userGroupId);
			Vector v = DbUtils.executeQuery(stmt, true);
			boolean hasRow = !v.isEmpty();
			return (hasRow);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * Change user password
	 * @param userId User id.
	 * @param pwd User password (encrypted).
	 */
	public static void changeUserPassword(int userId, byte[] pwd)
	        throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "changeUserPassword");
		try {
			PreparedStatement stmt = DbUtils.getPreparedStatement(changePasswordQuery);
			stmt.setBytes(1, pwd);
			stmt.setInt(2, userId);
			int rowsDeleted = DbUtils.executeUpdate(stmt);
			if (rowsDeleted == 0)
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_USER_GROUP,
				                          DbErrors.XMLRPC_NO_SUCH_USER_GROUP_MSG);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * Change user password (by a user)
	 * @param sessionId for user
	 * @param oldPwd User old password (encrypted).
	 * @param newPwd User new password (encrypted).
	 */
	public static void changeUserPassword(String sessionId, byte[] oldPwd, byte[] newPwd)
	        throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "changeUserPassword");
		try {
			// get user id
			String getSessionUser = "select USER_ID from SESSIONS where ID=?";
			PreparedStatement pStmt1 = DbUtils.getPreparedStatement(getSessionUser);
			pStmt1.setString(1, sessionId);
			Vector v1 = DbUtils.executeQuery(pStmt1, true);
			if (v1.isEmpty())
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_USER_GROUP,
				                          DbErrors.XMLRPC_NO_SUCH_USER_GROUP_MSG);
			int userId = ((Integer) v1.get(0)).intValue();

			//validate the old pwd
			String checkPassword = "select * from USERS_GROUPS where ID=? and PASSWORD=?";
			PreparedStatement pStmt = DbUtils.getPreparedStatement(checkPassword);
			pStmt.setInt(1, userId);
			pStmt.setBytes(2, oldPwd);
			Vector v = DbUtils.executeQuery(pStmt, true);
			if (v.isEmpty())
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_USER_GROUP,
				                          DbErrors.XMLRPC_NO_SUCH_USER_GROUP_MSG);
			else {
				PreparedStatement stmt = DbUtils.getPreparedStatement(changePasswordQuery);
				stmt.setBytes(1, newPwd);
				stmt.setInt(2, userId);
				int rowsDeleted = DbUtils.executeUpdate(stmt);
				if (rowsDeleted == 0)
					throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_USER_GROUP,
					                          DbErrors.XMLRPC_NO_SUCH_USER_GROUP_MSG);
			}
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * Edit user groups.
	 * @param userId User ID.
	 * @param groupIds
	 */
	public static void editGroupsForUser(int userId, Vector groupIds)
	        throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "editGroupsForUser");
		try {
			// delete old memberships
			String query = "delete from GROUP_MEMBERSHIP where MEMBER_ID='" + userId + "'";
			DbUtils.executeUpdate(query);

			// get all group ids
			query = "select ID from USERS_GROUPS where TYPE='" + DbConstants.GROUP_TYPE + "'";
			Vector v = DbUtils.executeQueryColumnToVector(query);
			Vector badIds = new Vector();
			if (!v.containsAll(groupIds)) {
				for (int i = groupIds.size() - 1; i >= 0; i--) {
					Object g = groupIds.get(i);
					if (!v.contains(g)) {
						groupIds.remove(g);
						badIds.add(g);
					}
				}
			}

			//make sure not trying to add the group to itself
			if (groupIds.remove(new Integer(userId)))
				badIds.addElement(new Integer(userId));

			query = "insert into GROUP_MEMBERSHIP (GROUP_ID, MEMBER_ID) values (?,?)";
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			stmt.setInt(2, userId);
			List errors = new ArrayList();
			for (int i = 0; i < groupIds.size(); i++) {
				stmt.setInt(1, ((Integer) groupIds.get(i)).intValue());
				try {
					stmt.executeUpdate();
				} catch (SQLException e) {
					errors.add(e);
				}
			}

			if (badIds.size() > 0 && errors.isEmpty()) {
				throw new RuntimeException("Invalid group IDs: " + badIds.toString());
			} else if (badIds.size() > 0) {
				errors.add(0, new RuntimeException("Invalid group IDs: " + badIds.toString()));
			}

			if (errors.size() == 1)
				throw (Exception) errors.get(0);
			else if (errors.size() > 1)
				throw new MultipleErrorsException("editGroupsForUser", errors);
		} catch (Exception e) {
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * Get group's members info (<member type, member id, member names, descr>)
	 * for a given group, where <> indicates a list of items.
	 * @return Vector containing the group's members info.
	 */
	public static Vector getMembersForGroup(int groupId) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "getMembersForGroup");
		try {
			PreparedStatement stmt = DbUtils.getPreparedStatement(getGroupMembersQuery);
			stmt.setInt(1, groupId);
			return DbUtils.executeQuery(stmt, false);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * Edit group members
	 * @param groupId Group ID.
	 * @param memberIds
	 */
	public static void editMembersForGroup(int groupId, Vector memberIds)
	        throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "editMembersForGroup");
		try {
			// delete old memberships
			String query = "delete from GROUP_MEMBERSHIP where GROUP_ID='" + groupId + "'";
			DbUtils.executeUpdate(query);

			// get all member ids
			query = "select ID from USERS_GROUPS";
			Vector v = DbUtils.executeQueryColumnToVector(query);
			Vector badIds = new Vector();
			if (!v.containsAll(memberIds)) {
				for (int i = memberIds.size() - 1; i >= 0; i--) {
					Object g = memberIds.get(i);
					if (!v.contains(g)) {
						memberIds.remove(g);
						badIds.add(g);
					}
				}
			}

			//make sure not trying to add the group to itself
			if (memberIds.remove(new Integer(groupId)))
				badIds.addElement(new Integer(groupId));

			query = "insert into GROUP_MEMBERSHIP (GROUP_ID, MEMBER_ID) values (?,?)";
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			stmt.setInt(1, groupId);
			List errors = new ArrayList();
			for (int i = 0; i < memberIds.size(); i++) {
				stmt.setInt(2, ((Integer) memberIds.get(i)).intValue());
				try {
					stmt.executeUpdate();
				} catch (SQLException e) {
					errors.add(e);
				}
			}

			if (badIds.size() > 0 && errors.isEmpty()) {
				throw new RuntimeException("Invalid member IDs: " + badIds.toString());
			} else if (badIds.size() > 0) {
				errors.add(0, new RuntimeException("Invalid member IDs: " + badIds.toString()));
			}

			if (errors.size() == 1)
				throw (Exception) errors.get(0);
			else if (errors.size() > 1)
				throw new MultipleErrorsException("editMembersForGroup", errors);
		} catch (Exception e) {
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	/**
	 *
	 * @param userId
	 * @param type
	 * @return
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Boolean userOrGroupHasSavePermission(int userId, String type)
	        throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "userOrGroupHasSavePermission");
		try {
			String query = null;
			if (type.equals(DbConstants.MODEL_TYPE))
				query = "select CAN_SAVE_MODEL from USERS_GROUPS where ID=" + userId;
			if (type.equals(DbConstants.PLAYSPACE_TYPE))
				query = "select CAN_SAVE_PLAYSPACE from USERS_GROUPS where ID=" + userId;
			boolean b = ((Boolean) ((DbUtils.executeQuery(query, true).get(0)))).booleanValue();
			return new Boolean(b);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 *
	 * @param userId
	 * @param type
	 * @return
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Boolean userInGroupWithSavePermission(int userId, String type)
	        throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "userInGroupWithSavePermission");
		try {
			Vector groups = getGroupsForUser(userId);
			Vector row;
			if (!groups.isEmpty()) {
				int groupId;
				for (int i = 0; i < groups.size(); i++) {
					row = ((Vector) groups.get(i));
					groupId = ((Integer) row.get(0)).intValue();
					if ((userOrGroupHasSavePermission(groupId, type)).booleanValue())
						return new Boolean(true);
				}
			}
			return new Boolean(false);
		} catch (Exception e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}
}