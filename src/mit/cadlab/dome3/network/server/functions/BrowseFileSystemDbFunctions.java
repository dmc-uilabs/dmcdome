// FileSystemDbFunctions.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.functions;

import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.server.db.DbErrors;
import mit.cadlab.dome3.network.server.db.DbUtils;
import org.apache.xmlrpc.XmlRpcException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;


//todo do we close the statement when a sql exception happens?
//todo how to return error messages?
//todo how to do PreparedStatements?

/**
 * set of functions for manipulating the virtual file system on the server
 */
public class BrowseFileSystemDbFunctions extends FileSystemDbFunctions
{

	/**
	 * self scope listing
	 * @param requestorId ID of requestor
	 * @param type "MODEL" or "PLAYSPACE"
	 * @return vector containing folder ids
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getUserSpaceForSession(String loginType, int requestorId, String type) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getUserSpaceForSession " + requestorId + "  " + type);
		try {
			if (!loginType.equalsIgnoreCase(DbConstants.LOGIN_TYPE_GUEST) && userGroupIsActive(requestorId)) {
				// check for public folders
				Vector folderIdVec = getUserGroupFolders(requestorId, type);
				int publicFolderId = ((Integer) folderIdVec.get(0)).intValue();

				if (publicFolderId != DbConstants.NULL) // public folder exist
				{
					return folderIdVec; // retrun both public and private folders
				}
			}
			System.out.println("exiting getUserSpaceForSession");
			return new Vector();
		} catch (Exception e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * get folders from the server scope
	 * @param loginType of requestor
	 * @param type "MODEL" or "PLAYSPACE"
	 * @return vector containing folder ids
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getServerSpace(String loginType, String type) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getServerSpace");
		try {
			int groupId = 1;    // administrators group

			// check for public folders
			Vector folderIdVec = getUserGroupFolders(groupId, type);
			int publicFolderId = ((Integer) folderIdVec.get(0)).intValue();

			if (publicFolderId != DbConstants.NULL) // public folder exist
			{
				// check whether the login type is admin
				if (!loginType.equalsIgnoreCase(DbConstants.LOGIN_TYPE_ADMIN)) {
					// not a member
					folderIdVec.removeElementAt(1);
					return folderIdVec; // retrun only public private folder
				}
				return folderIdVec; // retrun both public and private folders
			}
			return new Vector();
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
	public static Vector getUserGroupSpacesList(String userOrGroup, String type) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "getUserSpacesList");

		try {
			String query;

			query = "select ID, NAME from USERS_GROUPS where CAN_SAVE_" + type + " = 'true' and TYPE='" + userOrGroup;
			query += "' and STATUS='" + DbConstants.USER_GROUP_STATUS_ACTIVE + "'";
			return DbUtils.executeQuery(query, false);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * get both Public and Private folders of either model or playspace for a user or group
	 * @param id User or group id
	 * @param type has to be either MODEL or PLAYSPACE
	 * @return
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static Vector getUserGroupFolders(String loginType, int requestorId, int id, String type)
	        throws XmlRpcException
	{
		Vector v = new Vector();
		try {//check whether user or group
			String query = "select TYPE from USERS_GROUPS where ID='" + id + "'";
			String userGroup = (String) DbUtils.executeQuery(query, true).get(0);
			//System.out.println(userGroup);

			String getFolders = null;
			if (type.equals(DbConstants.MODEL_FOLDER_TYPE))
				getFolders = "select PUBLIC_MODEL_FOLDER_ID,PRIVATE_MODEL_FOLDER_ID from USER_GROUP_FOLDERS where USER_GROUP_ID=?";
			else if (type.equals(DbConstants.PLAYSPACE_FOLDER_TYPE))
				getFolders = "select PUBLIC_PLAYSPACE_FOLDER_ID,PRIVATE_PLAYSPACE_FOLDER_ID from USER_GROUP_FOLDERS where USER_GROUP_ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(getFolders);
			stmt.setInt(1, id);
			v = DbUtils.executeQuery(stmt, true);
			//System.out.println("original v" + v);

			if (v.isEmpty()) {
				throw new XmlRpcException(DbErrors.XMLRPC_NO_SUCH_USER_GROUP_OR_HAS_NO_FOLDER,
				                          DbErrors.XMLRPC_NO_SUCH_USER_GROUP_OR_HAS_NO_FOLDER_MSG);
			}

			if (loginType.equalsIgnoreCase(DbConstants.LOGIN_TYPE_GUEST)) {
				v.removeElementAt(1);
				//System.out.println("v after remove" + v);
				return v;
			} else if (loginType.equalsIgnoreCase(DbConstants.LOGIN_TYPE_USER)) {
				if (userGroup.equalsIgnoreCase(DbConstants.USER_TYPE)) {
					if (requestorId == id) { //one self
						//System.out.println("v without remove" + v);
						return v;
					} else {
						v.removeElementAt(1);
						//System.out.println("v after remove" + v);
						return v;
					}
				} else if (userGroup.equalsIgnoreCase(DbConstants.GROUP_TYPE)) {
					if (FileSystemDbFunctions.doesGroupContainMember(new Integer(id), new Integer(requestorId))) {// a member
						return v;
					} else {
						v.removeElementAt(1);
						return v;
					}
				}
			} else if (loginType.equalsIgnoreCase(DbConstants.LOGIN_TYPE_ADMIN)) {
				return v;
			}
			return new Vector();

		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static boolean userGroupIsActive(int id) throws XmlRpcException
	{
		//Debug.trace(Debug.ALL, "userGroupIsActive");

		try {
			String query = "select STATUS from USERS_GROUPS where ID='" + id + "'";
			String status = (String) (DbUtils.executeQuery(query, true)).get(0);
			return status.equalsIgnoreCase(DbConstants.USER_GROUP_STATUS_ACTIVE);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}
}
