// PermissionDbFunctions.java
// Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
package mit.cadlab.dome3.network.server.functions;

import mit.cadlab.dome3.network.server.Debug;
import mit.cadlab.dome3.util.MultipleErrorsException;
import mit.cadlab.dome3.gui.permission.PermissionUtils;
import mit.cadlab.dome3.network.server.db.DbConstants;
import mit.cadlab.dome3.network.server.db.DbUtils;
import mit.cadlab.dome3.network.server.db.DbErrors;

import org.apache.xmlrpc.XmlRpcException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

/**
 *  Functions to get and set permissions on objects in DOME.
 */
public class PermissionDbFunctions
{

	private static final String permissionCategoryIdQuery = "select ID from PERMISSION_CATEGORIES where NAME=?";
	private static final String permissionTypesForCategoryQuery = "select ID, NAME from PERMISSION_TYPES where CATEGORY_ID=?";
	private static final String permissionTypeIdsForCategoryQuery = "select ID from PERMISSION_TYPES where CATEGORY_ID=?";
	private static final String permissionLinksForCategoryQuery = "select PERMISSION_ID, PERMISSION_DEPENDENT_ID from PERMISSION_LINKS where PERMISSION_ID in (select ID from PERMISSION_TYPES where CATEGORY_ID=?)";
	private static final String usersForObjectPermissionsQuery = "select TYPE, ID, NAME from USERS_GROUPS where ID in " +
	        "(select USER_ID from OBJECT_PERMISSIONS where OBJECT_ID=? and PERMISSION_ID in " +
	        "(select ID from PERMISSION_TYPES where CATEGORY_ID=?)) and STATUS='" + DbConstants.USER_GROUP_STATUS_ACTIVE + "'";
	private static final String permissionsForObjectQuery = "select USER_ID, PERMISSION_ID from OBJECT_PERMISSIONS, USERS_GROUPS " +
	        "where OBJECT_ID=? and PERMISSION_ID in (select ID from PERMISSION_TYPES where CATEGORY_ID=?) " +
	        "and OBJECT_PERMISSIONS.USER_ID=USERS_GROUPS.ID and STATUS='" + DbConstants.USER_GROUP_STATUS_ACTIVE + "'";
	private static final String deleteObjectPermissionsByCategoryQuery = "delete from OBJECT_PERMISSIONS where OBJECT_ID=? and PERMISSION_ID in (select ID from PERMISSION_TYPES where CATEGORY_ID=?)";
	private static final String addUserObjectPermissionQuery = "insert into OBJECT_PERMISSIONS (OBJECT_ID,USER_ID,PERMISSION_ID) values (?,?,?)";
	private static final String deleteAllObjectPermissionsQuery = "delete from OBJECT_PERMISSIONS where OBJECT_ID=?";

