// FileSystemDbFunctions.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.functions;

import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.Debug;
import org.exolab.ID.UUIDGenerator;

import java.util.Vector;
import java.util.Collections;
import java.sql.*;

import org.apache.xmlrpc.XmlRpcException;


//todo do we close the statement when a sql exception happens?
//todo how to return error messages?
//todo how to do PreparedStatements?

/**
 * set of functions for manipulating the virtual file system on the server
 */
public class DeployFileSystemDbFunctions extends FileSystemDbFunctions
{

	/**
	 *
	 * @param userId ID of user to get folders from
	 * @param requestorId ID of requestor
	 * @param type "MODEL" or "PLAYSPACE"
	 * @return vector containing folder ids
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getUserSpace(String loginType, int userId, int requestorId, String type) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "getUserSpace " + userId + "  " + type);
		if (loginType.equalsIgnoreCase(DbConstants.LOGIN_TYPE_GUEST))
			return new Vector();
		else {
			try {
				// check for public folders
				Vector folderIdVec = getUserGroupFolders(userId, type);
				int publicFolderId = ((Integer) folderIdVec.get(0)).intValue();

				if (publicFolderId != DbConstants.NULL) // public folder exist
				{
					if (userId == requestorId) // the requestor is the user self
						return folderIdVec; // retrun both public and private folders
					else {
						folderIdVec.removeElementAt(1);
						return folderIdVec; // retrun only public private folder
					}
				}
				return new Vector();
			} catch (Exception e) {
				Debug.trace(Debug.ERROR, e.toString());
				throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
			}
		}
	}

	/**
	 * @param groupId ID of group to get folders from
	 * @param requestorId ID of requestor
	 * @param type "MODEL" or "PLAYSPACE"
	 * @return vector containing folder ids
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getGroupSpace(int groupId, int requestorId, String type) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "getGroupSpace");
		try {
			// check for public folders
			Vector folderIdVec = getUserGroupFolders(groupId, type);
			int publicFolderId = ((Integer) folderIdVec.get(0)).intValue();

			if (publicFolderId != DbConstants.NULL) // public folder exist
			{ // check whether the requestor is a member of the group
				Vector membersInfoVec = UserGroupDbFunctions.getMembersForGroup(groupId);

				for (int i = 0; i < membersInfoVec.size(); i++) {
					if (requestorId == ((Integer) ((Vector) membersInfoVec.get(i)).get(1)).intValue()) {
						return folderIdVec; // retrun both public and private folders
					}
				}
				// not a member
				folderIdVec.removeElementAt(1);
				return folderIdVec; // retrun only public private folder
			}
			return new Vector();
		} catch (Exception e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * @param groupId ID of group to get folders from
	 * @param requestorId ID of requestor
	 * @param type "MODEL" or "PLAYSPACE"
	 * @return vector containing folder ids
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getGroupSpaceNoMembershipCheck(int groupId, int requestorId, String type) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "getGroupSpace");
		try {
			// check for public folders
			Vector folderIdVec = getUserGroupFolders(groupId, type);
			int publicFolderId = ((Integer) folderIdVec.get(0)).intValue();

			if (publicFolderId != DbConstants.NULL) // public folder exist
			{
				return folderIdVec; // retrun both public and private folders

			}
			return new Vector();
		} catch (Exception e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * get folders from the server scope
	 * @param requestorId ID of requestor
	 * @param type "MODEL" or "PLAYSPACE"
	 * @return vector containing folder ids
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getServerSpace(int requestorId, String type) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "getServerSpace");
		try {
			// check for public folders
			int groupId = 1;    // administrators group
			return getGroupSpace(groupId, requestorId, type);
		} catch (Exception e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * get folders from the server scope without membership check
	 * @param requestorId ID of requestor
	 * @param type "MODEL" or "PLAYSPACE"
	 * @return vector containing folder ids
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getServerSpaceNoMembershipCheck(int requestorId, String type) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "getServerSpace");
		try {
			// check for public folders
			int groupId = 1;    // administrators group
			return getGroupSpaceNoMembershipCheck(groupId, requestorId, type);
		} catch (Exception e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	/**
	 *
	 * @param userOrGroup "U" or "G"
	 * @param type "MODEL" or "PLAYSPACE"
	 * @return vector containing IDs and names of the users/groups who can save models/playspaces
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getUserGroupSpacesList(String userOrGroup, String loginType, Integer userId, String type) throws XmlRpcException
	{
		Debug.trace(Debug.ALL, "getUserSpacesList");

		if (loginType.equalsIgnoreCase(DbConstants.LOGIN_TYPE_GUEST))
			return new Vector(); //return empty vector
		else if (loginType.equalsIgnoreCase(DbConstants.LOGIN_TYPE_USER)) { //return only myself

			try {
				String query;
				query = "select ID, NAME from USERS_GROUPS where STATUS = '" + DbConstants.USER_GROUP_STATUS_ACTIVE;
				if (type.equalsIgnoreCase(DbConstants.PLAYSPACE_TYPE))
					query += "' and CAN_SAVE_PLAYSPACE = 'true'";
				else if (type.equalsIgnoreCase(DbConstants.MODEL_TYPE))
					query += "' and CAN_SAVE_MODEL = 'true'";
				query += " and TYPE='" + userOrGroup + "'";

				Vector v = DbUtils.executeQuery(query, false);
				if (userOrGroup.equals(DbConstants.USER_TYPE)) {
					for (int i = v.size() - 1; i >= 0; i--) {
						//System.out.println("Test for " + (String)((Vector) v.get(i)).get(1));
						if (!userId.equals(((Vector) v.get(i)).get(0))) {//remove all the users in the vector except myself
							//System.out.println("----Remove " + (String) ((Vector) v.get(i)).get(1));
							v.remove(i);
						}
					}
				} else if (userOrGroup.equals(DbConstants.GROUP_TYPE)) {
					for (int i = v.size() - 1; i >= 0; i--) {
						Integer aGroupId = (Integer) ((Vector) v.get(i)).get(0);
						if (!doesGroupContainMember(aGroupId, userId)) {//remove all the group in the vector from which the user is not a member
							v.remove(i);
						}
					}
				}
				return v;
			} catch (SQLException e) {
				e.printStackTrace();
				throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
			}
		} else if (loginType.equalsIgnoreCase(DbConstants.LOGIN_TYPE_ADMIN)) { //return all users

			try {
				String query;
				query = "select ID, NAME from USERS_GROUPS where STATUS = '" + DbConstants.USER_GROUP_STATUS_ACTIVE;
				if (type.equalsIgnoreCase(DbConstants.PLAYSPACE_TYPE))
					query += "' and CAN_SAVE_PLAYSPACE = 'true'";
				else if (type.equalsIgnoreCase(DbConstants.MODEL_TYPE))
					query += "' and CAN_SAVE_MODEL = 'true'";
				query += " and TYPE='" + userOrGroup + "'";
				return DbUtils.executeQuery(query, false);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
			}
		}
		return new Vector();
	}


/*
    public static Vector getIsUserMember(String sessisonId) throws XmlRpcException {
        Debug.trace(Debug.ALL, "getUserIdFromSession");

        try {
            String query;

            query = "select USER_ID from SESSIONS where ID='" + sessisonId + "'";
            Vector v = DbUtils.executeQuery(query, true);
            if (v.isEmpty()) {
                throw new XmlRpcException(DbConstants.XMLRPC_BAD_SESSION_ID,
                        DbConstants.XMLRPC_BAD_SESSION_ID_MSG);
            }
            return (Integer) v.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new XmlRpcException(DbConstants.XMLRPC_DB_ERROR, e.getMessage());
        }
    }
*/
}