	public static int getPermissionCategoryId(String categoryName) throws XmlRpcException
	{
		try {
			PreparedStatement stmt = DbUtils.getPreparedStatement(permissionCategoryIdQuery);
			stmt.setString(1, categoryName);
			Vector v = DbUtils.executeQuery(stmt, true);
			if (v.isEmpty()) {
				throw new XmlRpcException(DbErrors.XMLRPC_SET_PERMISSION_ERROR, "invalid permission category \"" + categoryName + "\"");
			}
			return ((Integer) v.get(0)).intValue();
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * Returns information about a specified permission category.
	 * @param categoryName a permission category
	 * @return a Vector of two Vectors. The first vector has permission type information for the category (permissionTypeId, permissionTypeName).
	 *    The second vector has permission dependency information for the category (permissionTypeId, dependentPermissionTypeId).
	 *    Vectors are empty if there is no data of that type for this category.
	 * @throws org.apache.xmlrpc.XmlRpcException if categoryName is invalid
	 */
	public static Vector getPermissionCategoryInfo(String categoryName) throws XmlRpcException
	{
		try {
			int cid = getPermissionCategoryId(categoryName);
			Vector result = new Vector(2);
			PreparedStatement stmt = DbUtils.getPreparedStatement(permissionTypesForCategoryQuery);
			stmt.setInt(1, cid);
			Vector r1 = DbUtils.executeQuery(stmt, false);
			if (!r1.isEmpty()) {
				result.add(r1);
				stmt = DbUtils.getPreparedStatement(permissionLinksForCategoryQuery);
				stmt.setInt(1, cid);
				result.add(DbUtils.executeQuery(stmt, false));
			} else {
				result.add(r1);
				result.add(DbUtils.NO_VECTOR);
			}
			return result;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * Returns permission information about a particular object in a particular permission category
	 * @param objectId
	 * @param categoryName
	 * @return a Vector of two Vectors. The first vector has user/group information (userGroupType, userId, userName).
	 *    The second vector has permission information (userId,permissionId).
	 *    Vectors are empty if there is no data of that type for this category.
	 * @throws org.apache.xmlrpc.XmlRpcException if categoryName is invalid
	 */
	public static Vector getObjectPermissionInfo(String objectId, String categoryName) throws XmlRpcException
	{
		try {
			int cid = getPermissionCategoryId(categoryName);
			Vector result = new Vector(2);
			PreparedStatement stmt = DbUtils.getPreparedStatement(usersForObjectPermissionsQuery);
			stmt.setString(1, objectId);
			stmt.setInt(2, cid);
			Vector r1 = DbUtils.executeQuery(stmt, false);
			if (!r1.isEmpty()) {
				result.add(r1);
				stmt = DbUtils.getPreparedStatement(permissionsForObjectQuery);
				stmt.setString(1, objectId);
				stmt.setInt(2, cid);
				result.add(DbUtils.executeQuery(stmt, false));
			} else {
				result.add(r1);
				result.add(DbUtils.NO_VECTOR);
			}
			return result;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * Sets permissions in specified category for an object to be the given set of permissions.
	 * @param objectId id of object
	 * @param categoryName name of permission category
	 * @param permissions vector of users and permissions (userId,permissionId)
	 * @throws org.apache.xmlrpc.XmlRpcException
	 * todo validate object ids; add in logic between permission categories
	 */
	public static void setObjectPermissions(String objectId, String categoryName, Vector permissions) throws XmlRpcException
	{
		deleteObjectPermissions(objectId, categoryName);
		addObjectPermissions(objectId, categoryName, permissions);
	}

	/**
	 * Deletes all permissions in specified category for the object.
	 * @param objectId
	 * @param categoryName
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static void deleteObjectPermissions(String objectId, String categoryName) throws XmlRpcException
	{
		try {
			int cid = getPermissionCategoryId(categoryName);
			PreparedStatement stmt = DbUtils.getPreparedStatement(deleteObjectPermissionsByCategoryQuery);
			stmt.setString(1, objectId);
			stmt.setInt(2, cid);
			DbUtils.executeUpdate(stmt);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * Adds permissions in specified category for an object to be the given set of permissions.
	 * @param objectId id of object
	 * @param categoryName name of permission category
	 * @param permissions vector of users and permissions (userId,permissionId)
	 * @throws org.apache.xmlrpc.XmlRpcException
	 * assumes permissions added are new to the table
	 * todo validate incoming data (valid object id)
	 */
	public static void addObjectPermissions(String objectId, String categoryName, Vector permissions) throws XmlRpcException
	{
		try {
			int cid = getPermissionCategoryId(categoryName);
			PreparedStatement stmt = DbUtils.getPreparedStatement(permissionTypeIdsForCategoryQuery);
			stmt.setInt(1, cid);
			Vector permissionTypes = DbUtils.executeQueryColumnToVector(stmt);

			stmt = DbUtils.getPreparedStatement(addUserObjectPermissionQuery);
			Vector errors = new Vector();
			for (Iterator iterator = permissions.iterator(); iterator.hasNext();) {
				try {
					Vector v = (Vector) iterator.next();
					stmt.setString(1, objectId);
					Integer userId = (Integer) v.get(0);
					stmt.setInt(2, ((Integer) v.get(0)).intValue());
					Integer permissionType = (Integer) v.get(1);
					if (permissionTypes.contains(permissionType)) {
						stmt.setInt(3, ((Integer) v.get(1)).intValue());
						stmt.executeUpdate();
					} else { // message below isn't the friendliest, but invalid data would only be sent by direct programmers,
						// not the provided DOME clients
						throw new RuntimeException("invalid permission type id: " + permissionType + " for user id " + userId);
					}
				} catch (SQLException e) {
					errors.add(e);
				}
			}
			stmt.close();
			if (!errors.isEmpty())
				throw new XmlRpcException(DbErrors.XMLRPC_SET_PERMISSION_ERROR, new MultipleErrorsException("addObjectPermissions", errors).getMessage());
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	/**
	 * Removes all permissions associated with the particular object.
	 * @param objectId id of object whose permissions will be deleted
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */
	public static void deleteAllObjectPermissions(String objectId) throws XmlRpcException
	{
		try {
			PreparedStatement stmt = DbUtils.getPreparedStatement(deleteAllObjectPermissionsQuery);
			stmt.setString(1, objectId);
			DbUtils.executeUpdate(stmt);
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static boolean hasPermission(int userID, String modelObjectId, String permissionName) throws XmlRpcException
	{
		try {
			String hasPermissionToSetEditPrivQuery = "select * from OBJECT_PERMISSIONS where OBJECT_ID=? and USER_ID=? and PERMISSION_ID in (select ID from PERMISSION_TYPES where NAME=?) ";

			PreparedStatement stmt = DbUtils.getPreparedStatement(hasPermissionToSetEditPrivQuery);
			stmt.setString(1, modelObjectId);
			stmt.setInt(2, userID);
			stmt.setString(3, permissionName);
			Vector v = DbUtils.executeQuery(stmt, true);
			if (v.isEmpty()) {
				return false;
			}
			return v.get(0) != null;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}


	/**
	 * add for getting playspace members infor
	 * @param playspaceId
	 * @return a vector of members id
	 * @throws org.apache.xmlrpc.XmlRpcException
	 */

	public static Vector getPlayspaceMembers(String playspaceId) throws XmlRpcException
	{
		try {
			int useCatid = getPermissionCategoryId(PermissionUtils.PLAYSPACE_USE_PRIVILEGES);
			int editCatid = getPermissionCategoryId(PermissionUtils.PLAYSPACE_EDIT_PRIVILEGES);
			String query = "select USER_ID from OBJECT_PERMISSIONS where OBJECT_ID=? and PERMISSION_ID in (select ID from PERMISSION_TYPES where CATEGORY_ID = " + useCatid + "OR CATEGORY_ID = " + editCatid + ") ";
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			stmt.setString(1, playspaceId);

			Vector v = DbUtils.executeQuery(stmt, false);
			if (v != null && v.size() != 0)
				return removeRedundant(v);
			else
				return new Vector();
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static Vector getPlayspaceUserMembers(String playspaceId) throws XmlRpcException
	{
		try {
			int useCatid = getPermissionCategoryId(PermissionUtils.PLAYSPACE_USE_PRIVILEGES);
			int editCatid = getPermissionCategoryId(PermissionUtils.PLAYSPACE_EDIT_PRIVILEGES);
			String query = "select USER_ID from OBJECT_PERMISSIONS where OBJECT_ID=? and PERMISSION_ID in (select ID from PERMISSION_TYPES where CATEGORY_ID = " + useCatid + "OR CATEGORY_ID = " + editCatid + ") ";
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			stmt.setString(1, playspaceId);

			Vector v = DbUtils.executeQuery(stmt, false);
			if (v != null && v.size() != 0) {
				Vector cleanedVec = removeRedundant(v);
				return getOnlyUserIdsFromUserGroupIds(cleanedVec);
			} else
				return new Vector();
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	private static Vector getOnlyUserIdsFromUserGroupIds(Vector userGroupIds) throws XmlRpcException
	{
		try {
			Vector onlyUserIds = new Vector();
			String query = "select TYPE from USERS_GROUPS where ID=?";
			PreparedStatement stmt = DbUtils.getPreparedStatement(query);
			for (int i = 0; i < userGroupIds.size(); i++) {
				int id = ((Integer) userGroupIds.get(i)).intValue();
				stmt.setInt(1, id);
				Vector v = DbUtils.executeQuery(stmt, true);
				if (DbConstants.USER_TYPE.equals((String) v.get(0)))
					onlyUserIds.addElement(new Integer(id));
				else if (DbConstants.GROUP_TYPE.equals((String) v.get(0))) {
					Vector memberInfos = UserGroupDbFunctions.getMembersForGroup(id);
					for (int j = 0; j < memberInfos.size(); j++) {
						Vector memberInfo = (Vector) memberInfos.get(j);
						onlyUserIds.addElement(memberInfo.get(1));
					}
				}
			}
			return onlyUserIds;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}
	}

	public static boolean checkPermissionUserAndGroups(String objectId, int userId, String permissionName) throws XmlRpcException
	{

		Vector groupIds = null;
		try {
			groupIds = getGroupIds(userId);
		} catch (XmlRpcException e) {
			e.printStackTrace();  //To change body of catch statement use Options | File Templates.
		}


		groupIds.add(new Integer(userId));
		groupIds.add(new Integer(DbConstants.GUEST_USER_ID));
		String idsToCheck = createStringList(groupIds);

		try {
			String hasPermission = "select * from OBJECT_PERMISSIONS where OBJECT_ID=? and USER_ID IN " + idsToCheck + "and PERMISSION_ID in (select ID from PERMISSION_TYPES where NAME=?) ";
			PreparedStatement stmt = DbUtils.getPreparedStatement(hasPermission);
			stmt.setString(1, objectId);
			stmt.setString(2, permissionName);
			Vector v = DbUtils.executeQuery(stmt, false);
			if (v.isEmpty()) {
				return false;
			}
			return v.get(0) != null;
		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}

	}

	private static Vector getGroupIds(int userId) throws XmlRpcException
	{

		try {
			String getGroupId = "select Group_id FROM GROUP_MEMBERSHIP  Where Member_id = " + userId;
			return DbUtils.executeQueryColumnToVector(getGroupId);

		} catch (SQLException e) {
			Debug.trace(Debug.ERROR, e.toString());
			throw new XmlRpcException(DbErrors.XMLRPC_DB_ERROR, e.getMessage());
		}

	}

	public static String createStringList(Vector v)
	{

		int size = v.size();
		StringBuffer list = new StringBuffer();
		list.append("(");
		for (int i = 0; i < size; i++) {
			list = list.append(v.get(i).toString());
			if (i != size - 1)
				list.append(",");

		}
		list.append(")");
		return list.toString();

	}

	/**
	 * to remove redundant id entries
	 * @param original id vector
	 * @return
	 */
	public static Vector removeRedundant(Vector original)
	{
		Vector cleanedVector = new Vector();
		for (int i = 0; i < original.size(); i++) {
			Integer id = (Integer) ((Vector) original.get(i)).get(0);
			boolean alreadyhave = false;
			for (int j = 0; j < cleanedVector.size(); j++) {
				if (id.intValue() == ((Integer) cleanedVector.get(j)).intValue()) {
					alreadyhave = true;
					break;
				}
			}
			if (!alreadyhave)
				cleanedVector.add(id);
		}

		return cleanedVector;
	}

}